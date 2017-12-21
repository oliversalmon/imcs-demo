package com.mu.flink.streamer;

import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.PositionAccount;

import com.hazelcast.core.IMap;


public class HzPositionSink extends RichSinkFunction<PositionAccount>{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static Logger LOG = LoggerFactory.getLogger(HzPositionSink.class);
	private static final String POSITIONACCOUNTMAP = "postion-account";

	@Override
	public void invoke(PositionAccount arg0) throws Exception {
		
		IMap<String, PositionAccount> posMap = TradeFlinkStreamer.getHzClient().getMap(POSITIONACCOUNTMAP);
		String posKey = arg0.getAccountId()+arg0.getInstrumentid();
		//update the quantity and value before placing it back
		PositionAccount updateAccount = posMap.get(posKey);
		if(updateAccount ==null) {
			LOG.info("Creating a new Position Account in the map");
			posMap.put(posKey, arg0);
		}else {
			LOG.info("updating the position account in the map");
			updateAccount.setSize(updateAccount.getSize()+arg0.getSize());
			updateAccount.setPnl(updateAccount.getPnl()+arg0.getPnl());
			LOG.info("updated Pnl is "+updateAccount.getPnl());
		}
		
	}

}
