Feature: a single position account and its details must be retrieved

  Scenario: client makes call to GET /getPositionAccount/ACC1
    Given the list of Position Accounts to access a single account
      | accountId | instrumentid | size | pnl |
      | ACC1      | INS1         | 100  | 0.0 |
      | ACC1      | INS2         | 100  | 0.0 |
      | ACC2      | INS1         | 100  | 0.0 |
      | ACC2      | INS2         | 100  | 0.0 |
    When the client calls /getPositionAccount/ACC1
    Then the client receives response status code of 200
    And response contains only the following position account
      | accountId | instrumentid | size | pnl |
      | ACC1      | INS1         | 100  | 0.0 |
      | ACC1      | INS2         | 100  | 0.0 |