package com.mu.flink.streamer;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;


import com.example.mu.domain.Trade;

public class TradeToTupleKeyTrade extends RichFlatMapFunction<Trade, Tuple2<String,Trade>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void flatMap(Trade arg0, Collector<Tuple2<String, Trade>> arg1) throws Exception {
		
		arg1.collect(new Tuple2<String, Trade>(arg0.getPositionAccountInstrumentKey(), arg0));
		
	}

}
