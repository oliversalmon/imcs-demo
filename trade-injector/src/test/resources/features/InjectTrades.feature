Feature: Trades injected into Kafka topic: trade

  Scenario: injector will inject the following given trades successfully
    Given the list of trades
      |tradeId|firmTradeId|tradeType|executionId|originalTradeDate|clientId|executionVenueId|executingTraderId|positionAccountId|instrumentId|price|quantity|currency|tradeDate|settlementDate|
      |TR1|TRF1|0|TFE1|25-10-2018|1|1|1|1|1|10|10|USD|25-10-2018|25-10-2018|
      |TR2|TRF2|0|TFE2|25-10-2018|2|1|1|2|1|10|-10|USD|25-10-2018|25-10-2018|
    When the client injects trades
    Then the client receives exact match of success acks



