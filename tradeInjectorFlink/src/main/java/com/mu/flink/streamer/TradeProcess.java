package com.mu.flink.streamer;

import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.example.mu.domain.Trade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TradeProcess extends ProcessFunction<String, Trade> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static Logger LOG = LoggerFactory.getLogger(TradeProcess.class);
	private static Gson gson = new GsonBuilder().create();
	final OutputTag<Trade> outputTag = new OutputTag<Trade>("position-stream") {
	};

	@Override
	public void processElement(String value, ProcessFunction<String, Trade>.Context ctx, Collector<Trade> out)
			throws Exception {
		Trade p = gson.fromJson(value, Trade.class);
		out.collect(p);
		ctx.output(outputTag, p);
		
	}
	
	


	

	

}
