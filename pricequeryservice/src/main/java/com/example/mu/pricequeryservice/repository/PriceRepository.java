package com.example.mu.pricequeryservice.repository;

import static com.hazelcast.query.Predicates.equal;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import com.example.mu.domain.Price;
import com.example.mu.pricequeryservice.controllers.PriceQueryHandler;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Repository

public class PriceRepository {

	final Logger LOG = LoggerFactory.getLogger(PriceRepository.class);
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

	public Flux<Price> getIntermittentPrices() {

		LOG.info("In intermittent prices...");
		IMap<String, Price> priceMap = hazelcastInstance.getMap(PRICE_MAP);

		Flux<Price> prices =  Flux.fromStream(priceMap.values().stream());
		Flux<Long> duration = Flux.interval(Duration.ofSeconds(1));
			return Flux.zip(prices, duration).map(Tuple2::getT1);

	}
	
	public Mono<Price> getPrice(String instrumentId) {
		IMap<String, Price> priceMap = hazelcastInstance.getMap(PRICE_MAP);
		Predicate priceFilter = equal("instrumentId", instrumentId);
		return Mono.justOrEmpty(priceMap.values(priceFilter).stream().findFirst().orElse(null));

	}
}
