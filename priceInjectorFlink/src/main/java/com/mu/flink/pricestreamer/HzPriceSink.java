package com.mu.flink.pricestreamer;

import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.Price;
import com.hazelcast.core.IMap;



public class HzPriceSink extends RichSinkFunction<Price> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static Logger LOG = LoggerFactory.getLogger(HzPriceSink.class);
	private static final String PRICEMAP = "price";

	@Override
	public void invoke(Price arg0) throws Exception {
		LOG.info("adding the following price to the map "+arg0.getInstrumentId());
		IMap<String, Price> tradeMap = PriceFlinkStreamer.getHzClient().getMap(PRICEMAP);
		tradeMap.put(arg0.getInstrumentId(), arg0);
		LOG.info("successfully added to Price map");
		
	}

}
