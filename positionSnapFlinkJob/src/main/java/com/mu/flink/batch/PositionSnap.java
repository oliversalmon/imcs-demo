package com.mu.flink.batch;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.PositionAccount;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;


public class PositionSnap {
	
	final static Logger LOG = LoggerFactory.getLogger(PositionSnap.class);
	private static  HazelcastInstance hzClient = HazelcastClient.newHazelcastClient();
	private static final String POSITIONACCOUNTMAP = "position-account";
	
	public void snapPositionPnL() throws Exception{
		
		LOG.info("starting up Position Snaps");
		
		IMap<String, PositionAccount> posMap = hzClient.getMap(POSITIONACCOUNTMAP);
		
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		
		env.setParallelism(2);
		DataSource<PositionAccount> data = env.fromCollection(posMap.values());
		DataSet<PositionAccount> transformedData = data.flatMap(new PositionUpdate());

		transformedData.collect().forEach(a -> posMap.put(a.getAccountId() + a.getInstrumentid(), a));
		
		env.execute();
		
		LOG.info("Done Position Snaps");
		
		
	}
	
	public static HazelcastInstance getHzClient() {
		return hzClient;
	}

	public static void main(String[] args) throws Exception{
	
		new PositionSnap().snapPositionPnL();

	}

}
