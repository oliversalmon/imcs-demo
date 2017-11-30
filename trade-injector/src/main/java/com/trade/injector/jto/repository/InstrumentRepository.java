package com.trade.injector.jto.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.trade.injector.jto.Instrument;

public interface InstrumentRepository extends MongoRepository<Instrument, String> {

	public Instrument findByIdentifier(String identifier);
}
