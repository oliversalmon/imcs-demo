package com.trade.injector.enums;

public enum PartyRole {

	CLIENTID(1), EXECUTINGFIRM(2), EXCHANGE(3), POSITIONACCOUNTOWNER(4);

	private int partyRole;

	private PartyRole(int p_partyRole) {
		partyRole = p_partyRole;
	}

	public int getPartyRole() {
		return partyRole;

	}

}
