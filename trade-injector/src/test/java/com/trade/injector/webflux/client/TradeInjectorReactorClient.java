package com.trade.injector.webflux.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import com.trade.injector.controller.TradeInjectorController;

import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;


@RunWith(SpringRunner.class)
@SpringBootTest(
		  webEnvironment = WebEnvironment.RANDOM_PORT,
		  classes = TradeInjectorController.class)
@TestPropertySource(properties = {"kafka.bootstrap-servers=178.62.124.180:9092", "spring.data.mongodb.host=localhost", "webservices.priceservice.baseURL=http://localhost/8095"})
public class TradeInjectorReactorClient {
	
	@Autowired
	private WebClient webTestClient;
	
	@Test
	public void testDummy() throws Exception{
		
	}

}
