package com.example.mu;

import java.util.Properties;
import java.util.Map.Entry;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.Price;
import com.example.mu.domain.Trade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.hazelcast.jet.DAG;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.Vertex;

//import com.hazelcast.jet.connector.kafka.ReadKafkaP;
import com.hazelcast.jet.processor.Processors;
import com.hazelcast.jet.processor.Sinks;
import com.hazelcast.jet.stream.IStreamMap;

import static com.hazelcast.jet.Edge.between;

import com.hazelcast.jet.processor.KafkaProcessors;


import java.util.AbstractMap;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/*
 * Initial version
 */

public class TradeStreamer {

	private final static String TRADE_QUEUE = "trade";
	private final static String TRADE_MAP = "trade";

	private static Gson gson = new GsonBuilder().create();
	final Logger LOG = LoggerFactory.getLogger(TradeStreamer.class);

	private static Properties getKafkaProperties(String url) throws Exception {
		Properties properties = new Properties();
		properties.setProperty("group.id", "group-" + Math.random());
		// TODO: need to pass this as an environment variable
		properties.setProperty("bootstrap.servers", url);
		properties.setProperty("key.deserializer", StringDeserializer.class.getCanonicalName());
		properties.setProperty("value.deserializer", StringDeserializer.class.getCanonicalName());
		properties.setProperty("auto.offset.reset", "earliest");

		return properties;

	}

	public static void connectAndStreamToMap(String url) throws Exception {

		try {

			JetInstance jet = Jet.newJetInstance();
			//JetInstance jet = Jet.newJetClient();
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

		Vertex source = dag.newVertex("source", KafkaProcessors.streamKafka(getKafkaProperties(url), TRADE_QUEUE));

		Vertex tradeMapper = dag.newVertex("toTrade",
				Processors.map((Entry<String, String> f) -> new AbstractMap.SimpleEntry<>(f.getKey(),
						gson.fromJson(f.getValue(), Trade.class))));
		Vertex sink = dag.newVertex("sink", Sinks.writeMap(TRADE_MAP, HzClientConfig.getClientConfig()));

		source.localParallelism(1);
		tradeMapper.localParallelism(1);

		// connect the vertices
		dag.edge(between(source, tradeMapper));

		dag.edge(between(tradeMapper, sink));

		return dag;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TradeStreamer.connectAndStreamToMap(System.getProperty("kafka_url"));

	}

}
