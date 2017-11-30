package com.trade.injector.business.service;

import java.util.ArrayList;
import java.util.List;
import com.trade.injector.jto.Party;

public class GenerateRandomParty implements RandomDataService<Party> {

	public List<Party> createRandomData(int number) throws Exception {
		List<Party> partyList = new ArrayList<Party>();
		for(int i=0; i < number; i++){
			Party par = new Party();
			par.setPartyId("PAR"+i+"00000");
			par.setPartyName("CLI"+i);
			partyList.add(par);
		}
		
		return partyList;
	}
	
	
}
