package com.trade.imdg.hazelcast;

import static com.example.mu.database.MuSchemaConstants.ASSET_CLASS;
import static com.example.mu.database.MuSchemaConstants.CF_INSTRUMENT_DETAILS;
import static com.example.mu.database.MuSchemaConstants.HBASE_HOST;
import static com.example.mu.database.MuSchemaConstants.INSTRUMENT_ID;
import static com.example.mu.database.MuSchemaConstants.ISSUER;
import static com.example.mu.database.MuSchemaConstants.PRODUCT;
import static com.example.mu.database.MuSchemaConstants.SYMBOL;
import static com.example.mu.database.MuSchemaConstants.TABLE_INSTRUMENT;
import static com.example.mu.database.MuSchemaConstants.ZK_HOST;

import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import com.example.mu.cachemapstore.InstrumentMapStore;
import com.example.mu.domain.Instrument;

public class HBaseClientDemo {

    Configuration config;
    Table table;
    private Logger logger = Logger.getLogger(HBaseClientDemo.class);
	public static void main(String[] args) throws Exception{
		
		//connect to Hbase
		HBaseClientDemo demo = new HBaseClientDemo();
        demo.connectToHbase();
		
        //create the table
        
        
		//scan the instrument table and show the results
        
        
        //demo.scanTable();
	}
	
	public void connectToHbase() throws Exception{
		config =  HBaseConfiguration.create();
        config.setInt("timeout", 120000);
        //config.set("hbase.master", HBASE_HOST + ":60000");
        config.set("hbase.zookeeper.quorum",ZK_HOST);
        config.set("hbase.zookeeper.property.clientPort", "2181");

        logger.info("Trying to connect to HBase");
        Connection connection = ConnectionFactory.createConnection(config);
        TableName tname = TableName.valueOf(TABLE_INSTRUMENT);
        table = connection.getTable(tname);
        logger.info("Connected to HBase with following table "+table.getName());
        String key = UUID.randomUUID().toString();
        
        //test dummy data addition to this table
        Instrument sample = new Instrument();
		sample.setAssetClass("FX");
		sample.setInstrumentId(key);
		sample.setIssuer("Dinesh FX");
		sample.setProduct("PRODUCTFX");
		sample.setSymbol("FXX");

        Put put = new Put(Bytes.toBytes(key));
        put.addImmutable(CF_INSTRUMENT_DETAILS, INSTRUMENT_ID,Bytes.toBytes(sample.getInstrumentId()));
        put.addImmutable(CF_INSTRUMENT_DETAILS, SYMBOL,Bytes.toBytes(sample.getSymbol()));
        put.addImmutable(CF_INSTRUMENT_DETAILS, PRODUCT,Bytes.toBytes(sample.getProduct()));
        put.addImmutable(CF_INSTRUMENT_DETAILS, ASSET_CLASS,Bytes.toBytes(sample.getAssetClass()));
        put.addImmutable(CF_INSTRUMENT_DETAILS, ISSUER,Bytes.toBytes(sample.getIssuer()));
        
        logger.info("Done adding row ");
        
        //now read it back
        String rowKey = key;
        Result getResult = table.get(new Get(Bytes.toBytes(rowKey)));
        String symbol = Bytes.toString(getResult.getValue(CF_INSTRUMENT_DETAILS, SYMBOL));
        System.out.println("Get a single instrument by row key");
        System.out.printf("\t%s = %s\n", rowKey, symbol);
	}
	
	public void scanTable() throws Exception{
		
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
	        //return keys;
	
	}
}
