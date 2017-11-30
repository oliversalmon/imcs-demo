package com.trade.injector.enums;

public enum TradeInjectRunModes {
	
	RUNNING(0), SUSPENDED(1), COMPLETED(2), STOP(3);
	
	private int runMode;
	
	private TradeInjectRunModes(int p_runMode){
		runMode=p_runMode;
	}
	
	public int getRunMode(){
		return runMode;	
		
		
	}

}
