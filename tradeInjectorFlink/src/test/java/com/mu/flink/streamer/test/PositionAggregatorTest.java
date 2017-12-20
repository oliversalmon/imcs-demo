package com.mu.flink.streamer.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.junit.Before;
import org.junit.Test;

import com.example.mu.domain.Instrument;
import com.example.mu.domain.Party;
import com.example.mu.domain.PositionAccount;
import com.example.mu.domain.Price;
import com.example.mu.domain.Trade;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.mu.flink.streamer.PositionAggregator;
import com.mu.flink.streamer.TradeKeyForPositionAccount;

public class PositionAggregatorTest {

	HazelcastInstance hz = Hazelcast.newHazelcastInstance();

	@Before
	public void setUp() {

		// create new Trades, Instruments, Prices, Parties
		Party party1 = new Party();
		party1.setName("PARTY1");
		party1.setPartyId("PARTY1");
		party1.setPositionAccountId("ACC1");
		party1.setRole("POS ACC HOLDER");
		party1.setShortName("PTY1");

		Party party2 = new Party();
		party2.setName("PARTY2");
		party2.setPartyId("PARTY2");
		party2.setPositionAccountId("ACC2");
		party2.setRole("POS ACC HOLDER");
		party2.setShortName("PTY2");

		IMap<String, Party> map = hz.getMap("party");
		map.put("PARTY1", party1);
		map.put("PARTY2", party2);

		Instrument ins1 = new Instrument();
		ins1.setAssetClass("EQUITY");
		ins1.setInstrumentId("INS1");
		ins1.setIssuer("TEST");
		ins1.setProduct("INS1");
		ins1.setSymbol("INS1");

		Instrument ins2 = new Instrument();
		ins2.setAssetClass("EQUITY");
		ins2.setInstrumentId("INS2");
		ins2.setIssuer("TEST");
		ins2.setProduct("INS2");
		ins2.setSymbol("INS2");

		IMap<String, Instrument> mapIns = hz.getMap("instrument");
		mapIns.put("INS1", ins1);
		mapIns.put("INS2", ins2);

		Price price = new Price();
		price.setInstrumentId(ins1.getSymbol());
		price.setPrice((double) Math.random() * 1000 + 1);
		price.setPriceId(UUID.randomUUID().toString());
		price.setTimeStamp(System.currentTimeMillis());

		Price price2 = new Price();
		price2.setInstrumentId(ins2.getSymbol());
		price2.setPrice((double) Math.random() * 1000 + 1);
		price2.setPriceId(UUID.randomUUID().toString());
		price2.setTimeStamp(System.currentTimeMillis());

		IMap<String, Price> mapPrice = hz.getMap("price");
		mapPrice.put(price.getInstrumentId(), price);
		mapPrice.put(price2.getInstrumentId(), price2);

	}

	@Test
	public void testKeyBy() {

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPositionCalculation() throws Exception {

		IMap<String, Party> map = hz.getMap("party");
		assert (map.size() == 2);

		IMap<String, Instrument> mapIns = hz.getMap("instrument");
		assert (mapIns.size() == 2);

		IMap<String, Price> mapPrice = hz.getMap("price");
		assert (mapPrice.size() == 2);

		// set up the trade information for this test
		// set the date
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();

		// generate all the common attributes for both sides of the trade

		String executionId = UUID.randomUUID().toString();
		String currency = "USD";
		String executingFirmId = "TEST_EX1_" + 1;
		String executingTraderId = "TEST_TRD1";
		String executionVenue = "EX1";
		double trdpx = (double) (Math.random() * 1000 + 1);
		int quantity = (int) (Math.random() * 100 + 1);

		Party buyParty = map.get("PARTY1");
		Party sellParty = map.get("PARTY2");
		Date tradeDate = date;
		System.out.println("Current date and time in Date's toString() is : " + tradeDate + "\n");

		Instrument tradedInstrument = mapIns.get("INS1");
		// do the buy
		String buyKey = UUID.randomUUID().toString();
		Trade atrade = new Trade();
		atrade.setClientId(buyParty.getName());
		atrade.setCurrency(currency);
		atrade.setExecutingFirmId(executingFirmId);
		atrade.setExecutingTraderId(executingTraderId);
		atrade.setExecutionId(buyKey);
		atrade.setExecutionVenueId(executionVenue);
		atrade.setFirmTradeId(executionId);
		atrade.setInstrumentId(tradedInstrument.getSymbol());
		atrade.setOriginalTradeDate(tradeDate);
		atrade.setPositionAccountId(buyParty.getPositionAccountId());
		atrade.setPrice(trdpx);
		atrade.setQuantity(quantity);
		atrade.setSecondaryFirmTradeId(executionId);
		atrade.setSecondaryTradeId(executionId);
		atrade.setSettlementDate(tradeDate);
		atrade.setTradeDate(tradeDate);
		atrade.setTradeId(buyKey);
		atrade.setTradeType("0");
		atrade.setSecondaryTradeType("0");

		// trade.put(buyKey, atrade);

		// now generate Sell side
		String sellKey = UUID.randomUUID().toString();
		Trade aSelltrade = new Trade();
		aSelltrade.setClientId(sellParty.getName());
		aSelltrade.setCurrency(currency);
		aSelltrade.setExecutingFirmId(executingFirmId);
		aSelltrade.setExecutingTraderId(executingTraderId);
		aSelltrade.setExecutionId(sellKey);
		aSelltrade.setExecutionVenueId(executionVenue);
		aSelltrade.setFirmTradeId(executionId);
		aSelltrade.setInstrumentId(tradedInstrument.getSymbol());
		aSelltrade.setOriginalTradeDate(tradeDate);
		aSelltrade.setPositionAccountId(sellParty.getPositionAccountId());
		aSelltrade.setPrice(trdpx);
		aSelltrade.setQuantity(-quantity);
		aSelltrade.setSecondaryFirmTradeId(executionId);
		aSelltrade.setSecondaryTradeId(executionId);
		aSelltrade.setSettlementDate(tradeDate);
		aSelltrade.setTradeDate(tradeDate);
		aSelltrade.setTradeId(sellKey);
		aSelltrade.setTradeType("0");
		aSelltrade.setSecondaryTradeType("0");
		
	

		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		env.fromElements(atrade, aSelltrade)
		.keyBy(new TradeKeyForPositionAccount())
		.flatMap(new PositionAggregator())
		.addSink(new CollectSink());
		
		env.execute();
		
		//now assert the results
		Thread.sleep(65000);
		
		assertEquals(CollectSink.values.size(),2);
		//assertEquals(CollectSink.values.get(0).getSize(), 0);
		
		for(PositionAccount acc: CollectSink.values) {
			if(acc.getSize() > 0)
				assert(acc.getSize()==quantity);
			if(acc.getSize() < 0)
				assert(acc.getSize()==-quantity);
		}

	}
	
	@Test
	public void testManyTradesForTwoAccounts() throws Exception {
		
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();

		// generate all the common attributes for both sides of the trade
		IMap<String, Party> map = hz.getMap("party");
		assert (map.size() == 2);

		IMap<String, Instrument> mapIns = hz.getMap("instrument");
		assert (mapIns.size() == 2);

		IMap<String, Price> mapPrice = hz.getMap("price");
		assert (mapPrice.size() == 2);
		
		IMap<String, Trade> mapTrade = hz.getMap("trade");
		
		String currency = "USD";
		

		Party buyParty = map.get("PARTY1");
		Party sellParty = map.get("PARTY2");
		Date tradeDate = date;
		System.out.println("Current date and time in Date's toString() is : " + tradeDate + "\n");
		
		Instrument tradedInstrument = mapIns.get("INS1");
		
		//run 100 trades with 1 second intervals
		for(int i=0; i < 100; i++) {
			String executionId = UUID.randomUUID().toString();
			String executingFirmId = "TEST_EX1_" + i;
			String executingTraderId = "TEST_TRD"+i;
			String executionVenue = "EX1";
			double trdpx = (double) (Math.random() * 1000 + 1);
			int quantity = (int) (Math.random() * 100 + 1);
			
			String buyKey = UUID.randomUUID().toString();
			Trade atrade = new Trade();
			atrade.setClientId(buyParty.getName());
			atrade.setCurrency(currency);
			atrade.setExecutingFirmId(executingFirmId);
			atrade.setExecutingTraderId(executingTraderId);
			atrade.setExecutionId(buyKey);
			atrade.setExecutionVenueId(executionVenue);
			atrade.setFirmTradeId(executionId);
			atrade.setInstrumentId(tradedInstrument.getSymbol());
			atrade.setOriginalTradeDate(tradeDate);
			atrade.setPositionAccountId(buyParty.getPositionAccountId());
			atrade.setPrice(trdpx);
			atrade.setQuantity(quantity);
			atrade.setSecondaryFirmTradeId(executionId);
			atrade.setSecondaryTradeId(executionId);
			atrade.setSettlementDate(tradeDate);
			atrade.setTradeDate(tradeDate);
			atrade.setTradeId(buyKey);
			atrade.setTradeType("0");
			atrade.setSecondaryTradeType("0");

			mapTrade.put(buyKey, atrade);

			// now generate Sell side
			String sellKey = UUID.randomUUID().toString();
			Trade aSelltrade = new Trade();
			aSelltrade.setClientId(sellParty.getName());
			aSelltrade.setCurrency(currency);
			aSelltrade.setExecutingFirmId(executingFirmId);
			aSelltrade.setExecutingTraderId(executingTraderId);
			aSelltrade.setExecutionId(sellKey);
			aSelltrade.setExecutionVenueId(executionVenue);
			aSelltrade.setFirmTradeId(executionId);
			aSelltrade.setInstrumentId(tradedInstrument.getSymbol());
			aSelltrade.setOriginalTradeDate(tradeDate);
			aSelltrade.setPositionAccountId(sellParty.getPositionAccountId());
			aSelltrade.setPrice(trdpx);
			aSelltrade.setQuantity(-quantity);
			aSelltrade.setSecondaryFirmTradeId(executionId);
			aSelltrade.setSecondaryTradeId(executionId);
			aSelltrade.setSettlementDate(tradeDate);
			aSelltrade.setTradeDate(tradeDate);
			aSelltrade.setTradeId(sellKey);
			aSelltrade.setTradeType("0");
			aSelltrade.setSecondaryTradeType("0");
			
			mapTrade.put(sellKey, aSelltrade);
			
			
		}
		
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setParallelism(1);
		env.fromCollection(mapTrade.values())
		.keyBy(new TradeKeyForPositionAccount())
		.flatMap(new PositionAggregator())
		.addSink(new CollectSink());
		
		env.execute();
		
		Thread.sleep(70000);
		assertEquals(CollectSink.values.size(),2);
		
	}
	
	
	
	// create a testing sink
    private static class CollectSink implements SinkFunction<PositionAccount> {

        // must be static
        public static final List<PositionAccount> values = new ArrayList<PositionAccount>();

       

		public void invoke(PositionAccount arg0) throws Exception {
			values.add(arg0);
			
		}
    }

}
