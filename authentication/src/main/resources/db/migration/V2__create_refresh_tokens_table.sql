CREATE TABLE refresh_tokens (
                                id UUID PRIMARY KEY,
                                user_id UUID NOT NULL,
                                expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
);

CREATE INDEX idx_refresh_tokens_user_id
    ON refresh_tokens(user_id);