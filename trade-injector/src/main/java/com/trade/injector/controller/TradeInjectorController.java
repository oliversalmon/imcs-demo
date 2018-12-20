package com.trade.injector.controller;

import com.example.mu.domain.Instrument;
import com.example.mu.domain.Party;
import com.example.mu.domain.Trade;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;
import com.trade.injector.business.service.*;
import com.trade.injector.enums.TradeInjectRunModes;
import com.trade.injector.jto.*;
import com.trade.injector.jto.repository.MongoDBTemplate;
import com.trade.injector.jto.repository.TradeInjectorMessageRepository;
import com.trade.injector.jto.repository.TradeInjectorProfileRepository;
import com.trade.injector.jto.repository.TradeReportRepository;
import com.trade.injector.sinks.KafkaSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import com.trade.injector.business.service.GenerateRandomInstruments;
//import com.trade.injector.business.service.GenerateRandomParty;
//import com.trade.injector.dto.Trade;
//import com.trade.injector.jto.Instrument;
//import com.trade.injector.jto.Party;
//import com.trade.injector.jto.TradeInjectorMessage;

@SpringBootApplication(scanBasePackages = "com.trade.injector")
@EnableOAuth2Client
@RestController
@EnableMongoRepositories(basePackages = "com.trade.injector.jto.repository")
@EnableScheduling
@EnableCaching
@EnableFeignClients
//@EnableDiscoveryClient

public class TradeInjectorController extends WebSecurityConfigurerAdapter {

    final Logger LOG = LoggerFactory.getLogger(TradeInjectorController.class);

    @Autowired
    private PriceServiceClient priceClient;

    @Autowired
    private PositionServiceClient positionClient;

    @Autowired
    private TradeServiceClient tradeServiceClient;

    @Bean
    HazelcastInstance hazelcastInstance() {

        // for client HazelcastInstance LocalMapStatistics will not available

        return HazelcastClient.newHazelcastClient();

        // return Hazelcast.newHazelcastInstance();

    }

    @FeignClient(name = "priceQueryService")
    interface PriceServiceClient {
        @RequestMapping(value = "/hi", method = RequestMethod.GET)
        @ResponseBody
        String hi();


    }

    @FeignClient(name = "position-query")
    interface PositionServiceClient {

        @RequestMapping(value = "/getAllPositionAccounts", method = RequestMethod.GET)
        @ResponseBody
        Resource getAllPositionAccounts();

<<<<<<< HEAD
	@FeignClient(name = "positionqueryservice")
	interface PositionServiceClient {
=======
        @RequestMapping(method = RequestMethod.GET, value = "/hi_getall", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
        List<String> hi();
>>>>>>> feature-kubernetes-lb

    }


    @FeignClient(name = "trade-query")
    interface TradeServiceClient {

        @RequestMapping(value = "/getTradesForPositionAccountAndInstrument/{positionAccountId}/{instrumentId}", method = RequestMethod.GET)
        @ResponseBody
        Resource getTradesForPositionAccountAndInstrument(@PathVariable("positionAccountId") String positionAccountId,
                                                          @PathVariable("instrumentId") String instrumentId);

        @RequestMapping(method = RequestMethod.GET, value = "/ping", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
        List<String> hi();

    }

    @RequestMapping(value = "/pingPosition", method = RequestMethod.GET)
    public List<String> pingPosition() {
        return positionClient.hi();
    }

    @RequestMapping(value = "/pingTrades", method = RequestMethod.GET)
    public List<String> pingTrades() {
        return tradeServiceClient.hi();
    }

    @RequestMapping(value = "/getAllPositions", method = RequestMethod.GET)
    public Resource getAllPositions() {

        return positionClient.getAllPositionAccounts();
    }

    @RequestMapping(value = "/getTradesForPositionAccountAndInstrument/{positionAccountId}/{instrumentId}", method = RequestMethod.GET)
    public Resource getTradesForPositionAccountAndInstrument(@PathVariable String positionAccountId,
                                                             @PathVariable String instrumentId) {

        return tradeServiceClient.getTradesForPositionAccountAndInstrument(positionAccountId, instrumentId);
    }

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @Autowired
    private SimpMessagingTemplate messageSender;

    @Autowired
    private MongoDBTemplate template;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private MongoTemplate coreTemplate;

    @Autowired
    GenerateTradeData tradeData;

    @Autowired
    GenerateTradeCacheData tradeDataCache;

    @Autowired(required = true)
    private TradeInjectorMessageRepository repo;

    @Autowired
    private TradeReportRepository reportRepo;

    @Autowired
    private TradeInjectorProfileRepository profileRepo;

    @Autowired
    private KafkaSink sender;

    @Value("${kafka.topic.trade}")
    private String tradeTopic;

    @RequestMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**").authorizeRequests()
                .antMatchers("/", "/login/**", "/webjars/**", "/dist/**", "/scripts/**", "/jumbotron.css",
                        "/injectorUI/**")
                .permitAll().anyRequest().authenticated().and().logout().logoutSuccessUrl("/").permitAll().and().csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);

    }

    private Filter ssoFilter() {

        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<Filter>();

        OAuth2ClientAuthenticationProcessingFilter facebookFilter = new OAuth2ClientAuthenticationProcessingFilter(
                "/login/facebook");
        OAuth2RestTemplate facebookTemplate = new OAuth2RestTemplate(facebook(), oauth2ClientContext);
        facebookFilter.setRestTemplate(facebookTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(facebookResource().getUserInfoUri(),
                facebook().getClientId());
        tokenServices.setRestTemplate(facebookTemplate);
        facebookFilter.setTokenServices(tokenServices);

        filters.add(facebookFilter);

        OAuth2ClientAuthenticationProcessingFilter githubFilter = new OAuth2ClientAuthenticationProcessingFilter(
                "/login/github");
        OAuth2RestTemplate githubTemplate = new OAuth2RestTemplate(github(), oauth2ClientContext);
        githubFilter.setRestTemplate(githubTemplate);
        tokenServices = new UserInfoTokenServices(githubResource().getUserInfoUri(), github().getClientId());
        tokenServices.setRestTemplate(githubTemplate);
        githubFilter.setTokenServices(tokenServices);
        filters.add(githubFilter);

        filter.setFilters(filters);
        return filter;

    }

    @Bean
    @ConfigurationProperties("facebook.client")
    public AuthorizationCodeResourceDetails facebook() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("facebook.resource")
    public ResourceServerProperties facebookResource() {
        return new ResourceServerProperties();
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    @Bean
    @ConfigurationProperties("github.client")
    public AuthorizationCodeResourceDetails github() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("github.resource")
    public ResourceServerProperties githubResource() {
        return new ResourceServerProperties();
    }

    @RequestMapping(value = "/tradeMessageStopForProfile", method = RequestMethod.POST)
    public void tradeStopForProfile(@RequestBody String messageId) {

        LOG.info("Stop run for the following Id " + messageId);

        // we need to remove the id= bit from message id
        messageId = messageId.substring(messageId.indexOf('=') + 1);

        TradeInjectorProfile profile = coreTemplate.findOne(Query.query(Criteria.where("id").is(messageId)),
                TradeInjectorProfile.class);

        if (profile != null) {
            profile.setRun_mode(TradeInjectRunModes.STOP.getRunMode());
            profileRepo.save(profile);

            // refreshTradeInjectQueue();

        } else
            LOG.error("Unable to find message for the following id " + messageId);

    }

    @RequestMapping(value = "/tradeMessagePlayForProfile", method = RequestMethod.POST)
    public void tradePlayForProfile(@RequestBody String messageId) throws Exception {

        // we need to remove the id= bit from message id
        messageId = messageId.substring(messageId.indexOf('=') + 1);
        LOG.info("Playing for the following Id " + messageId);

        TradeInjectorProfile profile = coreTemplate.findOne(Query.query(Criteria.where("id").is(messageId)),
                TradeInjectorProfile.class);

        if (profile != null) {
            runTradeInjectForTradeProfileId(profile);
        } else
            LOG.error("Unable to find profile for the following id " + messageId);

    }

    private String getRHSOfString(String equation) {

        if (equation == null)
            return null;

        return equation.substring(equation.indexOf('=') + 1);
    }

    @RequestMapping(value = "/tradeMessageRepeatForProfile", method = RequestMethod.POST)
    public void repeatRunOnProfile(@RequestBody String profileId) throws Exception {

        // we need to remove the id= bit from message id
        profileId = profileId.substring(profileId.indexOf('=') + 1);
        LOG.info("Running for the following Id " + profileId);

        TradeInjectorProfile profile = coreTemplate.findOne(Query.query(Criteria.where("id").is(profileId)),
                TradeInjectorProfile.class);

        if (profile != null) {

            // remove the reports for Trade Data
            TradeReport tradeReport = coreTemplate
                    .findOne(Query.query(Criteria.where("injectorProfileId").is(profileId)), TradeReport.class);
            if (tradeReport != null)
                reportRepo.delete(tradeReport);

            // reset the message count to 0
            profile.setCurrentMessageCount(0);
            runTradeInjectForTradeProfileId(profile);

        } else
            LOG.error("Unable to find message for the following id " + profileId);

    }

    @RequestMapping(value = "/tradeRunStart", method = RequestMethod.POST)
    public void injectTradesOnProfile(@RequestBody TradeInjectorProfile profile) throws Exception {

        LOG.info("Running for the following profile... " + profile.id);
        runTradeInjectForTradeProfileId(profile);
        LOG.info("Done running for the following Profile " + profile.id);

    }

    @RequestMapping(value = "/deleteProfile", method = RequestMethod.POST)
    public void deleteProfile(@RequestBody String profileId) {

        profileId = profileId.substring(profileId.indexOf('=') + 1);

        // delete any reports associated to this profile
        TradeReport report = coreTemplate.findOne(Query.query(Criteria.where("injectorProfileId").is(profileId)),
                TradeReport.class);

        LOG.info("Deleting TradeReport... " + profileId);
        if (report != null) {
            reportRepo.delete(report);
        } else {
            LOG.warn("No trade report found with the following profile id " + profileId);
        }

        LOG.info("Deleting profile... " + profileId);
        TradeInjectorProfile profile = coreTemplate.findOne(Query.query(Criteria.where("id").is(profileId)),
                TradeInjectorProfile.class);

        if (profile != null) {

            profileRepo.delete(profile);

        } else {
            LOG.warn("No profile found with the following id " + profileId);
        }
        LOG.info("Done deleting profile " + profileId);

    }

    private void runTradeInjectForTradeProfileId(TradeInjectorProfile profile) throws Exception {

        // List<Instrument> listOfInstruments = new GenerateRandomInstruments()
        // .createRandomData(new Integer(profile.getNumberOfInstruments()));
        // List<Party> listOfParties = new GenerateRandomParty()
        // .createRandomData(new Integer(profile.getNumberOfParties()));

        IMap<String, Trade> mapTrades = hazelcastInstance.getMap("trade");
        IMap<String, Party> partyMap = hazelcastInstance.getMap("party");

        GeneratePartyCache cacheGenerator = new GeneratePartyCache();
        cacheGenerator.populateMap(profile.getNumberOfParties(), partyMap);

        LOG.info("Successfully generated party cache");

        IMap<String, Instrument> instrumentMap = hazelcastInstance.getMap("instrument");
        GenerateInstrumentCache insCacheGenerator = new GenerateInstrumentCache();
        insCacheGenerator.populateMap(profile.getNumberOfInstruments(), instrumentMap);

        LOG.info("Successfully generated instrument cache");

        int startFrom = new Integer(profile.getCurrentMessageCount());
        int numberOfTrades = new Integer(profile.getNumberOfTrades());

        // set it to run
        profile.setRun_mode(TradeInjectRunModes.RUNNING.getRunMode());
        while (startFrom != numberOfTrades) {

            startFrom++;
            Trade[] trades = tradeDataCache.createTrade(startFrom, partyMap, profile.getNumberOfParties(),
                    instrumentMap, profile.getNumberOfInstruments());

            // send it to the Kafka sink
            LOG.info("Sending the following trades " + trades[0].toJSON() + " ####### " + trades[1].toJSON());

            sender.send(tradeTopic, trades[0].getTradeId(), trades[0].toJSON());
            sender.send(tradeTopic, trades[1].getTradeId(), trades[1].toJSON());

            // mapTrades.put(trades[0].getExecutionId(), trades[0]); //for buy
            // mapTrades.put(trades[1].getExecutionId(), trades[1]); //for sell

            // convertToReportAndSaveForProfile(trades[0], profile.getUserId(),
            // profile.id);
            // convertToReportAndSaveForProfile(trades[1], profile.getUserId(),
            // profile.id);
            profile.setCurrentMessageCount(startFrom);

            // sleep for simulated wait time
            profileRepo.save(profile);
            Thread.sleep(profile.getSimulatedWaitTime());

            // if the kill flag is set by the UI return the process.
            TradeInjectorProfile runningProfile = coreTemplate.findOne(Query.query(Criteria.where("id").is(profile.id)),
                    TradeInjectorProfile.class);
            if (runningProfile.getRun_mode() == TradeInjectRunModes.STOP.getRunMode())

                // kill it and return

                break;
        }

        // finally mark it as complete
        if (startFrom == numberOfTrades) {
            profile.setCurrentMessageCount(startFrom);
            profile.setRun_mode(TradeInjectRunModes.COMPLETED.getRunMode());
            profileRepo.save(profile);
        }

        LOG.info("Successfully generated trades, quitting");

    }

    private void convertToReportAndSaveForProfile(Trade ack, String username, String injectorProfileId) {

        TradeReport tradeReport = coreTemplate
                .findOne(Query.query(Criteria.where("injectorProfileId").is(injectorProfileId)), TradeReport.class);

        if (tradeReport == null) {
            // create a new one
            tradeReport = new TradeReport();
            tradeReport.setCurrentTradeProgress(1);
            tradeReport.setInjectorProfileId(injectorProfileId);
            tradeReport.setName("Report_" + injectorProfileId);
            tradeReport.setReportDate(new Date(System.currentTimeMillis()));
            tradeReport.setTradeCount(1);
            tradeReport.setUserId(username);

            List<PartyReport> parties = new ArrayList<PartyReport>();
            PartyReport newParty = new PartyReport();
            newParty.setCurrentTradeCount(1);
            newParty.setPreviousTradeCount(1);
            newParty.setId(ack.getClientId());
            newParty.setName(ack.getClientId());
            parties.add(newParty);

            tradeReport.setParties(parties);

            // now add the newly created instrument
            List<InstrumentReport> instruments = new ArrayList<InstrumentReport>();
            InstrumentReport newInstrument = new InstrumentReport();
            newInstrument.setId(ack.getInstrumentId());
            newInstrument.setName(ack.getInstrumentId());
            newInstrument.setCurrentTradeCount(1);
            instruments.add(newInstrument);

            tradeReport.setInstruments(instruments);

        } else {
            // we have found it now update the all the counters
            int progress = tradeReport.getCurrentTradeProgress();
            tradeReport.setCurrentTradeProgress(++progress);
            List<PartyReport> parties = tradeReport.getParties();
            List<InstrumentReport> instruments = tradeReport.getInstruments();

            List<PartyReport> modifiedParties = parties.stream().filter(a -> a.getName().equals(ack.getClientId()))
                    .map(a -> a.incrementCountByOne()).collect(Collectors.toList());
            List<PartyReport> nonModifiedParties = parties.stream().filter(a -> !a.getName().equals(ack.getClientId()))
                    .collect(Collectors.toList());

            if (modifiedParties.size() == 0) {
                // add the new Party in
                PartyReport newParty = new PartyReport();
                newParty.setCurrentTradeCount(1);
                newParty.setId(ack.getClientId());
                newParty.setName(ack.getClientId());
                modifiedParties.add(newParty);
            }

            parties = Stream.concat(modifiedParties.stream(), nonModifiedParties.stream()).collect(Collectors.toList());

            // now do the same for the instruments
            List<InstrumentReport> modifiedInstruments = instruments.stream()
                    .filter(a -> a.getId().equals(ack.getInstrumentId())).map(a -> a.incrementCountByOne())
                    .collect(Collectors.toList());
            List<InstrumentReport> nonModifiedInstruments = instruments.stream()
                    .filter(a -> !a.getId().equals(ack.getInstrumentId())).collect(Collectors.toList());

            if (modifiedInstruments.size() == 0) {

                InstrumentReport newInstrument = new InstrumentReport();
                newInstrument.setId(ack.getInstrumentId());
                newInstrument.setName(ack.getInstrumentId());
                newInstrument.setCurrentTradeCount(1);
                modifiedInstruments.add(newInstrument);

            }

            // finally concat the list
            instruments = Stream.concat(modifiedInstruments.stream(), nonModifiedInstruments.stream())
                    .collect(Collectors.toList());

            tradeReport.setParties(parties);
            tradeReport.setInstruments(instruments);

        }

        reportRepo.save(tradeReport);

    }

    @Deprecated
    private void convertToReportAndSaveForProfile(TradeAcknowledge ack, String username) {

        TradeReport tradeReport = coreTemplate.findOne(
                Query.query(Criteria.where("injectorProfileId").is(ack.getProfileIdentifier())), TradeReport.class);

        if (tradeReport == null) {
            // create a new one
            tradeReport = new TradeReport();
            tradeReport.setCurrentTradeProgress(1);
            tradeReport.setInjectorMessageId(ack.getInjectIdentifier());
            tradeReport.setInjectorProfileId(ack.getProfileIdentifier());
            tradeReport.setName("Report_" + ack.getProfileIdentifier());
            tradeReport.setReportDate(new Date(System.currentTimeMillis()));
            tradeReport.setTradeCount(1);
            tradeReport.setUserId(username);

            List<PartyReport> parties = new ArrayList<PartyReport>();
            PartyReport newParty = new PartyReport();
            newParty.setCurrentTradeCount(1);
            newParty.setPreviousTradeCount(1);
            newParty.setId(ack.getClientName());
            newParty.setName(ack.getClientName());
            parties.add(newParty);

            tradeReport.setParties(parties);

            // now add the newly created instrument
            List<InstrumentReport> instruments = new ArrayList<InstrumentReport>();
            InstrumentReport newInstrument = new InstrumentReport();
            newInstrument.setId(ack.getInstrumentId());
            newInstrument.setName(ack.getInstrumentId());
            newInstrument.setCurrentTradeCount(1);
            instruments.add(newInstrument);

            tradeReport.setInstruments(instruments);

        } else {
            // we have found it now update the all the counters
            int progress = tradeReport.getCurrentTradeProgress();
            tradeReport.setCurrentTradeProgress(++progress);
            List<PartyReport> parties = tradeReport.getParties();
            List<InstrumentReport> instruments = tradeReport.getInstruments();

            List<PartyReport> modifiedParties = parties.stream().filter(a -> a.getId().equals(ack.getClientName()))
                    .map(a -> a.incrementCountByOne()).collect(Collectors.toList());
            List<PartyReport> nonModifiedParties = parties.stream().filter(a -> !a.getId().equals(ack.getClientName()))
                    .collect(Collectors.toList());

            if (modifiedParties.size() == 0) {
                // add the new Party in
                PartyReport newParty = new PartyReport();
                newParty.setCurrentTradeCount(1);
                newParty.setId(ack.getClientName());
                newParty.setName(ack.getClientName());
                modifiedParties.add(newParty);
            }

            parties = Stream.concat(modifiedParties.stream(), nonModifiedParties.stream()).collect(Collectors.toList());

            // now do the same for the instruments
            List<InstrumentReport> modifiedInstruments = instruments.stream()
                    .filter(a -> a.getId().equals(ack.getInstrumentId())).map(a -> a.incrementCountByOne())
                    .collect(Collectors.toList());
            List<InstrumentReport> nonModifiedInstruments = instruments.stream()
                    .filter(a -> !a.getId().equals(ack.getInstrumentId())).collect(Collectors.toList());

            if (modifiedInstruments.size() == 0) {

                InstrumentReport newInstrument = new InstrumentReport();
                newInstrument.setId(ack.getInstrumentId());
                newInstrument.setName(ack.getInstrumentId());
                newInstrument.setCurrentTradeCount(1);
                modifiedInstruments.add(newInstrument);

            }

            // finally concat the list
            instruments = Stream.concat(modifiedInstruments.stream(), nonModifiedInstruments.stream())
                    .collect(Collectors.toList());

            tradeReport.setParties(parties);
            tradeReport.setInstruments(instruments);

        }

        reportRepo.save(tradeReport);

    }

    @RequestMapping(value = "/saveTradeInjectProfile", method = RequestMethod.POST)
    public ResponseEntity<TradeInjectorProfile> saveTradeInjectProfile(@RequestBody TradeInjectorProfile profile) {

        // set it to stop so that it can show up as play on the profile
        profile.setRun_mode(TradeInjectRunModes.STOP.getRunMode());

        profileRepo.save(profile);

        return ResponseEntity.ok(profile);

    }

    @RequestMapping(value = "/getAllInjectProfiles", method = RequestMethod.GET)
    public ResponseEntity<List<TradeInjectorProfile>> saveTradeInjectProfile() {

        return ResponseEntity.ok(profileRepo.findAll());

    }

    @RequestMapping(value = "/getProfile", method = RequestMethod.POST)
    public ResponseEntity<TradeInjectorProfile> getProfile(@RequestBody String profileId) {

        profileId = profileId.substring(profileId.indexOf('=') + 1);

        TradeInjectorProfile profile = coreTemplate.findOne(Query.query(Criteria.where("id").is(profileId)),
                TradeInjectorProfile.class);

        if (profile == null)

            LOG.warn("No profile found with the following id " + profileId);

        return ResponseEntity.ok(profile);

    }

    /**
     * Returns the list of instruments available in the cache
     *
     * @return ResponseEntity
     */
    @RequestMapping(value = "/getAllInstruments", method = RequestMethod.POST)
    public ResponseEntity<Collection<InstrumentReport>> getAllInstruments(@RequestBody String pageMarker) {

        IMap<String, Instrument> mapInstruments = hazelcastInstance.getMap(BusinessServiceCacheNames.INSTRUMENT_CACHE);

        pageMarker = getRHSOfString(pageMarker);
        LOG.info("Page Marker is  " + pageMarker);

        // no data do not generate
        if (mapInstruments.size() == 0) {
            LOG.warn("No instruments found");
            return ResponseEntity.ok(new ArrayList<InstrumentReport>());
        }

        Predicate predicate = new SqlPredicate(String.format("instrumentId like %s", pageMarker + "%"));
        Collection<InstrumentReport> ins = mapInstruments.values(predicate).stream()
                .map(x -> convertInstrumentToReport(x)).collect(Collectors.toList());
        LOG.info("Number of Instruments to return for following page marker " + pageMarker + " " + ins.size());

        return ResponseEntity.ok(ins);

    }

    private InstrumentReport convertInstrumentToReport(Instrument ins) {
        InstrumentReport report = new InstrumentReport();
        report.setId(ins.getSymbol());
        report.setName(ins.getIssuer());

        return report;
    }

    @RequestMapping(value = "/getAllPositionAccounts", method = RequestMethod.GET)
    public ResponseEntity<List<Object>> getAllPositionAccounts() {

        RestTemplate restTemplate = new RestTemplate();
        LOG.info("Pinging the following URL http://192.168.1.176:8093/positionqueryservice/getAllPositionAccounts");

        // needs to be recoded via zookeeper
        // TODO: Move to Zookeeper and discover this service
        return ResponseEntity.ok(restTemplate
                .getForObject("http://192.168.1.176:8093/positionqueryservice/getAllPositionAccounts", List.class));

    }

    /*
     * private TradeAcknowledge convertToAckForProfile(Trade aTrade, String id) {
     * TradeAcknowledge ack = new TradeAcknowledge(); if (aTrade != null) {
     * ack.setProfileIdentifier(id); ack.setClientName(aTrade.getClientName());
     * ack.setInstrumentId(aTrade.getInstrumentId()); ack.setSide(aTrade.getSide());
     * ack.setTradeDate(aTrade.getTradeDate().toString()); ack.setTradePx(new
     * Double(aTrade.getTradePx()).toString()); ack.setTradeQty(new
     * Integer(aTrade.getTradeQty()).toString()); }
     *
     * return ack; }
     */

    public static void main(String[] args) {

        SpringApplication.run(TradeInjectorController.class, args);
    }

}
