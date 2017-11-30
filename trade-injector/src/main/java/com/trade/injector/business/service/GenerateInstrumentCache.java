package com.trade.injector.business.service;

import java.util.Map;
import java.util.UUID;

import com.example.mu.domain.*;

public class GenerateInstrumentCache implements RandomCacheDataService<String, Instrument> {

	@Override
	public void populateMap(int number, Map<String, Instrument> cache) {
		int instrumentsToGenerate = number-cache.size();
		if(instrumentsToGenerate < 0)
			//we have enough members in the cache
			return;
		
		int seedNumber = cache.size();
		for(int i=0; i<instrumentsToGenerate; i++){
			
			int numberIdentifier = seedNumber++;
			String key = UUID.randomUUID().toString();
			Instrument aIns = new Instrument();
			aIns.setAssetClass("COMMODITY");
			aIns.setInstrumentId(key);
			aIns.setIssuer("Exchange");
			aIns.setProduct("PROD_INJ_"+numberIdentifier);
			aIns.setSymbol("ISIN_INJ_"+numberIdentifier);
			
			cache.put(key, aIns);
			
		}
		
	}

}
