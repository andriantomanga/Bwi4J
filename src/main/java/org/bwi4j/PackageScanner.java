package org.bwi4j;

import org.bwi4j.exception.BwiException;

public interface PackageScanner {
    void checkClass(Class<?> clazz) throws BwiException;

    void checkMethods(Class<?> clazz) throws BwiException;

    void checkFields(Class<?> clazz) throws BwiException;
}
