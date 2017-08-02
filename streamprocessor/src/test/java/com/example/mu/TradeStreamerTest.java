package com.example.mu;

import static com.example.mu.database.MuSchemaConstants.CF_TRADE_DETAILS;
import static com.example.mu.database.MuSchemaConstants.CLIENT_ID_FK;
import static com.example.mu.database.MuSchemaConstants.CURRENCY;
import static com.example.mu.database.MuSchemaConstants.EXECUTING_FIRM_ID_FK;
import static com.example.mu.database.MuSchemaConstants.EXECUTING_TRADER_ID_FK;
import static com.example.mu.database.MuSchemaConstants.EXECUTION_ID;
import static com.example.mu.database.MuSchemaConstants.EXECUTION_VENUE_ID_FK;
import static com.example.mu.database.MuSchemaConstants.FIRM_TRADE_ID_FK;
import static com.example.mu.database.MuSchemaConstants.HBASE_HOST;
import static com.example.mu.database.MuSchemaConstants.INSTRUMENT_ID_FK;
import static com.example.mu.database.MuSchemaConstants.ORIGINAL_TRADE_DATE;
import static com.example.mu.database.MuSchemaConstants.POSITION_ACCOUNT_ID_FK;
import static com.example.mu.database.MuSchemaConstants.QUANTITY;
import static com.example.mu.database.MuSchemaConstants.SECONDARY_FIRM_TRADE_ID_FK;
import static com.example.mu.database.MuSchemaConstants.SECONDARY_TRADE_ID;
import static com.example.mu.database.MuSchemaConstants.SECONDARY_TRADE_TYPE;
import static com.example.mu.database.MuSchemaConstants.SETTLEMENT_DATE;
import static com.example.mu.database.MuSchemaConstants.TABLE_TRADE;
import static com.example.mu.database.MuSchemaConstants.TRADE_DATE;
import static com.example.mu.database.MuSchemaConstants.TRADE_ID;
import static com.example.mu.database.MuSchemaConstants.TRADE_PRICE;
import static com.example.mu.database.MuSchemaConstants.TRADE_TYPE;
import static com.example.mu.database.MuSchemaConstants.ZK_HOST;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.example.mu.cachemapstore.TradeMapStore;
import com.example.mu.domain.Trade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TradeStreamerTest extends TestCase {

	Configuration config;
	Admin admin;
	Table table;

	public static Test suite() {
		return new TestSuite(TradeStreamerTest.class);
	}

	public TradeStreamerTest(String testName) {
		super(testName);
	}

	public void testGSONTradeBuilder() throws Exception {

		// Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy
		// HH:mm:ss").create();

		Gson gson = new GsonBuilder().create();

		String jsonTestString = "{\"tradeId\":\"978ac042-16e7-4e3d-adbe-82437ec6009a\",\"secondaryTradeId\":\"67d86e24-332c-489c-ab42-dd67668a689c\",\"firmTradeId\":\"67d86e24-332c-489c-ab42-dd67668a689c\",\"secondaryFirmTradeId\":\"67d86e24-332c-489c-ab42-dd67668a689c\",\"tradeType\":\"0\",\"executionId\":\"978ac042-16e7-4e3d-adbe-82437ec6009a\",\"originalTradeDate\":\"Jul 31, 2017 10:33:39 AM\",\"executingFirmId\":\"TEST_EX1_5\",\"clientId\":\"TRDINJECT_CLI_7\",\"executionVenueId\":\"EX1\",\"executingTraderId\":\"TEST_TRD1\",\"positionAccountId\":\"TRDINJECT_ACC_7\",\"instrumentId\":\"ELECU\",\"price\":403.4228025436225,\"quantity\":19,\"currency\":\"USD\",\"tradeDate\":\"Jul 31, 2017 10:33:39 AM\",\"settlementDate\":\"Jul 31, 2017 10:33:39 AM\"}";
		Trade aTrade = gson.fromJson(jsonTestString, Trade.class);

		assertNotNull(aTrade.getOriginalTradeDate().getTime());

		System.out.println(aTrade.getOriginalTradeDate().getTime());

		assertNotNull(Bytes.toBytes(aTrade.getOriginalTradeDate().getTime()));

	}

	public void testTradeStoreInHbase() throws Exception {
		//Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss").create();

		Gson gson = new GsonBuilder().create();
		

         String jsonTestString = "{\"tradeId\":\"2eef25d6-de02-4cd1-a1d7-32cef04d08e0\",\"secondaryTradeId\":\"02474792-7978-4983-b64f-b5039a2025ac\",\"firmTradeId\":\"02474792-7978-4983-b64f-b5039a2025ac\",\"secondaryFirmTradeId\":\"02474792-7978-4983-b64f-b5039a2025ac\",\"tradeType\":\"0\",\"secondaryTradeType\":\"0\",\"executionId\":\"2eef25d6-de02-4cd1-a1d7-32cef04d08e0\",\"originalTradeDate\":\"Jul 31, 2017 1:26:17 PM\",\"executingFirmId\":\"TEST_EX1_4\",\"clientId\":\"TRDINJECT_CLI_10\",\"executionVenueId\":\"EX1\",\"executingTraderId\":\"TEST_TRD1\",\"positionAccountId\":\"TRDINJECT_ACC_10\",\"instrumentId\":\"ELECU\",\"price\":61.22522711308464,\"quantity\":23,\"currency\":\"USD\",\"tradeDate\":\"Jul 31, 2017 1:26:17 PM\",\"settlementDate\":\"Jul 31, 2017 1:26:17 PM\"}"; 
         		
 		 Trade trade = gson.fromJson(jsonTestString, Trade.class);
 		 TradeMapStore tms = new TradeMapStore();
 		 tms.load(trade.getTradeId());
 
         
         
         //now load and assert results
 		 assertNotNull(tms.load(trade.getTradeId()));
         
	}
	
	
	public void testLoadAll() throws Exception{
		
		 TradeMapStore tms = new TradeMapStore();
		 Iterable<String> keys = tms.loadAllKeys();
		 keys.forEach(a-> System.out.println(a));
		
	}
	
	public void testDeleteAll() throws Exception{
		 TradeMapStore tms = new TradeMapStore();
		 Iterable<String> keys = tms.loadAllKeys();
		 keys.forEach(a-> tms.delete(a));
		 
		 System.out.println("Completed deletion");
		
	}

}
