package com.trade.injector.jto.repository;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.trade.injector.controller.TradeInjectorController;
import com.trade.injector.enums.ExerciseStyle;
import com.trade.injector.enums.PartyRole;
import com.trade.injector.enums.SecurityType;
import com.trade.injector.enums.SettlementMethod;
import com.trade.injector.jto.Instrument;
import com.trade.injector.jto.Party;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradeInjectorController.class)
public class InstrumentTest {

	@Autowired
	InstrumentRepository repo;
	
	@Autowired
	PartyRepository partyRepo;
	
	@Before
	public void createSingleInstrument(){
		
		Party exchange = new Party();
		exchange.setPartyId("testExchange1");
		exchange.setRole(PartyRole.EXCHANGE);
		partyRepo.save(exchange);
		
		Instrument ins = new Instrument();
		ins.setExchange(exchange.id);
		ins.setIdenfitifier("INSTEST1");
		ins.setMMY("072017");
		ins.setExerciseStyle(ExerciseStyle.AMERICAN);
		ins.setSecurityType(SecurityType.FUT);
		ins.setSettMethod(SettlementMethod.PHYSICAL);
		
		repo.save(ins);
	}
	
	@Test
	public void checkSingleExists(){
		
		assertEquals(1, repo.findAll().size());
		
	}
	
	@Test
	public void checkUpdateOfPartyInInstrument(){
		Party test1party = partyRepo.findByPartyId("testExchange1");
		test1party.setPartyName("This is exchange for testing");
		partyRepo.save(test1party);
		
		assertEquals("This is exchange for testing", partyRepo.findByPartyId("testExchange1").getPartyName());
		
		//now check if the instrument has the above update
		Instrument testInstrument = repo.findByIdentifier("INSTEST1");
		assertEquals("This is exchange for testing", partyRepo.findById(testInstrument.getExchange()).get().getPartyName());
		
	}
	
	@Test
	public void updateOfInstrument(){
		
	}
	
	@After
	public void cleanUpAllInstruments(){
		
		Party test1party = partyRepo.findByPartyId("testExchange1");
		
		if(test1party !=null)
			partyRepo.delete(test1party);
		
		Instrument testInstrument = repo.findByIdentifier("INSTEST1");
		
		
		if(testInstrument != null)
			repo.delete(testInstrument);
	}
}
