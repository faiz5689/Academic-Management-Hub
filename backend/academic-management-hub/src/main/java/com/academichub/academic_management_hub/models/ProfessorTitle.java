package com.academichub.academic_management_hub.models;

public enum ProfessorTitle {
    ASSISTANT_PROFESSOR("Assistant Professor"),
    ASSOCIATE_PROFESSOR("Associate Professor"),
    PROFESSOR("Professor"),
    DISTINGUISHED_PROFESSOR("Distinguished Professor"),
    EMERITUS_PROFESSOR("Professor Emeritus"),
    VISITING_PROFESSOR("Visiting Professor"),
    ADJUNCT_PROFESSOR("Adjunct Professor");

    private final String displayValue;

    ProfessorTitle(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}