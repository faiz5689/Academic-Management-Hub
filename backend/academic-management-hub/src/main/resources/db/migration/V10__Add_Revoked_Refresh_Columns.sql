ALTER TABLE refresh_tokens
ADD COLUMN revoked BOOLEAN DEFAULT false;