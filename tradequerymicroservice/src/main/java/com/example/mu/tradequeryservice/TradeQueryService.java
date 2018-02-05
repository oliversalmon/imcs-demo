package com.example.mu.tradequeryservice;

import static com.hazelcast.query.Predicates.and;
import static com.hazelcast.query.Predicates.equal;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.mu.domain.PositionAccount;
import com.example.mu.domain.Trade;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;



@EnableCaching
@RestController
public class TradeQueryService {

	public final Logger LOG = LoggerFactory.getLogger(TradeQueryService.class);
	private final static String TRADE_MAP = "trade";
	
	@Value("${spring.application.name:tradequeryservice}")
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


	@Bean
	// @Profile("client")
	HazelcastInstance hazelcastInstance() {

		return HazelcastClient.newHazelcastClient();

	}
	
	@RequestMapping("/")
	public ServiceInstance lb() {
		return this.loadBalancer.choose(this.appName);
	}
	
	@Bean
	@LoadBalanced
	RestTemplate loadBalancedRestTemplate() {
		return new RestTemplate();
	}
	

	@RequestMapping("/ping")
	public String ping() {
		return "Trade service! from " + this.registration;
	}

	@Autowired
	private HazelcastInstance hazelcastInstance;

	@RequestMapping(value = "/getTradeCount", method = RequestMethod.GET)
	public ResponseEntity<Integer> getTradeCount() throws Exception {

		IMap<String, Trade> trade = hazelcastInstance.getMap(TRADE_MAP);
		return ResponseEntity.ok(trade.size());

	}

	//@CrossOrigin(origins = "http://localhost:8090")
	@RequestMapping(value = "/getAllTrades", method = RequestMethod.GET)
	public ResponseEntity<List<Object>> getAllTrades() throws Exception {

		IMap<String, Trade> trade = hazelcastInstance.getMap(TRADE_MAP);
		trade.loadAll(true);
		LOG.info("Size returning "+trade.size());
		return ResponseEntity.ok(trade.values().stream().collect(Collectors.toList()));

	}

	@RequestMapping(value = "/getTradesForPositionAccount/{positionAccountId}", method = RequestMethod.GET)
	public ResponseEntity<List<Object>> getTradesForPositionAccount(@PathVariable String positionAccountId)
			throws Exception {

		IMap<String, Trade> trade = hazelcastInstance.getMap(TRADE_MAP);
		Predicate positionAccount = equal("positionAccountId", positionAccountId);
		return ResponseEntity.ok(trade.values(positionAccount).stream().collect(Collectors.toList()));

	}

	//@CrossOrigin(origins = "http://localhost:8090")
	@RequestMapping(value = "/getTradesForPositionAccountAndInstrument/{positionAccountId}/{instrumentId}", method = RequestMethod.GET)
	public ResponseEntity<List<Trade>> getTradesForPositionAccountAndInstrument(@PathVariable String positionAccountId,
			@PathVariable String instrumentId) throws Exception {

		IMap<String, Trade> trade = hazelcastInstance.getMap(TRADE_MAP);
		trade.loadAll(true);
		Predicate positionAccount = equal("positionAccountId", positionAccountId);
		Predicate instrument = equal("instrumentId", instrumentId);
		Predicate predicate = and(positionAccount, instrument);
		return ResponseEntity.ok(trade.values(predicate).stream().collect(Collectors.toList()));

	}
	
	public String rt() {
		return this.rest.getForObject("http://" + this.appName + "/ping", String.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(TradeQueryService.class, args);

	}

}
