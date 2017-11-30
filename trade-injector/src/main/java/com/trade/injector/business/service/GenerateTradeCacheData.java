package com.trade.injector.business.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.mu.domain.Instrument;
import com.example.mu.domain.Party;
import com.example.mu.domain.Trade;

@Service
public class GenerateTradeCacheData {

	public Trade[] createTrade(int number, Map<String, Party> parties, int partyLimit,
			Map<String, Instrument> instruments, int instrumentLimit) throws Exception {

		// set the date
		Calendar cal = Calendar.getInstance();
		Date date = cal.getTime();

		// generate all the common attributes for both sides of the trade
		Trade[] matchedTrade = new Trade[2];
		String executionId = UUID.randomUUID().toString();
		String currency = "USD";
		String executingFirmId = "TEST_EX1_" + number;
		String executingTraderId = "TEST_TRD1";
		String executionVenue = "EX1";
		double price = (double) (Math.random() * 1000 + 1);
		int quantity = (int) (Math.random() * 100 + 1);

		Party buyParty = getRandomParty(parties, partyLimit);
		Party sellParty = getRandomParty(parties, partyLimit);
		Date tradeDate = date;
			System.out.println("Current date and time in Date's toString() is : " + tradeDate + "\n");
		while (buyParty.equals(sellParty)) {
			// keep looping till we get a different party
			sellParty = getRandomParty(parties, partyLimit);
		}
		Instrument tradedInstrument = getRandomInstrument(instruments, instrumentLimit);
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
		atrade.setPrice(price);
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
		aSelltrade.setPrice(price);
		aSelltrade.setQuantity(-quantity);
		aSelltrade.setSecondaryFirmTradeId(executionId);
		aSelltrade.setSecondaryTradeId(executionId);
		aSelltrade.setSettlementDate(tradeDate);
		aSelltrade.setTradeDate(tradeDate);
		aSelltrade.setTradeId(sellKey);
		aSelltrade.setTradeType("0");
		aSelltrade.setSecondaryTradeType("0");

		// trade.put(sellKey, aSelltrade);
		matchedTrade[0] = atrade;
		matchedTrade[1] = aSelltrade;

		return matchedTrade;

	}

	private Party getRandomParty(Map<String, Party> partyList, int limit) throws Exception {
		if (partyList.isEmpty())
			return null;
		else {
			List<String> keysAsArrays = new ArrayList<String>(partyList.keySet());
			Random r = new Random();
			if (limit > 0 && limit < keysAsArrays.size())
				return partyList.get(keysAsArrays.get(r.nextInt(limit)));
			else
				return partyList.get(keysAsArrays.get(r.nextInt(keysAsArrays.size())));
		}

	}

	private Instrument getRandomInstrument(Map<String, Instrument> insList, int limit) throws Exception {
		if (insList.isEmpty())
			return null;
		else {
			List<String> keysAsArrays = new ArrayList<String>(insList.keySet());
			Random r = new Random();
			if (limit > 0 && limit < keysAsArrays.size())
				return insList.get(keysAsArrays.get(r.nextInt(limit)));
			else
				return insList.get(keysAsArrays.get(r.nextInt(keysAsArrays.size())));
		}

	}

}
