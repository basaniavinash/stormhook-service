# Stormhook Service

A multi-tenant webhook management service. Tenants register event endpoints, the service routes events to them, and tracks every delivery attempt. Tenant isolation is enforced at the security filter layer using JWT claims — not at the application layer.

---

## What it does

| Feature | Detail |
|---------|--------|
| **Tenant management** | CRUD for tenant configurations; admin-scoped endpoints |
| **Endpoint registration** | Tenants register webhook URLs to receive events |
| **Event routing** | Events dispatched to registered endpoints per tenant |
| **Attempt tracking** | Full audit trail of delivery attempts with status and timestamps |
| **JWT enforcement** | `tenant_id` claim in JWT validated against URL path on every request |

---

## Stack

| Layer | Technology |
|-------|-----------|
| Runtime | Java 21, Spring Boot 3.4 (Gradle) |
| Persistence | PostgreSQL + Flyway migrations, Spring Data JPA |
| Auth | Spring Security, OAuth2 Resource Server |
| Container | OpenJDK 21 slim Docker image |

---

## Data model

```
Tenant (1)
  └── Endpoint (N)         — registered webhook URLs
        └── Event (N)      — events dispatched to endpoint
              └── Attempt (N) — delivery attempts per event
```

All entities use UUIDs. Status fields are enums (`EventStatus`, `AttemptStatus`).

---

## Design decisions

### Tenant isolation at the filter layer

A custom `SecurityFilter` intercepts every request, extracts the `tenant_id` path parameter using regex, and compares it against the `tenant_id` claim in the authenticated JWT. If they don't match, the request is rejected before it reaches any controller.

This means tenant isolation is **guaranteed by infrastructure**, not by developer discipline in every handler.

### Scope-based endpoint authorization

`@PreAuthorize` annotations enforce fine-grained OAuth2 scopes:
- `admin:tenants:create` — create tenants
- `admin:tenants:read` — list tenants
- `admin:tenants:write` — update tenant config

A token with catalog scopes cannot touch webhook endpoints even if it has a valid tenant claim.

### Attempt trail for reliability

Every delivery attempt is persisted with its status. This gives operators a complete audit trail for debugging failed webhooks — no guessing whether an event was dispatched or the endpoint was unreachable.

---

## Running locally

Requires PostgreSQL. Point `spring.datasource.url` at your instance and Flyway handles schema creation.

```bash
./gradlew bootRun
```

JWT validation requires the public key from the [jwt-auth-service](../jwt-auth-service). Configure `spring.security.oauth2.resourceserver.jwt.public-key-location` accordingly.
