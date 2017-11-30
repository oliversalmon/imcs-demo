package com.trade.injector.jto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class TradeReport {
	
	@Id
	private String id;
	
	private String name;
	
	private String injectorProfileId;
	private String injectorMessageId;
	private String userId;
	private Date reportDate;
	private int tradeCount;
	private int currentTradeProgress;
	
	private List<PartyReport> parties = new ArrayList<PartyReport>();
	private List<InstrumentReport> instruments = new ArrayList<InstrumentReport>();
	public String getInjectorProfileId() {
		return injectorProfileId;
	}
	public void setInjectorProfileId(String injectorProfileId) {
		this.injectorProfileId = injectorProfileId;
	}
	public String getInjectorMessageId() {
		return injectorMessageId;
	}
	public void setInjectorMessageId(String injectorMessageId) {
		this.injectorMessageId = injectorMessageId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Date getReportDate() {
		return reportDate;
	}
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}
	public int getTradeCount() {
		return tradeCount;
	}
	public void setTradeCount(int tradeCount) {
		this.tradeCount = tradeCount;
	}
	public int getCurrentTradeProgress() {
		return currentTradeProgress;
	}
	public void setCurrentTradeProgress(int currentTradeProgress) {
		this.currentTradeProgress = currentTradeProgress;
	}
	public List<PartyReport> getParties() {
		return parties;
	}
	public void setParties(List<PartyReport> parties) {
		this.parties = parties;
	}
	public List<InstrumentReport> getInstruments() {
		return instruments;
	}
	public void setInstruments(List<InstrumentReport> instruments) {
		this.instruments = instruments;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
