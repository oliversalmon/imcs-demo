package com.trade.injector.business.service;

import java.util.ArrayList;
import java.util.List;

import com.trade.injector.jto.Instrument;

public class GenerateRandomInstruments implements RandomDataService<Instrument> {

	public List<Instrument> createRandomData(int number) throws Exception {
		
		/**
		 * Generate random instrument number of instruments for a specified number
		 */
		List<Instrument> returnList = new ArrayList<Instrument>();
		
		for(int i=0; i < number; i++){
			Instrument ins = new Instrument();
			ins.setIdenfitifier("ISIN"+i+"000");
			returnList.add(ins);
		}
		
		return returnList;
	}

}
