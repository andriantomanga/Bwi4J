package org.bwi4j;

import org.bwi4j.exception.BwiException;
import org.bwi4j.model.TermType;

public interface TermChecker {
    void checkName(TermType type, String name) throws BwiException;
}
