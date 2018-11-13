package com.example.mu.cachemapstore;

import com.example.mu.domain.Instrument;
import com.hazelcast.core.MapStore;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.example.mu.database.MuSchemaConstants.*;

/**
 * Created by oliverbuckley-salmon on 05/05/2017.
 */
public class InstrumentMapStore implements MapStore<String, Instrument>{

    Configuration config;
    Admin admin;
    Table table;

    private Logger logger = Logger.getLogger(InstrumentMapStore.class);

    public InstrumentMapStore() {
        try{
            config =  HBaseConfiguration.create();
            config.setInt("timeout", 120000);
            config.set("hbase.master", InetAddress.getLocalHost().getHostAddress() + ":60000");
            config.set("hbase.zookeeper.quorum",InetAddress.getLocalHost().getHostAddress());
            config.set("hbase.zookeeper.property.clientPort", "2181");

            logger.info("Trying to connect to HBase");
            Connection connection = ConnectionFactory.createConnection(config);
            TableName tname = TableName.valueOf(TABLE_INSTRUMENT);
            table = connection.getTable(tname);
            logger.info("Connected to HBase");

        }
        catch(IOException e){
            logger.error("Error connecting to HBase Server" + e.toString());
        }
    }

    public void store(String s, Instrument instrument) {
        Put put = new Put(Bytes.toBytes(s));
        put.addImmutable(CF_INSTRUMENT_DETAILS, INSTRUMENT_ID,Bytes.toBytes(instrument.getInstrumentId()));
        put.addImmutable(CF_INSTRUMENT_DETAILS, SYMBOL,Bytes.toBytes(instrument.getSymbol()));
        put.addImmutable(CF_INSTRUMENT_DETAILS, PRODUCT,Bytes.toBytes(instrument.getProduct()));
        put.addImmutable(CF_INSTRUMENT_DETAILS, ASSET_CLASS,Bytes.toBytes(instrument.getAssetClass()));
        put.addImmutable(CF_INSTRUMENT_DETAILS, ISSUER,Bytes.toBytes(instrument.getIssuer()));


        logger.info("Created immutable record " + instrument.toJSON());
        try {
            table.put(put);
        }
        catch(IOException e){
            logger.error("Error writing to INSTRUMENT table" + e.toString());
        }
        logger.info("Inserted immutable record" + instrument.toJSON());

    }

    public void storeAll(Map<String, Instrument> map) {
        for (Map.Entry<String, Instrument> entry : map.entrySet())
            store(entry.getKey(), entry.getValue());

    }

    public void delete(String s) {

    }

    public void deleteAll(Collection<String> collection) {

    }

    public Instrument load(String s) {

        Instrument result = new Instrument();
        byte[] pk = Bytes.toBytes(s);

        Get get = new Get(pk);
        Result getResult;
        try {
            get.setMaxVersions(1);
            logger.info("Getting instrument with key "+s+" from HBase");
            getResult = table.get(get);
            logger.info("Got instrument with key "+s+" from HBase");
            result.setInstrumentId(Bytes.toString(getResult.getValue(CF_INSTRUMENT_DETAILS,INSTRUMENT_ID)));
            result.setSymbol(Bytes.toString(getResult.getValue(CF_INSTRUMENT_DETAILS,SYMBOL)));
            result.setProduct(Bytes.toString(getResult.getValue(CF_INSTRUMENT_DETAILS,PRODUCT)));
            result.setAssetClass(Bytes.toString(getResult.getValue(CF_INSTRUMENT_DETAILS,ASSET_CLASS)));
            result.setIssuer(Bytes.toString(getResult.getValue(CF_INSTRUMENT_DETAILS,ISSUER)));
        }
        catch(IOException e){
            logger.error("Error reading from INSTRUMENT table" + e.toString());
        }
        return result;
    }

    public Map<String, Instrument> loadAll(Collection<String> keys) {

        Map<String, Instrument> result = new HashMap<>();
        logger.info("loadAll loading "+keys.size()+" Instruments from HBase");
        for (String key : keys) result.put(key, load(key));
        logger.info("Got "+result.size()+" Parties from HBase");
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
                logger.info("Getting Row "+Bytes.toString(row.getRow()));

            }
        }
        catch(IOException e){
            logger.error("Error reading all keys from INSTRUMENT table" + e.toString());
        }
        return keys;
    }
}
