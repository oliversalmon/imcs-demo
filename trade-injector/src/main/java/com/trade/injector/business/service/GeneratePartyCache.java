package com.trade.injector.business.service;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.mu.domain.Party;
import com.trade.injector.enums.PartyRole;

public class GeneratePartyCache implements RandomCacheDataService<String, Party> {
	
	final Logger LOG = LoggerFactory.getLogger(GeneratePartyCache.class);

	@Override
	public void populateMap(int number, Map<String, Party> cache) {
		
		
		LOG.info("Current Party cache size is "+cache.size());
		
		int membersToGenerate = number-cache.size();
		if(membersToGenerate < 0)
			//we have enough members in the cache
			return;
		
		int seedNumber = cache.size();
		for(int i=0; i<membersToGenerate; i++){
			
			int partyNumber = seedNumber++;
			String partyId = UUID.randomUUID().toString();
			Party aParty = new Party();
			aParty.setName("TRDINJECT_CLI_"+partyNumber);
			aParty.setPartyId(partyId);
			aParty.setPositionAccountId("TRDINJECT_ACC_"+partyNumber);
			aParty.setRole(new Integer(PartyRole.POSITIONACCOUNTOWNER.getPartyRole()).toString());
			aParty.setShortName("CLI_"+partyNumber);
			
			cache.put(partyId, aParty);
		}
		
		
	}

}
