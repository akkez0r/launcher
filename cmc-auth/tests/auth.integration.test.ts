import assert from "node:assert/strict";
import { after, before, test } from "node:test";
import type { AddressInfo } from "node:net";

import type { Server } from "node:http";

import { createApp } from "../src/server";

type JsonValue = Record<string, unknown>;

let server: Server;
let baseUrl = "";

before(async () => {
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

test("register/login/me/refresh/logout lifecycle rotates refresh tokens", async () => {
  const registerResponse = await post("/auth/register", {
    username: "integration_user",
    email: "integration@example.com",
    password: "SuperSecure!123",
  });

  assert.equal(registerResponse.status, 201);
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
    email: "integration@example.com",
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
  assert.equal(logoutResponse.status, 204);

  const refreshAfterLogout = await post("/auth/refresh", {
    refreshToken: rotatedRefreshToken,
  });
  assert.equal(refreshAfterLogout.status, 401);
});
