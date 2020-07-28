Feature: Followers

  Background:
    Given the following profiles have been added to the database
      | first name | middle name | last name   | email                 | password |
      | Maurice    | B           | Benson      | test@user.com         | password |
      | Sally      |             | Callahan    | activity@user.com     | password |
    And the user with email "activity@user.com" has created an activity with the name "Test Activity"
    And the following activities have been created by the user with email "activity@user.com" and password "password"
      | name              | description           | is continuous | location | activity types |
      | Test Activity     | This is a req. field  | true          | Area 51  | Running        |
    And the profile with email "test@user.com" is following the activity with the name "Test Activity"
    And I am logged in as the profile with email "test@user.com" and password "password"

    @U16-followers
    Scenario Outline: Change activity details
      Given the profile with email "activity@user.com" has changed "Test Activity"'s "<detail>" to "<value>"
      When I check my homefeed
      Then I should see an entry that "Test Activity"'s "<detail>" has been changed to "<value>" by "Sally Callahan"
      And I should see an entry that I have followed "Test Activity"
      And my homefeed entries should be ordered by timestamp

    Examples:
      | detail           | value                        |
      | Description      | this should be really fun!   |
      | Location         | The Moon                     |

      @U16-followers
      Scenario Outline: Deleting an activity
        Given the profile with email "activity@user.com" has changed "Test Activity"'s "<detail>" to "<value>"
        And the profile with email "activity@user.com" has deleted "Test Activity"
        When I check my homefeed
        And I should see an entry that "Test Activity" has been deleted by "Sally Callahan"
        Then I should not see an entry that I have followed "Test Activity"
        And I should not see an entry that "Test Activity"'s "<detail>" has been changed to "<value>"

      Examples:
        | detail           | value                        |
        | Description      | "this should be really fun!" |

      @U16-followers
      Scenario Outline: Unfollowing an activity
        Given the profile with email "activity@user.com" has changed "Test Activity"'s "<detail>" to "<value>"
        And I have unfollowed the activity "Test Activity"
        When I check my homefeed
        Then I should see an entry that I have unfollowed "Test Activity"
        And I should not see an entry that "Test Activity"'s "<detail>" has been changed to "<value>"
        And I should not see an entry that I have followed "Test Activity"

        Examples:
          | detail           | value                        |
          | Description      | "this should be really fun!" |


      @U16-followers
      Scenario Outline: Change activity time frame
        Given the activity "Test Activity"'s time frame is from "<old start time>" to "<old end time>"
        And the profile with email "activity@user.com" has changed "Test Activity"'s time frame to be from "<new start time>" to "<new end time>"
        When I check my homefeed
        Then I should see an entry that "Test Activity"'s "time frame" has been set to "<new start time>" to "<new end time>" by "Sally Callahan"

        Examples:
          | old start time           | old end time             | new start time           | new end time             |
          | null                     | null                     | 2021-01-20T08:00:00+1300 | 2021-02-20T08:00:00+1300 |
          | 2021-02-20T08:00:00+1300 | 2021-02-22T08:00:00+1300 | 2021-01-20T08:09:00+1300 | 2021-02-20T08:15:30+1300 |
          | 2021-06-20T08:00:00+1300 | 2021-10-20T08:00:00+1300 | null                     | null                     |
