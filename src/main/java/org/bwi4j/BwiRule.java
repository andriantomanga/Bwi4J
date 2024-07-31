package org.bwi4j;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.bwi4j.annotation.BwiIgnore;
import org.bwi4j.exception.BwiException;
import org.reflections.util.ClasspathHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BwiRule {
    private static final Logger LOGGER = Logger.getLogger(BwiRule.class.getName());

    private final Set<String> packagesToScan = new HashSet<>();
    private final Set<String> blacklistedTerms = new HashSet<>();

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
        for (var currentPackage : packagesToScan) {
            LOGGER.log(Level.FINE, "Scanning package: {0}", currentPackage);

            try (ScanResult scanResult = new ClassGraph().acceptPackages(currentPackage).scan()) {
                var allClasses = scanResult.getAllClasses();
                if (allClasses.isEmpty()) {
                    LOGGER.log(Level.WARNING, "No classes found in package: {0}", currentPackage);
                } else {
                    for (var classInfo : allClasses) {
                        Class<?> clazz = classInfo.loadClass();
                        if (clazz.isAnnotationPresent(BwiIgnore.class)) {
                            LOGGER.log(Level.INFO, "Ignoring class and its elements: {0}", clazz.getName());
                            continue;
                        }
                        LOGGER.log(Level.FINE, "Found class: {0}", clazz.getName());
                        checkClass(clazz);
                        checkMethods(clazz);
                        checkFields(clazz);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error scanning package: " + currentPackage, e);
                throw new BwiException(e.getMessage());
            }
        }
    }

    private void logClassLoaders(ClassLoader[] classLoaders) {
        for (var classLoader : classLoaders) {
            var urls = ClasspathHelper.forClassLoader(classLoader);
            for (var url : urls) {
                LOGGER.log(Level.INFO, "Classpath URL: {0}", url);
            }
        }
    }

    private void checkClass(Class<?> clazz) throws BwiException {
        var className = clazz.getSimpleName();
        for (var term : blacklistedTerms) {
            if (className.toLowerCase().contains(term)) {
                throw new BwiException("Class name contains blacklisted term: " + className);
            }
        }
    }

    private void checkMethods(Class<?> clazz) throws BwiException {
        for (var method : clazz.getDeclaredMethods()) {
            var methodName = method.getName();
            if (method.isAnnotationPresent(BwiIgnore.class)) {
                LOGGER.log(Level.INFO, "Ignoring the following method: {0}", methodName);
                continue;
            }

            for (var term : blacklistedTerms) {
                if (methodName.toLowerCase().contains(term)) {
                    throw new BwiException("Method name contains blacklisted term: " + methodName);
                }
            }
        }
    }

    private void checkFields(Class<?> clazz) throws BwiException {
        for (var field : clazz.getDeclaredFields()) {
            var fieldName = field.getName();
            if (field.isAnnotationPresent(BwiIgnore.class)) {
                LOGGER.log(Level.INFO, "Ignoring the following field: {0}", fieldName);
                continue;
            }

            for (var term : blacklistedTerms) {
                if (fieldName.toLowerCase().contains(term)) {
                    throw new BwiException("Field name contains blacklisted term: " + fieldName);
                }
            }
        }
    }
}
