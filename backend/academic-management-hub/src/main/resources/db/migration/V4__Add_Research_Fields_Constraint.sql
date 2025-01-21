-- Create research_fields table to store valid values
CREATE TABLE research_fields (
    field VARCHAR(100) PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL
);

-- Insert valid research fields
INSERT INTO research_fields (field, display_name) VALUES
    ('ARTIFICIAL_INTELLIGENCE', 'Artificial Intelligence'),
    ('MACHINE_LEARNING', 'Machine Learning'),
    ('DATA_SCIENCE', 'Data Science'),
    ('SOFTWARE_ENGINEERING', 'Software Engineering'),
    ('COMPUTER_NETWORKS', 'Computer Networks'),
    ('CYBERSECURITY', 'Cybersecurity'),
    ('DATABASE_SYSTEMS', 'Database Systems'),
    ('BIOINFORMATICS', 'Bioinformatics'),
    ('ROBOTICS', 'Robotics'),
    ('QUANTUM_COMPUTING', 'Quantum Computing'),
    ('CLOUD_COMPUTING', 'Cloud Computing'),
    ('DISTRIBUTED_SYSTEMS', 'Distributed Systems'),
    ('COMPUTER_VISION', 'Computer Vision'),
    ('NATURAL_LANGUAGE_PROCESSING', 'Natural Language Processing'),
    ('HUMAN_COMPUTER_INTERACTION', 'Human-Computer Interaction'),
    ('OTHER', 'Other');

-- Create function to validate research interests
CREATE OR REPLACE FUNCTION validate_research_interests(interests text[])
RETURNS BOOLEAN AS $$
BEGIN
    RETURN (
        interests IS NOT NULL 
        AND array_length(interests, 1) > 0 
        AND NOT EXISTS (
            SELECT unnest(interests) EXCEPT SELECT field FROM research_fields
        )
    );
END;
$$ LANGUAGE plpgsql;

-- Add the trigger function to validate research interests before insert or update
CREATE OR REPLACE FUNCTION research_interests_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT validate_research_interests(NEW.research_interests) THEN
        RAISE EXCEPTION 'Invalid research interests. All values must be from the research_fields table.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger
CREATE TRIGGER validate_research_interests_trigger
    BEFORE INSERT OR UPDATE ON professors
    FOR EACH ROW
    EXECUTE FUNCTION research_interests_trigger();