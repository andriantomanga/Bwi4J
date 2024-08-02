# Bwi4j
# Blacklisted Words Inspector For Java

![Version](https://img.shields.io/badge/version-v1.0.0-green.svg)
![Build Status](https://github.com/andriantomanga/Bwi4j/actions/workflows/main.yml/badge.svg)
![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)

Bwi4j is a Java library that allows you to define and enforce rules on the names of classes, methods, and fields within specified packages. This can be useful for enforcing naming conventions or avoiding the use of certain prohibited terms in your codebase.

Similar to ArchUnit, it is ideally used in a test file. It is useful, for example, to prevent contamination of the domain package in a `Hexagonal architecture`.

## Usage example

In the following example, we forbid certain terms in our domain package.
Thus, if a collaborator adds a class named `ProductRestClient` in our domain, the test will fail.

```java
package org.bwi4j.demos;

import org.bwi4j.BwiRule;
import org.bwi4j.exception.BwiException;
import org.junit.jupiter.api.Test;

public class Bwi4jExamplesTest {
    @Test
    void elements_in_defined_packages_should_not_contain_the_given_terms() throws BwiException {
        // Given:
        BwiRule rule = BwiRule.elements()
                .inThesePackages("org.myproject.domain")
                .mustNotContainTheseTerms("Rest", "Hexa", "Dao", "Repository", "Controller", "Service", "Util", "Helper");

        // When + Then:
        rule.check();
    }
}
```

## The exception that proves the rule

If you want a particular element (a class, method, or field) to be ignored by the Bwi4J scanner, simply annotate it with the `@BwiIgnore` annotation.

```java
    @BwiIgnore
    public static class BadClassButIgnored {
        private String validField;
        private int forbiddenField;
        public void validMethod() {}
    }
```

## Examples

For mor examples, please refer to the [Examples repo](https://github.com/andriantomanga/Bwi4J-examples).

