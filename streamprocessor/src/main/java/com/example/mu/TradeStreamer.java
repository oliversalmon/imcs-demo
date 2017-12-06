package com.example.mu;

import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.hadoop.util.StringUtils;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.PositionAccount;
import com.example.mu.domain.Price;
import com.example.mu.domain.Trade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.impl.HazelcastClientInstanceImpl;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.AggregateOperation;
import com.hazelcast.jet.DAG;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.Session;
import com.hazelcast.jet.TimestampKind;
import com.hazelcast.jet.TimestampedEntry;
import com.hazelcast.jet.Vertex;
import com.hazelcast.jet.WindowDefinition;
//import com.hazelcast.jet.connector.kafka.ReadKafkaP;
import com.hazelcast.jet.processor.Processors;
import com.hazelcast.jet.processor.Sinks;
import com.hazelcast.jet.server.JetBootstrap;
import com.hazelcast.jet.stream.IStreamMap;
import com.hazelcast.query.Predicate;
import static com.hazelcast.query.Predicates.*;
import com.hazelcast.jet.processor.DiagnosticProcessors;

import static com.hazelcast.jet.AggregateOperations.allOf;
import static com.hazelcast.jet.AggregateOperations.counting;
import static com.hazelcast.jet.AggregateOperations.mapping;
import static com.hazelcast.jet.AggregateOperations.summingLong;
import static com.hazelcast.jet.AggregateOperations.toSet;
import static com.hazelcast.jet.Edge.between;
import static com.hazelcast.jet.Edge.from;
import static com.hazelcast.jet.Partitioner.HASH_CODE;
import static com.hazelcast.jet.WatermarkEmissionPolicy.emitByFrame;
import static com.hazelcast.jet.WatermarkEmissionPolicy.emitByMinStep;
import static com.hazelcast.jet.WatermarkPolicies.*;
import static com.hazelcast.jet.WindowDefinition.slidingWindowDef;
import static com.hazelcast.jet.impl.connector.ReadWithPartitionIteratorP.readMap;
import static com.hazelcast.jet.processor.DiagnosticProcessors.writeLogger;
import static com.hazelcast.jet.processor.Processors.aggregateToSessionWindow;
import static com.hazelcast.jet.processor.Processors.combineToSlidingWindow;
import static com.hazelcast.jet.processor.Processors.accumulateByFrame;
import static com.hazelcast.jet.processor.Processors.insertWatermarks;

import com.hazelcast.jet.processor.KafkaProcessors;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/*
 * Initial version
 */

public class TradeStreamer {

	private final static String TRADE_QUEUE = "trade";
	private final static String TRADE_MAP = "trade";
	private final static String POSITION_ACCOUNT_MAP = "position-account";
	private final static String PRICE_MAP = "price";
	public static final int MAX_LAG = 1000;
	private static final int SLIDING_WINDOW_LENGTH_MILLIS = 5000;
	private static final int SLIDE_STEP_MILLIS = 10;
	private static final int SESSION_TIMEOUT = 5000;
	private static JetInstance jet = Jet.newJetInstance();
	private static HazelcastInstance hzClient = HazelcastClient.newHazelcastClient();
	
	
	


	private static Gson gson = new GsonBuilder().create();
	final static Logger LOG = LoggerFactory.getLogger(TradeStreamer.class);

	private static Properties getKafkaProperties(String url) throws Exception {
		Properties properties = new Properties();
		properties.setProperty("group.id", "group-mu");

		properties.setProperty("bootstrap.servers", url);
		properties.setProperty("key.deserializer", StringDeserializer.class.getCanonicalName());
		properties.setProperty("value.deserializer", StringDeserializer.class.getCanonicalName());
		properties.setProperty("auto.offset.reset", "latest");

		return properties;

	}

	/**
	 * The main job that pulls trade from Kafka and aggregates to Positions
	 * 
	 * @param url
	 * @throws Exception
	 */
	public static void connectAndStreamToMap(String url) throws Exception {

		try {

			// JetInstance jet = Jet.newJetClient();
			Job job = jet.newJob(getDAG(url));
			long start = System.nanoTime();
			job.execute();
			// Thread.sleep(SECONDS.toMillis(JOB_DURATION));

			IStreamMap<String, Trade> sinkMap = jet.getMap(TRADE_MAP);

			while (true) {
				int mapSize = sinkMap.size();
				System.out.format("Received %d entries in %d milliseconds.%n", mapSize,
						NANOSECONDS.toMillis(System.nanoTime() - start));
				Thread.sleep(10000);
			}

		}

		finally {

		}
	}

	private static DAG getDAG(String url) throws Exception {
		DAG dag = new DAG();

		AggregateOperation<Trade, List<Object>, List<Object>> aggrOp = allOf(summingLong(e -> e.getQuantity()),
				summingLong(e -> e.getTradeValue()), mapping(e -> e.getPositionAccountInstrumentKey(), toSet()));

		Vertex source = dag.newVertex("source", KafkaProcessors.streamKafka(getKafkaProperties(url), TRADE_QUEUE));

		Vertex tradeMapper = dag.newVertex("toTrade",
				Processors.map((Entry<String, String> f) -> new AbstractMap.SimpleEntry<>(f.getKey(),
						gson.fromJson(f.getValue(), Trade.class))));

		// emit each trade to the Position stream
		Vertex tradeProcessor = dag.newVertex("trade-processor",
				Processors.map((Entry<String, Trade> f) -> f.getValue()));

		// create a watermark based on the Trade Timestamp (trade date)
		Vertex insertWatermarks = dag.newVertex("insert-watermarks",
				insertWatermarks(Trade::getTradeTime, limitingLagAndLull(MAX_LAG, 3000), emitByMinStep(10)));

		Vertex aggregateSessions = dag.newVertex("aggregateSessions",
				DiagnosticProcessors.peekOutput(aggregateToSessionWindow(SESSION_TIMEOUT, Trade::getTradeTime,
						Trade::getPositionAccountInstrumentKey, aggrOp)));

		Vertex createPosition = dag.newVertex("create-position",
				DiagnosticProcessors.peekOutput(Processors.map(TradeStreamer::createPositionMapEntry)));

		// sink to Position map 
		//HzClientConfig.buildClientConfig("192.168.1.176:2181");
		//com.hazelcast.client.impl.HazelcastClientProxy impl = (com.hazelcast.client.impl.HazelcastClientProxy)hzClient;

		//impl.getClientConfig().getAddresses().forEach(a->LOG.info("THE FOLLOWING ADDRESSES HAVE BEEN ESTABLISHED "+a));
		
		//Hack to work around Zookeeper and Curator not doing its job with ClientConfig
		
		//List<String> addresses = new ArrayList();
		//addresses.add("172.19.0.3");
		//addresses.add("172.19.0.4");
		
		//impl.getClientConfig().addAddress("172.19.0.3", "172.19.0.4");
		
		Vertex sinkToPosition = dag
				.newVertex("sinkPosition", Sinks.writeMap(POSITION_ACCOUNT_MAP))
				.localParallelism(1);

		Vertex sink = dag.newVertex("sink", Sinks.writeMap(TRADE_MAP));

		source.localParallelism(1);
		tradeMapper.localParallelism(1);

		createPosition.localParallelism(1);
		sinkToPosition.localParallelism(1);

		// connect the vertices to sink to Map
		dag.edge(between(source, tradeMapper));

		dag.edge(between(tradeMapper, sink));

		// connect the vertices to aggregate to Positions
		dag.edge(from(tradeMapper, 1).to(tradeProcessor)).edge(between(tradeProcessor, insertWatermarks).isolated())

				.edge(between(insertWatermarks, aggregateSessions).partitioned(Trade::getPositionAccountInstrumentKey)
						.distributed())
				.edge(between(aggregateSessions, createPosition)).edge(between(createPosition, sinkToPosition));

		return dag;
	}

	private static SimpleEntry<String, PositionAccount> createPositionMapEntry(Session<String, List<Long>> s) {

		// check to see if the record exists in the Position
		PositionAccount aPosition;
		IMap<String, PositionAccount> posMap = jet.getMap(POSITION_ACCOUNT_MAP);
		
		LOG.info("Size of Position Map is " + posMap.size());
		String accountId = s.getKey().split("&")[0];
		String instrumnetId = s.getKey().split("&")[1];

		aPosition = posMap.get(accountId.trim() + instrumnetId.trim());

		if (aPosition == null) {

			aPosition = new PositionAccount();
			LOG.info(
					"Creating a new position account with the following key " + accountId.trim() + instrumnetId.trim());

		}

		LOG.info("Position before amendment is " + aPosition.toJSON());

		aPosition.setAccountId(accountId.trim());
		aPosition.setInstrumentid(instrumnetId.trim());

		// set the position quantity
		LOG.info("aPosition.size for account and instrument is " + aPosition.getAccountId()
				+ aPosition.getInstrumentid() + "=" + aPosition.getSize());
		LOG.info("aPosition.result for account and instrument is " + aPosition.getAccountId()
				+ aPosition.getInstrumentid() + "=" + s.getResult().get(0));
		aPosition.setSize(aPosition.getSize() + s.getResult().get(0));
		LOG.info("final position of size is for account and instrument is " + aPosition.getAccountId()
				+ aPosition.getInstrumentid() + "= " + aPosition.getSize());

		// now do the P&L calculation
		LOG.info("aPosition.tradeValue result for account and instrument is " + aPosition.getAccountId()
				+ aPosition.getInstrumentid() + "=" + s.getResult().get(1));
		IMap<String, Price> pxMap = hzClient.getMap(PRICE_MAP);
		Price spotPx = pxMap.get(instrumnetId.trim());
		if (spotPx == null) {
			LOG.warn("spot px for instrument id is not available defaulting to 1 " + aPosition.getInstrumentid());
			spotPx = new Price();
			spotPx.setPrice(1.00);
		}

		// PNL = qty * spot Px - Traded Value
		double Pnl = s.getResult().get(0) * spotPx.getPrice() - s.getResult().get(1);
		LOG.info("aPosition.spot*qty value for account and instrument is " + aPosition.getAccountId()
				+ aPosition.getInstrumentid() + "=" + s.getResult().get(0) * spotPx.getPrice()+" spot px applied "+spotPx.getPrice());
		aPosition.setPnl(aPosition.getPnl()+Pnl);
		LOG.info("aPosition.final p&l result for account and instrument is " + aPosition.getAccountId()
				+ aPosition.getInstrumentid() + "=" + Pnl);
		LOG.info("Position created is " + aPosition.toJSON());
		return new AbstractMap.SimpleEntry<>(aPosition.getAccountId() + aPosition.getInstrumentid(), aPosition);
	}

	@Deprecated
	private static SimpleEntry<String, PositionAccount> createPositionMapForTimeStampedEntry(
			TimestampedEntry<String, Long> s) {

		// check to see if the record exists in the Position
		PositionAccount aPosition;
		IStreamMap<String, PositionAccount> posMap = jet.getMap(POSITION_ACCOUNT_MAP);
		// LOG.info("Size of Position Map is " + posMap.size());
		String accountId = s.getKey().split("&")[0];
		String instrumnetId = s.getKey().split("&")[1];

		aPosition = posMap.get(accountId.trim() + instrumnetId.trim());

		if (aPosition == null) {

			aPosition = new PositionAccount();
			// LOG.info(
			// "Creating a new position account with the following key " + accountId.trim()
			// + instrumnetId.trim());

		}

		long size = 0;
		size = s.getValue();

		aPosition.setAccountId(accountId.trim());
		aPosition.setInstrumentid(instrumnetId.trim());
		// LOG.info("aPosition.size for account and instrument is " +
		// aPosition.getAccountId()
		// + aPosition.getInstrumentid() + "=" + aPosition.getSize());
		// LOG.info("aPosition.result for account and instrument is " +
		// aPosition.getAccountId()
		// + aPosition.getInstrumentid() + "=" + size);
		aPosition.setSize(aPosition.getSize() + size);
		// LOG.info("final position of size is for account and instrument is " +
		// aPosition.getAccountId()
		// + aPosition.getInstrumentid() + "= " + aPosition.getSize());
		// LOG.info("Position created is " + aPosition.toJSON());
		return new AbstractMap.SimpleEntry<>(aPosition.getAccountId() + aPosition.getInstrumentid(), aPosition);
	}

	/**
	 * This job mainly runs the aggregation against the Trade Map to ensure all
	 * positions are correctly line up to the trade before accepting trades from
	 * Kafka
	 * 
	 * @throws Exception
	 */
	public static void runPositionAggregation() throws Exception {

		Job job = jet.newJob(getPositionAggregationOnMapDAG());
		job.execute();

	}

	private static DAG getPositionAggregationOnMapDAG() throws Exception {

		DAG dag = new DAG();
		AggregateOperation<Trade, List<Object>, List<Object>> aggrOp = allOf(summingLong(e -> e.getQuantity()),
				mapping(e -> e.getPositionAccountInstrumentKey(), toSet()));

		WindowDefinition windowDef = slidingWindowDef(SLIDING_WINDOW_LENGTH_MILLIS, SLIDE_STEP_MILLIS);

		Vertex source = dag.newVertex("trade-source", readMap(TRADE_MAP));

		// emit each trade
		Vertex tradeProcessor = dag.newVertex("trade-processor",
				Processors.map((Entry<String, Trade> f) -> f.getValue()));

		// create a watermark based on the Trade Timestamp (trade date)
		Vertex insertWatermarks = dag.newVertex("insert-watermarks",
				(insertWatermarks(Trade::getTradeTime, limitingLagAndDelay(MAX_LAG, 100), emitByFrame(windowDef))));

		// aggregate quantity based on key
		Vertex aggregateSessions = dag.newVertex("aggregateSessions", aggregateToSessionWindow(SESSION_TIMEOUT,
				Trade::getTradeTime, Trade::getPositionAccountInstrumentKey, aggrOp));

		// convert the aggregations to positions
		Vertex createPosition = dag.newVertex("create-position", Processors.map(TradeStreamer::createPositionMapEntry));

		// sink to Position map
		Vertex sinkToPosition = dag.newVertex("sinkPosition", Sinks.writeMap(POSITION_ACCOUNT_MAP)).localParallelism(1);

		source.localParallelism(1);

		// connect the vertices to sink to Map
		dag.edge(between(source, tradeProcessor));

		// connect the vertices to aggregate to Positions
		dag.edge(between(tradeProcessor, insertWatermarks).isolated())
				// This edge needs to be partitioned+distributed.
				.edge(between(insertWatermarks, aggregateSessions).partitioned(Trade::getTradeId).distributed())
				.edge(between(aggregateSessions, createPosition)).edge(between(createPosition, sinkToPosition));

		return dag;
	}

	public static void main(String[] args) throws Exception {
		// run the position aggregation to ensure all trades are mapped to Positions
		// correctly
		// TradeStreamer.runPositionAggregation();

		// now stream trades from Kafka to Positions and Trades
		TradeStreamer.connectAndStreamToMap(System.getProperty("kafka_url"));

	}

}
