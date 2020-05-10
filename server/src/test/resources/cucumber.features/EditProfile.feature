@U10-profile-visibility
Feature: Viewing User Profiles

  Background:
    Given there is a user in the database with profile id 1

  Scenario: View a profile when not logged in
    Given I am not not logged in
    When I try to view the profile with id 1
    Then I will receive a message that they are not logged in

  Scenario: User is logged in
    Given I am logged in as the profile with id 2
    When I try to view the profile with id 1
    Then I will view the profile with id 1

  Scenario: User can edit their own profile
    Given I am logged in as the profile with id 1
    And I am updating my profile's "bio" information to "This is my new bio!"
    When I update my profile information
    Then the profile with id 1 will be updated

  Scenario: User cannot edit other profile
    Given I am logged in as the profile with id 2
    When I try to edit the profile with id 1
    Then the profile with id 1 will not be updated

  Scenario: Admin edits profile
    Given I am logged in as an admin user
    And I am updating the profile with id 1's "bio" information to "This is user 1's new bio!"
    When I update the profile information of the profile with id 1
    Then the profile with id 1 will be updated
