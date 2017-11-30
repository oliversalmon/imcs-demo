package com.trade.injector.business.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.trade.injector.dto.Trade;

@RunWith(SpringRunner.class)
// @SpringBootTest
public class GenerateTradeDataTest {

	@Test
	public void runEmptyTest() {

	}

	@Test
	public void testGenerateRandomInstrument() throws Exception {
		GenerateRandomInstruments ranIns = new GenerateRandomInstruments();

		assert (ranIns.createRandomData(50).size() == 50);

	}

	@Test
	public void testGeneratePartyData() throws Exception {
		GenerateRandomParty ransPty = new GenerateRandomParty();

		assert (ransPty.createRandomData(50).size() == 50);

	}

	@Test
	public void testTradeDataBasic() throws Exception {
		GenerateRandomInstruments ranIns = new GenerateRandomInstruments();

		GenerateRandomParty ransPty = new GenerateRandomParty();

		GenerateTradeData tradeData = new GenerateTradeData();
		Trade tr = tradeData.createTradeData(1, ransPty.createRandomData(5),
				ranIns.createRandomData(5));

		assert (tr != null);
		assert (tr.getTradeDate() != null);
		System.out.println(tr.toString());

	}

}
