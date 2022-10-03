[![test-java](https://github.com/cucumber/split-java/actions/workflows/test-java.yml/badge.svg)](https://github.com/cucumber/split-java/actions/workflows/test-java.yml)

# Cucumber Split

Cucumber Split is a [Cucumber-JVM](https://github.com/cucumber/cucumber-jvm/#readme) plugin
that lets you toggle [Split](https://www.split.io/) feature flags on or off using [Cucumber tags](https://cucumber.io/docs/cucumber/api/#tags).

## Motivation

Software teams that use Split [feature flags](https://martinfowler.com/articles/feature-toggles.html)
need to test that the software works as expected for different combinations of feature flags.

With Cucumber Split you can toggle feature flags declaratively with tags, and then
verify that the software works as expected.

## Example

Imagine we're building firmware for a coffee machine, and that we can deploy the new firmware over the Internet
with continuous deployment.

The coffee machine is currently able to serve **espresso**, and **caffe latte**, and the team is currently working 
on an experimental new feature to serve **flat white** as well.

We have decided to make the new **flat white** drink available to a small set of customers, so we have introduced a 
feature flag to control whether it is available or not.

Even though only a small set of customers will have the **flat white** functionality, we want to test that it works as
expected. We have two scenarios that verify options in the drink selection menu; One where **flat white** is not available
(the feature flag is `off`), and another where it is (the feature flag is `on`):

```gherkin
Feature: Make Coffee

  @split[flat-white:off]
  Scenario: Display available drinks
    Given the machine is not empty
    Then the following drinks should be available:
      | name          | price |
      | espresso      |  1.90 |
      | caffe latte   |  2.30 |

  @split[flat-white:on]
  Scenario: Display available drinks (including the new experimental flat white)
    Given the machine is not empty
    Then the following drinks should be available:
      | name          | price |
      | espresso      |  1.90 |
      | flat white    |  2.10 |
      | caffe latte   |  2.30 |
```

## Installation

First, add Cucumber Split to your project:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-split</artifactId>
    <version>0.0.2</version>
    <scope>test</scope>
</dependency>
```

```groovy
// build.gradle(.kts)
dependencies {
  testImplementation 'io.cucumber:cucumber-split:0.0.2'
}
```

Next, configure Cucumber to toggle Split features based on tags. Define a [before hook](https://cucumber.io/docs/cucumber/api/#hooks):

```java
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.split.CucumberSplit;
import io.split.client.testing.SplitClientForTest;

public class StepDefinitions {
    private final SplitClientForTest splitClient = new SplitClientForTest();

    @Before
    public void configureSplit(Scenario scenario) {
        CucumberSplit.configureSplit(splitClient, scenario);
    }
}
```

The `configureSplit` before hook will run before each scenario and configure the `splitClient` with feature flags declared
as scenario tags.

## Production code design

Your production code must be designed in such a way that a `io.split.client.SplitClient` can be passed
to it, rather than creating its own instance. This is so that we can pass it the `splitClient` instantiated and configured
by Cucumber.

One way to achieve this is with the [dependency injection](https://en.wikipedia.org/wiki/Dependency_injection) pattern.

In the example, the `CoffeeMachine` class expects a `splitClient` to be passed via its constructor.

## Default feature flags

You may have some flags that you want to set by default, only to override them in certain scenarios. One way to achieve this
is via Cucumber's [tag inheritance](https://cucumber.io/docs/cucumber/api/#tags):

```gherkin
# Turn flat white off by default
@split[flat-white:off]
Feature: Make Coffee

  Scenario: Display available drinks

  # Override flat white to on just in this scenario
  @split[flat-white:on]
  Scenario: Display available drinks (including the new experimental flat white)
```

## Suggestions, questions or feedback

Please use the [issue tracker](/cucumber/split-java/issues) if you have suggestions, questions or feedback about this library.
