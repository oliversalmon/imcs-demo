package com.mu.flink.streamer;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.PositionAccount;
import com.example.mu.domain.Price;
import com.example.mu.domain.Trade;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class PositionFoldAggregator implements AggregateFunction<Tuple2<String,Trade>, PositionAccount, PositionAccount> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static Logger LOG = LoggerFactory.getLogger(PositionFoldAggregator.class);
	private static HazelcastInstance hzClient = TradeFlinkStreamer.getHzClient();

	public PositionAccount createAccumulator() {
		
		return new PositionAccount();
	}

	public void add(Tuple2<String, Trade> value, PositionAccount accumulator) {
		accumulator.setAccountId(value.f1.getPositionAccountId());
		accumulator.setInstrumentid(value.f1.getInstrumentId());
		
		//set size
		accumulator.setSize(accumulator.getSize()+value.f1.getQuantity());
		
		//pnl 
		IMap<String, Price> mapPrice = hzClient.getMap("price");
		Price spotPx = mapPrice.get(value.f1.getInstrumentId());
		if (spotPx == null)
			LOG.warn("NO Spot PX available for the following instrument id, not calculating PnL " + value.f1.getInstrumentId());
		else {
			//LOG.debug("Spot px used =" + spotPx.getPrice());
			//LOG.debug("traded value is " + value.f1.getTradeValue());
			double pnl = spotPx.getPrice() * value.f1.getQuantity() - value.f1.getTradeValue();
		
			//LOG.debug("Pnl calculated is " + pnl);
			double currentPnl = accumulator.getPnl();
			accumulator.setPnl( currentPnl+= pnl);
		}
		
	}

	public PositionAccount getResult(PositionAccount accumulator) {
		LOG.debug("returning the following accumulator "+accumulator.getAccountId());
		return accumulator;
	}

	public PositionAccount merge(PositionAccount a, PositionAccount b) {
		a.setSize(a.getSize()+b.getSize());
		
		return a;
	}
	
	
	
	

}
