import { test } from "node:test";
import assert from "node:assert/strict";

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
    sub: "user-123",
    username: "test_user",
    cmcUuid: "cmc-uuid-123",
  };
  const secret = "test-secret";
  const token = signAccessToken(claims, secret);
  const payload = verifyAccessToken(token, secret);

  assert.equal(payload.sub, "user-123");
  assert.equal(payload.username, "test_user");
  assert.equal(payload.cmcUuid, "cmc-uuid-123");
});
