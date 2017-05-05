package com.example.mu.cachemapstore;

import com.example.mu.domain.PositionAccount;
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
public class PositionAccountMapStore {

    Configuration config;
    Admin admin;
    Table table;

    private Logger logger = Logger.getLogger(PositionAccountMapStore.class);

    public PositionAccountMapStore() {
        try{
            config =  HBaseConfiguration.create();
            config.setInt("timeout", 120000);
            config.set("hbase.master", HBASE_HOST + ":60000");
            config.set("hbase.zookeeper.quorum",ZK_HOST);
            config.set("hbase.zookeeper.property.clientPort", "2181");

            logger.info("Trying to connect to HBase");
            Connection connection = ConnectionFactory.createConnection(config);
            TableName tname = TableName.valueOf(TABLE_POSITION_ACCOUNT);
            table = connection.getTable(tname);
            logger.info("Connected to HBase");

        }
        catch(IOException e){
            logger.error("Error connecting to HBase Server" + e.toString());
        }
    }

    public void store(String s, PositionAccount account) {
        Put put = new Put(Bytes.toBytes(s));
        put.addImmutable(CF_ACCOUNT_DETAILS, ACCOUNT_ID,Bytes.toBytes(account.getAccountId()));
        put.addImmutable(CF_ACCOUNT_DETAILS, ACC_INSTRUMENT_ID,Bytes.toBytes(account.getInstrumentid()));
        put.addImmutable(CF_ACCOUNT_DETAILS, SIZE,Bytes.toBytes(account.getSize()));
        put.addImmutable(CF_ACCOUNT_DETAILS, PNL,Bytes.toBytes(account.getPnl()));

        logger.info("Created immutable record " + account.toJSON());
        try {
            table.put(put);
        }
        catch(IOException e){
            logger.error("Error writing to POSITION_ACCOUNT table" + e.toString());
        }
        logger.info("Inserted immutable record" + account.toJSON());

    }

    public void storeAll(Map<String, PositionAccount> map) {
        for (Map.Entry<String, PositionAccount> entry : map.entrySet())
            store(entry.getKey(), entry.getValue());

    }

    public void delete(String s) {

    }

    public void deleteAll(Collection<String> collection) {

    }

    public PositionAccount load(String s) {

        PositionAccount result = new PositionAccount();
        byte[] pk = Bytes.toBytes(s);

        Get get = new Get(pk);
        Result getResult;
        try {
            get.setMaxVersions(1);
            logger.info("Getting account with key "+s+" from HBase");
            getResult = table.get(get);
            logger.info("Got account with key "+s+" from HBase");
            result.setAccountId(Bytes.toString(getResult.getValue(CF_ACCOUNT_DETAILS,ACCOUNT_ID)));
            result.setInstrumentid(Bytes.toString(getResult.getValue(CF_ACCOUNT_DETAILS,ACC_INSTRUMENT_ID)));
            result.setSize(Bytes.toLong(getResult.getValue(CF_ACCOUNT_DETAILS,SIZE)));
            result.setPnl(Bytes.toDouble(getResult.getValue(CF_ACCOUNT_DETAILS,PNL)));

        }
        catch(IOException e){
            logger.error("Error reading from POSITION_ACCOUNT table" + e.toString());
        }
        return result;
    }

    public Map<String, PositionAccount> loadAll(Collection<String> keys) {

        Map<String, PositionAccount> result = new HashMap<>();
        logger.info("loadAll loading "+keys.size()+" Accounts from HBase");
        for (String key : keys) result.put(key, load(key));
        logger.info("Got "+result.size()+" Accounts from HBase");
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
            logger.error("Error reading all keys from POSITION_ACCOUNT table" + e.toString());
        }
        return keys;
    }
}
