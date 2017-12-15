package com.mu.flink.streamer;

import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.Trade;
import com.hazelcast.core.IMap;

public class HzTradeSink extends RichSinkFunction<Trade> {
	
	 final static Logger LOG = LoggerFactory.getLogger(HzTradeSink.class);
	private static final String TRADEMAP = "trade";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void invoke(Trade arg0) throws Exception {
		
		LOG.info("adding the following trade to the map "+arg0.getTradeId());
		IMap<String, Trade> tradeMap = TradeFlinkStreamer.getHzClient().getMap(TRADEMAP);
		tradeMap.put(arg0.getTradeId(), arg0);
		LOG.info("successfully added to TradeMap");
		
	}

}
