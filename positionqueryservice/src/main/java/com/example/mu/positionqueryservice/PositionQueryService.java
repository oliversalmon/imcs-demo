package com.example.mu.positionqueryservice;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.mu.domain.PositionAccount;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import static com.hazelcast.query.Predicates.*;
@CrossOrigin(origins = "http://178.62.124.180:32509")
@SpringBootApplication(scanBasePackages = "com.example.mu.positionqueryservice")
@EnableCaching
@RestController
public class PositionQueryService {

	public final Logger LOG = LoggerFactory.getLogger(PositionQueryService.class);
	private final static String POSITION_ACCOUNT_MAP = "position-account";

	@Bean
	// @Profile("client")
	HazelcastInstance hazelcastInstance() {

		return HazelcastClient.newHazelcastClient();

	}

	@Autowired
	private HazelcastInstance hazelcastInstance;

	//@CrossOrigin(origins = "http://localhost:8090")
	@RequestMapping(value = "/getAllPositionAccounts", method = RequestMethod.GET)
	public ResponseEntity<List<Object>> getAllPositionAccounts() throws Exception {

		IMap<String, PositionAccount> posMap = hazelcastInstance.getMap(POSITION_ACCOUNT_MAP);
		posMap.loadAll(false);
		return ResponseEntity.ok(posMap.values().stream().collect(Collectors.toList()));

	}
	
	@RequestMapping(value = "/getPositionAccount/{positionAccountId}", method = RequestMethod.GET)
	public ResponseEntity<List<Object>> getPositionAccount(@PathVariable String positionAccountId) throws Exception {

		IMap<String, PositionAccount> posMap = hazelcastInstance.getMap(POSITION_ACCOUNT_MAP);
		Predicate positionAccount =   equal("accountId", positionAccountId);
		return ResponseEntity.ok(posMap.values(positionAccount).stream().collect(Collectors.toList()));

	}
	
	@RequestMapping(value = "/getPositionAccountAndInstrument/{positionAccountId}/{instrumentId}", method = RequestMethod.GET)
	public ResponseEntity<List<Object>> getPositionAccountForInstrument(@PathVariable String positionAccountId, @PathVariable String instrumentId) throws Exception {

		IMap<String, PositionAccount> posMap = hazelcastInstance.getMap(POSITION_ACCOUNT_MAP);
		Predicate positionAccount =   equal("accountId", positionAccountId);
		Predicate instrumentPredicate = equal("instrumentid", instrumentId);
		Predicate predicate = and(positionAccount, instrumentPredicate);
		return ResponseEntity.ok(posMap.values(predicate).stream().collect(Collectors.toList()));

	}

	public static void main(String[] args) {
		SpringApplication.run(PositionQueryService.class, args);

	}

}
