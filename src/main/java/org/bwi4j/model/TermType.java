package org.bwi4j.model;

public enum TermType {
    CLASS("Class"), METHOD("Method"), FIELD("Field");
    private final String name;

    TermType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
