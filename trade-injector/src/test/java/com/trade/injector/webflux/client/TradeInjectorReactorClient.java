package com.trade.injector.webflux.client;

import com.trade.injector.controller.TradeInjectorController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(
		  webEnvironment = WebEnvironment.RANDOM_PORT,
		  classes = TradeInjectorController.class)
@TestPropertySource(properties = {"kafka.bootstrap-servers=178.62.124.180:9092", "spring.data.mongodb.host=localhost", "webservices.priceservice.baseURL=http://localhost/8095"})
public class TradeInjectorReactorClient {
	

	
	@Test
	public void testDummy() {
		
	}
	
	@Test
	public void getPositions() {
		
	}

}
