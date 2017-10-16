package com.example.mu.pricequeryservice.controllers;

import com.example.mu.domain.Price;
import com.example.mu.pricequeryservice.repository.PriceRepository;
import com.hazelcast.core.IMap;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;



@RestController
public class PriceQueryHandler {

	final Logger LOG = LoggerFactory.getLogger(PriceQueryHandler.class);
	@Autowired
	private PriceRepository priceRepo;

	public Mono<ServerResponse> getAllPrices(ServerRequest request) {

		return ServerResponse.ok().contentType(APPLICATION_JSON).body(priceRepo.getAllPrices(), Price.class);
	}

	public Mono<ServerResponse> getPrice(ServerRequest request) {

		return priceRepo.getPrice(request.pathVariable("id"))
				.flatMap(price -> ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(price), Price.class))
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value="pricequeryservice/pricestream")
	public Flux<Price> getFluxPrices(){
		
		LOG.info("In pricestream...");
	
		Flux<Price> prices =  priceRepo.getAllPrices();
		Flux<Long> duration = Flux.interval(Duration.ofSeconds(1));
		
		LOG.info("returning flux prices...");
			return Flux.zip(prices, duration).map(Tuple2::getT1);
		
	}

	public static void main(String[] args) {
		SpringApplication.run(PriceQueryHandler.class, args);

	}

}
