package com.trade.injector.jto.repository;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;
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
import com.trade.injector.jto.InstrumentReport;
import com.trade.injector.jto.PartyReport;
import com.trade.injector.jto.TradeReport;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradeInjectorController.class)
public class TradeReportRepositoryTest {

	@Autowired
	public TradeReportRepository repo;

	@Autowired
	private MongoTemplate coreTemplate;

	@Before
	public void setUpData() {
		PartyReport party1 = new PartyReport();
		party1.setId("party1");
		party1.setName("party1");
		party1.setCurrentTradeCount(1);
		List<PartyReport> partyList = new ArrayList<PartyReport>();
		partyList.add(party1);

		InstrumentReport instrument1 = new InstrumentReport();
		instrument1.setId("ins1");
		instrument1.setName("ins1");
		instrument1.setCurrentTradeCount(1);
		List<InstrumentReport> instruments = new ArrayList<InstrumentReport>();
		instruments.add(instrument1);

		TradeReport report = new TradeReport();
		report.setName("ABCD");
		report.setCurrentTradeProgress(1);
		report.setInjectorMessageId("ABCD");
		report.setInjectorProfileId("ABCD");
		report.setInstruments(instruments);
		report.setParties(partyList);
		report.setReportDate(new Date(System.currentTimeMillis()));
		report.setTradeCount(1);
		report.setUserId("dinesh pillai");

		repo.save(report);
	}

	@Test
	public void checkForOneExistence() {

		BasicQuery query1 = new BasicQuery("{ name : 'ABCD'}");

		TradeReport report = coreTemplate.findOne(query1, TradeReport.class);

		assertNotNull(report);

	}

	@Test
	public void addParty() {

		BasicQuery query1 = new BasicQuery("{ name : 'ABCD'}");

		TradeReport report = coreTemplate.findOne(query1, TradeReport.class);

		assertNotNull(report);

		PartyReport report2 = new PartyReport();
		report2.setId("party2");
		report2.setName("party2");
		report2.setCurrentTradeCount(2);
		report.getParties().add(report2);

		repo.save(report);

		TradeReport report3 = coreTemplate.findOne(query1, TradeReport.class);

		assertEquals(2, report3.getParties().size());

	}

	@Test
	public void addInstrument() {

		BasicQuery query1 = new BasicQuery("{ name : 'ABCD'}");

		TradeReport report = coreTemplate.findOne(query1, TradeReport.class);

		assertNotNull(report);

		InstrumentReport report2 = new InstrumentReport();
		report2.setId("ins2");
		report2.setName("ins2");
		report2.setCurrentTradeCount(2);
		report.getInstruments().add(report2);

		repo.save(report);

		TradeReport report3 = coreTemplate.findOne(query1, TradeReport.class);

		assertEquals(2, report3.getInstruments().size());

	}
	
	//this test is purely to clean the db when required and not meant to be run regularly
	
	@Test
	public void deleteAll(){
		repo.deleteAll();
	}

	

	@After
	public void tearDown() {

		BasicQuery query1 = new BasicQuery("{ name : 'ABCD'}");

		TradeReport report = coreTemplate.findOne(query1, TradeReport.class);
		if (report != null)
			repo.delete(report);

	}

}
