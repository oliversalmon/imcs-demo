package com.example.mu.pricequeryservice.router;

import org.springframework.web.reactive.function.server.RouterFunction;

import com.example.mu.pricequeryservice.controllers.PriceQueryHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
public class Router {

	private PriceQueryHandler handler;

	public Router(PriceQueryHandler handler) {
		this.handler = handler;
	}

	@Bean
	public RouterFunction<?> routerFunction() {
		return route(GET("/pricequeryservice/prices").and(accept(MediaType.APPLICATION_JSON)), handler::getAllPrices)
				.and(route(GET("/pricequeryservice/price/{id}").and(accept(MediaType.APPLICATION_JSON)),
						handler::getPrice));
	}

}
