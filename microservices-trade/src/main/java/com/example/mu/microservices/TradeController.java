package com.example.mu.microservices;

import com.example.mu.domain.Trade;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by oliverbuckley-salmon on 08/05/2017.
 */
@RestController
public class TradeController {

    @Bean
    CacheManager cacheManager() {
        return new HazelcastCacheManager(hazelcastInstance());
    }

    @Bean
    HazelcastInstance hazelcastInstance() {
        // for client HazelcastInstance LocalMapStatistics will not available
        return HazelcastClient.newHazelcastClient();
        // return Hazelcast.newHazelcastInstance();
    }


    @RequestMapping(value="/trade/byid", method=GET)
    public Trade tradeById(@RequestParam(value="id", defaultValue="none") String tradeId){

        return new Trade();
    }
}
