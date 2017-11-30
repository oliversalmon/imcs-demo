package com.trade.injector.jto.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.trade.injector.jto.TradeInjectorProfile;

public interface TradeInjectorProfileRepository extends MongoRepository<TradeInjectorProfile, String> {
	public TradeInjectorProfile findByName(String name);
}
