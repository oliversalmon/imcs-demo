package com.mu.flink.streamer;

import java.util.Properties;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.Trade;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class TradeFlinkStreamer {

	final static Logger LOG = LoggerFactory.getLogger(TradeFlinkStreamer.class);
	private static final HazelcastInstance hzClient = HazelcastClient.newHazelcastClient();
	private static Gson gson = new GsonBuilder().create();

	public Properties consumerConfigs() {
		Properties props = new Properties();
		// list of host:port pairs used for establishing the initial connections
		// to the Kakfa cluster
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

		// allows a pool of processes to divide the work of consuming and
		// processing records
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "mu");

		return props;
	}

	public void connectToTradeStream() throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
		env.setParallelism(3);

		FlinkKafkaConsumer010 kafkaConsumer = new FlinkKafkaConsumer010("trade", new SimpleStringSchema(),
				consumerConfigs());
		DataStream<String> stream = env.addSource(kafkaConsumer);

		SingleOutputStreamOperator<Trade> mainDataStream = stream.process(new TradeProcess());
		mainDataStream.addSink(new HzTradeSink());

		final OutputTag<Trade> outputTag = new OutputTag<Trade>("position-stream") {
		};

		 DataStream<Trade> sideOutputStream = mainDataStream.getSideOutput(outputTag);
		 sideOutputStream.flatMap(new TradeToTupleKeyTrade()).keyBy(0).flatMap(new
		 PositionAggregator())
		 .addSink(new HzPositionSink());

		//DataStream<Trade> sideOutputStream = mainDataStream.getSideOutput(outputTag);
		//sideOutputStream.assignTimestampsAndWatermarks(new TradeTimeStampWaterMarkAssigner(Time.seconds(10)))
		//		.flatMap(new TradeToTupleKeyTrade()).keyBy(0).window(TumblingEventTimeWindows.of(Time.seconds(5)))
		//		.aggregate(new PositionFoldAggregator()).addSink(new HzPositionWindowSink());

		env.execute();
	}

	public static HazelcastInstance getHzClient() {
		return hzClient;
	}

	public static void main(String[] args) throws Exception {

		LOG.info("Starting Trade Flink Streamer");

		new TradeFlinkStreamer().connectToTradeStream();

		LOG.info("Completed Trade Flink Streamer");

	}

}
