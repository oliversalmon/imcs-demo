package com.example.mu.pricequeryservice.controllers;

import com.example.mu.domain.Price;
import com.example.mu.pricequeryservice.repository.PriceRepository;

import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;


@Service
public class PriceQueryHandler {

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

	public static void main(String[] args) {
		SpringApplication.run(PriceQueryHandler.class, args);

	}

}
