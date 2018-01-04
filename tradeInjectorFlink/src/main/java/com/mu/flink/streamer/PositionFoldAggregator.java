package com.mu.flink.streamer;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.PositionAccount;
import com.example.mu.domain.Trade;

public class PositionFoldAggregator implements AggregateFunction<Tuple2<String,Trade>, PositionAccount, PositionAccount> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static Logger LOG = LoggerFactory.getLogger(PositionFoldAggregator.class);

	public PositionAccount createAccumulator() {
		
		return new PositionAccount();
	}

	public void add(Tuple2<String, Trade> value, PositionAccount accumulator) {
		accumulator.setAccountId(value.f1.getPositionAccountId());
		accumulator.setInstrumentid(value.f1.getInstrumentId());
		accumulator.setSize(accumulator.getSize()+value.f1.getQuantity());
		
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
