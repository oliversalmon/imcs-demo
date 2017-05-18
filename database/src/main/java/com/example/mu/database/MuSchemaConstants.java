package com.example.mu.database;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * This interface contains all the constants defining the HBase schema
 * Created by oliver.salmon@gmail.com on 02/05/2017.
 */
public interface MuSchemaConstants {
    // HBASE connectivity
    String HBASE_HOST = "138.68.176.187";
    String ZK_HOST = "138.68.176.187";

    // TRADE Table
    String TABLE_TRADE = "mu:trade";
    byte[] CF_TRADE_DETAILS = Bytes.toBytes("td");
    byte[]  TRADE_ID = Bytes.toBytes("tradeId")
    ,                           SECONDARY_TRADE_ID = Bytes.toBytes("secondaryTradeId")
    ,                           FIRM_TRADE_ID_FK = Bytes.toBytes("firmTradeId")
    ,                           SECONDARY_FIRM_TRADE_ID_FK  = Bytes.toBytes("secondaryFirmTradeId")
    ,                           TRADE_TYPE = Bytes.toBytes("tradeType")
    ,                           SECONDARY_TRADE_TYPE = Bytes.toBytes("secondaryTradeType")
    ,                           EXECUTION_ID = Bytes.toBytes("executionId")
    ,                           ORIGINAL_TRADE_DATE = Bytes.toBytes("originalTradeDate")
    ,                           EXECUTING_FIRM_ID_FK = Bytes.toBytes("executingFirmId")
    ,                           CLIENT_ID_FK = Bytes.toBytes("clientId")
    ,                           EXECUTION_VENUE_ID_FK = Bytes.toBytes("executionVenueId")
    ,                           EXECUTING_TRADER_ID_FK = Bytes.toBytes("executingTraderId")
    ,                           POSITION_ACCOUNT_ID_FK = Bytes.toBytes("positionAccountId")
    ,                           INSTRUMENT_ID_FK = Bytes.toBytes("instrumentId")
    ,                           TRADE_PRICE = Bytes.toBytes("price")
    ,                           QUANTITY = Bytes.toBytes("quantity")
    ,                           CURRENCY = Bytes.toBytes("currency")
    ,                           TRADE_DATE = Bytes.toBytes("tradeDate")
    ,                           SETTLEMENT_DATE = Bytes.toBytes("settlementDate");

    // PARTY Table
    String TABLE_PARTY = "mu:party";
    byte[] CF_PARTY_DETAILS = Bytes.toBytes("pd");
    byte[]  PARTY_ID = Bytes.toBytes("partyId")
    ,                           SHORT_NAME = Bytes.toBytes("shortName")
    ,                           NAME = Bytes.toBytes("name")
    ,                           ROLE = Bytes.toBytes("role")
    ,                           POSTION_ACC_FK = Bytes.toBytes("positionAccId");

    // INSTRUMENT Table
    String TABLE_INSTRUMENT = "mu:instrument";
    byte[] CF_INSTRUMENT_DETAILS = Bytes.toBytes("id");
    byte[]  INSTRUMENT_ID = Bytes.toBytes("instrumentId")
    ,                           SYMBOL = Bytes.toBytes("symbol")
    ,                           PRODUCT = Bytes.toBytes("product")
    ,                           ASSET_CLASS = Bytes.toBytes("assetClass")
    ,                           ISSUER = Bytes.toBytes("issuer");

    // PRICE Table
    String TABLE_PRICE = "mu:price";
    byte[] CF_PRICE_DETAILS = Bytes.toBytes("pxd");
    byte[]  PRICE_ID = Bytes.toBytes("priceId")
    ,                           PRICE_INSTRUMENT_ID = Bytes.toBytes("instrumentId")
    ,                           PRICE = Bytes.toBytes("price")
    ,                           TIMESTAMP = Bytes.toBytes("timeStamp");

    // POSITION_ACCOUNT Table
    String TABLE_POSITION_ACCOUNT = "mu:account";
    byte[] CF_ACCOUNT_DETAILS = Bytes.toBytes("pad");
    byte[]  ACCOUNT_ID = Bytes.toBytes("accountId")
    ,                           ACC_INSTRUMENT_ID = Bytes.toBytes("instrumentId")
    ,                           SIZE = Bytes.toBytes("size")
    ,                           PNL = Bytes.toBytes("pnl");


}
