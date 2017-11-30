package com.trade.injector.jto;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

public class TradeInjectorMessage {

	@Id
	public String id;

	@Indexed
	private String userId;

	private String loginType;

	private String noOfTrades;
	private String noOfClients;
	private String noOfInstruments;
	private String timeDelay;
	private String tradeDate;
	private String currenMessageCount;
	private InjectorProfile profileUsed;
	private int run_mode=0; //0=running, 1=completed, 2=suspended
	private String injectDate;
	
	public String getNoOfTrades() {
		return noOfTrades;
	}

	public void setNoOfTrades(String noOfTrades) {
		this.noOfTrades = noOfTrades;
	}

	public String getNoOfClients() {
		return noOfClients;
	}

	public void setNoOfClients(String noOfClients) {
		this.noOfClients = noOfClients;
	}

	public String getNoOfInstruments() {
		return noOfInstruments;
	}

	public void setNoOfInstruments(String noOfInstruments) {
		this.noOfInstruments = noOfInstruments;
	}

	public String getTimeDelay() {
		return timeDelay;
	}

	public void setTimeDelay(String timeDelay) {
		this.timeDelay = timeDelay;
	}

	public String getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public InjectorProfile getProfileUsed() {
		return profileUsed;
	}

	public void setProfileUsed(InjectorProfile profileUsed) {
		this.profileUsed = profileUsed;
	}

	@Override
	public String toString() {
		return String
				.format("TradeInjector[id=%s, userId='%s', noOfTrades='%s', noOfClients='%s', noOfInstruments='%s']",
						id, userId, noOfTrades, noOfClients, noOfInstruments);
	}

	public String getCurrenMessageCount() {
		return currenMessageCount;
	}

	public void setCurrenMessageCount(String currenMessageCount) {
		this.currenMessageCount = currenMessageCount;
	}

	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	public int getRun_mode() {
		return run_mode;
	}

	public void setRun_mode(int run_mode) {
		this.run_mode = run_mode;
	}

	public String getInjectDate() {
		return injectDate;
	}

	public void setInjectDate(String injectDate) {
		this.injectDate = injectDate;
	}

}
