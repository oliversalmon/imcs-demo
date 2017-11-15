package com.example.mu;

import static com.hazelcast.jet.Edge.between;
import static com.hazelcast.jet.Edge.from;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Properties;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.Instrument;
import com.example.mu.domain.PositionAccount;
import com.example.mu.domain.Price;
import com.example.mu.domain.Trade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.DAG;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.Vertex;
import com.hazelcast.jet.processor.KafkaProcessors;
import com.hazelcast.jet.processor.Processors;
import com.hazelcast.jet.processor.Sinks;
import com.hazelcast.jet.stream.DistributedCollectors;
import com.hazelcast.jet.stream.IStreamMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;

public class PriceStreamer {

	private final static String PRICE_QUEUE = "market_data";
	private final static String PRICE_MAP = "price";
	

	private static Gson gson = new GsonBuilder().create();
	final static Logger LOG = LoggerFactory.getLogger(PriceStreamer.class);

	private static Properties getKafkaProperties(String url) throws Exception {
		Properties properties = new Properties();
		properties.setProperty("group.id", "group-mu");
		// TODO: need to pass this as an environment variable
		properties.setProperty("bootstrap.servers", url);
		properties.setProperty("key.deserializer", StringDeserializer.class.getCanonicalName());
		properties.setProperty("value.deserializer", StringDeserializer.class.getCanonicalName());
		properties.setProperty("auto.offset.reset", "latest");

		return properties;

	}

	public static void connectAndStreamToMap(String url) throws Exception {

		try {

			JetInstance jet = Jet.newJetInstance();
			// JetInstance jet = Jet.newJetClient();
			Job job = jet.newJob(getDAG(url));
			long start = System.nanoTime();
			job.execute();

		}

		finally {

		}
	}

	private static DAG getDAG(String url) throws Exception {
		DAG dag = new DAG();
		
		
		//Hack to work around Zookeeper and Curator not doing its job with ClientConfig
		
		List<String> addresses = new ArrayList();
		addresses.add("172.19.0.3");
		addresses.add("172.19.0.4");
		
		//HzClientConfig.getClientConfig().addAddress("172.19.0.3", "172.19.0.4");

		Vertex source = dag.newVertex("source", KafkaProcessors.streamKafka(getKafkaProperties(url), PRICE_QUEUE));

		Vertex priceMapper = dag.newVertex("toPrice",
				Processors.map((Entry<String, String> f) -> new AbstractMap.SimpleEntry<>(f.getKey(),
						gson.fromJson(f.getValue(), Price.class))));
		Vertex sink = dag.newVertex("sink", Sinks.writeMap(PRICE_MAP, HzClientConfig.getClientConfig()));

		source.localParallelism(1);
		priceMapper.localParallelism(1);

		// connect the vertices
		dag.edge(between(source, priceMapper));

		dag.edge(between(priceMapper, sink));

		// for each price go through positions with the same instrument and update P&L
		
		return dag;
	}

	

	private static PositionAccount updatePositionValue(double newPrice, PositionAccount p) {
		
		LOG.info("Position before update on Pnl "+p.getPnl());
		LOG.info("Price applied on Pnl "+newPrice);
		
		p.setPnl(p.getSize() * newPrice - p.getPnl());

		LOG.info("Position after update on Pnl "+p.getPnl());
		return p;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		System.out.println("Url fed in is " + System.getProperty("kafka_url"));
		PriceStreamer.connectAndStreamToMap(System.getProperty("kafka_url"));

	}

}
