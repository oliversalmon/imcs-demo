package com.trade.injector.business.service;

import java.util.Map;

public interface RandomCacheDataService<K,V> {
	
	public void populateMap(int number, Map<K,V> cache);

}
