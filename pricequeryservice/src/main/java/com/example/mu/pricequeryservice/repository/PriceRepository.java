package com.example.mu.pricequeryservice.repository;

import static com.hazelcast.query.Predicates.equal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import com.example.mu.domain.Price;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class PriceRepository {

	private final static String PRICE_MAP = "price";

	@Bean
	// @Profile("client")
	HazelcastInstance hazelcastInstance() {

		return HazelcastClient.newHazelcastClient();

	}

	@Autowired
	private HazelcastInstance hazelcastInstance;

	public Flux<Price> getAllPrices() {

		IMap<String, Price> priceMap = hazelcastInstance.getMap(PRICE_MAP);

		return Flux.fromStream(priceMap.values().stream());

	}

	public Mono<Price> getPrice(String instrumentId) {
		IMap<String, Price> priceMap = hazelcastInstance.getMap(PRICE_MAP);
		Predicate priceFilter = equal("instrumentId", instrumentId);
		return Mono.justOrEmpty(priceMap.values(priceFilter).stream().findFirst().orElse(null));

	}
}
