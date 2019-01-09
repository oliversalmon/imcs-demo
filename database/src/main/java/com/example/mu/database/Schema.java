package com.example.mu.database;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;

import java.io.IOException;

import static com.example.mu.database.MuSchemaConstants.*;


/**
 * Created by oliverbuckley-salmon on 02/05/2017.
 */
public class Schema {



    public static void createOrOverwrite(Admin admin, HTableDescriptor table) throws IOException {
        if (admin.tableExists(table.getTableName())) {
            admin.disableTable(table.getTableName());
            admin.deleteTable(table.getTableName());
        }
        admin.createTable(table);
    }

    public static void createSchemaTables(Configuration config) throws IOException {
        try (Connection connection = ConnectionFactory.createConnection(config);
             Admin admin = connection.getAdmin()) {
            // Create the namespace
            NamespaceDescriptor namespace = NamespaceDescriptor.create("mu").build();
            admin.createNamespace(namespace);

            // Create the TRADE table
            HTableDescriptor tableTrade = new HTableDescriptor(TableName.valueOf(TABLE_TRADE));
            tableTrade.addFamily(new HColumnDescriptor(CF_TRADE_DETAILS).setCompressionType(Algorithm.NONE));
            System.out.print("Creating table TRADE. ");
            createOrOverwrite(admin, tableTrade);
            System.out.println(" Done.");

            // Create the PARTY table
            HTableDescriptor tableParty = new HTableDescriptor(TableName.valueOf(TABLE_PARTY));
            tableParty.addFamily(new HColumnDescriptor(CF_PARTY_DETAILS).setCompressionType(Algorithm.NONE));
            System.out.print("Creating table PARTY. ");
            createOrOverwrite(admin, tableParty);
            System.out.println(" Done.");

            // Create the INSTRUMENT table
            HTableDescriptor tableInstrument = new HTableDescriptor(TableName.valueOf(TABLE_INSTRUMENT));
            tableInstrument.addFamily(new HColumnDescriptor(CF_INSTRUMENT_DETAILS).setCompressionType(Algorithm.NONE));
            System.out.print("Creating table INSTRUMENT. ");
            createOrOverwrite(admin, tableInstrument);
            System.out.println(" Done.");

            // Create the PRICE table
            HTableDescriptor tablePrice = new HTableDescriptor(TableName.valueOf(TABLE_PRICE));
            tablePrice.addFamily(new HColumnDescriptor(CF_PRICE_DETAILS).setCompressionType(Algorithm.NONE));
            System.out.print("Creating table PRICE. ");
            createOrOverwrite(admin, tablePrice);
            System.out.println(" Done.");

            // Create the POSITION ACCOUNT table
            HTableDescriptor tablePositionAccount = new HTableDescriptor(TableName.valueOf(TABLE_POSITION_ACCOUNT));
            tablePositionAccount.addFamily(new HColumnDescriptor(CF_ACCOUNT_DETAILS).setCompressionType(Algorithm.NONE));
            System.out.print("Creating table POSITION ACCOUNT. ");
            createOrOverwrite(admin, tablePositionAccount);
            System.out.println(" Done.");


        }
    }



    public static void main(String... args) throws IOException {
        
    		String hbaseHost = args[0].substring(args[0].indexOf('=')+1);
		String zkHost = args[1].substring(args[1].indexOf('=')+1);
		
		 System.out.println("Connection to Hbase: "+hbaseHost);
		 System.out.println("Connection to zk: "+zkHost);		
    	
    		Configuration config =  HBaseConfiguration.create();
        config.setInt("timeout", 120000);
        config.set("hbase.master", hbaseHost + ":16000");
        config.set("hbase.zookeeper.quorum",zkHost);
        config.set("hbase.zookeeper.property.clientPort", "2181");
        createSchemaTables(config);
    }
}
