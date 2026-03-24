@ui
Feature: SauceDemo login and checkout validation
  # Summary
    # As a user of SauceDemo
    # I want to validate login behavior and shopping flow
    # So that I ensure system works correctly for different users

  # Scenario 1: Login outcome validation - standard_user lands on products page

  @smoke
  Scenario: Standard user can login successfully
    Given I am on the SauceDemo login page
    When I login with username "standard_user" and password "secret_sauce"
    Then I should be on the products page

  # Scenario 2: Login outcome validation - locked_out_user sees error

  @smoke
  Scenario: Locked out user sees error message on login
    Given I am on the SauceDemo login page
    When I login with username "locked_out_user" and password "secret_sauce"
    Then I should see the error message "Epic sadface: Sorry, this user has been locked out."

  # Scenario 3: problem_user and visual_user land on products page

  @regression
  Scenario: Problem user can login and lands on products page
    Given I am on the SauceDemo login page
    When I login with username "problem_user" and password "secret_sauce"
    Then I should be on the products page

  @regression
  Scenario: Visual user can login and lands on products page
    Given I am on the SauceDemo login page
    When I login with username "visual_user" and password "secret_sauce"
    Then I should be on the products page

  # Scenario 4: Negative login - wrong credentials

  @regression
  Scenario: Login with invalid credentials shows error
    Given I am on the SauceDemo login page
    When I login with username "wrong_user" and password "wrong_pass"
    Then I should see the error message "Epic sadface: Username and password do not match any user in this service"


  @regression
  Scenario: Performance glitch user should still login successfully
    Given I am on the SauceDemo login page
    When I login with username "performance_glitch_user" and password "secret_sauce"
    Then I should be on the products page

  # Scenario 6: Full E2E checkout flow - standard user only

  @smoke
  Scenario: Standard user completes full checkout flow
    Given I am on the SauceDemo login page
    And I login with username "standard_user" and password "secret_sauce"
    And I should be on the products page

    # Add items to basket
    When I add the following items to the basket
      | Sauce Labs Backpack      |
      | Sauce Labs Fleece Jacket |
      | Sauce Labs Bolt T-Shirt  |
      | Sauce Labs Onesie        |
    Then the shopping cart badge should show 4

    # Remove one item
    When I remove "Sauce Labs Onesie" from the basket
    Then the shopping cart badge should show 3

    # Cart validation
    When I click on the shopping cart icon
    Then I should be on the cart page
    And each item in the cart should have quantity 1

    # Proceed to checkout
    When I click on the CHECKOUT button
    Then I should be on the checkout information page

    # Fill checkout details
    When I enter checkout details
      | first_name | last_name | zip_code |
      | John       | Doe       | EC1A 9JU |
    And I click on the CONTINUE button
    Then I should be on the checkout overview page

    # Price validation
    Then the item total should equal the sum of all selected item prices
    And a tax of 8 percent should be applied to the item total
    And the order total should equal the item total plus tax
