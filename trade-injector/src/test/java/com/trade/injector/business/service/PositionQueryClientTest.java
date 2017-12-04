package com.trade.injector.business.service;

import java.util.List;

import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.mu.domain.PositionAccount;

public class PositionQueryClientTest {

	@Test
	public void getAllPosition() {

		RestTemplate restTemplate = new RestTemplate();
		List<PositionAccount> posObject = restTemplate.getForObject(
				"http://localhost:8093/positionqueryservice/getAllPositionAccounts", List.class);
		
		
		
		

	}

}
