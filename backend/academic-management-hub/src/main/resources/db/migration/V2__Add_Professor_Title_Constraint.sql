-- First, update any existing NULL or invalid titles to a default value
UPDATE professors 
SET title = 'ASSISTANT_PROFESSOR' 
WHERE title IS NULL OR title NOT IN (
    'ASSISTANT_PROFESSOR',
    'ASSOCIATE_PROFESSOR',
    'PROFESSOR',
    'DISTINGUISHED_PROFESSOR',
    'EMERITUS_PROFESSOR',
    'VISITING_PROFESSOR',
    'ADJUNCT_PROFESSOR'
);

-- Add the constraint
ALTER TABLE professors
    ADD CONSTRAINT chk_professor_title CHECK (
        title IN (
            'ASSISTANT_PROFESSOR',
            'ASSOCIATE_PROFESSOR',
            'PROFESSOR',
            'DISTINGUISHED_PROFESSOR',
            'EMERITUS_PROFESSOR',
            'VISITING_PROFESSOR',
            'ADJUNCT_PROFESSOR'
        )
    );