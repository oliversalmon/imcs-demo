package com.trade.injector.jto;

public class PartyReport {

	private String id;
	private String name;
	private int currentTradeCount;
	private int previousTradeCount;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCurrentTradeCount() {
		return currentTradeCount;
	}
	public void setCurrentTradeCount(int currentTradeCount) {
		this.currentTradeCount = currentTradeCount;
	}
	
	public PartyReport incrementCountByOne(){
		
		//first set previous to what it was before incrementing
		this.setPreviousTradeCount(currentTradeCount);
		this.currentTradeCount++;
		
		return this;
	}
	public int getPreviousTradeCount() {
		return previousTradeCount;
	}
	public void setPreviousTradeCount(int previousTradeCount) {
		this.previousTradeCount = previousTradeCount;
	}

}
