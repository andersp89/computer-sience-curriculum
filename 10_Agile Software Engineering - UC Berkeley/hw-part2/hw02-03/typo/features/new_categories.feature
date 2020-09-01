Feature: Create Categories
  As a blog administrator
  In order to group similar articles
  I want to be able to group articles into categories

  Background:
    Given the blog is set up
    And I am logged into the admin panel

  Scenario: Successfully create category
    Given I am on the admin categories page
    When I fill in "category_name" with "category1"
    And I fill in "category_description" with "this is my new category"
    And I press "Save"
    Then I should see "category1"
    And I should see "this is my new category"

  Scenario: view categories
    When I go to the articles category page
    Then I should see "Categories"