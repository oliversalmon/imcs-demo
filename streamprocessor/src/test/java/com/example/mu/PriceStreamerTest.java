package com.example.mu;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Table;

import com.example.mu.cachemapstore.PriceMapStore;
import com.example.mu.domain.Price;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.stream.IStreamMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PriceStreamerTest extends TestCase {
	
	Configuration config;
	Admin admin;
	Table table;

	public static Test suite() {
		return new TestSuite(PriceStreamerTest.class);
	}

	public PriceStreamerTest(String testName) {
		super(testName);
	}
	
	public void testStoreOfPrice() throws Exception{
		Price aPrice = new Price();
		
		aPrice.setInstrumentId("11111");
		aPrice.setPrice(11.00);
		aPrice.setPriceId("22222");
		aPrice.setTimeStamp(System.currentTimeMillis());
		
		//save this to the map 

		JetInstance jet = Jet.newJetInstance();
		try {
			
			IStreamMap<String, Price> sinkMap = jet.getMap("price");
			sinkMap.put(aPrice.getPriceId(), aPrice);
			Thread.sleep(60000);
			assertEquals(sinkMap.size(),1);
			
		}finally {
			jet.shutdown();
		}
		
		
	}
	
	public void testStoreOfPriceInHbase() throws Exception{
		Price aPrice = new Price();
		
		aPrice.setInstrumentId("11111");
		aPrice.setPrice(11.00);
		aPrice.setPriceId("22222");
		aPrice.setTimeStamp(System.currentTimeMillis());
		
		JetInstance jet = Jet.newJetInstance();

		
		try {
			
			PriceMapStore pms = new PriceMapStore();
			pms.store(aPrice.getPriceId(), aPrice);
			//Thread.sleep(60000);
			//load into map
			IStreamMap<String, Price> sinkMap = jet.getMap("price");
			
			sinkMap.loadAll(true);
			//pms.load(aPrice.getPriceId());
			assertEquals(1, sinkMap.size());
			
			//finally delete the entry
			pms.delete(aPrice.getPriceId());
			
			
		}finally {
			jet.shutdown();
		}
		
		
	}

}
