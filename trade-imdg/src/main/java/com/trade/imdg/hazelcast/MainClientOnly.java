package com.trade.imdg.hazelcast;

import java.util.UUID;

import com.example.mu.domain.Instrument;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class MainClientOnly {
	
	public static void main(String args[]) throws Exception{
		System.out.println("Starting client...");
		HazelcastInstance hz = HazelcastClient.newHazelcastClient();
		
		while(true){
		
			Thread.sleep(2000);
			System.out.println("Hazelcast clien size is "+ hz.getMap(Main.MAP_INSTRUMENTS).size());
		}
		
		
		/*Instrument sample = new Instrument();
		sample.setAssetClass("FX");
		sample.setInstrumentId(UUID.randomUUID().toString());
		sample.setIssuer("Dinesh FX");
		sample.setProduct("PRODUCTFX");
		sample.setSymbol("FXX");
		
		hz.getMap(Main.MAP_INSTRUMENTS).put(sample.getInstrumentId(), sample);*/
	}
	

}
