package com.academichub.academic_management_hub.models;

public enum ResearchField {
    ARTIFICIAL_INTELLIGENCE("Artificial Intelligence"),
    MACHINE_LEARNING("Machine Learning"),
    DATA_SCIENCE("Data Science"),
    SOFTWARE_ENGINEERING("Software Engineering"),
    COMPUTER_NETWORKS("Computer Networks"),
    CYBERSECURITY("Cybersecurity"),
    DATABASE_SYSTEMS("Database Systems"),
    BIOINFORMATICS("Bioinformatics"),
    ROBOTICS("Robotics"),
    QUANTUM_COMPUTING("Quantum Computing"),
    CLOUD_COMPUTING("Cloud Computing"),
    DISTRIBUTED_SYSTEMS("Distributed Systems"),
    COMPUTER_VISION("Computer Vision"),
    NATURAL_LANGUAGE_PROCESSING("Natural Language Processing"),
    HUMAN_COMPUTER_INTERACTION("Human-Computer Interaction"),
    OTHER("Other");

    private final String displayValue;

    ResearchField(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}