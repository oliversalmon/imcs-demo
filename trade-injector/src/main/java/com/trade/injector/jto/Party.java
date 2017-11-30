package com.trade.injector.jto;



import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import com.trade.injector.enums.PartyRole;

public class Party {

	@Id
	public String id;
	
	@Indexed
	private String partyId;
	private String partyName;
	private String accountNumber;
	private String subAccount;
	private PartyRole role;
	private String immediateParent_id;
	private int apportionment;
	private String rootParent_id;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getPartyName() {
		return partyName;
	}

	public void setPartyName(String partyName) {
		this.partyName = partyName;
	}

	public PartyRole getRole() {
		return role;
	}

	public void setRole(PartyRole role) {
		this.role = role;
	}

	public String getImmediateParent() {
		return immediateParent_id;
	}

	public void setImmediateParent(String immediateParent) {
		this.immediateParent_id = immediateParent;
	}

	public String getRootParent() {
		return rootParent_id;
	}

	public void setRootParent(String rootParent) {
		this.rootParent_id = rootParent;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getSubAccount() {
		return subAccount;
	}

	public void setSubAccount(String subAccount) {
		this.subAccount = subAccount;
	}

	public int getApportionment() {
		return apportionment;
	}

	public void setApportionment(int apportionment) {
		this.apportionment = apportionment;
	}

	

}
