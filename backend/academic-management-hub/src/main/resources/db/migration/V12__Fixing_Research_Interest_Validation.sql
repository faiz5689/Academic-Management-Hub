-- Drop existing trigger and functions
DROP TRIGGER IF EXISTS validate_research_interests_trigger ON professors;
DROP FUNCTION IF EXISTS research_interests_trigger();
DROP FUNCTION IF EXISTS validate_research_interests(text[]);

-- Create new function to validate comma-separated research interests
CREATE OR REPLACE FUNCTION validate_research_interests(interests varchar)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN (
        interests IS NULL 
        OR interests = ''
        OR NOT EXISTS (
            SELECT unnest(string_to_array(interests, ',')) EXCEPT SELECT field FROM research_fields
        )
    );
END;
$$ LANGUAGE plpgsql;

-- Add the new trigger function
CREATE OR REPLACE FUNCTION research_interests_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT validate_research_interests(NEW.research_interests) THEN
        RAISE EXCEPTION 'Invalid research interests. All values must be from the research_fields table.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the new trigger
CREATE TRIGGER validate_research_interests_trigger
    BEFORE INSERT OR UPDATE ON professors
    FOR EACH ROW
    EXECUTE FUNCTION research_interests_trigger();