CREATE TABLE revocated_tokens (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   token TEXT NOT NULL,
   user_id UUID NOT NULL REFERENCES users(id),
   expiry_date TIMESTAMP NOT NULL,
   revoked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_revocated_tokens_token ON revocated_tokens(token);