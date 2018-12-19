Feature: publish a single position into a queue and read from it

  Scenario: client publishes the following data to the queue
    Given the list of Position Accounts to publish to positionQ
      | accountId | instrumentid | size | pnl |
      | ACC1      | INS1         | 100  | 0.0 |
      | ACC1      | INS2         | 100  | 0.0 |
      | ACC2      | INS1         | 100  | 0.0 |
      | ACC2      | INS2         | 100  | 0.0 |
    When the client publishes the above data to positionQ
    Then the client receives the data with success from to positionQ
    And the messages received contains the following data
      | accountId | instrumentid | size | pnl |
      | ACC1      | INS1         | 100  | 0.0 |
      | ACC1      | INS2         | 100  | 0.0 |
      | ACC2      | INS1         | 100  | 0.0 |
      | ACC2      | INS2         | 100  | 0.0 |