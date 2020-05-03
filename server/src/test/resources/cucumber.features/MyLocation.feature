Feature: Create, read, update and delete my location

  Background:
    Given there is a user with the email "lola.cation@test.com" in the database
    And I am logged in as the user with the email "lola.cation@test.com"


  Scenario Outline: Set my location as a valid location
    Given I have not set my location
    When I set my location where I am normally based as <city> <state> <country>
    Then my profile should show my location info as <city> <state> <country>

    Examples:
      | city         | state           | country     |
      | Christchurch | Canterbury      | New Zealand |
      | Sydney       | New South Wales | Australia   |
      | Oslo         | null            | Norway      |
      | The Hague    | null            | Netherlands |


  Scenario Outline: Check my location when it is set
    Given I have set my location where I am normally based as <city> <state> <country>
    When I view my profile
    Then my profile should show my location info as <city> <state> <country>

  Examples:
    | city         | state           | country     |
    | Christchurch | Canterbury      | New Zealand |
    | Sydney       | New South Wales | Australia   |
    | Oslo         | null            | Norway      |
    | The Hague    | null            | Netherlands |


  Scenario: Check my location when it is not set
    Given I have not set my location
    When I view my profile
    Then my location info should be empty


  Scenario Outline: Set my location as an invalid location
    When I try to set my location where I am normally based as <city> <state> <country>
    Then I should receive an error message informing me that a field that I entered was invalid

    Examples:
      | city         | state           | country     |
      | Christchorch | Canterbury      | New Zealand |
      | Sydney       | New South Wales | Down Under  |
      | null         | null            | Norway      |
      | The Hague    | null            | null        |


  Scenario Outline: Update my location
    Given I have set my location where I am normally based as "Christchurch" "Canterbury" "New Zealand"
    When I set my location where I am normally based as <city> <state> <country>
    Then my profile should show my location info as <city> <state> <country>

    Examples:
      | city         | state           | country     |
      | Sydney       | New South Wales | Australia   |
      | Oslo         | null            | Norway      |
      | The Hague    | null            | Netherlands |
      | Christchurch | Canterbury      | New Zealand |


  Scenario: Update my location
    Given I have set my location where I am normally based as "Christchurch" "Canterbury" "New Zealand"
    When I set my location where I am normally based as "null" "null" "null"
    Then my location info should be empty
