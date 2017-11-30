package com.trade.injector.sinks;

public interface ISink {
	
	public void writeTo(String url, String key, String message) throws Exception;

}
