package com.trade.injector.business.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trade.injector.dto.Trade;
import com.trade.injector.jto.Instrument;
import com.trade.injector.jto.Party;

@Service
public class GenerateTradeData {

	private static boolean isBuy = false;

	public Trade createTradeData(int number, List<Party> partyList,
			List<Instrument> instrumentList) throws Exception {

		Trade aTrade = new Trade();
		aTrade.setTradeId("TRADE_" + new Integer(number) + "0");
		if (isBuy) {
			aTrade.setSide("Sell");
			isBuy = false;
		} else {
			aTrade.setSide("Buy");
			isBuy = true;
		}
		aTrade.setTradeDate(new Date(System.currentTimeMillis()));

		if (!partyList.isEmpty())
			aTrade.setClientName(getRandomParty(partyList).getPartyName());
		else
			throw new Exception("Unable to populate Party from party List");

		if (!instrumentList.isEmpty()) {
			aTrade.setInstrumentId(getRandomInstrument(instrumentList)
					.getIdenfitifier());
		}
		aTrade.setTradePx((int) (Math.random() * 1000 + 1));

		aTrade.setTradeQty((int) (Math.random() * 100 + 1));
		return aTrade;
	}

	private Party getRandomParty(List<Party> partyList) throws Exception {
		if (partyList.isEmpty())
			return null;
		else
			return partyList.get((int) (Math.random() * partyList.size()-1 + 1));

	}

	private Instrument getRandomInstrument(List<Instrument> insList)
			throws Exception {
		if (insList.isEmpty())
			return null;
		else
			return insList.get((int) (Math.random() * insList.size()-1 + 1));

	}

	public List<Trade> createRandomData(int number) throws Exception {
		// TODO Auto-generated method stub
		return null;

	}

}
