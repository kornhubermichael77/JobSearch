ALTER TABLE address
    ADD COLUMN owner_user_id BIGINT NULL;

CREATE INDEX idx_address_owner_user
    ON address (owner_user_id);