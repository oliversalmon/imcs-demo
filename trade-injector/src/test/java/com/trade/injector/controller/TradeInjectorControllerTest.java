package com.trade.injector.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.example.mu.domain.Instrument;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.trade.injector.application.Application;
import com.trade.injector.business.service.BusinessServiceCacheNames;
import com.trade.injector.jto.TradeInjectorMessage;
import com.trade.injector.jto.TradeInjectorProfile;
import com.trade.injector.jto.repository.TradeInjectorProfileRepository;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TradeInjectorController.class)
@WebAppConfiguration
@TestPropertySource(properties = {"kafka.bootstrap-servers=138.68.168.237:9092", "spring.data.mongodb.host=localhost"})
public class TradeInjectorControllerTest {

	private MediaType contentType = new MediaType(
			MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;
	private HttpMessageConverter mappingJackson2HttpMessageConverter;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private MongoTemplate coreTemplate;

	@Autowired
	TradeInjectorProfileRepository repo;

	@Autowired
	private HazelcastInstance hazelcastInstance; 
	
	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

		this.mappingJackson2HttpMessageConverter = Arrays
				.asList(converters)
				.stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
				.findAny().orElse(null);

		assertNotNull("the JSON message converter must not be null",
				this.mappingJackson2HttpMessageConverter);
	}

	@Test
	public void dummyTest() {
	}

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();

		BasicQuery query = new BasicQuery("{ name : 'testProfile'}");
		TradeInjectorProfile profile = coreTemplate.findOne(query,
				TradeInjectorProfile.class);

		if (profile != null)
			repo.delete(profile);

		/*
		 * this.bookmarkRepository.deleteAllInBatch();
		 * this.accountRepository.deleteAllInBatch();
		 * 
		 * this.account = accountRepository.save(new Account(userName,
		 * "password")); this.bookmarkList.add(bookmarkRepository.save(new
		 * Bookmark(account, "http://bookmark.com/1/" + userName,
		 * "A description"))); this.bookmarkList.add(bookmarkRepository.save(new
		 * Bookmark(account, "http://bookmark.com/2/" + userName,
		 * "A description")));
		 */
	}

	@Test
	public void testMessageInjectTrigger() throws Exception {
		TradeInjectorMessage msg = new TradeInjectorMessage();
		msg.setNoOfClients("5");
		msg.setNoOfInstruments("3");
		msg.setNoOfTrades("200");
		msg.setTimeDelay("1000");

		String jsonMessage = this.json(msg);

		
		  mockMvc.perform(
		  get("/tradeMessageInject").content(jsonMessage)
		 .contentType(contentType));
		 
	}

	@Test
	public void testTradeProfileSave() throws Exception {

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

		String jsonMessage = this.json(testProfile);

		MvcResult result = mockMvc.perform(
				post("/saveTradeInjectProfile").content(jsonMessage)
						.contentType(contentType)).andExpect(status().isOk()).andReturn();

		String content = result.getResponse().getContentAsString();
		System.out.println(content);
		assertTrue(content.contains("testProfile"));
		// now confirm whether it actually saved the object
		BasicQuery query = new BasicQuery("{ name : 'testProfile'}");
		TradeInjectorProfile profile = coreTemplate.findOne(query,
				TradeInjectorProfile.class);
		assertNotNull(profile);

		repo.delete(profile);

	}
	
	@Test
	public void testGetProfiles() throws Exception {

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

		String jsonMessage = this.json(testProfile);

		mockMvc.perform(
				post("/saveTradeInjectProfile").content(jsonMessage)
						.contentType(contentType)).andExpect(status().isOk());
		// now confirm whether it actually saved the object
		BasicQuery query = new BasicQuery("{ name : 'testProfile'}");
		TradeInjectorProfile profile = coreTemplate.findOne(query,
				TradeInjectorProfile.class);
		assertNotNull(profile);
		
		//now read it back through get method
		MvcResult result = mockMvc.perform(
				get("/getAllInjectProfiles")).andExpect(status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		System.out.println(content);
		assertTrue(content.contains("testProfile"));

		repo.delete(profile);

	}
	
	
	@Test
	public void testGetAllInstrumentsFromCache() throws Exception{
		
		Instrument testInstrument = new Instrument();
		testInstrument.setAssetClass("TEST");
		testInstrument.setInstrumentId(UUID.randomUUID().toString());
		testInstrument.setIssuer("TEST");
		testInstrument.setProduct("TESTPRODUCT");
		testInstrument.setSymbol("TESTSYM");
		
		IMap<String, Instrument> mapInstruments = hazelcastInstance.getMap(BusinessServiceCacheNames.INSTRUMENT_CACHE);
		mapInstruments.put(testInstrument.getInstrumentId(), testInstrument);
		
		//now call the business service
		MvcResult result = mockMvc.perform(
				get("/getAllInstruments")).andExpect(status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		System.out.println(content);
		assertTrue(content.contains("TESTPRODUCT"));
		
		
	}

	
	@After
	public void cleanUp(){
		
		BasicQuery query = new BasicQuery("{ name : 'testProfile'}");
		TradeInjectorProfile profile = coreTemplate.findOne(query,
				TradeInjectorProfile.class);

		if (profile != null)
			repo.delete(profile);
		
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o,
				MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}

}
