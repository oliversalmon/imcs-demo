package com.example.mu.cachemapstore;

import com.example.mu.domain.Party;
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
 * Created by oliverbuckley-salmon on 04/05/2017.
 */
public class PartyMapStore implements MapStore<String, Party>{

    Configuration config;
    Admin admin;
    Table table;

    private Logger logger = Logger.getLogger(PartyMapStore.class);

    public PartyMapStore() {
        try{
            config =  HBaseConfiguration.create();
            config.setInt("timeout", 120000);
            config.set("hbase.master", HBASE_HOST + ":60000");
            config.set("hbase.zookeeper.quorum",ZK_HOST);
            config.set("hbase.zookeeper.property.clientPort", "2181");

            logger.info("Trying to connect to HBase");
            Connection connection = ConnectionFactory.createConnection(config);
            TableName tname = TableName.valueOf(TABLE_PARTY);
            table = connection.getTable(tname);
            logger.info("Connected to HBase");

        }
        catch(IOException e){
            logger.error("Error connecting to HBase Server" + e.toString());
        }
    }

    public void store(String s, Party party) {
        Put put = new Put(Bytes.toBytes(s));
        put.addImmutable(CF_PARTY_DETAILS, PARTY_ID,Bytes.toBytes(party.getPartyId()));
        put.addImmutable(CF_PARTY_DETAILS, SHORT_NAME,Bytes.toBytes(party.getShortName()));
        put.addImmutable(CF_PARTY_DETAILS, NAME,Bytes.toBytes(party.getName()));
        put.addImmutable(CF_PARTY_DETAILS, ROLE,Bytes.toBytes(party.getRole()));
        put.addImmutable(CF_PARTY_DETAILS, POSTION_ACC_FK,Bytes.toBytes(party.getPositionAccountId()));

        logger.info("Created immutable record " + party.toJSON());
        try {
            table.put(put);
        }
        catch(IOException e){
            logger.error("Error writing to PARTY table" + e.toString());
        }
        logger.info("Inserted immutable record" + party.toJSON());

    }

    public void storeAll(Map<String, Party> map) {
        for (Map.Entry<String, Party> entry : map.entrySet())
            store(entry.getKey(), entry.getValue());

    }

    public void delete(String s) {

    }

    public void deleteAll(Collection<String> collection) {

    }

    public Party load(String s) {

        Party result = new Party();
        byte[] pk = Bytes.toBytes(s);

        Get get = new Get(pk);
        Result getResult;
        try {
            get.setMaxVersions(1);
            logger.info("Getting party with key "+s+" from HBase");
            getResult = table.get(get);
            logger.info("Got party with key "+s+" from HBase");
            result.setPartyId(Bytes.toString(getResult.getValue(CF_PARTY_DETAILS,PARTY_ID)));
            result.setShortName(Bytes.toString(getResult.getValue(CF_PARTY_DETAILS,SHORT_NAME)));
            result.setName(Bytes.toString(getResult.getValue(CF_PARTY_DETAILS,NAME)));
            result.setRole(Bytes.toString(getResult.getValue(CF_PARTY_DETAILS,ROLE)));
            result.setPositionAccountId(Bytes.toString(getResult.getValue(CF_PARTY_DETAILS,POSTION_ACC_FK)));

        }
        catch(IOException e){
            logger.error("Error reading from PARTY table" + e.toString());
        }
        return result;
    }

    public Map<String, Party> loadAll(Collection<String> keys) {

        Map<String, Party> result = new HashMap<>();
        logger.info("loadAll loading "+keys.size()+" Parties from HBase");
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
            logger.error("Error reading all keys from PARTY table" + e.toString());
        }
        return keys;
    }
}
