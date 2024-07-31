package org.bwi4j.implementation;

import org.bwi4j.PackageScanner;
import org.bwi4j.TermChecker;
import org.bwi4j.annotation.BwiIgnore;
import org.bwi4j.exception.BwiException;
import org.bwi4j.model.TermType;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultPackageScanner implements PackageScanner {
    private static final Logger LOGGER = Logger.getLogger(DefaultPackageScanner.class.getName());
    private final TermChecker termChecker;

    public DefaultPackageScanner(TermChecker termChecker) {
        this.termChecker = termChecker;
    }

    @Override
    public void checkClass(Class<?> clazz) throws BwiException {
        termChecker.checkName(TermType.CLASS, clazz.getSimpleName());
    }

    @Override
    public void checkMethods(Class<?> clazz) throws BwiException {
        for (var method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(BwiIgnore.class)) {
                LOGGER.log(Level.INFO, "Ignoring the following method: {0}", method.getName());
                continue;
            }
            termChecker.checkName(TermType.METHOD, method.getName());
        }
    }

    @Override
    public void checkFields(Class<?> clazz) throws BwiException {
        for (var field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(BwiIgnore.class)) {
                LOGGER.log(Level.INFO, "Ignoring the following field: {0}", field.getName());
                continue;
            }
            termChecker.checkName(TermType.FIELD, field.getName());
        }
    }
}
