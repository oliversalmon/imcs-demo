package com.trade.injector.jto.repository;

import static org.junit.Assert.*;

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
import com.trade.injector.enums.PartyRole;
import com.trade.injector.jto.Party;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradeInjectorController.class)
public class PartyTest {

	@Autowired
	PartyRepository repo;

	@Autowired
	private MongoTemplate coreTemplate;

	@Before
	public void createSingleClient() {
		Party newParty = new Party();
		newParty.setAccountNumber("TEST01");
		newParty.setPartyId("PARTY01");
		newParty.setPartyName("CLIENT01");
		newParty.setRole(PartyRole.CLIENTID);

		repo.save(newParty);

		assertEquals(1, repo.findAll().size());

	}

	@Test
	public void singleClient() {

		assertEquals(1, repo.findAll().size());

	}

	@Test
	public void updateTestUser() {

		Party test1party = repo.findByPartyId("PARTY01");
		test1party.setPartyName("CLIENT02");

		repo.save(test1party);

		assertEquals(1, repo.findAll().size());

		assertEquals("PARTY01", repo.findByPartyId("PARTY01").getPartyId());

		System.out.println("Party role is " + test1party.getRole());

	}

	@Test
	public void createImmediateParent() {

		Party immediateParent = repo.findByPartyId("PARTY01");

		Party executingBroker = new Party();
		executingBroker.setAccountNumber("EX1");
		executingBroker.setPartyId("TESTEXECID1");
		executingBroker.setRole(PartyRole.EXECUTINGFIRM);
		executingBroker.setImmediateParent(immediateParent.id);

		repo.save(executingBroker);

		assertEquals(2, repo.findAll().size());

		Party test2party = repo.findByPartyId("TESTEXECID1");

		assertNotNull(test2party);
		assertNotNull(test2party.getImmediateParent());

	}

	@Test
	public void testImmediateParentSearch() {

		Party immediateParent = repo.findByPartyId("PARTY01");

		Party executingBroker = new Party();
		executingBroker.setAccountNumber("EX1");
		executingBroker.setPartyId("TESTEXECID1");
		executingBroker.setRole(PartyRole.EXECUTINGFIRM);
		executingBroker.setImmediateParent(immediateParent.id);

		repo.save(executingBroker);

		assertEquals(2, repo.findAll().size());

		BasicQuery query1 = new BasicQuery(
				"{ partyId : 'TESTEXECID1', role : 'EXECUTINGFIRM' }");
		Party userTest1 = coreTemplate.findOne(query1, Party.class);

		assertNotNull(userTest1);

	}

	@After
	public void deleteData() {
		Party test1party = repo.findByPartyId("PARTY01");
		Party test2party = repo.findByPartyId("TESTEXECID1");

		if (test1party != null)
			repo.delete(test1party);

		if (test2party != null)
			repo.delete(test2party);

	}

}
