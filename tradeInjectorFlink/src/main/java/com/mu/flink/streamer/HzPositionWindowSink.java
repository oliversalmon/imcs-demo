package com.mu.flink.streamer;

import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.PositionAccount;
import com.hazelcast.core.IMap;

public class HzPositionWindowSink extends RichSinkFunction<PositionAccount> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String POSITIONACCOUNTMAP = "position-account";
	final static Logger LOG = LoggerFactory.getLogger(HzPositionWindowSink.class);

	@Override
	public void invoke(PositionAccount value) throws Exception {
		IMap<String, PositionAccount> posMap = TradeFlinkStreamer.getHzClient().getMap(POSITIONACCOUNTMAP);
		String posKey = value.getAccountId()+value.getInstrumentid();
		posMap.put(posKey, value);
		LOG.info("Placed the followoing position in the map "+value.getAccountId());
		
	}

}
