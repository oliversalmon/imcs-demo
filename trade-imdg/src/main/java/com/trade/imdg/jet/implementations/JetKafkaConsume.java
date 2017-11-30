package com.trade.imdg.jet.implementations;

import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.DAG;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.Vertex;
import com.hazelcast.jet.config.JetConfig;
import com.hazelcast.jet.processor.KafkaProcessors;
import com.hazelcast.jet.processor.Processors;
import com.hazelcast.jet.processor.Sinks;
import com.hazelcast.jet.processor.Sources;
import com.hazelcast.jet.stream.IStreamMap;
import com.hazelcast.jet.Util;
import com.trade.imdg.business.service.BusinessServiceCacheNames;
import com.example.mu.domain.Instrument;

import static com.hazelcast.jet.Edge.between;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

//@Configuration
public class JetKafkaConsume {

	public final Logger LOG = LoggerFactory.getLogger(JetKafkaConsume.class);
	
	//@Autowired
	//JetInstance jet;
	
	@Autowired
	private HazelcastInstance hzInstance;

	@Value("${kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${kafka.topic.marketData}")
	private  String marketDataTopic;

	@PostConstruct
	public void run() throws Exception {
		
		LOG.info("running the Kafka consume with the following properties "+ bootstrapServers +" "+ marketDataTopic);
		Config config = hzInstance.getConfig();
		
		LOG.info("Group name is "+config.getGroupConfig().getName());
		JetConfig jetconfig = new JetConfig();
		jetconfig.getInstanceConfig().setCooperativeThreadCount(2);
		//jetconfig.getHazelcastConfig().addCacheConfig(new CacheSimpleConfig().setName("kappa-serving-layer"));
		
		
		//jetconfig.setHazelcastConfig(config);
		LOG.info("Group name witin jet config "+jetconfig.getHazelcastConfig().getGroupConfig().getName());
		
		JetInstance instance = Jet.newJetInstance(jetconfig);
		//Jet.newJetInstance(jetconfig);
		
		/*Job job = createJetJob(instance);
		long start = System.nanoTime();
		job.execute();

		IStreamMap<String, Integer> sinkMap = instance
				.getMap(BusinessServiceCacheNames.PRICE_CACHE);
		
		  while (true) {
	            int mapSize = sinkMap.size();
	            System.out.format("Received %d entries in %d milliseconds.%n",
	                    mapSize, NANOSECONDS.toMillis(System.nanoTime() - start));
	            
	            job.execute();
	            //System.out.format("Received %d entries in %d milliseconds.%n",
	                    //mapSize, NANOSECONDS.toMillis(System.nanoTime() - start));
	            Thread.sleep(3000);
	        }
*/
		System.out.println("Executing copy Job");
		Job job = createASimpleCacheJob(instance);
		job.execute().get();
		
		IStreamMap<String, Instrument> sinkMap = instance.getMap("InstrumentMap");
		System.out.println("The new instrument cache size is "+ sinkMap.size());
	}

	/*private Job createJetJob(JetInstance instance) {
		DAG dag = new DAG();
		Properties props = props("group.id", "mu", "bootstrap.servers",
				bootstrapServers, "key.deserializer",
				StringDeserializer.class.getCanonicalName(),
				"value.deserializer",
				IntegerDeserializer.class.getCanonicalName(),
				"auto.offset.reset", "earliest");
		Vertex source = dag.newVertex("source",
				KafkaProcessors.streamKafka(props, marketDataTopic));
		Vertex sink = dag.newVertex("sink",
				Sinks.writeMap(BusinessServiceCacheNames.PRICE_CACHE));
		dag.edge(between(source, sink));
		return instance.newJob(dag);
	}*/

	private Job createASimpleCacheJob(JetInstance instance){
		
		DAG dag = new DAG();
		
		Vertex source = dag.newVertex("source", Sources.readMap(BusinessServiceCacheNames.INSTRUMENT_CACHE));

		Vertex transform = dag.newVertex("transform", Processors.map((Entry<String, Instrument> e)
				->Util.entry(e.getKey(), e.getValue())));
		Vertex sink = dag.newVertex("sink", Sinks.writeMap("InstrumentMap"));
	
		dag.edge(between(source,transform));
		dag.edge(between(transform,sink));
		
		return instance.newJob(dag);
	}
	
	private static Properties props(String... kvs) {
		final Properties props = new Properties();
		for (int i = 0; i < kvs.length;) {
			props.setProperty(kvs[i++], kvs[i++]);
		}
		return props;
	}

}
