package com.mu.flink.streamer;


import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

import com.example.mu.domain.Trade;

public class TradeTimeStampWaterMarkAssigner extends BoundedOutOfOrdernessTimestampExtractor<Trade> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TradeTimeStampWaterMarkAssigner(Time maxOutOfOrderness) {
		super(maxOutOfOrderness);
		
	}

	@Override
	public long extractTimestamp(Trade element) {
	
		return element.getTradeDate().getTime();
		
	}

}
