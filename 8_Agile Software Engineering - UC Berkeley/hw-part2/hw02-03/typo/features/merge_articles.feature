Feature: Merge Articles
  As a blog administrator
  In order to combine similar articles
  I want to be able to merge two similar articles into one

  Background:
    Given the blog is set up
    And I am logged into the admin panel
    # create one article to merge with
    Given I am on the new article page
    When I fill in "article_title" with "My first new article"
    And I fill in "article__body_and_extended_editor" with "Lorem Ipsum"
    And I press "Publish"

  Scenario: Successfully merge two articles
    Given I am on the admin content page
    When I follow "Hello World!"
    When I fill in "merge_with" with "3"
    And I press "Merge With This Article"
    Then I should be on the admin content page
    And I should see "Successfully merged articles"
    When I go to the home page
    Then I should see "Hello World!"
    When I follow "Hello World!"
    Then I should see "Lorem Ipsum"
    And I should see "Welcome to Typo. This is your first article. Edit or delete it, then start blogging!"

  Scenario: Merging an article with itself
    Given I am on the admin content page
    When I follow "Hello World!"
    When I fill in "merge_with" with "1"
    And I press "Merge With This Article"
    Then I should see "Cannot merge an article with itself"

  Scenario: Merging an article with an article that does not exist
    Given I am on the admin content page
    When I follow "Hello World!"
    When I fill in "merge_with" with "4"
    And I press "Merge With This Article"
    Then I should see "The selected article doesn't exist"