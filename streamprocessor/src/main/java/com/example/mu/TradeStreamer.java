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
import com.hazelcast.jet.AggregateOperation;
import com.hazelcast.jet.DAG;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.Session;
import com.hazelcast.jet.Vertex;
import com.hazelcast.jet.WindowDefinition;
//import com.hazelcast.jet.connector.kafka.ReadKafkaP;
import com.hazelcast.jet.processor.Processors;
import com.hazelcast.jet.processor.Sinks;
import com.hazelcast.jet.server.JetBootstrap;
import com.hazelcast.jet.stream.IStreamMap;
import com.hazelcast.query.Predicate;
import static com.hazelcast.query.Predicates.*;

import static com.hazelcast.jet.AggregateOperations.allOf;
import static com.hazelcast.jet.AggregateOperations.mapping;
import static com.hazelcast.jet.AggregateOperations.summingLong;
import static com.hazelcast.jet.AggregateOperations.toSet;
import static com.hazelcast.jet.Edge.between;
import static com.hazelcast.jet.Edge.from;

import static com.hazelcast.jet.WatermarkEmissionPolicy.emitByFrame;
import static com.hazelcast.jet.WatermarkPolicies.limitingLagAndDelay;
import static com.hazelcast.jet.WindowDefinition.slidingWindowDef;
import static com.hazelcast.jet.processor.DiagnosticProcessors.writeLogger;
import static com.hazelcast.jet.processor.Processors.aggregateToSessionWindow;
import static com.hazelcast.jet.processor.Processors.insertWatermarks;

import com.hazelcast.jet.processor.KafkaProcessors;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
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
	public static final int MAX_LAG = 1000;
	private static final int SLIDING_WINDOW_LENGTH_MILLIS = 1000;
	private static final int SLIDE_STEP_MILLIS = 10;
	private static final int SESSION_TIMEOUT = 5000;
	private static JetInstance jet = Jet.newJetInstance();

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
				mapping(e -> e.getPositionAccountInstrumentKey(), toSet()));

		WindowDefinition windowDef = slidingWindowDef(SLIDING_WINDOW_LENGTH_MILLIS, SLIDE_STEP_MILLIS);

		Vertex source = dag.newVertex("source", KafkaProcessors.streamKafka(getKafkaProperties(url), TRADE_QUEUE));

		Vertex tradeMapper = dag.newVertex("toTrade",
				Processors.map((Entry<String, String> f) -> new AbstractMap.SimpleEntry<>(f.getKey(),
						gson.fromJson(f.getValue(), Trade.class))));

		// emit each trade to the Position stream
		Vertex tradeProcessor = dag.newVertex("trade-processor",
				Processors.map((Entry<String, Trade> f) -> f.getValue()));

		// create a watermark based on the Trade Timestamp (trade date)
		Vertex insertWatermarks = dag.newVertex("insert-watermarks",
				insertWatermarks(Trade::getTradeTime, limitingLagAndDelay(MAX_LAG, 100), emitByFrame(windowDef)));

		// aggregate quantity based on key
		Vertex aggregateSessions = dag.newVertex("aggregateSessions", aggregateToSessionWindow(SESSION_TIMEOUT,
				Trade::getTradeTime, Trade::getPositionAccountInstrumentKey, aggrOp));

		// convert the aggregations to positions
		Vertex createPosition = dag.newVertex("create-position", Processors.map(TradeStreamer::createPositionMapEntry));

		// sink to Position map
		Vertex sinkToPosition = dag
				.newVertex("sinkPosition", Sinks.writeMap(POSITION_ACCOUNT_MAP, HzClientConfig.getClientConfig()))
				.localParallelism(1);

		// print out
		// Vertex sinkToPosition = dag.newVertex("sinkPosition",
		// writeLogger(TradeStreamer::sessionToString))
		// .localParallelism(1);

		Vertex sink = dag.newVertex("sink", Sinks.writeMap(TRADE_MAP));

		source.localParallelism(1);
		tradeMapper.localParallelism(1);

		// connect the vertices to sink to Map
		dag.edge(between(source, tradeMapper));

		dag.edge(between(tradeMapper, sink));

		// connect the vertices to aggregate to Positions
		dag.edge(from(tradeMapper, 1).to(tradeProcessor)).edge(between(tradeProcessor, insertWatermarks).isolated())
				// This edge needs to be partitioned+distributed.
				.edge(between(insertWatermarks, aggregateSessions).partitioned(Trade::getTradeId).distributed())
				.edge(between(aggregateSessions, createPosition)).edge(between(createPosition, sinkToPosition));

		return dag;
	}

	private static SimpleEntry<String, PositionAccount> createPositionMapEntry(Session<String, List<Long>> s) {

		// check to see if the record exists in the Position
		PositionAccount aPosition;
		IStreamMap<String, PositionAccount> posMap = jet.getMap(POSITION_ACCOUNT_MAP);
		LOG.info("Size of Position Map is "+posMap.size());
		String accountId = s.getKey().split("&")[0];
		String instrumnetId = s.getKey().split("&")[1];

		Set<PositionAccount> resultSet = getPositionAccountWithMatchingInstrumentAndAccount(accountId, instrumnetId,
				posMap);
		if (!resultSet.isEmpty()) {
			PositionAccount[] positionAccounts = new PositionAccount[resultSet.size()];
			if (resultSet.size() > 1)
				LOG.warn("Too many rows returned for account and instrument " + accountId + " " + instrumnetId
						+ " taking the most recent record");

			aPosition = resultSet.toArray(positionAccounts)[positionAccounts.length - 1];

		} else {
			LOG.warn("Creating a new position record " + accountId + " " + instrumnetId);
			aPosition = new PositionAccount();
		}

		aPosition.setAccountId(accountId);
		aPosition.setInstrumentid(instrumnetId);
		aPosition.setSize(aPosition.getSize()+s.getResult().get(0));
		LOG.info("Position created is " + aPosition.toJSON());
		return new AbstractMap.SimpleEntry<>(aPosition.getAccountId()+aPosition.getInstrumentid(), aPosition);
	}

	private static Set<PositionAccount> getPositionAccountWithMatchingInstrumentAndAccount(String account,
			String instrument, IStreamMap<String, PositionAccount> positionMap) {

		Predicate accountPredicate = equal("accountId", account);
		Predicate instrumentPredicate = equal("instrumentid", instrument);
		Predicate predicate = and(accountPredicate, instrumentPredicate);
		return (Set<PositionAccount>) positionMap.values(predicate);

	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TradeStreamer.connectAndStreamToMap(System.getProperty("kafka_url"));
		// JetInstance jet = JetBootstrap.getInstance();
		// jet.newJob(getDAG(System.getProperty("kafka_url"))).execute().get();

	}

}
