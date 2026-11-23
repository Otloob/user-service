CREATE TABLE user_profile (
      id          BIGINT       PRIMARY KEY,
      full_name   VARCHAR(120),
      email       VARCHAR(255),
      phone       VARCHAR(20),
      avatar_url  VARCHAR(500),
      locale      VARCHAR(10)  NOT NULL DEFAULT 'en',
      status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
      created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
      updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
      version     BIGINT       NOT NULL DEFAULT 0
);