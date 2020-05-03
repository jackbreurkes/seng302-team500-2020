Feature: Editing an entire profile with location data included

  Scenario Outline: Create a user with location information
    Given there is not a user with the email "new.user@test.com" in the database
    And I am in the process of creating a new user
    And I have entered my location where I am normally based as <city> <state> <country>
    And I have entered valid information for all other fields
    When I create the new user with the email "new.user@test.com"
    Then the user should show their location info as <city> <state> <country>

    Examples:
      | city         | state           | country     |
      | Christchurch | Canterbury      | New Zealand |
      | Sydney       | New South Wales | Australia   |
      | Oslo         | null            | Norway      |
      | The Hague    | null            | Netherlands |


  Scenario Outline: Update a user without changing location information
    Given there is a user with the email "existing.user@test.com" in the database
    And I am logged in as the user with the email "existing.user@test.com"
    And I have set my location where I am normally based as <city> <state> <country>
    And I am updating my profile information
    And I am not changing my location where I am normally based
    When I update my profile information
    Then my profile should show my location info as <city> <state> <country>

    Examples:
      | city         | state           | country     |
      | Christchurch | Canterbury      | New Zealand |
      | Sydney       | New South Wales | Australia   |
      | Oslo         | null            | Norway      |
      | The Hague    | null            | Netherlands |
