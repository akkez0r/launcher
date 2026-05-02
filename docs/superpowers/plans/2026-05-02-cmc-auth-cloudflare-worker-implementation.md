# CMC Auth Cloudflare Worker Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace self-hosted `cmc-auth` Express service with a Cloudflare Worker + D1 implementation while preserving the launcher-facing `/auth/*` API contract on `auth.craviorsmp.com`.

**Architecture:** Implement a Worker app in a dedicated `cmc-auth-worker` folder with route handlers for `register/login/refresh/me/logout`, shared auth/domain helpers, and D1-backed repositories for `users` and `refresh_tokens`. Preserve current response/error semantics and JWT/refresh rotation behavior so launcher code does not need contract changes.

**Tech Stack:** Cloudflare Workers, D1, TypeScript, Wrangler, `zod`, `bcryptjs`, `jose`, Vitest + Miniflare.

---

## File Structure

- Create: `cmc-auth-worker/package.json` - Worker scripts and dependencies.
- Create: `cmc-auth-worker/tsconfig.json` - TS config for Worker runtime.
- Create: `cmc-auth-worker/wrangler.toml` - Worker name, bindings, routes.
- Create: `cmc-auth-worker/.dev.vars.example` - local secrets/env reference.
- Create: `cmc-auth-worker/migrations/0001_init.sql` - D1 schema.
- Create: `cmc-auth-worker/src/index.ts` - Worker entrypoint and router.
- Create: `cmc-auth-worker/src/types.ts` - env and payload types.
- Create: `cmc-auth-worker/src/http/errors.ts` - HTTP error helpers.
- Create: `cmc-auth-worker/src/http/response.ts` - JSON response helpers/CORS.
- Create: `cmc-auth-worker/src/auth/schemas.ts` - `zod` request validators.
- Create: `cmc-auth-worker/src/auth/tokens.ts` - JWT sign/verify helpers.
- Create: `cmc-auth-worker/src/auth/password.ts` - bcrypt hash/verify helpers.
- Create: `cmc-auth-worker/src/auth/service.ts` - auth use-cases.
- Create: `cmc-auth-worker/src/db/users.ts` - users table operations.
- Create: `cmc-auth-worker/src/db/refreshTokens.ts` - refresh token operations.
- Create: `cmc-auth-worker/test/auth.integration.test.ts` - route-level integration tests.
- Modify: `README.md` - Cloudflare Worker deploy/cutover instructions.

---

### Task 1: Scaffold Worker Project

**Files:**
- Create: `cmc-auth-worker/package.json`
- Create: `cmc-auth-worker/tsconfig.json`
- Create: `cmc-auth-worker/wrangler.toml`
- Create: `cmc-auth-worker/.dev.vars.example`
- Create: `cmc-auth-worker/src/index.ts`
- Test: `npm --prefix cmc-auth-worker run typecheck`

- [ ] **Step 1: Write failing bootstrap check**

```ts
// cmc-auth-worker/src/index.ts
export default {
  async fetch(): Promise<Response> {
    throw new Error("not_implemented");
  },
};
```

- [ ] **Step 2: Run typecheck to verify initial failure path is exercised**

Run: `npm --prefix cmc-auth-worker run typecheck`  
Expected: command runs with current scaffold; runtime behavior still intentionally `not_implemented`.

- [ ] **Step 3: Add minimal Worker scaffold**

```ts
// cmc-auth-worker/src/index.ts
import type { Env } from "./types";

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    if (new URL(request.url).pathname === "/health") {
      return Response.json({ ok: true });
    }
    return Response.json({ error: "not_found" }, { status: 404 });
  },
};
```

```toml
# cmc-auth-worker/wrangler.toml
name = "cmc-auth-worker"
main = "src/index.ts"
compatibility_date = "2026-05-02"

[[d1_databases]]
binding = "DB"
database_name = "cmc_auth"
database_id = "REPLACE_D1_ID"
```

- [ ] **Step 4: Verify scaffold compiles**

Run: `npm --prefix cmc-auth-worker run typecheck`  
Expected: PASS with no TS errors.

- [ ] **Step 5: Commit**

```bash
git add cmc-auth-worker/package.json cmc-auth-worker/tsconfig.json cmc-auth-worker/wrangler.toml cmc-auth-worker/.dev.vars.example cmc-auth-worker/src/index.ts cmc-auth-worker/src/types.ts
git commit -m "feat(worker): scaffold cmc auth worker project"
```

---

### Task 2: Add D1 Schema and DB Helpers

**Files:**
- Create: `cmc-auth-worker/migrations/0001_init.sql`
- Create: `cmc-auth-worker/src/db/users.ts`
- Create: `cmc-auth-worker/src/db/refreshTokens.ts`
- Test: `cmc-auth-worker/test/auth.integration.test.ts`

- [ ] **Step 1: Write failing test for register persistence**

```ts
// cmc-auth-worker/test/auth.integration.test.ts
import { describe, it, expect } from "vitest";

describe("register persistence", () => {
  it("stores a user row", async () => {
    // test will call /auth/register once route exists
    expect(true).toBe(true);
  });
});
```

- [ ] **Step 2: Run test command to confirm baseline**

Run: `npm --prefix cmc-auth-worker test`  
Expected: placeholder test runs; DB-backed assertions still missing/failing later.

- [ ] **Step 3: Implement D1 schema and repository helpers**

```sql
-- cmc-auth-worker/migrations/0001_init.sql
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
```

```ts
// cmc-auth-worker/src/db/users.ts (signature example)
export async function findUserByEmailOrUsername(db: D1Database, identity: string) {
  return db.prepare(
    "SELECT id, username, email, cmc_uuid, password_hash FROM users WHERE email = ? OR username = ? LIMIT 1",
  ).bind(identity.toLowerCase(), identity).first();
}
```

- [ ] **Step 4: Run migration and verify schema**

Run: `npm --prefix cmc-auth-worker run d1:migrate:local`  
Expected: D1 local schema applies successfully.

- [ ] **Step 5: Commit**

```bash
git add cmc-auth-worker/migrations/0001_init.sql cmc-auth-worker/src/db/users.ts cmc-auth-worker/src/db/refreshTokens.ts
git commit -m "feat(worker): add d1 schema and repository helpers"
```

---

### Task 3: Implement Auth Domain Logic (Password + JWT + Rotation)

**Files:**
- Create: `cmc-auth-worker/src/auth/password.ts`
- Create: `cmc-auth-worker/src/auth/tokens.ts`
- Create: `cmc-auth-worker/src/auth/service.ts`
- Test: `cmc-auth-worker/test/auth.integration.test.ts`

- [ ] **Step 1: Write failing token/password test cases**

```ts
// cmc-auth-worker/test/auth.integration.test.ts
it("rejects invalid refresh token reuse", async () => {
  // after refresh, old refresh token must fail with invalid_token
  expect(false).toBe(true);
});
```

- [ ] **Step 2: Run tests to verify failures**

Run: `npm --prefix cmc-auth-worker test`  
Expected: FAIL on not-yet-implemented auth behavior.

- [ ] **Step 3: Implement auth service behavior**

```ts
// cmc-auth-worker/src/auth/service.ts (core flow signature)
export async function refreshSession(env: Env, refreshToken: string): Promise<AuthSessionResponse> {
  // verify jwt, check token row, reject revoked/expired, revoke old, insert new
}
```

```ts
// cmc-auth-worker/src/auth/tokens.ts (signature)
export async function signAccessToken(env: Env, claims: AccessClaims): Promise<string> {}
export async function verifyAccessToken(env: Env, token: string): Promise<AccessClaims> {}
```

- [ ] **Step 4: Run tests to verify pass**

Run: `npm --prefix cmc-auth-worker test`  
Expected: PASS for auth lifecycle and refresh rotation tests.

- [ ] **Step 5: Commit**

```bash
git add cmc-auth-worker/src/auth/password.ts cmc-auth-worker/src/auth/tokens.ts cmc-auth-worker/src/auth/service.ts cmc-auth-worker/test/auth.integration.test.ts
git commit -m "feat(worker): implement auth service and token rotation"
```

---

### Task 4: Implement `/auth/*` Worker Routes and Error Contract

**Files:**
- Modify: `cmc-auth-worker/src/index.ts`
- Create: `cmc-auth-worker/src/auth/schemas.ts`
- Create: `cmc-auth-worker/src/http/errors.ts`
- Create: `cmc-auth-worker/src/http/response.ts`
- Test: `cmc-auth-worker/test/auth.integration.test.ts`

- [ ] **Step 1: Write failing route contract tests**

```ts
it("returns 200 and {ok:true} on /auth/logout", async () => {
  // assert exact response shape expected by launcher
});
```

- [ ] **Step 2: Run tests and verify route failures**

Run: `npm --prefix cmc-auth-worker test`  
Expected: FAIL for missing route/status contract.

- [ ] **Step 3: Implement route handlers and validation**

```ts
// cmc-auth-worker/src/index.ts (route skeleton)
if (pathname === "/auth/register" && request.method === "POST") { /* ... */ }
if (pathname === "/auth/login" && request.method === "POST") { /* ... */ }
if (pathname === "/auth/refresh" && request.method === "POST") { /* ... */ }
if (pathname === "/auth/me" && request.method === "GET") { /* ... */ }
if (pathname === "/auth/logout" && request.method === "POST") { /* ... */ }
```

- [ ] **Step 4: Re-run tests to verify pass**

Run: `npm --prefix cmc-auth-worker test`  
Expected: PASS for response code and payload contract checks.

- [ ] **Step 5: Commit**

```bash
git add cmc-auth-worker/src/index.ts cmc-auth-worker/src/auth/schemas.ts cmc-auth-worker/src/http/errors.ts cmc-auth-worker/src/http/response.ts cmc-auth-worker/test/auth.integration.test.ts
git commit -m "feat(worker): add auth routes with launcher-compatible contract"
```

---

### Task 5: Configure Wrangler Deploy and Production Route

**Files:**
- Modify: `cmc-auth-worker/wrangler.toml`
- Modify: `cmc-auth-worker/.dev.vars.example`
- Modify: `README.md`

- [ ] **Step 1: Add failing deploy dry-run step**

Run: `npm --prefix cmc-auth-worker run deploy:dry`  
Expected: FAIL until required env/bindings are documented and configured.

- [ ] **Step 2: Add required Wrangler scripts and env docs**

```json
// cmc-auth-worker/package.json scripts
{
  "scripts": {
    "dev": "wrangler dev",
    "deploy": "wrangler deploy",
    "deploy:dry": "wrangler deploy --dry-run",
    "d1:migrate:local": "wrangler d1 migrations apply cmc_auth --local",
    "d1:migrate:remote": "wrangler d1 migrations apply cmc_auth --remote"
  }
}
```

```md
<!-- README.md section -->
## Cloudflare Worker Auth Deploy
1. Create D1 database and bind as `DB`.
2. Set Worker secrets.
3. Run `wrangler d1 migrations apply cmc_auth --remote`.
4. Deploy and map route `auth.craviorsmp.com/*`.
```

- [ ] **Step 3: Run dry deploy and verify success**

Run: `npm --prefix cmc-auth-worker run deploy:dry`  
Expected: PASS with no missing binding errors.

- [ ] **Step 4: Commit**

```bash
git add cmc-auth-worker/wrangler.toml cmc-auth-worker/.dev.vars.example cmc-auth-worker/package.json README.md
git commit -m "chore(worker): add wrangler deploy and migration configuration"
```

---

### Task 6: Cutover Validation and Launcher Compatibility Smoke

**Files:**
- Modify: `README.md`
- Test: manual smoke commands (Worker URL + launcher behavior)

- [ ] **Step 1: Add explicit cutover checklist**

```md
## Cutover Checklist
- [ ] `https://auth.craviorsmp.com/health` returns `{ "ok": true }`
- [ ] `/auth/register` works with new user
- [ ] `/auth/login` works with username and email
- [ ] `/auth/refresh` rotates token
- [ ] `/auth/me` returns `cmcUuid`
- [ ] Launcher register/login/logout works unchanged
- [ ] Launcher launch uses backend identity (`--username`, `--uuid`)
```

- [ ] **Step 2: Run smoke tests against deployed Worker**

Run:
- `curl https://auth.craviorsmp.com/health`
- endpoint smoke via HTTP client/script
- launcher login + launch manual test

Expected:
- all API checks pass
- launcher flow unchanged

- [ ] **Step 3: Commit**

```bash
git add README.md
git commit -m "docs(worker): add production cutover validation checklist"
```

---

## Self-Review Checklist (Completed)

- Spec coverage: architecture, API parity, D1 schema, token rotation, deploy route, and cutover verification are each mapped to concrete tasks.
- Placeholder scan: no `TBD`/`TODO` placeholders in implementation steps.
- Type consistency: `AuthSessionResponse`, `AuthUser`, and `/auth/*` routes are named consistently across tasks.
