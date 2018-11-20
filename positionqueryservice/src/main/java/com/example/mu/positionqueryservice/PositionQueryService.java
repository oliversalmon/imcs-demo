package com.example.mu.positionqueryservice;

import com.example.mu.domain.PositionAccount;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static com.hazelcast.query.Predicates.and;
import static com.hazelcast.query.Predicates.equal;

//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@CrossOrigin(origins = "http://178.62.124.180:31680")

@EnableCaching
@RestController
// @EnableDiscoveryClient
public class PositionQueryService {

	public final Logger LOG = LoggerFactory.getLogger(PositionQueryService.class);
	private final static String POSITION_ACCOUNT_MAP = "position-account";

	@Value("${spring.application.name:positionqueryservice}")
	private String appName;

	@Autowired
	private LoadBalancerClient loadBalancer;

	@Autowired
	private DiscoveryClient discovery;

	@Autowired
	private Environment env;

	@Autowired(required = false)
	private Registration registration;

	@Autowired
	RestTemplate rest;

	@RequestMapping("/")
	public ServiceInstance lb() {
		return this.loadBalancer.choose(this.appName);
	}


	@Bean
		// @Profile("client")
	HazelcastInstance hazelcastInstance() {

		return HazelcastClient.newHazelcastClient();

	}

	//only will be used for testing
	@Value("${requireHz}")
	private String requireHz;
	@Bean
	HazelcastInstance hazelcastInstanceMember() {

		if(requireHz != null)
		return Hazelcast.newHazelcastInstance();
		else return null;

	}

	@Autowired
	private HazelcastInstance hazelcastInstanceMember;

	@Autowired
	private HazelcastInstance hazelcastInstance;

	// @CrossOrigin(origins = "http://localhost:8090")
	@RequestMapping(value = "/getAllPositionAccounts", method = RequestMethod.GET)
	public ResponseEntity<List<PositionAccount>> getAllPositionAccounts() {

		IMap<String, PositionAccount> posMap = hazelcastInstance.getMap(POSITION_ACCOUNT_MAP);
		posMap.size();
		posMap.loadAll(true);
		return ResponseEntity.ok(posMap.values().stream()
				//.map(a -> a.toJSON())
				.collect(Collectors.toList()));

	}

	@RequestMapping(value = "/getPositionAccount/{positionAccountId}", method = RequestMethod.GET)
	public ResponseEntity<List<Object>> getPositionAccount(@PathVariable String positionAccountId) {

		IMap<String, PositionAccount> posMap = hazelcastInstance.getMap(POSITION_ACCOUNT_MAP);
		Predicate positionAccount = equal("accountId", positionAccountId);
		return ResponseEntity.ok(posMap.values(positionAccount).stream().collect(Collectors.toList()));

	}

	@RequestMapping(value = "/getPositionAccountAndInstrument/{positionAccountId}/{instrumentId}", method = RequestMethod.GET)
	public ResponseEntity<List<Object>> getPositionAccountForInstrument(@PathVariable String positionAccountId,
			@PathVariable String instrumentId) {

		IMap<String, PositionAccount> posMap = hazelcastInstance.getMap(POSITION_ACCOUNT_MAP);
		Predicate positionAccount = equal("accountId", positionAccountId);
		Predicate instrumentPredicate = equal("instrumentid", instrumentId);
		Predicate predicate = and(positionAccount, instrumentPredicate);
		return ResponseEntity.ok(posMap.values(predicate).stream().collect(Collectors.toList()));

	}

	@Bean
	@LoadBalanced
	RestTemplate loadBalancedRestTemplate() {
		return new RestTemplate();
	}

	public ResponseEntity<List<Object>> rt() {
		return this.rest.getForObject("http://" + this.appName + "/getAllPositionAccounts", ResponseEntity.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(PositionQueryService.class, args);

	}

}
