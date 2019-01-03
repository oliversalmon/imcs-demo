Feature: Connect and stream Positions to Hz

  Scenario: Connect to Trade Source, Read Trades, aggregate to Positions and push to Hz
    Given the list of trades to aggregate into Positions
      |tradeId|firmTradeId|tradeType|executionId|originalTradeDate|clientId|executionVenueId|executingTraderId|positionAccountId|instrumentId|price|quantity|currency|tradeDate|settlementDate|
      |TR1|TRF1|0|TFE1|25-10-2018|CL1|1|1|POS1|INS1|10|100|USD|25-10-2018|25-10-2018|
      |TR2|TRF2|0|TFE2|25-10-2018|CL1|1|1|POS1|INS1|10|200|USD|25-10-2018|25-10-2018|
    When the streamer reads from trade source for positions
    And the spot price for the instruments are as follows
      |priceId|instrumentid|price|timeStamp|
      |PR1|INS1|20|1546508733558|
    Then the streamer aggregates the following positions and calculates the pnl as follows
      |accountId|instrumentid|size|pnl|
      |POS1|INS1|300|3000.0|
    And pushes the above position into Position Map in Hz
      |accountId|instrumentid|size|pnl|
      |POS1|INS1|300|3000.0|