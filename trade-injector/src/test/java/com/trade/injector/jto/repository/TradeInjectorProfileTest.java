package com.trade.injector.jto.repository;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.test.context.junit4.SpringRunner;

import com.trade.injector.controller.TradeInjectorController;
import com.trade.injector.enums.ExerciseStyle;
import com.trade.injector.enums.PartyRole;
import com.trade.injector.enums.SecurityType;
import com.trade.injector.enums.SettlementMethod;
import com.trade.injector.jto.Instrument;
import com.trade.injector.jto.Party;
import com.trade.injector.jto.TradeInjectorProfile;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradeInjectorController.class)
public class TradeInjectorProfileTest {

	@Autowired
	TradeInjectorProfileRepository repo;

	@Autowired
	PartyRepository partyRepo;

	@Autowired
	InstrumentRepository insRepo;

	@Autowired
	private MongoTemplate coreTemplate;

	@Before
	public void setUpPartyInstrumentInjectorProfile() {

		TradeInjectorProfile testProfile = new TradeInjectorProfile();
		testProfile.setName("testProfile");
		testProfile.setNumberOfParties(10);
		testProfile.setNumberOfInstruments(10);
		testProfile.setMaxPxRange(200.00);
		testProfile.setMaxQtyRange(200);
		testProfile.setMinPxRange(50.00);
		testProfile.setMinQtyRange(10);
		testProfile.setNumberOfTrades(1000);
		testProfile.setSimulatedWaitTime(1000);
		testProfile.setThreads(5);
		testProfile.setTradeDate(new Date(System.currentTimeMillis()));

		repo.save(testProfile);

		Party newParty = new Party();
		newParty.setAccountNumber("TEST01");
		newParty.setPartyId("PARTY01");
		newParty.setPartyName("CLIENT01");
		newParty.setRole(PartyRole.CLIENTID);
		newParty.setApportionment(100);

		partyRepo.save(newParty);

		Instrument ins = new Instrument();
		ins.setIdenfitifier("INSTEST1");
		ins.setMMY("072017");
		ins.setExerciseStyle(ExerciseStyle.AMERICAN);
		ins.setSecurityType(SecurityType.FUT);
		ins.setSettMethod(SettlementMethod.PHYSICAL);
		ins.setApportionment(100);

		insRepo.save(ins);

	}

	@Test
	public void testSingleInstanceOfProfile() {

		BasicQuery query = new BasicQuery("{ name : 'testProfile'}");
		TradeInjectorProfile profile = coreTemplate.findOne(query,
				TradeInjectorProfile.class);
		assertNotNull(profile);

		BasicQuery query1 = new BasicQuery(
				"{ partyId : 'PARTY01', role : 'CLIENTID' }");
		Party userTest1 = coreTemplate.findOne(query1, Party.class);

		assertNotNull(userTest1);

		BasicQuery query2 = new BasicQuery("{ identifier : 'INSTEST1'}");
		Instrument insTest1 = coreTemplate.findOne(query2, Instrument.class);

		assertNotNull(insTest1);

	}

	@Test
	public void testAddingNewParty() {

		BasicQuery query = new BasicQuery("{ name : 'testProfile'}");
		TradeInjectorProfile profile = coreTemplate.findOne(query,
				TradeInjectorProfile.class);
		assertNotNull(profile);

		BasicQuery query1 = new BasicQuery(
				"{ partyId : 'PARTY01', role : 'CLIENTID' }");
		Party userTest1 = coreTemplate.findOne(query1, Party.class);
		assertNotNull(userTest1);

		List<String> parties = new ArrayList<String>();
		parties.add(userTest1.id);

		profile.setParties(parties);
		repo.save(profile);

		// now check if the update has the link to party
		BasicQuery query2 = new BasicQuery("{ name : 'testProfile'}");
		TradeInjectorProfile profile2 = coreTemplate.findOne(query2,
				TradeInjectorProfile.class);
		assertNotNull(profile2);

		assertEquals(1, profile2.getParties().size());

		// also check if the id matches
		assertEquals(userTest1.id, profile2.getParties().get(0));

	}

	@Test
	public void testRemovingParty() {
		
		BasicQuery query = new BasicQuery("{ name : 'testProfile'}");
		TradeInjectorProfile profile = coreTemplate.findOne(query,
				TradeInjectorProfile.class);
		assertNotNull(profile);

		BasicQuery query1 = new BasicQuery(
				"{ partyId : 'PARTY01', role : 'CLIENTID' }");
		Party userTest1 = coreTemplate.findOne(query1, Party.class);
		assertNotNull(userTest1);

		List<String> parties = new ArrayList<String>();
		parties.add(userTest1.id);

		profile.setParties(parties);
		repo.save(profile);
		
		BasicQuery query2 = new BasicQuery("{ name : 'testProfile'}");
		TradeInjectorProfile profile2 = coreTemplate.findOne(query2,
				TradeInjectorProfile.class);
		assertNotNull(profile2);
		
		//now remove the Party
		profile2.getParties().remove(0);
		repo.save(profile2);
		
		BasicQuery query3 = new BasicQuery("{ name : 'testProfile'}");
		TradeInjectorProfile profile3 = coreTemplate.findOne(query3,
				TradeInjectorProfile.class);
		assertNotNull(profile3);
		assertEquals(0, profile3.getParties().size());
		

	}

	@Test
	public void testAddingInstrument() {
		
		BasicQuery query = new BasicQuery("{ name : 'testProfile'}");
		TradeInjectorProfile profile = coreTemplate.findOne(query,
				TradeInjectorProfile.class);
		assertNotNull(profile);
		
		BasicQuery query1 = new BasicQuery("{ identifier : 'INSTEST1'}");
		Instrument insTest1 = coreTemplate.findOne(query1, Instrument.class);
		assertNotNull(insTest1);
		
		List<String> instruments = new ArrayList<String>();
		instruments.add(insTest1.id);
		profile.setInstruments(instruments);

		repo.save(profile);
		
		
		TradeInjectorProfile profile2 = coreTemplate.findOne(query,
				TradeInjectorProfile.class);
		assertNotNull(profile2);
		
		assertEquals(1, profile2.getInstruments().size());

		// also check if the id matches
		assertEquals(insTest1.id, profile2.getInstruments().get(0));


	}

	@Test
	public void removingInstrument() {
		
		BasicQuery query = new BasicQuery("{ name : 'testProfile'}");
		TradeInjectorProfile profile = coreTemplate.findOne(query,
				TradeInjectorProfile.class);
		assertNotNull(profile);

		BasicQuery query1 = new BasicQuery("{ identifier : 'INSTEST1'}");
		Instrument insTest1 = coreTemplate.findOne(query1, Instrument.class);
		assertNotNull(insTest1);
		

		List<String> instruments = new ArrayList<String>();
		instruments.add(insTest1.id);

		profile.setInstruments(instruments);
		repo.save(profile);
		
		
		TradeInjectorProfile profile2 = coreTemplate.findOne(query,
				TradeInjectorProfile.class);
		assertNotNull(profile2);
		
		//now remove the Party
		profile2.getInstruments().remove(0);
		repo.save(profile2);
		
		
		TradeInjectorProfile profile3 = coreTemplate.findOne(query,
				TradeInjectorProfile.class);
		assertNotNull(profile3);
		assertEquals(0, profile3.getInstruments().size());


	}

	@After
	public void destroyTestData() {

		TradeInjectorProfile testProfile = repo.findByName("testProfile");
		Party test1party = partyRepo.findByPartyId("PARTY01");

		Instrument testInstrument = insRepo.findByIdentifier("INSTEST1");

		if (testInstrument != null)
			insRepo.delete(testInstrument);

		if (testProfile != null)
			repo.delete(testProfile);

		if (test1party != null)
			partyRepo.delete(test1party);

	}

}
