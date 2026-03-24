@api
Feature: Reqres user API

  # Summary
    # As a consumer of the Reqres REST API
    # I want to validate all user management and auth endpoints
    # So that I can ensure correct status codes, response structure and data integrity


  # LIST USERS
 # Get a list of users using GET Method
  @smoke
  Scenario: List users on page 1 returns correct pagination metadata
    When I request the list of users on page 1
    Then the response status code should be 200
    And the response should contain the following fields
      | page        |
      | per_page    |
      | total       |
      | total_pages |

  # Cross-page validation
  @regression
  Scenario: Total user count matches the number of unique user IDs across all pages
    Given I am connected to the Reqres API
    When I request the list of users on page 1
    And I retrieve users from all available pages
    Then the response status code should be 200
    And the total user count should equal the number of unique user IDs

  # SINGLE USER
  # Get a single user using GET Method
  @smoke
  Scenario: Get single user returns correct data for a known user
    When I request details for user with ID 3
    Then the response status code should be 200
    And the response should contain the following user details
      | first_name | email               |
      | Emma       | emma.wong@reqres.in |

  @regression
  Scenario: Get single user returns 404 for a non-existent user
    When I request details for user with ID 55
    Then the response status code should be 404

  # CREATE USER
  # It will Create a User for Examples
  @smoke
  Scenario Outline: Create a user returns 201 with all required response fields
    When I create a user with name "<Name>" and job "<Job>"
    Then the response status code should be 201
    And the response should contain name "<Name>" and job "<Job>"
    And the response should contain a non-empty id and createdAt

    Examples:
      | Name  | Job     |
      | Peter | Manager |
      | Liza  | Sales   |

  # UPDATE USER
  # Verify by PUT method for update user
  @smoke
  Scenario: Update a user returns 200 with updated name and job
    When I update user with ID 2 with name "John" and job "Senior Manager"
    Then the response status code should be 200
    And the response should contain name "John" and job "Senior Manager"
    And the response should contain a non-empty updatedAt

  # DELETE USER
  # Verify by DELETE Method User gets Deleted
  @regression
  Scenario: Delete a user returns 204 with no response body
    When I delete user with ID 2
    Then the response status code should be 204

  # LOGIN
  @smoke
  Scenario: Successful login with valid credentials returns a token
    When I login to the API with email "eve.holt@reqres.in" and password "cityslicka"
    Then the response status code should be 200
    And the response should contain a non-empty authentication token

  @regression
  Scenario: Login with missing password returns 400 and error message
    When I login to the API with email "eve.holt@reqres.in" and empty password
    Then the response status code should be 400
    And the response should contain error message "Missing password"

  # DELAYED RESPONSE
  # Validates API still returns valid data under server delay
  @regression
  Scenario: Delayed user list response returns valid users with unique IDs
    When I request the user list with a response delay of 3 seconds
    Then the response status code should be 200
    And every user in the response should have a unique ID
    And every user should have non-empty fields
      | email      |
      | first_name |
      | last_name  |