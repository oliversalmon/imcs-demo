package com.example.mu.pricequeryservice.controllers;

import com.example.mu.domain.Price;
import com.example.mu.pricequeryservice.repository.PriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
//import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
//import org.springframework.cloud.client.serviceregistry.Registration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.env.Environment;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.reactive.config.EnableWebFlux;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestController
//@EnableWebFlux

public class PriceQueryHandler {


	final Logger LOG = LoggerFactory.getLogger(PriceQueryHandler.class);
	@Autowired
	private PriceRepository priceRepo;
	


	@RequestMapping("/hi")
	public String hi() {
		return "Price service says a hi " ;
	}
	



//	public Mono<ServerResponse> getAllPrices(ServerRequest request) {
//
//		return ServerResponse.ok().contentType(APPLICATION_JSON).body(priceRepo.getAllPrices(), Price.class);
//	}
//
//	@RequestMapping(value = "/getAllPrices", method = RequestMethod.GET)
//	public ResponseEntity<List<Price>> getAllPrices() {
//
//		return ResponseEntity.ok(priceRepo.getAllPxs());
//
//	}
//
//	public Mono<ServerResponse> getPrice(ServerRequest request) {
//
//		return priceRepo.getPrice(request.pathVariable("id"))
//				.flatMap(price -> ServerResponse.ok().contentType(APPLICATION_JSON).body(Mono.just(price), Price.class))
//				.switchIfEmpty(ServerResponse.notFound().build());
//	}
//
//	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE, value = "pricequeryservice/pricestream")
//	public Flux<Price> getFluxPrices() {
//
//		LOG.info("In pricestream...");
//
//		Flux<Price> prices = priceRepo.getAllPrices();
//
//		LOG.info("returning flux prices...");
//
//		return prices;
//
//	}
	

	public static void main(String[] args) {
		SpringApplication.run(PriceQueryHandler.class, args);

	}

}
