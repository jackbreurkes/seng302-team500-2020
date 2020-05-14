# SEARCH STYLE:
  # NAME SEARCH
  # not case sensitive (not tested here)
  # search pattern: for each word in the search, match word plus any non-blank input following
  # e.g. "word word word" -> "word(![0-9])* word(![0-9])* word(![0-9])*"
  # names should be matchable both with and without the middle name
  # e.g. "Fname(Mname )? Lname"
  # written by Jack and Olivia

  # EMAIL SEARCH
  # must match full text before '@' symbol if there is no @ in the search query
  # if there is an @ in the query, match the query string then anything after
  # e.g. test@gmail.co(.*)

  # NICKNAME SEARCH
  # similar to full name search, but for only one word.


Feature: Searching user profiles

  Background:
    Given the following profiles have been added to the database
      | profile id | first name | middle name | last name   | email                 | nickname     | interests                 |
      | 1          | Maurice    | B           | Benson      | mau@gmail.com         | mauxx500     | Walking, Running          |
      | 2          | Sally      |             | Callahan    | sally@yahoo.com       | sally.calla  |                           |
      | 3          | James      | John        | Callahan    | james@xtra.co.nz      | johnny500    | Scootering, Skateboarding |
      | 4          | Clarice    |             | Holmes      | clarice@test.co.nz    | clararara    | Cycling                   |
      | 5          | Maurice    |             | Jameson     | mau@govt.nz           | maurattack   | Running                   |
      | 6          | Maude      | B           | Benson      | maude@xtra.co.nz      | benson4eva   |                           |
      | 7          | Benedict   |             | Cumberbatch | ben@hollywood.com     | sh3r10ck     | Acting                    |
    And I am logged in as the profile with id 1

    @U11-search-full-name
    Scenario Outline: Search users by full name
      When I search for profiles by "full name" with the search term <name>
      Then I should receive exactly <count> results

    Examples:
      | name             | count |
      | Maurice          | 2     |
      | Call             | 2     |
      | Ben              | 3     |
      | Zoe              | 0     |
      | rice             | 0     |
      | Maurice B Benson | 1     |
      | B Benson         | 2     |
      | Mau Ben          | 2     |
      | James Callahan   | 1     |
      | Maude B          | 1     |

      @U11-search-email
      Scenario Outline: Search users by email
        When I search for profiles by "email" with the search term <email>
        Then I should receive exactly <count> results

      Examples:
        | email                    | count |
        | mau                      | 2     |
        | sally                    | 1     |
        | zoe                      | 0     |
        | maude@xtra.co.nz         | 1     |
        | test@gmail.com           | 0     |
        | clarice@test             | 1     |
        | mes@xtra.co.nz           | 0     |
        | @xtra.co.nz              | 0     |
        | maude@xtra.co            | 1     |
        | mau@                     | 2     |

    @U11-search-nickname
    Scenario Outline: Search users by nickname
      When I search for profiles by "nickname" with the search term <nickname>
      Then I should receive exactly <count> results

      Examples:
        | nickname         | count |
        | mau              | 2     |
        | 500              | 0     |
        | benson4eva       | 1     |
        | c                | 1     |


    @U12-search-interests
    Scenario Outline: Search users by interests
      When I search for profiles interested in <interests> that are <method>ed together
      Then I should receive exactly <count> results

      Examples:
        | interests                    | method | count |
        | Scootering Skateboarding     | and    | 1     |
        | Running Cycling              | or     | 3     |
        |                              | or     | 0     |
        | Running                      | and    | 2     |
        | Running Cycling              | and    | 0     |
