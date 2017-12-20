package com.mu.flink.streamer;

import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.PositionAccount;
import com.example.mu.domain.Trade;

public class TradeProcess extends ProcessFunction<Trade, PositionAccount> {
	
	final static Logger LOG = LoggerFactory.getLogger(TradeProcess.class);

	@Override
	public void processElement(Trade arg0, ProcessFunction<Trade, PositionAccount>.Context arg1,
			Collector<PositionAccount> arg2) throws Exception {
		
		LOG.info("Starting to process trades into positions...");
		
		//get the position account from Hz
		
		//update the position account qty with the trade value through the RichFlatMapFunction based on Value State
		
		//get the price from Hz
		
		//update the position value with the curren
		
		LOG.info("Done.");
		
		
	}

	

}
