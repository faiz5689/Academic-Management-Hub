-- Drop the trigger
DROP TRIGGER IF EXISTS validate_research_interests_trigger ON professors;

-- Drop the trigger function
DROP FUNCTION IF EXISTS research_interests_trigger();

-- Drop the validation function
DROP FUNCTION IF EXISTS validate_research_interests(text[]);
DROP FUNCTION IF EXISTS validate_research_interests(varchar);

-- Drop the research_fields table
DROP TABLE IF EXISTS research_fields;