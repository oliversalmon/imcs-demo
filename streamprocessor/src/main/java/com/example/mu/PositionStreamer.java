package com.example.mu;

import static com.hazelcast.jet.Edge.between;
import static com.hazelcast.jet.impl.connector.ReadWithPartitionIteratorP.readMap;

import java.util.AbstractMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.PositionAccount;
import com.example.mu.domain.Price;
import com.example.mu.domain.Trade;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.DAG;
import com.hazelcast.jet.Vertex;
import com.hazelcast.jet.processor.KafkaProcessors;
import com.hazelcast.jet.processor.Processors;
import com.hazelcast.jet.processor.Sinks;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;

public class PositionStreamer {

	private final static String POSITION_ACCOUNT_MAP = "position-account";
	private final static String PRICE_MAP = "price";
	private static HazelcastInstance hzClient = HazelcastClient.newHazelcastClient();
	final static Logger LOG = LoggerFactory.getLogger(PriceStreamer.class);

	private static DAG getDAG(String url) throws Exception {
		DAG dag = new DAG();

		Vertex source = dag.newVertex("position-source",
				readMap(POSITION_ACCOUNT_MAP, HzClientConfig.getClientConfig()));
		
		Vertex positionProcessor = dag.newVertex("position-processor",
				Processors.map((Entry<String, PositionAccount> f) -> f.getValue()));

		// for each price go through positions with the same instrument and update P&L
		Vertex positionUpdater = dag.newVertex("position-update",Processors.map(PositionStreamer::updatePositionValue));

		// sink to Position map
		Vertex sinkToPosition = dag
				.newVertex("sinkPosition", Sinks.writeMap(POSITION_ACCOUNT_MAP, HzClientConfig.getClientConfig()))
				.localParallelism(1);

		return dag;
	}

	private static PositionAccount updatePositionValue(PositionAccount p) {

		IMap<String, Price> pxMap = hzClient.getMap(PRICE_MAP);
		Price spotPx = pxMap.get(p.getInstrumentid().trim());

		LOG.info("Position before update on Pnl " + p.getPnl());
		LOG.info("Price applied on Pnl " + spotPx.getPrice());

		p.setPnl(p.getSize() * spotPx.getPrice() - p.getPnl());

		LOG.info("Position after update on Pnl " + p.getPnl());
		return p;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
