package com.trade.injector.dto;

import java.util.Date;

public class Trade {

	private String tradeId;
	private Date tradeDate;
	private String clientName;
	private String instrumentId;
	private String side;
	private int tradeQty;
	private double tradePx;

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(String instrumentId) {
		this.instrumentId = instrumentId;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	public int getTradeQty() {
		return tradeQty;
	}

	public void setTradeQty(int tradeQty) {
		this.tradeQty = tradeQty;
	}

	public double getTradePx() {
		return tradePx;
	}

	public void setTradePx(double tradePx) {
		this.tradePx = tradePx;
	}

	@Override
	public String toString() {

		return "Client " + this.clientName + " Insttrument Id "
				+ this.instrumentId + " Side " + this.side + " Trade id "
				+ this.tradeId + " Trade Px " + this.tradePx + " Trade Qty "
				+ this.tradeQty+" Trade Dt "+this.tradeDate.toString();

	}

}
