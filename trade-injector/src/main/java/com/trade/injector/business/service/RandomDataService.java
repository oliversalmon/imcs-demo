package com.trade.injector.business.service;

import java.util.List;

public interface RandomDataService<T> {
	
	public List<T> createRandomData(int number) throws Exception;

}
