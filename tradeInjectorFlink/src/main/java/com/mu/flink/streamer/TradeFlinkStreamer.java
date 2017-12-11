package com.mu.flink.streamer;

import org.apache.zookeeper.version.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;



public class TradeFlinkStreamer {
	
	final static Logger LOG = LoggerFactory.getLogger(TradeFlinkStreamer.class);
	
	
	public static void main(String[] args) throws Exception {
		
		ClassLoader classLoader = TradeFlinkStreamer.class.getClassLoader();
		LOG.info("Starting Trade Flink Streamer");
		System.out.println("Zookeeper version used is "+Info.MAJOR+"."+Info.MINOR+"."+Info.MICRO);
	    //
		
		Class aClass = classLoader.loadClass("com.hazelcast.zookeeper.ZookeeperDiscoveryStrategy");
        //System.out.println("aClass.getName() = " + aClass.getName());
        HazelcastInstance hzClient = HazelcastClient.newHazelcastClient();
		LOG.info("Completed Trade Flink Streamer");
		//hzClient.shutdown();
		
	}

}
