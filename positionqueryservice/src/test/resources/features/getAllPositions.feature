Feature: the positions can be retrieved

  Scenario: client makes call to GET /getAllPositionAccounts
    Given the list of Position Accounts
      |accountId|instrumentid|size|pnl|
      | ACC1 | INS1 | 100 | 0.0 |
      | ACC1 | INS2 | 100 | 0.0 |
      | ACC2 | INS1 | 100 | 0.0 |
      | ACC2 | INS2 | 100 | 0.0 |
    When the client calls /getAllPositionAccounts
    Then the client receives response status code of 200
    And response contains the above list of Position Accounts
