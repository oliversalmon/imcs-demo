package com.example.mu.positionqueryservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


@EnableCaching
@RestController

public class PositionQueryService {

    public final Logger LOG = LoggerFactory.getLogger(PositionQueryService.class);
    final static String POSITION_ACCOUNT_MAP = "position-account";



//    @Bean
//    HazelcastInstance hazelcastInstance() {
//
//        return HazelcastClient.newHazelcastClient();
//
//    }
//
////    //only will be used for testing
//    @Value("${requireHz}")
//    private String requireHz;
//
//
//
//    @Autowired
//    private HazelcastInstance hazelcastInstance;
//
//
//    @RequestMapping(value = "/getAllPositionAccounts", method = RequestMethod.GET)
//    public ResponseEntity<List<PositionAccount>> getAllPositionAccounts() {
//
//        IMap<String, PositionAccount> posMap = hazelcastInstance.getMap(POSITION_ACCOUNT_MAP);
//        posMap.size();
//        //do not load on test
//        if (requireHz.equals("false") )
//            posMap.loadAll(true);
//        return ResponseEntity.ok(posMap.values().stream()
//                //.map(a -> a.toJSON())
//                .collect(Collectors.toList()));
//
//    }
//
//    @RequestMapping(value = "/getPositionAccount/{positionAccountId}", method = RequestMethod.GET)
//    public ResponseEntity<List<PositionAccount>> getPositionAccount(@PathVariable String positionAccountId) {
//
//        IMap<String, PositionAccount> posMap = hazelcastInstance.getMap(POSITION_ACCOUNT_MAP);
//        Predicate positionAccount = equal("accountId", positionAccountId);
//        return ResponseEntity.ok(posMap.values(positionAccount).stream().collect(Collectors.toList()));
//
//    }
//
//    @RequestMapping(value = "/getPositionAccountAndInstrument/{positionAccountId}/{instrumentId}", method = RequestMethod.GET)
//    public ResponseEntity<List<Object>> getPositionAccountForInstrument(@PathVariable String positionAccountId,
//                                                                        @PathVariable String instrumentId) {
//
//        IMap<String, PositionAccount> posMap = hazelcastInstance.getMap(POSITION_ACCOUNT_MAP);
//        Predicate positionAccount = equal("accountId", positionAccountId);
//        Predicate instrumentPredicate = equal("instrumentid", instrumentId);
//        Predicate predicate = and(positionAccount, instrumentPredicate);
//        return ResponseEntity.ok(posMap.values(predicate).stream().collect(Collectors.toList()));
//
//    }

    @RequestMapping(value = "/hi_getall")
    public List<String> getall() {
        LOG.info("Access /getall");

        List<String> greetings = Arrays.asList("Hi there", "Greetings", "Salutations");
        return greetings;
    }



}
