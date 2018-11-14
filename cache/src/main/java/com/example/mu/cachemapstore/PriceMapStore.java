package com.example.mu.cachemapstore;

import com.example.mu.domain.Price;
import com.hazelcast.core.MapStore;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.example.mu.database.MuSchemaConstants.*;

/**
 * Created by oliverbuckley-salmon on 05/05/2017.
 */
public class PriceMapStore implements MapStore<String, Price> {

	Configuration config;
	Admin admin;
	Table table;

	private Logger logger = Logger.getLogger(PriceMapStore.class);

	public PriceMapStore() {
		try {
			config = HBaseConfiguration.create();
			config.setInt("timeout", 120000);
			config.set("hbase.master", HBASE_HOST + ":60000");
			config.set("hbase.zookeeper.quorum", ZK_HOST);
			config.set("hbase.zookeeper.property.clientPort", "2181");

			logger.info("Trying to connect to HBase");
			Connection connection = ConnectionFactory.createConnection(config);
			TableName tname = TableName.valueOf(TABLE_PRICE);
			table = connection.getTable(tname);
			logger.info("Connected to HBase");

		} catch (IOException e) {
			logger.error("Error connecting to HBase Server" + e.toString());
		}
	}

	public void store(String s, Price price) {
		Put put = new Put(Bytes.toBytes(s));
		put.addImmutable(CF_PRICE_DETAILS, PRICE_ID, Bytes.toBytes(price.getPriceId()));
		put.addImmutable(CF_PRICE_DETAILS, PRICE_INSTRUMENT_ID, Bytes.toBytes(price.getInstrumentId()));
		put.addImmutable(CF_PRICE_DETAILS, PRICE, Bytes.toBytes(price.getPrice()));
		put.addImmutable(CF_PRICE_DETAILS, TIMESTAMP, Bytes.toBytes(price.getTimeStamp()));

		logger.info("Created immutable record " + price.toJSON());
		try {
			table.put(put);
		} catch (IOException e) {
			logger.error("Error writing to PRICE table" + e.toString());
		}
		logger.info("Inserted immutable record" + price.toJSON());

	}

	public void storeAll(Map<String, Price> map) {
		for (Map.Entry<String, Price> entry : map.entrySet())
			store(entry.getKey(), entry.getValue());

	}

	public void delete(String s) {

		try {

			logger.info("Deleting  price with key " + s + " from HBase");

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

	public Price load(String s) {

		Price result = new Price();
		byte[] pk = Bytes.toBytes(s);

		Get get = new Get(pk);
		Result getResult;
		try {
			get.setMaxVersions(1);
			logger.info("Getting price with key " + s + " from HBase");
			getResult = table.get(get);
			logger.info("Got price with key " + s + " from HBase");
			result.setPriceId(Bytes.toString(getResult.getValue(CF_PRICE_DETAILS, PRICE_ID)));
			result.setInstrumentId(Bytes.toString(getResult.getValue(CF_PRICE_DETAILS, PRICE_INSTRUMENT_ID)));
			if (getResult.getValue(CF_PRICE_DETAILS, PRICE) != null)
				result.setPrice(Bytes.toDouble(getResult.getValue(CF_PRICE_DETAILS, PRICE)));
			if (getResult.getValue(CF_PRICE_DETAILS, TIMESTAMP) != null)
				result.setTimeStamp(Bytes.toLong(getResult.getValue(CF_PRICE_DETAILS, TIMESTAMP)));

		} catch (IOException e) {
			logger.error("Error reading from PRICE table" + e.toString());
		}
		return result;
	}

	public Map<String, Price> loadAll(Collection<String> keys) {

		Map<String, Price> result = new HashMap<>();
		logger.info("loadAll loading " + keys.size() + " Prices from HBase");
		for (String key : keys)
			result.put(key, load(key));
		logger.info("Got " + result.size() + " Prices from HBase");
		return result;
	}

	public Iterable<String> loadAllKeys() {

		Scan scan = new Scan();
		scan.setMaxVersions(1);
		HashSet<String> keys = new HashSet<>();
		try {
			ResultScanner scanner = table.getScanner(scan);
			for (Result row : scanner) {
				keys.add(Bytes.toString(row.getRow()));
				logger.info("Getting Row " + Bytes.toString(row.getRow()));

			}
		} catch (IOException e) {
			logger.error("Error reading all keys from PRICE table" + e.toString());
		}
		return keys;
	}
}
