package com.mu.flink.batch;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.PositionAccount;
import com.example.mu.domain.Price;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;


public class PositionUpdate implements FlatMapFunction<PositionAccount, PositionAccount> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static Logger LOG = LoggerFactory.getLogger(PositionUpdate.class);
	private static HazelcastInstance hzClient = PositionSnap.getHzClient();
	private static final String PRICEMAP = "price";

	@Override
	public void flatMap(PositionAccount value, Collector<PositionAccount> out) throws Exception {
		LOG.info("Starting to update Positions...");
		
		String instrumentId = value.getInstrumentid();
		
		IMap<String, Price> mapPrice = hzClient.getMap(PRICEMAP);
		Price spotPx = mapPrice.get(instrumentId);
		if (spotPx == null)
			LOG.warn("NO Spot PX available for the following instrument id, not updating PnL " + instrumentId);
		else {
			LOG.debug("Spot px used =" + spotPx.getPrice());
		
			double pnl = spotPx.getPrice() * value.getSize() - value.getPnl();
		
			LOG.debug("Pnl calculated is " + pnl);
			
			value.setPnl(pnl);
			out.collect(value);
		}

		
	}




}
