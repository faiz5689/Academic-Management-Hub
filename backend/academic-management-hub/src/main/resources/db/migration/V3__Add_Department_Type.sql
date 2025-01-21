-- Add the type column with a default value
ALTER TABLE departments
ADD COLUMN type VARCHAR(50) NOT NULL DEFAULT 'OTHER';

-- Add the constraint
ALTER TABLE departments
ADD CONSTRAINT chk_department_type CHECK (
    type IN (
        'SCIENCE',
        'ENGINEERING',
        'ARTS',
        'HUMANITIES',
        'BUSINESS',
        'MEDICAL',
        'LAW',
        'SOCIAL_SCIENCES',
        'TECHNOLOGY',
        'OTHER'
    )
);