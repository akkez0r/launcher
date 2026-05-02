# CMC Auth Cloudflare Worker Migration Design

## Goal

Migrate CMC auth from self-hosted Node/Express (`cmc-auth`) to Cloudflare Workers + D1 while keeping launcher-facing API behavior unchanged.

## Scope

- Add a new Worker-based auth service implementation.
- Use Cloudflare D1 for persistence of users and refresh tokens.
- Preserve existing endpoint paths and response shapes used by launcher:
  - `POST /auth/register`
  - `POST /auth/login`
  - `POST /auth/refresh`
  - `GET /auth/me`
  - `POST /auth/logout`
- Cut over `auth.craviorsmp.com` to Worker route.

Out of scope:

- Launcher-side API contract changes.
- Social login providers.
- Password reset/email verification flows.

## Architecture

### Runtime

- Cloudflare Worker handles HTTP routing and auth logic.
- No Express runtime.
- Validation via `zod`.

### Persistence

- Cloudflare D1 database (`cmc_auth`) with SQL schema for:
  - `users`
  - `refresh_tokens`

### Security

- `bcryptjs` for password hash/verify.
- JWT access and refresh tokens with same semantic model as current backend.
- Refresh token rotation with revocation tracking.
- Secrets stored as Cloudflare Worker secrets.

## API Compatibility Contract

The Worker must maintain the same request/response contract currently consumed by launcher.

### `POST /auth/register`

Request:

```json
{
  "username": "player_name",
  "email": "player@example.com",
  "password": "Password123!"
}
```

Response `200`:

```json
{
  "user": {
    "id": "uuid",
    "username": "player_name",
    "email": "player@example.com",
    "cmcUuid": "uuid"
  },
  "accessToken": "jwt",
  "refreshToken": "jwt"
}
```

### `POST /auth/login`

Request:

```json
{
  "emailOrUsername": "player_name",
  "password": "Password123!"
}
```

Response `200`: same shape as register.

### `POST /auth/refresh`

Request:

```json
{
  "refreshToken": "jwt"
}
```

Response `200`: same session shape with rotated tokens.

### `GET /auth/me`

Header:

`Authorization: Bearer <accessToken>`

Response `200`:

```json
{
  "user": {
    "id": "uuid",
    "username": "player_name",
    "email": "player@example.com",
    "cmcUuid": "uuid"
  }
}
```

### `POST /auth/logout`

Request:

```json
{
  "refreshToken": "jwt"
}
```

Response `200`:

```json
{
  "ok": true
}
```

### Error shape

Keep current simple error payloads and status behavior used by launcher:

- `400` invalid request
- `401` invalid credentials/token
- `409` email/username taken
- `500` internal error

## D1 Schema

Use text UUID storage for D1 compatibility while preserving UUID semantics at application layer.

```sql
CREATE TABLE IF NOT EXISTS users (
  id TEXT PRIMARY KEY,
  username TEXT NOT NULL UNIQUE,
  cmc_uuid TEXT NOT NULL UNIQUE,
  email TEXT NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  created_at TEXT NOT NULL,
  updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
  id TEXT PRIMARY KEY,
  user_id TEXT NOT NULL,
  token_hash TEXT NOT NULL UNIQUE,
  expires_at TEXT NOT NULL,
  revoked_at TEXT,
  created_at TEXT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id
  ON refresh_tokens(user_id);
```

## Token and Session Behavior

- Access token: short TTL (`CMC_ACCESS_TOKEN_TTL`).
- Refresh token: long TTL (`CMC_REFRESH_TOKEN_TTL_MS`).
- Refresh flow:
  1. Verify refresh JWT.
  2. Hash token and read token row.
  3. Validate token ownership, not revoked, not expired.
  4. Revoke old token row.
  5. Issue new access/refresh tokens.
  6. Insert new refresh token row.

`/auth/me` must validate access JWT and confirm user exists in DB.

## Worker Config

### Secrets

- `CMC_ACCESS_TOKEN_SECRET`
- `CMC_REFRESH_TOKEN_SECRET`
- `CMC_ACCESS_TOKEN_TTL`
- `CMC_REFRESH_TOKEN_TTL_MS`
- `CORS_ALLOWED_ORIGINS`

### Bindings

- D1 binding: `DB`

### Route

- `auth.craviorsmp.com/*` mapped to Worker.

## CORS Policy

- Start permissive only if needed for bootstrap.
- Final policy should allow only required origins.
- Return proper `OPTIONS` preflight responses.

## Deployment Plan

1. Create Worker project (GitHub deploy from Cloudflare dashboard).
2. Create D1 DB `cmc_auth`.
3. Bind D1 as `DB`.
4. Apply schema migration SQL.
5. Configure secrets.
6. Deploy Worker.
7. Attach route `auth.craviorsmp.com/*`.
8. Run health and endpoint smoke tests.
9. Remove old nginx/cloudflared auth path for this hostname.

## Verification Plan

- `GET /health` returns healthy response.
- Register new user and verify DB row creation.
- Login via username and via email.
- `/auth/me` succeeds with valid access token.
- `/auth/refresh` rotates token and revokes previous token.
- `/auth/logout` revokes current refresh token.
- Launcher Account tab register/login/logout works unchanged.
- Launcher launch path receives backend identity (`--username`, `--uuid`) via `/auth/me`.

## Risks and Mitigations

- **D1 SQL differences**: use simple SQL and text timestamps/UUIDs.
- **Edge runtime crypto differences**: use compatible libs and Worker-safe code.
- **Cutover mismatch**: keep endpoint contract unchanged and test with launcher before removing old backend.

## Success Criteria

- `auth.craviorsmp.com` is served by Worker over HTTPS.
- Launcher works without API-path changes.
- No dependency on self-hosted auth process for player logins.
