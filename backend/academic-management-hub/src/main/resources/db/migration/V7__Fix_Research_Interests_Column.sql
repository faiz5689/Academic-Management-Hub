ALTER TABLE professors 
DROP COLUMN research_interests;

ALTER TABLE professors
ADD COLUMN research_interests varchar(255);