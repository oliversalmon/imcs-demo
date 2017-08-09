package com.example.mu.cachemapstore;

import com.example.mu.domain.Trade;
import com.hazelcast.core.MapStore;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

import static com.example.mu.database.MuSchemaConstants.*;

/**
 * Created by oliverbuckley-salmon on 17/11/2016.
 */
public class TradeMapStore implements MapStore<String, Trade> {

	Configuration config;
	Admin admin;
	Table table;

	private Logger logger = Logger.getLogger(TradeMapStore.class);

	public TradeMapStore() {
		try {
			config = HBaseConfiguration.create();
			config.setInt("timeout", 120000);
			config.set("hbase.master", HBASE_HOST + ":60000");
			config.set("hbase.zookeeper.quorum", ZK_HOST);
			config.set("hbase.zookeeper.property.clientPort", "2181");

			logger.info("Trying to connect to HBase");
			Connection connection = ConnectionFactory.createConnection(config);
			TableName tname = TableName.valueOf(TABLE_TRADE);
			table = connection.getTable(tname);
			logger.info("Connected to HBase");

		} catch (IOException e) {
			logger.error("Error connecting to HBase Server" + e.toString());
		}
	}

	public void store(String s, Trade trade) {
		Put put = new Put(Bytes.toBytes(s));
		put.addImmutable(CF_TRADE_DETAILS, TRADE_ID, Bytes.toBytes(trade.getTradeId()));
		put.addImmutable(CF_TRADE_DETAILS, SECONDARY_TRADE_ID, Bytes.toBytes(trade.getSecondaryTradeId()));
		put.addImmutable(CF_TRADE_DETAILS, FIRM_TRADE_ID_FK, Bytes.toBytes(trade.getFirmTradeId()));
		put.addImmutable(CF_TRADE_DETAILS, SECONDARY_FIRM_TRADE_ID_FK, Bytes.toBytes(trade.getSecondaryFirmTradeId()));
		put.addImmutable(CF_TRADE_DETAILS, TRADE_TYPE, Bytes.toBytes(trade.getTradeType()));
		put.addImmutable(CF_TRADE_DETAILS, SECONDARY_TRADE_TYPE, Bytes.toBytes(trade.getSecondaryTradeType()));
		put.addImmutable(CF_TRADE_DETAILS, EXECUTION_ID, Bytes.toBytes(trade.getExecutionId()));
		put.addImmutable(CF_TRADE_DETAILS, ORIGINAL_TRADE_DATE, Bytes.toBytes(trade.getOriginalTradeDate().getTime()));
		put.addImmutable(CF_TRADE_DETAILS, EXECUTING_FIRM_ID_FK, Bytes.toBytes(trade.getExecutingFirmId()));
		put.addImmutable(CF_TRADE_DETAILS, CLIENT_ID_FK, Bytes.toBytes(trade.getClientId()));
		put.addImmutable(CF_TRADE_DETAILS, EXECUTION_VENUE_ID_FK, Bytes.toBytes(trade.getExecutionVenueId()));
		put.addImmutable(CF_TRADE_DETAILS, EXECUTING_TRADER_ID_FK, Bytes.toBytes(trade.getExecutingTraderId()));
		put.addImmutable(CF_TRADE_DETAILS, POSITION_ACCOUNT_ID_FK, Bytes.toBytes(trade.getPositionAccountId()));
		put.addImmutable(CF_TRADE_DETAILS, INSTRUMENT_ID_FK, Bytes.toBytes(trade.getInstrumentId()));
		put.addImmutable(CF_TRADE_DETAILS, TRADE_PRICE, Bytes.toBytes(trade.getPrice()));
		put.addImmutable(CF_TRADE_DETAILS, QUANTITY, Bytes.toBytes(trade.getQuantity()));
		put.addImmutable(CF_TRADE_DETAILS, CURRENCY, Bytes.toBytes(trade.getCurrency()));
		put.addImmutable(CF_TRADE_DETAILS, TRADE_DATE, Bytes.toBytes(trade.getTradeDate().getTime()));
		put.addImmutable(CF_TRADE_DETAILS, SETTLEMENT_DATE, Bytes.toBytes(trade.getSettlementDate().getTime()));

		logger.info("Created immutable record " + trade.toJSON());
		try {
			table.put(put);
		} catch (IOException e) {
			logger.error("Error writing to TRADE table" + e.toString());
		}
		logger.info("Inserted immutable record" + trade.toJSON());

	}

	public void storeAll(Map<String, Trade> map) {
		for (Map.Entry<String, Trade> entry : map.entrySet())
			store(entry.getKey(), entry.getValue());

	}

	public void delete(String s) {

		try {

			logger.info("Deleting  trade with key " + s + " from HBase");

			Delete delete = new Delete(Bytes.toBytes(s));
			table.delete(delete);

		} catch (IOException e) {
			logger.error("Error deleting from TRADE table" + e.toString());
		}

	}

	public void deleteAll(Collection<String> collection) {

		for (String key : collection) {

			delete(key);
		}

	}

	public Trade load(String s) {

		Trade result = new Trade();
		byte[] pk = Bytes.toBytes(s);

		Get get = new Get(pk);
		Result getResult;
		try {
			get.setMaxVersions(1);
			logger.info("Getting trade with key " + s + " from HBase");
			getResult = table.get(get);
			logger.info("Got trade with key " + s + " from HBase");
			result.setTradeId(Bytes.toString(getResult.getValue(CF_TRADE_DETAILS, TRADE_ID)));
			result.setSecondaryTradeId(Bytes.toString(getResult.getValue(CF_TRADE_DETAILS, SECONDARY_TRADE_ID)));
			result.setFirmTradeId(Bytes.toString(getResult.getValue(CF_TRADE_DETAILS, FIRM_TRADE_ID_FK)));
			result.setSecondaryFirmTradeId(
					Bytes.toString(getResult.getValue(CF_TRADE_DETAILS, SECONDARY_FIRM_TRADE_ID_FK)));
			result.setTradeType(Bytes.toString(getResult.getValue(CF_TRADE_DETAILS, TRADE_TYPE)));
			result.setSecondaryTradeType(Bytes.toString(getResult.getValue(CF_TRADE_DETAILS, SECONDARY_TRADE_TYPE)));
			result.setExecutionId(Bytes.toString(getResult.getValue(CF_TRADE_DETAILS, EXECUTION_ID)));
			result.setOriginalTradeDate(
					new Date(Bytes.toLong(getResult.getValue(CF_TRADE_DETAILS, ORIGINAL_TRADE_DATE))));
			result.setExecutingFirmId(Bytes.toString(getResult.getValue(CF_TRADE_DETAILS, EXECUTING_FIRM_ID_FK)));
			result.setClientId(Bytes.toString(getResult.getValue(CF_TRADE_DETAILS, CLIENT_ID_FK)));
			result.setExecutionVenueId(Bytes.toString(getResult.getValue(CF_TRADE_DETAILS, EXECUTION_VENUE_ID_FK)));
			result.setExecutingTraderId(Bytes.toString(getResult.getValue(CF_TRADE_DETAILS, EXECUTING_TRADER_ID_FK)));
			result.setPositionAccountId(Bytes.toString(getResult.getValue(CF_TRADE_DETAILS, POSITION_ACCOUNT_ID_FK)));
			result.setInstrumentId(Bytes.toString(getResult.getValue(CF_TRADE_DETAILS, INSTRUMENT_ID_FK)));
			result.setPrice(Bytes.toDouble(getResult.getValue(CF_TRADE_DETAILS, TRADE_PRICE)));
			result.setQuantity(Bytes.toInt(getResult.getValue(CF_TRADE_DETAILS, QUANTITY)));
			result.setCurrency(Bytes.toString(getResult.getValue(CF_TRADE_DETAILS, CURRENCY)));
			result.setTradeDate(new Date(Bytes.toLong(getResult.getValue(CF_TRADE_DETAILS, TRADE_DATE))));
			result.setSettlementDate(new Date(Bytes.toLong(getResult.getValue(CF_TRADE_DETAILS, SETTLEMENT_DATE))));

		} catch (IOException e) {
			logger.error("Error reading from TRADE table" + e.toString());
		}
		return result;
	}

	public Map<String, Trade> loadAll(Collection<String> keys) {

		Map<String, Trade> result = new HashMap<String, Trade>();
		logger.info("loadAll loading " + keys.size() + " Trades from HBase");
		for (String key : keys)
			result.put(key, load(key));
		logger.info("Got " + result.size() + " Trades from HBase");
		return result;
	}

	public Iterable<String> loadAllKeys() {

		Scan scan = new Scan();
		scan.setMaxVersions(1);
		HashSet<String> keys = new HashSet<String>();
		try {
			ResultScanner scanner = table.getScanner(scan);
			for (Result row : scanner) {
				keys.add(Bytes.toString(row.getRow()));
				logger.info("Getting Row " + Bytes.toString(row.getRow()));

			}
		} catch (IOException e) {
			logger.error("Error reading all keys from TRADE table" + e.toString());
		}
		return keys;
	}
}
