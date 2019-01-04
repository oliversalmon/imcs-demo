Feature: Connect and stream Trades to Hz

  Scenario: Connect to Local Kafka and Read Trades
    Given the list of trades
      |tradeId|firmTradeId|tradeType|executionId|originalTradeDate|clientId|executionVenueId|executingTraderId|positionAccountId|instrumentId|price|quantity|currency|tradeDate|settlementDate|
      |TR1|TRF1|0|TFE1|25-10-2018|1|1|1|1|1|10|10|USD|25-10-2018|25-10-2018|
      |TR2|TRF2|0|TFE2|25-10-2018|2|1|1|2|1|10|-10|USD|25-10-2018|25-10-2018|
    When the streamer reads from source
    Then the streamer pushes the following to Hz
      |tradeId|firmTradeId|tradeType|executionId|originalTradeDate|clientId|executionVenueId|executingTraderId|positionAccountId|instrumentId|price|quantity|currency|tradeDate|settlementDate|
      |TR1|TRF1|0|TFE1|25-10-2018|1|1|1|1|1|10|10|USD|25-10-2018|25-10-2018|
      |TR2|TRF2|0|TFE2|25-10-2018|2|1|1|2|1|10|-10|USD|25-10-2018|25-10-2018|
