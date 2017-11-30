package com.trade.injector.jto.repository;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.util.DBObjectUtils;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.trade.injector.application.Application;
import com.trade.injector.controller.TradeInjectorController;
import com.trade.injector.jto.TradeInjectorMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradeInjectorController.class)
public class TradeMessageInjectorMessageTest {

	@Autowired
	private MongoDBTemplate template;

	@Autowired
	private TradeInjectorMessageRepository repo;

	@Before
	public void testData() {

		System.out.println("Preparing Test data");
		TradeInjectorMessage message = new TradeInjectorMessage();
		DBObject dbObject = createDBObject(message);
		DB db = (DB) template.getDbFactory().getDb();
		DBCollection coll = db.createCollection("tradeInjector", dbObject);

		// if there is anything in the repository please delete it
		repo.deleteAll();

	}

	private static DBObject createDBObject(TradeInjectorMessage message) {
		BasicDBObjectBuilder docBuilder = BasicDBObjectBuilder.start();

		docBuilder.append("_id", message.getUserId());
		docBuilder.append("noOfClients", message.getNoOfClients());
		docBuilder.append("noOfTrades", message.getNoOfTrades());
		docBuilder.append("noOfInstruments", message.getNoOfInstruments());
		return docBuilder.get();
	}

	@Test
	public void testIfCollectionExists() {
		DB db = (DB) template.getDbFactory().getDb();
		DBCollection coll = db.getCollection("tradeInjector");

		assertNotNull(coll);

	}

	@Test
	public void testInsertion() {
		DB db = (DB) template.getDbFactory().getDb();
		DBCollection coll = db.getCollection("tradeInjector");

		// create one new document
		TradeInjectorMessage message = new TradeInjectorMessage();
		message.setNoOfClients("10");
		message.setNoOfInstruments("10");
		message.setNoOfTrades("1000");
		message.setTradeDate(new Date(System.currentTimeMillis()).toString());
		message.setTimeDelay("1000");
		message.setUserId("testUser");
		BasicDBObject documentDetail = new BasicDBObject();
		// documentDetail.put("id", message);
		documentDetail.put("userId", message.getUserId());
		documentDetail.put("noOfClients", message.getNoOfClients());
		documentDetail.put("noOfInstruments", message.getNoOfInstruments());
		documentDetail.put("noOfTrades", message.getNoOfTrades());
		documentDetail.put("tradeDate", message.getTradeDate());
		documentDetail.put("timeDelay", message.getTimeDelay());

		coll.insert(documentDetail);

		DBCursor cursorDoc = coll.find();
		assertEquals(1, cursorDoc.count());

		while (cursorDoc.hasNext()) {
			DBObject document = cursorDoc.next();
			System.out.println(document.toString());
		}

	}

	@Test
	public void testDelete() {
		DB db = (DB) template.getDbFactory().getDb();
		DBCollection coll = db.getCollection("tradeInjector");

		// create one new document
		TradeInjectorMessage message = new TradeInjectorMessage();
		message.setNoOfClients("10");
		message.setNoOfInstruments("10");
		message.setNoOfTrades("1000");
		message.setTradeDate(new Date(System.currentTimeMillis()).toString());
		message.setTimeDelay("1000");
		message.setUserId("testUser");
		BasicDBObject documentDetail = new BasicDBObject();
		// documentDetail.put("id", message);
		documentDetail.put("userId", message.getUserId());
		documentDetail.put("noOfClients", message.getNoOfClients());
		documentDetail.put("noOfInstruments", message.getNoOfInstruments());
		documentDetail.put("noOfTrades", message.getNoOfTrades());
		documentDetail.put("tradeDate", message.getTradeDate());
		documentDetail.put("timeDelay", message.getTimeDelay());

		coll.insert(documentDetail);

		DBCursor cursorDoc = coll.find();
		assertEquals(1, cursorDoc.count());

		while (cursorDoc.hasNext()) {
			DBObject document = cursorDoc.next();
			System.out.println(document.toString());

		}

		// now delete the data
		DBCursor cursorDocForDelete = coll.find();
		while (cursorDocForDelete.hasNext()) {
			DBObject document = cursorDocForDelete.next();
			System.out.println(document.toString());
			coll.remove(document);

		}
		assertEquals(0, cursorDocForDelete.count());

	}

	@Test
	public void testReadAll() {
		DB db = (DB) template.getDbFactory().getDb();
		DBCollection coll = db.getCollection("tradeInjector");
		assertEquals(0, coll.count());

		// insert 2 and read all back
		TradeInjectorMessage message = new TradeInjectorMessage();
		message.setNoOfClients("10");
		message.setNoOfInstruments("10");
		message.setNoOfTrades("1000");
		message.setTradeDate(new Date(System.currentTimeMillis()).toString());
		message.setTimeDelay("1000");
		message.setUserId("testUser");
		BasicDBObject documentDetail = new BasicDBObject();
		// documentDetail.put("id", message);
		documentDetail.put("userId", message.getUserId());
		documentDetail.put("noOfClients", message.getNoOfClients());
		documentDetail.put("noOfInstruments", message.getNoOfInstruments());
		documentDetail.put("noOfTrades", message.getNoOfTrades());
		documentDetail.put("tradeDate", message.getTradeDate());
		documentDetail.put("timeDelay", message.getTimeDelay());

		coll.insert(documentDetail);

		TradeInjectorMessage message2 = new TradeInjectorMessage();
		message2.setNoOfClients("20");
		message2.setNoOfInstruments("20");
		message2.setNoOfTrades("2000");
		message2.setTradeDate(new Date(System.currentTimeMillis()).toString());
		message2.setTimeDelay("2000");
		message2.setUserId("testUser");
		BasicDBObject documentDetail2 = new BasicDBObject();
		// documentDetail.put("id", message);
		documentDetail2.put("userId", message2.getUserId());
		documentDetail2.put("noOfClients", message2.getNoOfClients());
		documentDetail2.put("noOfInstruments", message2.getNoOfInstruments());
		documentDetail2.put("noOfTrades", message2.getNoOfTrades());
		documentDetail2.put("tradeDate", message2.getTradeDate());
		documentDetail2.put("timeDelay", message2.getTimeDelay());

		coll.insert(documentDetail2);

		assertEquals(2, coll.find().count());

	}

	@Test
	public void testReadOne() {

		DB db = (DB) template.getDbFactory().getDb();
		DBCollection coll = db.getCollection("tradeInjector");
		assertEquals(0, coll.count());

		// insert 2 and read all back
		TradeInjectorMessage message = new TradeInjectorMessage();
		message.setNoOfClients("10");
		message.setNoOfInstruments("10");
		message.setNoOfTrades("1000");
		message.setTradeDate(new Date(System.currentTimeMillis()).toString());
		message.setTimeDelay("1000");
		message.setUserId("testUser");
		BasicDBObject documentDetail = new BasicDBObject();
		// documentDetail.put("id", message);
		documentDetail.put("userId", message.getUserId());
		documentDetail.put("noOfClients", message.getNoOfClients());
		documentDetail.put("noOfInstruments", message.getNoOfInstruments());
		documentDetail.put("noOfTrades", message.getNoOfTrades());
		documentDetail.put("tradeDate", message.getTradeDate());
		documentDetail.put("timeDelay", message.getTimeDelay());

		coll.insert(documentDetail);

		TradeInjectorMessage message2 = new TradeInjectorMessage();
		message2.setNoOfClients("20");
		message2.setNoOfInstruments("20");
		message2.setNoOfTrades("2000");
		message2.setTradeDate(new Date(System.currentTimeMillis()).toString());
		message2.setTimeDelay("2000");
		message2.setUserId("testUser");
		BasicDBObject documentDetail2 = new BasicDBObject();
		// documentDetail.put("id", message);
		documentDetail2.put("userId", message2.getUserId());
		documentDetail2.put("noOfClients", message2.getNoOfClients());
		documentDetail2.put("noOfInstruments", message2.getNoOfInstruments());
		documentDetail2.put("noOfTrades", message2.getNoOfTrades());
		documentDetail2.put("tradeDate", message2.getTradeDate());
		documentDetail2.put("timeDelay", message2.getTimeDelay());

		coll.insert(documentDetail2);

		assertEquals(2, coll.find().count());

		// now read first one
		DBObject firstDocument = coll.findOne();
		assertEquals(firstDocument.get("noOfTrades"), "1000");

	}

	@Test
	public void testFindByUserId() {
		DB db = (DB) template.getDbFactory().getDb();
		DBCollection coll = db.getCollection("tradeInjector");
		assertEquals(0, coll.count());

		// insert 2 and read all back
		TradeInjectorMessage message = new TradeInjectorMessage();
		message.setNoOfClients("10");
		message.setNoOfInstruments("10");
		message.setNoOfTrades("1000");
		message.setTradeDate(new Date(System.currentTimeMillis()).toString());
		message.setTimeDelay("1000");
		message.setUserId("testUser");
		BasicDBObject documentDetail = new BasicDBObject();
		// documentDetail.put("id", message);
		documentDetail.put("userId", message.getUserId());
		documentDetail.put("noOfClients", message.getNoOfClients());
		documentDetail.put("noOfInstruments", message.getNoOfInstruments());
		documentDetail.put("noOfTrades", message.getNoOfTrades());
		documentDetail.put("tradeDate", message.getTradeDate());
		documentDetail.put("timeDelay", message.getTimeDelay());

		coll.insert(documentDetail);

		BasicDBObject query = new BasicDBObject("userId", "testUser");
		DBCursor cursor = coll.find(query);

		assertEquals(1, cursor.count());

	}

	@Test
	public void testUpdateOne() {

		DB db = (DB) template.getDbFactory().getDb();
		DBCollection coll = db.getCollection("tradeInjector");
		assertEquals(0, coll.count());

		// insert 2 and read all back
		TradeInjectorMessage message = new TradeInjectorMessage();
		message.setNoOfClients("10");
		message.setNoOfInstruments("10");
		message.setNoOfTrades("1000");
		message.setTradeDate(new Date(System.currentTimeMillis()).toString());
		message.setTimeDelay("1000");
		message.setUserId("testUser");
		BasicDBObject documentDetail = new BasicDBObject();
		// documentDetail.put("id", message);
		documentDetail.put("userId", message.getUserId());
		documentDetail.put("noOfClients", message.getNoOfClients());
		documentDetail.put("noOfInstruments", message.getNoOfInstruments());
		documentDetail.put("noOfTrades", message.getNoOfTrades());
		documentDetail.put("tradeDate", message.getTradeDate());
		documentDetail.put("timeDelay", message.getTimeDelay());

		coll.insert(documentDetail);

		BasicDBObject query = new BasicDBObject("userId", "testUser");
		DBCursor cursor = coll.find(query);

		assertEquals(1, cursor.count());

		while (cursor.hasNext()) {
			DBObject document = cursor.next();
			System.out.println("Before update " + document.toString());

		}

		// now update test user to updated user
		message.setUserId("updatedUser");

		BasicDBObject document = new BasicDBObject();
		// documentDetail.put("id", message);
		document.put("userId", message.getUserId());
		document.put("noOfClients", message.getNoOfClients());
		document.put("noOfInstruments", message.getNoOfInstruments());
		document.put("noOfTrades", message.getNoOfTrades());
		document.put("tradeDate", message.getTradeDate());
		document.put("timeDelay", message.getTimeDelay());

		coll.update(query, document);

		BasicDBObject queryUpdted = new BasicDBObject("userId", "updatedUser");
		DBCursor cursorUpdated = coll.find(queryUpdted);

		assertEquals(1, cursorUpdated.count());

		// now update test user to updated user
		while (cursorUpdated.hasNext()) {
			DBObject documentUpdated = cursorUpdated.next();
			System.out.println("After update " + documentUpdated.toString());

		}

		// finally iterate through the whole collection and check the results
		DBCursor finalresult = coll.find();
		assertEquals(1, finalresult.count());

		while (finalresult.hasNext()) {
			DBObject documentUpdated = finalresult.next();
			System.out.println("After update " + documentUpdated.toString());

		}

	}

	@Test
	public void testTradeInjectRepositorySave() {

		TradeInjectorMessage message = new TradeInjectorMessage();
		message.setNoOfClients("10");
		message.setNoOfInstruments("10");
		message.setNoOfTrades("1000");
		message.setTradeDate(new Date(System.currentTimeMillis()).toString());
		message.setTimeDelay("1000");
		message.setUserId("testRepoUser");

		repo.save(message);

		assertEquals(1, repo.count());

	}

	@Test
	public void testTradeInjectRepositoryFindByUserId() {

		// first create it
		TradeInjectorMessage message = new TradeInjectorMessage();
		message.setNoOfClients("10");
		message.setNoOfInstruments("10");
		message.setNoOfTrades("1000");
		message.setTradeDate(new Date(System.currentTimeMillis()).toString());
		message.setTimeDelay("1000");
		message.setUserId("testRepoUser");

		repo.save(message);

		// now find it with the same user

		assertEquals("testRepoUser", repo.findByUserId("testRepoUser")
				.getUserId());

	}

	@Test
	public void testTradeInjectRepositoryUpdate() {

		// first create it
		TradeInjectorMessage message = new TradeInjectorMessage();
		message.setNoOfClients("10");
		message.setNoOfInstruments("10");
		message.setNoOfTrades("1000");
		message.setTradeDate(new Date(System.currentTimeMillis()).toString());
		message.setTimeDelay("1000");
		message.setUserId("testRepoUser");

		repo.save(message);

		// now update it with a different user
		TradeInjectorMessage messageToUpdate = repo
				.findByUserId("testRepoUser");
		System.out.println("Before update " + messageToUpdate.toString());

		messageToUpdate.setUserId("testRepoUserAfterUpdate");
		repo.save(messageToUpdate);

		// make sure there is only one

		assertEquals(1, repo.count());

		// check if the update worked
		assertEquals("testRepoUserAfterUpdate",
				repo.findByUserId("testRepoUserAfterUpdate").getUserId());

	}

	@Test
	public void testFindById() throws Exception {
		// first create it
		TradeInjectorMessage message = new TradeInjectorMessage();
		message.setNoOfClients("10");
		message.setNoOfInstruments("10");
		message.setNoOfTrades("1000");
		message.setTradeDate(new Date(System.currentTimeMillis()).toString());
		message.setTimeDelay("1000");
		message.setUserId("testRepoUser");

		TradeInjectorMessage messageSaved = repo.save(message);
		assertEquals(messageSaved.id, repo.findById(messageSaved.id).get().id);
	}

	@After
	public void deleteAll() {
		System.out.println("Destroying data");

		DB db = (DB) template.getDbFactory().getDb();
		DBCollection coll = db.getCollection("tradeInjector");
		// drop the entire collection

		if (coll != null)
			coll.drop();

	}

}
