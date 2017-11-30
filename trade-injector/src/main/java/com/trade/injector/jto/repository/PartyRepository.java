package com.trade.injector.jto.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.trade.injector.enums.PartyRole;
import com.trade.injector.jto.Party;

public interface PartyRepository extends MongoRepository<Party, String> {
	
	public Party findByPartyId(String partyId);
	
	

}
