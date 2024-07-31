package org.bwi4j.implementation;

import org.bwi4j.TermChecker;
import org.bwi4j.exception.BwiException;
import org.bwi4j.model.TermType;

import java.util.Set;

public class DefaultTermChecker implements TermChecker {
    private final Set<String> blacklistedTerms;

    public DefaultTermChecker(Set<String> blacklistedTerms) {
        this.blacklistedTerms = blacklistedTerms;
    }

    @Override
    public void checkName(TermType type, String name) throws BwiException {
        for (var term : blacklistedTerms) {
            if (name.toLowerCase().contains(term)) {
                throw new BwiException(type.getName() + " name contains blacklisted term: " + name);
            }
        }
    }
}
