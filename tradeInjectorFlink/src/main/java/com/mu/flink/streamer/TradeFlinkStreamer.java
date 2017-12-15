package com.mu.flink.streamer;

import java.util.Properties;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
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
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
					"kafka:9092");
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
					StringDeserializer.class);
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
					JsonDeserializer.class);
			props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
			
			// allows a pool of processes to divide the work of consuming and
			// processing records
			props.put(ConsumerConfig.GROUP_ID_CONFIG, "mu");

			return props;
		}
	 
	 public void connectToTradeStream() throws Exception {
			StreamExecutionEnvironment env = StreamExecutionEnvironment
					.getExecutionEnvironment();
			
			FlinkKafkaConsumer010 kafkaConsumer = new FlinkKafkaConsumer010(
					"trade", new SimpleStringSchema(), consumerConfigs());
			DataStream<String> stream = env.addSource(kafkaConsumer);
			
			stream.map(new MapFunction<String, Trade>() {
				private static final long serialVersionUID = -6867736771747690202L;
				
				
				//returns a price object
				
				public Trade map(String value) throws Exception {
					
						
			            Trade p = gson.fromJson(value, Trade.class);

					return p;
				}
			}).addSink(new HzTradeSink());
			
			env.execute();
	 }
	 
	 public static HazelcastInstance getHzClient() {
		return hzClient;
	 }
	
	
	private SinkFunction<Trade> writeToHz() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) throws Exception {
		
		ClassLoader classLoader = TradeFlinkStreamer.class.getClassLoader();
		LOG.info("Starting Trade Flink Streamer");
		//System.out.println("Zookeeper version used is "+Info.MAJOR+"."+Info.MINOR+"."+Info.MICRO);
	    //
		new TradeFlinkStreamer().connectToTradeStream();
		//Class aClass = classLoader.loadClass("com.hazelcast.zookeeper.ZookeeperDiscoveryStrategy");
        //System.out.println("aClass.getName() = " + aClass.getName());
       
		LOG.info("Completed Trade Flink Streamer");
		//hzClient.shutdown();
		
	}

}
