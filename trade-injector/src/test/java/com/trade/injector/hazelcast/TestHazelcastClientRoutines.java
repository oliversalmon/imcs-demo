package com.trade.injector.hazelcast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.mu.domain.Instrument;
import com.example.mu.domain.Party;
import com.example.mu.domain.Price;
import com.example.mu.domain.Trade;
//import com.google.common.collect.Maps;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.client.HazelcastClient;
//import com.hazelcast.jet.DAG;
//import com.hazelcast.jet.Jet;
//import com.hazelcast.jet.JetInstance;
//import com.hazelcast.jet.Processors;
//import com.hazelcast.jet.Vertex;
//import com.hazelcast.jet.stream.IStreamMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;
import com.trade.injector.business.service.GenerateInstrumentCache;
import com.trade.injector.business.service.GeneratePartyCache;
import com.trade.injector.business.service.GeneratePriceData;
import com.trade.injector.business.service.GenerateTradeCacheData;
import com.trade.injector.controller.TradeInjectorController;
import com.trade.injector.jto.InstrumentReport;
import com.trade.injector.jto.PartyReport;
import com.trade.injector.jto.TradeAcknowledge;
import com.trade.injector.jto.TradeReport;
import com.trade.injector.jto.repository.TradeReportRepository;
import com.trade.injector.sinks.ISink;
import com.trade.injector.sinks.KafkaSink;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradeInjectorController.class)
@TestPropertySource(properties = {"kafka.bootstrap-servers=138.68.168.237:9092", "spring.data.mongodb.host=localhost"})
public class TestHazelcastClientRoutines {

	@Autowired
	HazelcastInstance hzInstance;

	@Autowired
	private MongoTemplate coreTemplate;

	@Autowired
	private TradeReportRepository reportRepo;

	@Autowired
	private KafkaSink sender;

	final String injectProfileId = UUID.randomUUID().toString();

	@Before
	public void setUpCache() {

	}

	/*
	 * @Test public void testHZstart() { Map<String, Trade> map =
	 * hzInstance.getMap("trade"); assertNotNull(map);
	 * 
	 * map = hzInstance.getMap("trade");
	 * 
	 * assertEquals(1, map.size());
	 * 
	 * }
	 */
	@Test
	public void testClientGeneration() {
		IMap<String, Party> map = hzInstance.getMap("party");
		assertNotNull(map);

		assertEquals(0, map.size());

		Predicate predicate = new SqlPredicate(String.format("name like %s",
				"TRDINJECT_CLI_%"));
		Set<Party> parties = (Set<Party>) map.values(predicate);

		assertEquals(0, parties.size());

		// now add parties and make sure there is one
		GeneratePartyCache cacheGenerator = new GeneratePartyCache();
		cacheGenerator.populateMap(1, map);

		map = hzInstance.getMap("party");
		assertEquals(1, map.size());

		Set<Party> parties2 = (Set<Party>) map.values(predicate);
		assertEquals(1, parties2.size());

		// we need the key set to remove
		Set<String> partiesKeys = (Set<String>) map.keySet(predicate);
		assertEquals(1, partiesKeys.size());

		// now delete the parties
		for (String aparty : partiesKeys) {

			System.out.println("Party in question is " + aparty);
			map.remove(aparty);
		}

		// now check hz there should be none in there
		map = hzInstance.getMap("party");
		Set<Party> parties3 = (Set<Party>) map.values(predicate);
		assertEquals(0, parties3.size());

		map = hzInstance.getMap("party");
		assertEquals(0, map.size());
	}

	@Test
	public void testTradeGeneration() throws Exception {

		// first generate party information
		IMap<String, Party> map = hzInstance.getMap("party");
		assertNotNull(map);

		assertEquals(0, map.size());

		GeneratePartyCache cacheGenerator = new GeneratePartyCache();
		cacheGenerator.populateMap(1000, map);

		map = hzInstance.getMap("party");
		assertEquals(1000, map.size());

		// now generate instrument information
		System.out.println("Runnning many instruments...");

		IMap<String, Instrument> mapInstruments = hzInstance
				.getMap("instrument");
		assertNotNull(mapInstruments);

		assertEquals(0, mapInstruments.size());

		GenerateInstrumentCache cacheGeneratorInstruments = new GenerateInstrumentCache();
		cacheGeneratorInstruments.populateMap(1000, mapInstruments);

		mapInstruments = hzInstance.getMap("instrument");
		assertEquals(1000, mapInstruments.size());

		// now generate trades
		IMap<String, Trade> mapTrades = hzInstance.getMap("trade");
		assertEquals(0, mapTrades.size());
		GenerateTradeCacheData tradeGen = new GenerateTradeCacheData();
		for (int i = 0; i < 100; i++) {
			// generate 100 trades
			Trade[] trades = tradeGen.createTrade(i, map, 20, mapInstruments,
					30);
			mapTrades.put(trades[0].getExecutionId(), trades[0]); // for buy
			mapTrades.put(trades[1].getExecutionId(), trades[1]); // for sell
		}

		// now check to see if it is done
		mapTrades = hzInstance.getMap("trade");
		assertEquals(200, mapTrades.size());

	}

	@Test
	public void testGeneratePrices() throws Exception{
		
		System.out.println("Runnning many instruments...");

		IMap<String, Instrument> mapInstruments = hzInstance.getMap("instrument");
		assertNotNull(mapInstruments);

		assertEquals(0, mapInstruments.size());

		GenerateInstrumentCache cacheGeneratorInstruments = new GenerateInstrumentCache();
		cacheGeneratorInstruments.populateMap(1000, mapInstruments);

		mapInstruments = hzInstance.getMap("instrument");
		assertEquals(1000, mapInstruments.size());
		
		
		
		//generate price and sink it to Kafka
		Predicate predicate = new SqlPredicate(String.format("symbol like %s",
				"ISIN_INJ_%"));
		Collection<Instrument>ins = mapInstruments.values(predicate);
		for(Instrument aIns : ins){
			sinkToKafka(GeneratePriceData.generateRandomDataOnInstruments(aIns), sender, "market_data");
			System.out.println("Successfully created prices");
		}
		//GeneratePriceData.generateRandomDataOnInstruments(a);
		//JetInstance jet = Jet.newJetInstance();
		//Jet.newJetInstance();
		
		//IStreamMap<Integer, String> source = jet.getMap("instrument");
		//assertTrue(source.size() ==1000);
	}

	private void sinkToKafka(Price data, ISink sink, String topic)
			throws Exception {

		System.out.println("price data is " + data.toJSON());
		sink.writeTo(topic, data.getPriceId(), data.toJSON());

	}

	@Test
	public void testConversionToReport() throws Exception {
		System.out.println("test report generation..."
				+ new Date(System.currentTimeMillis()));
		// testTradeGeneration();

		IMap mapTrades = hzInstance.getMap("trade");
		assertEquals(0, mapTrades.size());

		// create 100 clients
		IMap<String, Party> partyMap = hzInstance.getMap("party");
		assertNotNull(partyMap);

		assertEquals(0, partyMap.size());

		GeneratePartyCache cacheGenerator = new GeneratePartyCache();
		cacheGenerator.populateMap(100, partyMap);

		partyMap = hzInstance.getMap("party");
		assertEquals(100, partyMap.size());

		// create 1000 instruments
		IMap<String, Instrument> mapInstruments = hzInstance
				.getMap("instrument");
		assertNotNull(mapInstruments);

		assertEquals(0, mapInstruments.size());

		GenerateInstrumentCache cacheGeneratorInstruments = new GenerateInstrumentCache();
		cacheGeneratorInstruments.populateMap(1000, mapInstruments);

		mapInstruments = hzInstance.getMap("instrument");
		assertEquals(1000, mapInstruments.size());

		// generate 1000 trades and report it
		mapTrades = hzInstance.getMap("trade");
		assertEquals(0, mapTrades.size());
		GenerateTradeCacheData tradeGen = new GenerateTradeCacheData();

		for (int i = 0; i < 1000 / 2; i++) {
			Trade[] trades = tradeGen.createTrade(i, partyMap, 10,
					mapInstruments, 10);
			mapTrades.put(trades[0].getExecutionId(), trades[0]); // for buy
			mapTrades.put(trades[1].getExecutionId(), trades[1]); // for sell

			convertToReportAndSaveForProfile(trades[0], "Dinesh Pillai",
					injectProfileId);
			convertToReportAndSaveForProfile(trades[1], "Dinesh Pillai",
					injectProfileId);

		}

		// finally assert and display results
		mapTrades = hzInstance.getMap("trade");
		assertEquals(1000, mapTrades.size());

		TradeReport tradeReport = coreTemplate.findOne(
				Query.query(Criteria.where("injectorProfileId").is(
						injectProfileId)), TradeReport.class);

		assertNotNull(tradeReport);
		assertEquals(1000, tradeReport.getCurrentTradeProgress());

		System.out.println("trade report party size is "
				+ tradeReport.getParties().size());
		System.out.println("trade report instrument size is "
				+ tradeReport.getInstruments().size());

		System.out.println("finished report generation "
				+ new Date(System.currentTimeMillis()));

	}

	@Test
	public void testWithLimitedInstrumentsParties() {
		// create 100 clients
		IMap<String, Party> partyMap = hzInstance.getMap("party");
		assertNotNull(partyMap);

		assertEquals(0, partyMap.size());

		GeneratePartyCache cacheGenerator = new GeneratePartyCache();
		cacheGenerator.populateMap(100, partyMap);

		partyMap = hzInstance.getMap("party");
		assertEquals(100, partyMap.size());

		// now get only limited set 10

	}

	private void convertToReportAndSaveForProfile(Trade ack, String username,
			String injectorProfileId) throws Exception {

		TradeReport tradeReport = coreTemplate.findOne(
				Query.query(Criteria.where("injectorProfileId").is(
						injectorProfileId)), TradeReport.class);

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

			List<PartyReport> modifiedParties = parties.stream()
					.filter(a -> a.getName().equals(ack.getClientId()))
					.map(a -> a.incrementCountByOne())
					.collect(Collectors.toList());
			List<PartyReport> nonModifiedParties = parties.stream()
					.filter(a -> !a.getName().equals(ack.getClientId()))
					.collect(Collectors.toList());

			if (modifiedParties.size() == 0) {
				// add the new Party in
				PartyReport newParty = new PartyReport();
				newParty.setCurrentTradeCount(1);
				newParty.setId(ack.getClientId());
				newParty.setName(ack.getClientId());
				modifiedParties.add(newParty);
			}

			parties = Stream.concat(modifiedParties.stream(),
					nonModifiedParties.stream()).collect(Collectors.toList());

			// now do the same for the instruments
			List<InstrumentReport> modifiedInstruments = instruments.stream()
					.filter(a -> a.getId().equals(ack.getInstrumentId()))
					.map(a -> a.incrementCountByOne())
					.collect(Collectors.toList());
			List<InstrumentReport> nonModifiedInstruments = instruments
					.stream()
					.filter(a -> !a.getId().equals(ack.getInstrumentId()))
					.collect(Collectors.toList());

			if (modifiedInstruments.size() == 0) {

				InstrumentReport newInstrument = new InstrumentReport();
				newInstrument.setId(ack.getInstrumentId());
				newInstrument.setName(ack.getInstrumentId());
				newInstrument.setCurrentTradeCount(1);
				modifiedInstruments.add(newInstrument);

			}

			// finally concat the list
			instruments = Stream.concat(modifiedInstruments.stream(),
					nonModifiedInstruments.stream()).collect(
					Collectors.toList());

			tradeReport.setParties(parties);
			tradeReport.setInstruments(instruments);

		}

		reportRepo.save(tradeReport);

	}

	@Test
	public void testManyClients() {

		System.out.println("Runnning many clients...");

		IMap<String, Party> map = hzInstance.getMap("party");
		assertNotNull(map);

		assertEquals(0, map.size());

		GeneratePartyCache cacheGenerator = new GeneratePartyCache();
		cacheGenerator.populateMap(1000, map);

		map = hzInstance.getMap("party");
		assertEquals(1000, map.size());

		System.out.println("Finished");

	}
	
	@Test
	public void testJetPrices(){
		
		System.out.println("Runnning many instruments...");

		IMap<String, Instrument> mapInstruments = hzInstance.getMap("instrument");
		assertNotNull(mapInstruments);

		assertEquals(0, mapInstruments.size());

		GenerateInstrumentCache cacheGeneratorInstruments = new GenerateInstrumentCache();
		cacheGeneratorInstruments.populateMap(1000, mapInstruments);

		mapInstruments = hzInstance.getMap("instrument");
		assertEquals(1000, mapInstruments.size());
		
		//JetInstance jet = Jet.newJetInstance();
        //Jet.newJetInstance();

		
		//DAG dag = new DAG();
		//Vertex source = dag.newVertex("source", Processors.readMap("instrument"));
		
		//Create a vertex to populate the price
		//Vertex priceConverter = dag.newVertex("priceConverter", Processors.flatMap((Entry<String, Instrument> e)->
		//Traversers.traverseArray(GeneratePriceData.generateRandomDataOnInstruments(e.getValue())))
        //);
		
		//finally create the vertex to store the prices in map
		//Vertex sink = dag.newVertex("sink", Processors.writeMap("price"));
		
		//Jet.shutdownAll();

		
	}

	@Test
	public void testManyClientsAndIncrease() {

		System.out.println("Runnning many clients and more...");

		IMap<String, Party> map = hzInstance.getMap("party");
		assertNotNull(map);

		assertEquals(0, map.size());

		GeneratePartyCache cacheGenerator = new GeneratePartyCache();
		cacheGenerator.populateMap(1000, map);

		map = hzInstance.getMap("party");
		assertEquals(1000, map.size());

		// now increase to 1500
		cacheGenerator.populateMap(1500, map);
		assertEquals(1500, map.size());

		System.out.println("Finished");

	}

	@Test
	public void generateInstruments() {

		// instrument
		System.out.println("Runnning many instruments...");

		IMap<String, Instrument> map = hzInstance.getMap("instrument");
		assertNotNull(map);

		assertEquals(0, map.size());

		GenerateInstrumentCache cacheGenerator = new GenerateInstrumentCache();
		cacheGenerator.populateMap(1000, map);

		map = hzInstance.getMap("instrument");
		assertEquals(1000, map.size());

	}

	@After
	public void removeCache() {

		IMap<String, Trade> mapTrade = hzInstance.getMap("trade");
		assertNotNull(mapTrade);

		Predicate predicateTrade = new SqlPredicate(String.format(
				"executionVenueId = %s", "EX1"));

		Set<String> tradeId = (Set<String>) mapTrade.keySet(predicateTrade);
		for (String key : tradeId) {
			mapTrade.remove(key);
		}

		mapTrade = hzInstance.getMap("trade");
		assertEquals(0, mapTrade.size());

		// now delete Instruments
		IMap<String, Instrument> mapInstruments = hzInstance
				.getMap("instrument");
		assertNotNull(mapInstruments);

		Predicate predicate = new SqlPredicate(String.format("symbol like %s",
				"ISIN_INJ_%"));
		Set<String> ins = (Set<String>) mapInstruments.keySet(predicate);

		for (String aIns : ins) {
			mapInstruments.remove(aIns);
		}

		mapInstruments = hzInstance.getMap("instrument");
		assertEquals(0, mapInstruments.size());

		// now delete parties
		IMap<String, Instrument> parties = hzInstance.getMap("party");
		assertNotNull(parties);

		Predicate predicateParty = new SqlPredicate(String.format(
				"name like %s", "TRDINJECT_CLI_%"));
		Set<String> partiesKey = (Set<String>) parties.keySet(predicateParty);

		for (String aParty : partiesKey) {
			parties.remove(aParty);
		}

		parties = hzInstance.getMap("party");
		assertEquals(0, parties.size());

		TradeReport tradeReport = coreTemplate.findOne(
				Query.query(Criteria.where("injectorProfileId").is(
						injectProfileId)), TradeReport.class);

		if (tradeReport != null)
			reportRepo.delete(tradeReport);

		
		//

		
		System.out.println("Finished");
		
		

	}

}
