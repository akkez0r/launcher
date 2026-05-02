import assert from "node:assert/strict";
import type { AddressInfo } from "node:net";
import { after, before, beforeEach, test } from "node:test";

import type { Server } from "node:http";

const TEST_DB_URL = "postgresql://postgres:postgres@localhost:5432/cmc_auth";
process.env.CMC_DB_URL ??= TEST_DB_URL;
process.env.CMC_AUTH_USE_PGMEM ??= "1";

const { query } = require("../src/db") as typeof import("../src/db");
const { createApp } = require("../src/server") as typeof import("../src/server");

type JsonValue = Record<string, unknown>;

let server: Server;
let baseUrl = "";

before(async () => {
  await ensureTestSchema();
  await resetAuthTables();

  const app = createApp({
    auth: {
      accessTokenSecret: "test-access-secret",
      refreshTokenSecret: "test-refresh-secret",
      refreshTokenTtlMs: 60_000,
    },
  });

  server = app.listen(0);
  await new Promise<void>((resolve) => server.once("listening", resolve));

  const address = server.address() as AddressInfo;
  baseUrl = `http://127.0.0.1:${address.port}`;
});

beforeEach(async () => {
  await resetAuthTables();
});

after(async () => {
  if (!server) {
    return;
  }

  await new Promise<void>((resolve, reject) => {
    server.close((error) => {
      if (error) {
        reject(error);
        return;
      }

      resolve();
    });
  });
});

async function resetAuthTables(): Promise<void> {
  await query("DELETE FROM user_skins");
  await query("DELETE FROM refresh_tokens");
  await query("DELETE FROM users");
}

async function ensureTestSchema(): Promise<void> {
  await query(`
    CREATE TABLE IF NOT EXISTS users (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      username TEXT NOT NULL UNIQUE,
      cmc_uuid UUID NOT NULL UNIQUE,
      email TEXT NOT NULL UNIQUE,
      password_hash TEXT NOT NULL,
      created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
      updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    )
  `);
  await query(`
    CREATE TABLE IF NOT EXISTS refresh_tokens (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
      token_hash TEXT NOT NULL UNIQUE,
      expires_at TIMESTAMPTZ NOT NULL,
      revoked_at TIMESTAMPTZ,
      created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    )
  `);
  await query(`DROP TABLE IF EXISTS user_skins`);
  await query(`
    CREATE TABLE user_skins (
      user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
      cmc_uuid_hex CHARACTER(32) NOT NULL UNIQUE,
      skin_png_base64 TEXT NOT NULL,
      width INTEGER NOT NULL,
      height INTEGER NOT NULL,
      updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    )
  `);
}

async function post(
  path: string,
  body: JsonValue,
  token?: string,
): Promise<{ status: number; body: JsonValue }> {
  const response = await fetch(`${baseUrl}${path}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify(body),
  });

  const responseBody =
    response.status === 204 ? {} : ((await response.json()) as JsonValue);
  return { status: response.status, body: responseBody };
}

async function get(
  path: string,
  token?: string,
): Promise<{ status: number; body: JsonValue }> {
  const response = await fetch(`${baseUrl}${path}`, {
    method: "GET",
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });

  const responseBody = (await response.json()) as JsonValue;
  return { status: response.status, body: responseBody };
}

function makePngBuffer(width: number, height: number): Buffer {
  // eslint-disable-next-line @typescript-eslint/no-require-imports
  const { PNG } = require("pngjs") as typeof import("pngjs");
  const png = new PNG({ width, height, colorType: 6, inputHasAlpha: true });
  for (let y = 0; y < height; y++) {
    for (let x = 0; x < width; x++) {
      const idx = (width * y + x) << 2;
      png.data[idx] = 72;
      png.data[idx + 1] = 168;
      png.data[idx + 2] = 255;
      png.data[idx + 3] = 255;
    }
  }
  return PNG.sync.write(png);
}

async function getSkinPng(path: string): Promise<{ status: number; contentType: string | null }> {
  const response = await fetch(`${baseUrl}${path}`);
  const contentType = response.headers.get("content-type");
  return { status: response.status, contentType };
}

test("register/login/me/refresh/logout lifecycle rotates refresh tokens", async () => {
  const registerResponse = await post("/auth/register", {
    username: "integration_user",
    email: "integration@example.com",
    password: "SuperSecure!123",
  });

  assert.equal(registerResponse.status, 200);
  assert.equal(typeof registerResponse.body.accessToken, "string");
  assert.equal(typeof registerResponse.body.refreshToken, "string");

  const registeredAccessToken = registerResponse.body.accessToken as string;
  const meFromRegister = await get("/auth/me", registeredAccessToken);
  assert.equal(meFromRegister.status, 200);
  assert.equal(
    (meFromRegister.body.user as Record<string, unknown>).username,
    "integration_user",
  );

  const loginResponse = await post("/auth/login", {
    emailOrUsername: "integration_user",
    password: "SuperSecure!123",
  });

  assert.equal(loginResponse.status, 200);
  assert.equal(typeof loginResponse.body.accessToken, "string");
  assert.equal(typeof loginResponse.body.refreshToken, "string");

  const loginRefreshToken = loginResponse.body.refreshToken as string;
  const refreshResponse = await post("/auth/refresh", {
    refreshToken: loginRefreshToken,
  });

  assert.equal(refreshResponse.status, 200);
  assert.equal(typeof refreshResponse.body.accessToken, "string");
  assert.equal(typeof refreshResponse.body.refreshToken, "string");
  assert.notEqual(refreshResponse.body.refreshToken, loginRefreshToken);

  const reusedRefreshResponse = await post("/auth/refresh", {
    refreshToken: loginRefreshToken,
  });
  assert.equal(reusedRefreshResponse.status, 401);

  const rotatedRefreshToken = refreshResponse.body.refreshToken as string;
  const rotatedAccessToken = refreshResponse.body.accessToken as string;

  const meAfterRefresh = await get("/auth/me", rotatedAccessToken);
  assert.equal(meAfterRefresh.status, 200);
  assert.equal(
    (meAfterRefresh.body.user as Record<string, unknown>).email,
    "integration@example.com",
  );

  const logoutResponse = await post("/auth/logout", {
    refreshToken: rotatedRefreshToken,
  });
  assert.equal(logoutResponse.status, 200);
  assert.deepEqual(logoutResponse.body, { ok: true });

  const refreshAfterLogout = await post("/auth/refresh", {
    refreshToken: rotatedRefreshToken,
  });
  assert.equal(refreshAfterLogout.status, 401);
});

test("upload skin and fetch public URL", async () => {
  const registerResponse = await post("/auth/register", {
    username: "skin_user",
    email: "skin@example.com",
    password: "SuperSecure!123",
  });
  assert.equal(registerResponse.status, 200);

  const token = registerResponse.body.accessToken as string;
  const cmcUuid = (registerResponse.body.user as Record<string, unknown>).cmcUuid as string;
  const hex = cmcUuid.replace(/-/g, "").toLowerCase();

  const png = makePngBuffer(64, 64);
  const upload = await post(
    "/auth/skins",
    {
      skinBase64: png.toString("base64"),
    },
    token,
  );

  assert.equal(upload.status, 200);
  assert.equal((upload.body.ok as boolean) ?? false, true);

  const badAuth = await post("/auth/skins", { skinBase64: png.toString("base64") });
  assert.equal(badAuth.status, 401);

  const fetched = await getSkinPng(`/skins/${hex}.png`);
  assert.equal(fetched.status, 200);
  assert.equal(fetched.contentType, "image/png");
});

test("reject non-skin dimensions", async () => {
  const registerResponse = await post("/auth/register", {
    username: "skin_bad",
    email: "skinbad@example.com",
    password: "SuperSecure!123",
  });
  assert.equal(registerResponse.status, 200);
  const token = registerResponse.body.accessToken as string;
  const png = makePngBuffer(32, 32);
  const upload = await post(
    "/auth/skins",
    {
      skinBase64: png.toString("base64"),
    },
    token,
  );
  assert.equal(upload.status, 400);
  assert.equal(upload.body.error as string, "invalid_skin");
});
