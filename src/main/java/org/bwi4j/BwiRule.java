/**
 * Copyright (c) 2024 Nabil Andriantomanga
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
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
import io.github.classgraph.ScanResult;
import org.bwi4j.annotation.BwiIgnore;
import org.bwi4j.exception.BwiException;
import org.bwi4j.implementation.DefaultPackageScanner;
import org.bwi4j.implementation.DefaultTermChecker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nabil Andriantomanga
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

    public BwiRule inThesePackages(String... packages) {
        packagesToScan.addAll(Arrays.asList(packages));
        return this;
    }

    public BwiRule mustNotContainTheseTerms(String... terms) {
        var preparedTerms = Arrays.stream(terms)
                .map(term -> term.toLowerCase().strip())
                .toList();
        blacklistedTerms.addAll(preparedTerms);
        return this;
    }

    public void check() throws BwiException {
        var scanner = new DefaultPackageScanner(new DefaultTermChecker(blacklistedTerms));

        for (var currentPackage : packagesToScan) {
            LOGGER.log(Level.FINE, "Scanning package: {0}", currentPackage);

            try (ScanResult scanResult = new ClassGraph().acceptPackages(currentPackage).scan()) {
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
