import { test } from "node:test";
import assert from "node:assert/strict";
import jwt from "jsonwebtoken";

import { hashPassword, verifyPassword } from "../src/security/password";
import { signAccessToken, verifyAccessToken } from "../src/security/tokens";

test("hashPassword returns a hash distinct from plain text", async () => {
  const plain = "S3curePass!42";
  const hash = await hashPassword(plain);

  assert.notEqual(hash, plain);
  assert.ok(hash.length > 20);
});

test("verifyPassword returns true for matching password", async () => {
  const plain = "S3curePass!42";
  const hash = await hashPassword(plain);

  const isMatch = await verifyPassword(plain, hash);

  assert.equal(isMatch, true);
});

test("verifyPassword returns false for non-matching password", async () => {
  const hash = await hashPassword("S3curePass!42");

  const isMatch = await verifyPassword("WrongPass!42", hash);

  assert.equal(isMatch, false);
});

test("token roundtrip signs and verifies access claims", () => {
  const claims = {
    sub: "c0a8015e-8ad2-4a40-80b9-f98f5087d6f2",
    username: "test_user",
    cmcUuid: "d4fbb8f7-cad0-4f82-b2f8-45ddf95a6f43",
  };
  const secret = "test-secret";
  const token = signAccessToken(claims, secret);
  const payload = verifyAccessToken(token, secret);

  assert.equal(payload.sub, "c0a8015e-8ad2-4a40-80b9-f98f5087d6f2");
  assert.equal(payload.username, "test_user");
  assert.equal(payload.cmcUuid, "d4fbb8f7-cad0-4f82-b2f8-45ddf95a6f43");
});

test("verifyAccessToken rejects invalid UUID claims", () => {
  const secret = "test-secret";
  const tokenWithBadSub = jwt.sign(
    {
      sub: "not-a-uuid",
      username: "test_user",
      cmcUuid: "d4fbb8f7-cad0-4f82-b2f8-45ddf95a6f43",
    },
    secret,
  );
  const tokenWithBadCmcUuid = jwt.sign(
    {
      sub: "c0a8015e-8ad2-4a40-80b9-f98f5087d6f2",
      username: "test_user",
      cmcUuid: "not-a-uuid",
    },
    secret,
  );

  assert.throws(() => verifyAccessToken(tokenWithBadSub, secret), /invalid/i);
  assert.throws(
    () => verifyAccessToken(tokenWithBadCmcUuid, secret),
    /invalid/i,
  );
});

test("verifyAccessToken rejects empty or invalid usernames", () => {
  const secret = "test-secret";
  const token = jwt.sign(
    {
      sub: "c0a8015e-8ad2-4a40-80b9-f98f5087d6f2",
      username: "   ",
      cmcUuid: "d4fbb8f7-cad0-4f82-b2f8-45ddf95a6f43",
    },
    secret,
  );

  assert.throws(() => verifyAccessToken(token, secret), /invalid/i);
});
