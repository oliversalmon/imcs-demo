package com.trade.injector.jto;

public class TradeAcknowledge {
	
	private String injectIdentifier;
	private String profileIdentifier;
	private String clientName;
	private String tradeDate;
	private String instrumentId;
	private String side;
	private String tradeQty;
	private String tradePx;
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getTradeDate() {
		return tradeDate;
	}
	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
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
	public String getTradeQty() {
		return tradeQty;
	}
	public void setTradeQty(String tradeQty) {
		this.tradeQty = tradeQty;
	}
	public String getTradePx() {
		return tradePx;
	}
	public void setTradePx(String tradePx) {
		this.tradePx = tradePx;
	}
	public String getInjectIdentifier() {
		return injectIdentifier;
	}
	public void setInjectIdentifier(String injectIdentifier) {
		this.injectIdentifier = injectIdentifier;
	}
	public String getProfileIdentifier() {
		return profileIdentifier;
	}
	public void setProfileIdentifier(String profileIdentifier) {
		this.profileIdentifier = profileIdentifier;
	}
	
	

}
