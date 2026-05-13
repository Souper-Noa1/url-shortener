
-- ── users table ──────────────────────────────────────────────
CREATE TABLE users (
                       id         BIGSERIAL    PRIMARY KEY,
                       username   VARCHAR(50)  NOT NULL UNIQUE,
                       email      VARCHAR(255) NOT NULL UNIQUE,
                       password   VARCHAR(255) NOT NULL,
                       created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ── urls table ───────────────────────────────────────────────
CREATE TABLE urls (
                      id           BIGSERIAL    PRIMARY KEY,
                      original_url TEXT         NOT NULL,
                      short_code   VARCHAR(10)  NOT NULL,
                      status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
                      click_count  BIGINT       NOT NULL DEFAULT 0,
                      expires_at   TIMESTAMPTZ,
                      created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                      updated_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                      owner_id     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

-- ── Indexes ──────────────────────────────────────────────────
CREATE UNIQUE INDEX idx_urls_short_code ON urls(short_code);


CREATE INDEX idx_urls_expires_at ON urls(expires_at) WHERE expires_at IS NOT NULL;


CREATE INDEX idx_urls_owner_id ON urls(owner_id);
