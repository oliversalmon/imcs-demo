package com.mu.flink.streamer;

import org.apache.flink.api.java.functions.KeySelector;
	
import com.example.mu.domain.Trade;

public class TradeKeyForPositionAccount implements KeySelector<Trade, String>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getKey(Trade arg0) throws Exception {
		// TODO Auto-generated method stub
		return arg0.getPositionAccountInstrumentKey();
	}

}
