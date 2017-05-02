package com.example.mu.domain;

import java.util.Date;

/**
 * Created by oliverbuckley-salmon on 28/04/2017.
 */
public class Trade {

    private String  tradeId
    ,               secondaryTradeId
    ,               firmTradeId
    ,               secondaryFirmTradeId
    ,               tradeType
    ,               secondaryTradeType
    ,               executionId;
    private Date    originalTradeDate;
    private String  executingFirmId
    ,               clientId
    ,               executionVenueId
    ,               executingTraderId
    ,               positionAccountId
    ,               instrumentId;
    private double  price;
    private int     quantity;
    private String  currency;
    private Date    tradeDate
    ,               settlementDate;
}
