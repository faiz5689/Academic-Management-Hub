package com.academichub.academic_management_hub.models;

public enum DepartmentType {
    SCIENCE("Science"),
    ENGINEERING("Engineering"),
    ARTS("Arts"),
    HUMANITIES("Humanities"),
    BUSINESS("Business"),
    MEDICAL("Medical"),
    LAW("Law"),
    SOCIAL_SCIENCES("Social Sciences"),
    TECHNOLOGY("Technology"),
    OTHER("Other");

    private final String displayValue;

    DepartmentType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}