package com.mu.flink.streamer.test;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

//import org.I0Itec.zkclient.ZkClient;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction.Context;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010.FlinkKafkaProducer010Configuration;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.kafka.common.serialization.StringDeserializer;

//import kafka.utils.ZKStringSerializer$;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.Instrument;
import com.example.mu.domain.Party;
import com.example.mu.domain.Price;
import com.example.mu.domain.Trade;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.mu.flink.streamer.HzPositionSink;
import com.mu.flink.streamer.HzTradeSink;
import com.mu.flink.streamer.PositionAggregator;
import com.mu.flink.streamer.TradeFlinkStreamer;
import com.mu.flink.streamer.TradeProcess;
import com.mu.flink.streamer.TradeToTupleKeyTrade;

//import kafka.admin.TopicCommand;
//import kafka.consumer.ConsumerConfig;
//import kafka.consumer.ConsumerIterator;
//import kafka.consumer.KafkaStream;
//import kafka.javaapi.consumer.ConsumerConnector;
//import kafka.producer.KeyedMessage;
//import kafka.producer.Producer;
//import kafka.producer.ProducerConfig;
//import kafka.server.KafkaConfig;
//import kafka.server.KafkaServer;
//import kafka.utils.MockTime;
//import kafka.utils.TestUtils;
//import kafka.utils.TestZKUtils;
//import kafka.utils.Time;
//import kafka.utils.ZKStringSerializer$;
//import kafka.zk.EmbeddedZookeeper;

public class TradeFlinkStreamerTest {
	HazelcastInstance hz = Hazelcast.newHazelcastInstance();
	final static Logger LOG = LoggerFactory.getLogger(TradeFlinkStreamerTest.class);
	private static Gson gson = new GsonBuilder().create();

	private int brokerId = 0;
	private String topic = "trade";
//
//	Producer producer;
//	ZkClient zkClient;
//	KafkaServer kafkaServer;
//	ConsumerConnector consumer;
//	EmbeddedZookeeper zkServer;
	Properties consumerProperties;
	Properties properties;
	int port;

	@Before
	public void setUpKafkaAndZoo() {

//		String zkConnect = TestZKUtils.zookeeperConnect();
//		zkServer = new EmbeddedZookeeper(zkConnect);
//		zkClient = new ZkClient(zkServer.connectString(), 30000, 30000, ZKStringSerializer$.MODULE$);
//
//		// setup Broker
//		port = TestUtils.choosePort();
//		Properties props = TestUtils.createBrokerConfig(brokerId, port, true);
//
//		KafkaConfig config = new KafkaConfig(props);
//		Time mock = new MockTime();
//		kafkaServer = TestUtils.createServer(config, mock);
//		String[] arguments = new String[] { "--topic", topic, "--partitions", "1", "--replication-factor", "1" };
//		// create topic
//		TopicCommand.createTopic(zkClient, new TopicCommand.TopicCommandOptions(arguments));
//
//		List<KafkaServer> servers = new ArrayList<KafkaServer>();
//		servers.add(kafkaServer);
//		TestUtils.waitUntilMetadataIsPropagated(scala.collection.JavaConversions.asScalaBuffer(servers), topic, 0,
//				5000);
//
//		// setup producer
//		properties = TestUtils.getProducerConfig("localhost:" + port);
//		ProducerConfig producerConfig = new ProducerConfig(properties);
//		producer = new Producer(producerConfig);
//
//		// setup simple consumer
//		consumerProperties = TestUtils.createConsumerProperties(zkServer.connectString(), "group0", "consumer0", -1);
//		consumerProperties.put("bootstrap.servers", "localhost:"+port);
//		//consumerProperties.put("partition.assignment.strategy", "range");
//		consumerProperties.put("auto.offset.reset", "none");
//		consumerProperties.put("key.deserializer", StringDeserializer.class);
//		consumerProperties.put("value.deserializer", JsonDeserializer.class);
		
		//consumer = kafka.consumer.Consumer.createJavaConsumerConnector(new ConsumerConfig(consumerProperties));

		// set up Test Data
		// create new Trades, Instruments, Prices, Parties
		Party party1 = new Party();
		party1.setName("PARTY1");
		party1.setPartyId("PARTY1");
		party1.setPositionAccountId("ACC1");
		party1.setRole("POS ACC HOLDER");
		party1.setShortName("PTY1");

		Party party2 = new Party();
		party2.setName("PARTY2");
		party2.setPartyId("PARTY2");
		party2.setPositionAccountId("ACC2");
		party2.setRole("POS ACC HOLDER");
		party2.setShortName("PTY2");

		IMap<String, Party> map = hz.getMap("party");
		map.put("PARTY1", party1);
		map.put("PARTY2", party2);

		Instrument ins1 = new Instrument();
		ins1.setAssetClass("EQUITY");
		ins1.setInstrumentId("INS1");
		ins1.setIssuer("TEST");
		ins1.setProduct("INS1");
		ins1.setSymbol("INS1");

		Instrument ins2 = new Instrument();
		ins2.setAssetClass("EQUITY");
		ins2.setInstrumentId("INS2");
		ins2.setIssuer("TEST");
		ins2.setProduct("INS2");
		ins2.setSymbol("INS2");

		IMap<String, Instrument> mapIns = hz.getMap("instrument");
		mapIns.put("INS1", ins1);
		mapIns.put("INS2", ins2);

		Price price = new Price();
		price.setInstrumentId(ins1.getSymbol());
		price.setPrice(340.8987978);
		price.setPriceId(UUID.randomUUID().toString());
		price.setTimeStamp(System.currentTimeMillis());

		Price price2 = new Price();
		price2.setInstrumentId(ins2.getSymbol());
		price2.setPrice((double) Math.random() * 1000 + 1);
		price2.setPriceId(UUID.randomUUID().toString());
		price2.setTimeStamp(System.currentTimeMillis());

		IMap<String, Price> mapPrice = hz.getMap("price");
		mapPrice.put(price.getInstrumentId(), price);
		mapPrice.put(price2.getInstrumentId(), price2);

	}

	@Test
	public void testdummyMessageToKafka() {

//		KeyedMessage<Integer, byte[]> data = new KeyedMessage(topic, "test-message".getBytes(StandardCharsets.UTF_8));
//
//		List<KeyedMessage> messages = new ArrayList<KeyedMessage>();
//		messages.add(data);
//
//		// send message
//
//		producer.send(scala.collection.JavaConversions.asScalaBuffer(messages));
//		producer.close();
//
//		// deleting zookeeper information to make sure the consumer starts from the
//		// beginning
//		// see
//		// https://stackoverflow.com/questions/14935755/how-to-get-data-from-old-offset-point-in-kafka
//		zkClient.delete("/consumers/group0");
//
//		// starting consumer
//		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
//		topicCountMap.put(topic, 1);
//		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
//		KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
//		ConsumerIterator<byte[], byte[]> iterator = stream.iterator();
//
//		if (iterator.hasNext()) {
//			String msg = new String(iterator.next().message(), StandardCharsets.UTF_8);
//			System.out.println(msg);
//			assertEquals("test-message", msg);
//		} else {
//			fail();
//		}

	}

	@Test
	public void sendTestTradeMessageToKafka() throws Exception {

		int buyQty = 0;
		int sellQty = 0;

		double buyPnL = 0;
		double sellPnl = 0;

		IMap<String, Party> map = hz.getMap("party");
		assert (map.size() == 2);

		IMap<String, Instrument> mapIns = hz.getMap("instrument");
		assert (mapIns.size() == 2);

		IMap<String, Price> mapPrice = hz.getMap("price");
		assert (mapPrice.size() == 2);

		// set up the trade information for this test
		// set the date
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();

		// generate all the common attributes for both sides of the trade

		String executionId = UUID.randomUUID().toString();
		String currency = "USD";
		String executingFirmId = "TEST_EX1_" + 1;
		String executingTraderId = "TEST_TRD1";
		String executionVenue = "EX1";
		double trdpx = (double) (Math.random() * 1000 + 1);
		int quantity = (int) (Math.random() * 100 + 1);

		Party buyParty = map.get("PARTY1");
		Party sellParty = map.get("PARTY2");
		Date tradeDate = date;
		System.out.println("Current date and time in Date's toString() is : " + tradeDate + "\n");

		Instrument tradedInstrument = mapIns.get("INS1");
		Price spotPx = mapPrice.get(tradedInstrument.getSymbol());

		// do the buy
		String buyKey = UUID.randomUUID().toString();
		Trade atrade = new Trade();
		atrade.setClientId(buyParty.getName());
		atrade.setCurrency(currency);
		atrade.setExecutingFirmId(executingFirmId);
		atrade.setExecutingTraderId(executingTraderId);
		atrade.setExecutionId(buyKey);
		atrade.setExecutionVenueId(executionVenue);
		atrade.setFirmTradeId(executionId);
		atrade.setInstrumentId(tradedInstrument.getSymbol());
		atrade.setOriginalTradeDate(tradeDate);
		atrade.setPositionAccountId(buyParty.getPositionAccountId());
		atrade.setPrice(trdpx);
		atrade.setQuantity(quantity);
		atrade.setSecondaryFirmTradeId(executionId);
		atrade.setSecondaryTradeId(executionId);
		atrade.setSettlementDate(tradeDate);
		atrade.setTradeDate(tradeDate);
		atrade.setTradeId(buyKey);
		atrade.setTradeType("0");
		atrade.setSecondaryTradeType("0");

		// run the calculation here for buy
		buyQty += atrade.getQuantity();

		LOG.info("Spot px used " + spotPx.getPrice());
		double pnl = spotPx.getPrice() * atrade.getQuantity() - atrade.getTradeValue();
		buyPnL += pnl;

		// now generate Sell side
		String sellKey = UUID.randomUUID().toString();
		Trade aSelltrade = new Trade();
		aSelltrade.setClientId(sellParty.getName());
		aSelltrade.setCurrency(currency);
		aSelltrade.setExecutingFirmId(executingFirmId);
		aSelltrade.setExecutingTraderId(executingTraderId);
		aSelltrade.setExecutionId(sellKey);
		aSelltrade.setExecutionVenueId(executionVenue);
		aSelltrade.setFirmTradeId(executionId);
		aSelltrade.setInstrumentId(tradedInstrument.getSymbol());
		aSelltrade.setOriginalTradeDate(tradeDate);
		aSelltrade.setPositionAccountId(sellParty.getPositionAccountId());
		aSelltrade.setPrice(trdpx);
		aSelltrade.setQuantity(-quantity);
		aSelltrade.setSecondaryFirmTradeId(executionId);
		aSelltrade.setSecondaryTradeId(executionId);
		aSelltrade.setSettlementDate(tradeDate);
		aSelltrade.setTradeDate(tradeDate);
		aSelltrade.setTradeId(sellKey);
		aSelltrade.setTradeType("0");
		aSelltrade.setSecondaryTradeType("0");

		// run the calculation here for buy
		sellQty += aSelltrade.getQuantity();

		double sellpnl = spotPx.getPrice() * aSelltrade.getQuantity() - aSelltrade.getTradeValue();
		sellPnl += sellpnl;
		
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		
		DataStream<String> stream = env.fromElements(atrade.toJSON(), aSelltrade.toJSON());

		SingleOutputStreamOperator<Trade> mainDataStream   = stream.process(new TradeProcess());
		mainDataStream.addSink(new HzTradeSink());
		
		final OutputTag<Trade> outputTag = new OutputTag<Trade>("position-stream") {
		};
		
		DataStream<Trade> sideOutputStream = mainDataStream.getSideOutput(outputTag);
		sideOutputStream.flatMap(new TradeToTupleKeyTrade()).keyBy(0).flatMap(new PositionAggregator())
		.addSink(new HzPositionSink());
		
		env.execute();
		
		//now confirm if the counts are the same
		assertEquals(2, hz.getMap(HzTradeSink.getMapName()).size());
		assertEquals(2, hz.getMap(HzPositionSink.getMapName()).size());

		// now send the trades to Kafka queue

		// KeyedMessage<Integer, byte[]> data = new KeyedMessage(topic,
		// atrade.getTradeId().getBytes(StandardCharsets.UTF_8),
		// atrade.toJSON().getBytes(StandardCharsets.UTF_8));
		// KeyedMessage<Integer, byte[]> data2 = new KeyedMessage(topic,
		// aSelltrade.getTradeId().getBytes(StandardCharsets.UTF_8),
		// aSelltrade.toJSON().getBytes(StandardCharsets.UTF_8));
		//
		// List<KeyedMessage> messages = new ArrayList<KeyedMessage>();
		// messages.add(data);
		// messages.add(data2);
		//
		// // send message
		//
		// producer.send(scala.collection.JavaConversions.asScalaBuffer(messages));
		// producer.close();
		
//		Properties props = new Properties();
//		// list of host:port pairs used for establishing the initial connections
//		// to the Kakfa cluster
//		props.put("bootstrap.servers", "localhost:"+port);
//		consumerProperties.put("key.deserializer", StringDeserializer.class);
//		consumerProperties.put("value.deserializer", JsonDeserializer.class);
		

		// allows a pool of processes to divide the work of consuming and
		// processing records
		
//
//		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//		DataStream stream = env.fromElements(atrade.toJSON(),aSelltrade.toJSON());
//		FlinkKafkaProducer010<String> producer = new FlinkKafkaProducer010<String>(
//				"localhost:" + port,
//				"trade",
//				new SimpleStringSchema());                // custom configuration for KafkaProducer (including broker list)
//
//		// the following is necessary for at-least-once delivery guarantee
//		producer.setLogFailuresOnly(false);   // "false" by default
//		producer.setFlushOnCheckpoint(true);
//		
//		stream.addSink(producer);
//		
//
//		// deleting zookeeper information to make sure the consumer starts from the
//		// beginning
//		// see
//		// https://stackoverflow.com/questions/14935755/how-to-get-data-from-old-offset-point-in-kafka
////		zkClient.delete("/consumers/group0");
//
//		// starting consumer
//		//
//	
//		// run through Main streamer
//		// TradeFlinkStreamer streamer = new TradeFlinkStreamer();
//		// streamer.consumerConfigs().setProperty("bootstrap.servers",
//		// zkServer.connectString());
//		// streamer.connectToTradeStream();
//		
//		
//		
//
//		FlinkKafkaConsumer010 kafkaConsumer = new FlinkKafkaConsumer010("trade", new SimpleStringSchema(),
//				consumerProperties);
//		DataStream<String> stream2 = env.addSource(kafkaConsumer);
//		stream2.print();
//		env.execute();

	}

	@After
	public void shutdownKafkaZooHz() {
		// cleanup
//		consumer.shutdown();
//		kafkaServer.shutdown();
//		zkClient.close();
//		zkServer.shutdown();

		hz.shutdown();
	}

}
