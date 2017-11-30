package com.trade.injector.jto;

public class InstrumentReport {

	private String id;
	private String name;
	private int currentTradeCount;
	private double price, prevPrice;

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

	public InstrumentReport incrementCountByOne() {
		this.currentTradeCount++;
		return this;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPrevPrice() {
		return prevPrice;
	}

	public void setPrevPrice(double prevPrice) {
		this.prevPrice = prevPrice;
	}

}
