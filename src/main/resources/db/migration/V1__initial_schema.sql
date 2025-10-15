-- Needed for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Tenants → customers who own webhooks.
CREATE TABLE tenants (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255) NOT NULL,
    api_key         TEXT NOT NULL UNIQUE,              -- store a hash in prod
    signing_secret  TEXT NOT NULL,                     -- store securely in prod
    rate_limit_rps  INT NOT NULL DEFAULT 10 CHECK (rate_limit_rps >= 0),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Endpoints → URLs each tenant registers to receive events.
CREATE TABLE endpoints (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    url         TEXT NOT NULL,
    timeout_ms  INT  NOT NULL DEFAULT 5000 CHECK (timeout_ms >= 0),
    max_retries INT  NOT NULL DEFAULT 5    CHECK (max_retries >= 0),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    -- avoid duplicate endpoints per tenant (optional but useful)
    UNIQUE (tenant_id, url)
);

-- Events → inbound events enqueued for delivery.
CREATE TABLE events (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    idempotency_key  VARCHAR(128),
    payload          JSONB NOT NULL,
    status           VARCHAR(32) NOT NULL DEFAULT 'pending'
                      CHECK (status IN ('pending','delivered','failed')),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    -- idempo key uniqueness *per tenant* (safer than global UNIQUE)
    UNIQUE (tenant_id, idempotency_key)
);

-- Attempts → each delivery attempt (success/failure, latency, error).
CREATE TABLE attempts (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id      UUID NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    endpoint_id   UUID NOT NULL REFERENCES endpoints(id) ON DELETE CASCADE,
    response_code INT,                                  -- HTTP status
    latency_ms    INT CHECK (latency_ms IS NULL OR latency_ms >= 0),
    error         TEXT,
    status        VARCHAR(32) NOT NULL
                   CHECK (status IN ('succeeded','failed','timeout','canceled')),
    attempted_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

---- Helpful indexes for common queries
--CREATE INDEX idx_events_tenant_created   ON events(tenant_id, created_at DESC);
--CREATE INDEX idx_attempts_event          ON attempts(event_id);
--CREATE INDEX idx_attempts_endpoint_time  ON attempts(endpoint_id, attempted_at DESC);
