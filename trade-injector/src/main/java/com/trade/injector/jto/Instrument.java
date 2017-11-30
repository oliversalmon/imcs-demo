package com.trade.injector.jto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import com.trade.injector.enums.ExerciseStyle;
import com.trade.injector.enums.PutCall;
import com.trade.injector.enums.SecurityType;
import com.trade.injector.enums.SettlementMethod;

public class Instrument {

	@Id
	public String id;
	
	@Indexed
	private String identifier;
	private SecurityType securityType;
	private PutCall putCall;
	private String MMY;
	private String maturityDate;
	private String strikePx;
	private SettlementMethod settMethod;
	private ExerciseStyle exerciseStyle;
	private int apportionment;
	private String exchange_id;

	public String getIdenfitifier() {
		return identifier;
	}

	public void setIdenfitifier(String idenfitifier) {
		this.identifier = idenfitifier;
	}

	public SecurityType getSecurityType() {
		return securityType;
	}

	public void setSecurityType(SecurityType securityType) {
		this.securityType = securityType;
	}

	public PutCall getPutCall() {
		return putCall;
	}

	public void setPutCall(PutCall putCall) {
		this.putCall = putCall;
	}

	public String getMMY() {
		return MMY;
	}

	public void setMMY(String mMY) {
		MMY = mMY;
	}

	public String getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getStrikePx() {
		return strikePx;
	}

	public void setStrikePx(String strikePx) {
		this.strikePx = strikePx;
	}

	public String getExchange() {
		return exchange_id;
	}

	public void setExchange(String exchange) {
		this.exchange_id = exchange;
	}

	public SettlementMethod getSettMethod() {
		return settMethod;
	}

	public void setSettMethod(SettlementMethod settMethod) {
		this.settMethod = settMethod;
	}

	public ExerciseStyle getExerciseStyle() {
		return exerciseStyle;
	}

	public void setExerciseStyle(ExerciseStyle exerciseStyle) {
		this.exerciseStyle = exerciseStyle;
	}

	public int getApportionment() {
		return apportionment;
	}

	public void setApportionment(int apportionment) {
		this.apportionment = apportionment;
	}

	
}
