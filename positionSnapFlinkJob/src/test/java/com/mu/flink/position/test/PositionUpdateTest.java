package com.mu.flink.position.test;

import static org.junit.Assert.*;

import java.util.UUID;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.example.mu.domain.PositionAccount;
import com.example.mu.domain.Price;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.mu.flink.batch.PositionSnap;
import com.mu.flink.batch.PositionUpdate;

public class PositionUpdateTest {

	private static final String POSITIONACCOUNTMAP = "position-account";
	private static final String PRICEMAP = "price";
	private static HazelcastInstance instance;

	@Before
	public void setUp() {

		instance = Hazelcast.newHazelcastInstance();
		IMap<String, PositionAccount> posMap = PositionSnap.getHzClient().getMap(POSITIONACCOUNTMAP);
		for (int i = 0; i < 5; i++) {
			PositionAccount posAcc1 = new PositionAccount();
			posAcc1.setAccountId("AC" + i);
			posAcc1.setInstrumentid("INS1");
			posAcc1.setPnl(0);
			posAcc1.setSize(0);

			posMap.put(posAcc1.getAccountId() + posAcc1.getInstrumentid(), posAcc1);

		}

	}

	@Test
	public void testPositionSnapWithoutSpotPx() throws Exception {
		IMap<String, PositionAccount> posMap = PositionSnap.getHzClient().getMap(POSITIONACCOUNTMAP);
		assertEquals(5, posMap.size());
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		// configure your test environment
		env.setParallelism(1);

		DataSource<PositionAccount> data = env.fromCollection(posMap.values());
		DataSet<PositionAccount> transformedData = data.flatMap(new PositionUpdate());

		transformedData.collect().forEach(a -> posMap.put(a.getAccountId() + a.getInstrumentid(), a));
		assertEquals(5, posMap.size());

		assertEquals(0, posMap.get("AC1INS1").getSize());
		assertEquals(0, posMap.get("AC1INS1").getPnl(), 0);

	}

	@Test
	public void testMultiplePositionSnap() throws Exception {

		IMap<String, PositionAccount> posMap = PositionSnap.getHzClient().getMap(POSITIONACCOUNTMAP);

		// first set the spot px
		IMap<String, Price> priceMap = PositionSnap.getHzClient().getMap(PRICEMAP);
		Price price = new Price();
		price.setInstrumentId("INS1");
		price.setPrice(340.8987978);
		price.setPriceId(UUID.randomUUID().toString());
		price.setTimeStamp(System.currentTimeMillis());

		priceMap.put(price.getInstrumentId(), price);

		assertEquals(5, posMap.size());

		// set the quantity
		posMap.values().forEach(a -> {
			a.setSize(10);
			a.setPnl(100);
			posMap.put(a.getAccountId()+a.getInstrumentid(), a);
		});
		ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		// configure your test environment
		env.setParallelism(1);

		DataSource<PositionAccount> data = env.fromCollection(posMap.values());
		DataSet<PositionAccount> transformedData = data.flatMap(new PositionUpdate());

		transformedData.collect().forEach(a -> posMap.put(a.getAccountId() + a.getInstrumentid(), a));
		assertEquals(5, posMap.size());

		assertEquals(10 * price.getPrice() - 100, posMap.get("AC1INS1").getPnl(), 0);

	}

	@After
	public void tearDown() {

		// PositionSnap.getHzClient().getMap(POSITIONACCOUNTMAP).clear();
		instance.shutdown();

	}
}
