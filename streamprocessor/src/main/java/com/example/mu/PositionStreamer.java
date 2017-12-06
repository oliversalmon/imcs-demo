package com.example.mu;

import static com.hazelcast.jet.Edge.between;
import static com.hazelcast.jet.impl.connector.ReadWithPartitionIteratorP.readMap;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.PositionAccount;
import com.example.mu.domain.Price;
import com.example.mu.domain.Trade;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.DAG;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.Vertex;
import com.hazelcast.jet.processor.DiagnosticProcessors;
import com.hazelcast.jet.processor.KafkaProcessors;
import com.hazelcast.jet.processor.Processors;
import com.hazelcast.jet.processor.Sinks;
import com.hazelcast.jet.stream.IStreamMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;

public class PositionStreamer {

	private final static String POSITION_ACCOUNT_MAP = "position-account";
	private final static String PRICE_MAP = "price";
	private static HazelcastInstance hzClient = HazelcastClient.newHazelcastClient();
	final static Logger LOG = LoggerFactory.getLogger(PriceStreamer.class);
	
	
	private static DAG getDAG(String url) throws Exception {
		DAG dag = new DAG();
		ClientConfig clientConfig = HzClientConfig.buildClientConfig(url);
		
		Vertex source = dag.newVertex("position-source",
				readMap(POSITION_ACCOUNT_MAP));
		
	
		Vertex positionProcessor = dag.newVertex("position-processor",
				DiagnosticProcessors.peekOutput(Processors.map((Entry<String, PositionAccount> f) -> f.getValue())));

		// for each price go through positions with the same instrument and update P&L
		Vertex positionUpdater = dag.newVertex("position-update",
				DiagnosticProcessors.peekOutput(Processors.map(PositionStreamer::updatePositionValue)));
		
		//Hack to work around Zookeeper and Curator not doing its job with ClientConfig
		
//		List<String> addresses = new ArrayList();
//		addresses.add("172.19.0.3");
//		addresses.add("172.19.0.4");
//		
//		HzClientConfig.getClientConfig().addAddress("172.19.0.3", "172.19.0.4");

		// sink to Position map
		Vertex sinkToPosition = dag
				.newVertex("sinkPosition", Sinks.writeMap(POSITION_ACCOUNT_MAP))
				.localParallelism(1);

		dag.edge(between(source, positionProcessor));

		dag.edge(between(positionProcessor, positionUpdater));

		dag.edge(between(positionUpdater, sinkToPosition));

		return dag;
	}

	private static SimpleEntry<String, PositionAccount> updatePositionValue(PositionAccount p) {

		IMap<String, Price> pxMap = hzClient.getMap(PRICE_MAP);
		Price spotPx = pxMap.get(p.getInstrumentid().trim());

		LOG.info("Position before update on Pnl " + p.getPnl());
		LOG.info("Price applied on Pnl " + spotPx.getPrice());

		p.setPnl(p.getSize() * spotPx.getPrice() - p.getPnl());

		LOG.info("Position after update on Pnl " + p.getPnl());
		return new AbstractMap.SimpleEntry<>(p.getAccountId(), p);

	}

	public static void connectAndUpdatePositions(String hzHost, String runs, String delay) throws Exception {
		
		JetInstance jetInstance = Jet.newJetInstance();
		Job job = jetInstance.newJob(getDAG(hzHost));
		
		
		
		int countdown = new Integer(runs);
		while(countdown !=0) {
			
			//load all positions into Jet cache
			IStreamMap<String, PositionAccount> sinkMap = jetInstance.getMap(POSITION_ACCOUNT_MAP);
			job.execute();
			countdown --;
			Thread.sleep(new Integer(delay));
		}
		
		jetInstance.shutdown();
		return;
	}

	public static void main(String[] args) throws Exception {
		PositionStreamer.connectAndUpdatePositions(System.getProperty("hzHost"), System.getProperty("runs"), System.getProperty("delay"));
	}

}
