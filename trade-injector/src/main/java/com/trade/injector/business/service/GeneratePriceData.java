package com.trade.injector.business.service;

import java.util.UUID;



import com.example.mu.domain.Instrument;
import com.example.mu.domain.Price;


public class GeneratePriceData {

	public static Price generateRandomDataOnInstruments(Instrument ins)
			 {

		Price price = new Price();
		price.setInstrumentId(ins.getSymbol());
		price.setPrice((double)Math.random() * 1000 + 1);
		price.setPriceId(UUID.randomUUID().toString());
		price.setTimeStamp(System.currentTimeMillis());

		return price;

	}
}
