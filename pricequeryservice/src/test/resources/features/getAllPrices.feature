Feature: the prices can be retrieved

  Scenario: client makes call to GET /getAllPrices
    Given the list of Prices
      |priceId|instrumentId|price|timestamp|
      | 1 | INS1 | 100 | 0 |
      | 2 | INS2 | 150 | 0 |
      | 3 | INS3 | 200 | 0 |
      | 4 | INS4 | 250 | 0 |
    When the client says /getAllPrices
    Then the client receives response status code of 200
    And response contains the above list of Prices
