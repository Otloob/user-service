CREATE TABLE address (
     id          UUID         PRIMARY KEY,
     user_id     BIGINT       NOT NULL REFERENCES user_profile (id),
     label       VARCHAR(50)  NOT NULL,
     latitude    DOUBLE PRECISION NOT NULL,
     longitude   DOUBLE PRECISION NOT NULL,
     street      VARCHAR(255) NOT NULL,
     building    VARCHAR(50),
     floor       VARCHAR(20),
     apartment   VARCHAR(20),
     landmark    VARCHAR(255),
     phone       VARCHAR(20),
     is_default  BOOLEAN      NOT NULL DEFAULT FALSE,
     deleted_at  TIMESTAMPTZ,
     created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
     updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_address_user
    ON address (user_id) WHERE deleted_at IS NULL; -- partial index

CREATE UNIQUE INDEX uq_address_one_default
    ON address (user_id) WHERE is_default AND deleted_at IS NULL;