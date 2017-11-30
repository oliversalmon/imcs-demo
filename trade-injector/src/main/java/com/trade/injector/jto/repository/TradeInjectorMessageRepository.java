package com.trade.injector.jto.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.trade.injector.jto.TradeInjectorMessage;

@Repository
public interface TradeInjectorMessageRepository extends MongoRepository<TradeInjectorMessage, String> {
	
	public TradeInjectorMessage findByUserId(String userId);
	
	
}
