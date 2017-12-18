package com.mu.flink.pricestreamer;

import java.util.Properties;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.Price;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;




public class PriceFlinkStreamer {
	
	final static Logger LOG = LoggerFactory.getLogger(PriceFlinkStreamer.class);
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
	 
	 public void connectToPriceStream() throws Exception {
			StreamExecutionEnvironment env = StreamExecutionEnvironment
					.getExecutionEnvironment();
			
			FlinkKafkaConsumer010 kafkaConsumer = new FlinkKafkaConsumer010(
					"market_data", new SimpleStringSchema(), consumerConfigs());
			DataStream<String> stream = env.addSource(kafkaConsumer);
			
			stream.map(new MapFunction<String, Price>() {
				private static final long serialVersionUID = -6867736771747690202L;
				
				
				//returns a price object
				
				public Price map(String value) throws Exception {
					
						
			            Price p = gson.fromJson(value, Price.class);

					return p;
				}
			}).addSink(new HzPriceSink());
			
			env.execute();
	 }
	 
	 public static HazelcastInstance getHzClient() {
		return hzClient;
	 }
	
	
	

	public static void main(String[] args) throws Exception {
		LOG.info("Starting Trade Flink Streamer");
		
		new PriceFlinkStreamer().connectToPriceStream();
	
		LOG.info("Completed Trade Flink Streamer");

	}

}
