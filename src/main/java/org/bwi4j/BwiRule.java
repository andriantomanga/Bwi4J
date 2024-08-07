/**
 * Copyright (c) 2024 Nabil Andriantomanga
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.bwi4j;

import io.github.classgraph.ClassGraph;
import org.bwi4j.annotation.BwiIgnore;
import org.bwi4j.exception.BwiException;
import org.bwi4j.implementation.DefaultPackageScanner;
import org.bwi4j.implementation.DefaultTermChecker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The BwiRule class is used to define and enforce validation rules on specified packages.
 * It allows specifying packages to scan and blacklisted terms that the classes and their elements must not contain.
 * <p>
 * Example usage:
 * <pre>
 *     BwiRule rule = BwiRule.elements()
 *                           .inThesePackages("com.example")
 *                           .mustNotContainTheseTerms("temp", "test");
 *     rule.check();
 * </pre>
 *
 * @author Nabil Andriantomanga (https://github.com/andriantomanga)
 * @version 1.0
 * @since 2024
 */
public class BwiRule {
    private static final Logger LOGGER = Logger.getLogger(BwiRule.class.getName());

    private final Set<String> packagesToScan = new HashSet<>();
    private final Set<String> blacklistedTerms = new HashSet<>();

    private BwiRule() {
    }

    public static BwiRule elements() {
        return new BwiRule();
    }

    /**
     * Specifies the packages to be scanned.
     *
     * @param packages the packages to scan
     * @return the current instance of BwiRule
     */
    public BwiRule inThesePackages(String... packages) {
        packagesToScan.addAll(Arrays.asList(packages));
        return this;
    }

    /**
     * Specifies the terms that must not be contained in the classes and their elements.
     *
     * @param terms the blacklisted terms
     * @return the current instance of BwiRule
     */
    public BwiRule mustNotContainTheseTerms(String... terms) {
        var preparedTerms = Arrays.stream(terms)
                .map(term -> term.toLowerCase().strip())
                .collect(Collectors.toSet());
        blacklistedTerms.addAll(preparedTerms);
        return this;
    }

    /**
     * Performs the check on the specified packages and their components (classes, methods, fields ...) against the defined rules.
     *
     * @throws BwiException if an error occurs during scanning or checking
     */
    public void check() throws BwiException {
        var scanner = new DefaultPackageScanner(new DefaultTermChecker(blacklistedTerms));

        for (var currentPackage : packagesToScan) {
            LOGGER.log(Level.FINE, "Scanning package: {0}", currentPackage);

            try (var scanResult = new ClassGraph().acceptPackages(currentPackage).scan()) {
                var allClasses = scanResult.getAllClasses();
                if (allClasses.isEmpty()) {
                    LOGGER.log(Level.WARNING, "No classes found in package: {0}", currentPackage);
                } else {
                    for (var classInfo : allClasses) {
                        var clazz = classInfo.loadClass();
                        if (clazz.isAnnotationPresent(BwiIgnore.class)) {
                            LOGGER.log(Level.INFO, "Ignoring class and its elements: {0}", clazz.getName());
                            continue;
                        }
                        LOGGER.log(Level.FINE, "Found class: {0}", clazz.getName());
                        scanner.checkClass(clazz);
                        scanner.checkMethods(clazz);
                        scanner.checkFields(clazz);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error scanning package: " + currentPackage, e);
                throw new BwiException(e.getMessage(), e);
            }
        }
    }
}
