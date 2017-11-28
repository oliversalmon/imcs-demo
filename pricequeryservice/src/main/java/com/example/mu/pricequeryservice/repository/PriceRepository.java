package com.example.mu.pricequeryservice.repository;

import static com.hazelcast.query.Predicates.equal;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
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
	private static ConcurrentLinkedQueue<Price> listOfPrices = new ConcurrentLinkedQueue<Price>();

	@Bean
	// @Profile("client")
	HazelcastInstance hazelcastInstance() {

		return HazelcastClient.newHazelcastClient();

	}

	@Autowired
	private HazelcastInstance hazelcastInstance;

	public Flux<Price> getAllPrices() {

		IMap<String, Price> priceMap = hazelcastInstance.getMap(PRICE_MAP);

		if (listOfPrices.size() < 1)
			priceMap.values().stream().forEach(a -> listOfPrices.add(a));

		return Flux.fromStream(listOfPrices.stream()
		// .sorted((Price p1, Price p2) ->
		// p1.getInstrumentId().compareTo(p2.getInstrumentId()))
		).delayElements(Duration.ofMillis(1000))
				.log();

	}

	@Scheduled(fixedDelay = 100000)
	private void updatePrices() {

		LOG.info("Updating Price Stream...; current price count is " + listOfPrices.size());

		IMap<String, Price> priceMap = hazelcastInstance.getMap(PRICE_MAP);
		priceMap.values().stream().sorted((Price p1, Price p2) -> p1.getInstrumentId().compareTo(p2.getInstrumentId()))
				.forEach(listOfPrices::add);

		if (listOfPrices.size() > priceMap.size() * 2) {
			//remove the first badge
			for (int i = 0; i < priceMap.size(); i++)
				listOfPrices.remove();
		}

		LOG.info("Done");
	}

	public Mono<Price> getPrice(String instrumentId) {
		IMap<String, Price> priceMap = hazelcastInstance.getMap(PRICE_MAP);
		Predicate priceFilter = equal("instrumentId", instrumentId);
		return Mono.justOrEmpty(priceMap.values(priceFilter).stream().findFirst().orElse(null));

	}
}
