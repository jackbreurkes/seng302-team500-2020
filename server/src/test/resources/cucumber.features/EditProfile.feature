@U10-profile-visibility
Feature: Viewing User Profiles

  Background:
    Given there is a profile in the database with profile id 1

  Scenario: View a profile when not logged in
    Given I am not not logged in
    When I try to view the profile with id 1
    Then I will receive a message that I am not logged in

  Scenario: User is logged in
    Given I am logged in as the profile with id 2
    When I try to view the profile with id 1
    Then I will view the profile with id 1

  Scenario: User can edit their own profile
    Given I am logged in as the profile with id 1
    And I am trying to update the profile with id 1's "bio" information to "This is my new bio!"
    When I try to update the profile information of the profile with id 1
    Then the profile with id 1's "bio" will have been updated to "This is my new bio!"

  Scenario: User cannot edit other profile
    Given I am logged in as the profile with id 2
    And I am trying to update the profile with id 1's "bio" information to "This is my new bio!"
    When I try to update the profile information of the profile with id 1
    Then I will receive a message that I am not authenticated as the target user

  Scenario: Admin edits profile
    Given there is an admin user in the database with profile id 2
    Given I am logged in as the user with id 2
    And I am trying to update the profile with id 1's "bio" information to "This is user 1's new bio!"
    When I try to update the profile information of the profile with id 1
    Then the profile with id 1's "bio" will have been updated to "This is user 1's new bio!"
