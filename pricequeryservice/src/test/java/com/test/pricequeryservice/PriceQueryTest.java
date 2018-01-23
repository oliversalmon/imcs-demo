package com.test.pricequeryservice;

import static org.junit.Assert.*;

import org.apache.curator.test.TestingServer;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.SocketUtils;

import com.example.mu.domain.Price;
import com.example.mu.pricequeryservice.controllers.StartUp;

import reactor.core.publisher.Flux;

public class PriceQueryTest {
	

	@Test public void contextLoads() throws Exception {
		int zkPort = SocketUtils.findAvailableTcpPort();
		TestingServer server = new TestingServer(zkPort);

		int port = SocketUtils.findAvailableTcpPort(zkPort+1);

		ConfigurableApplicationContext context = new SpringApplicationBuilder(StartUp.class).run(
				"--server.port="+port,
				"--management.endpoints.web.expose=*",
				"--spring.cloud.zookeeper.connect-string=localhost:" + zkPort);

		ResponseEntity<String> response = new TestRestTemplate().getForEntity("http://priceQueryService/ping", String.class);
		assertEquals(response.getStatusCode(), HttpStatus.OK);

		context.close();
		server.close();
	}

}
