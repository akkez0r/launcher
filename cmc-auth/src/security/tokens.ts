import jwt from "jsonwebtoken";

export type AccessClaims = {
  sub: string;
  username: string;
  cmcUuid: string;
};

const ACCESS_TOKEN_TTL_SECONDS = parseAccessTokenTtlSeconds(
  process.env.CMC_ACCESS_TOKEN_TTL,
);
const UUID_RE =
  /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
const USERNAME_RE = /^[a-zA-Z0-9_]{3,32}$/;

function isValidAccessClaims(value: unknown): value is AccessClaims {
  if (typeof value !== "object" || value === null) {
    return false;
  }

  const claims = value as Record<string, unknown>;

  return (
    typeof claims.sub === "string" &&
    UUID_RE.test(claims.sub) &&
    typeof claims.cmcUuid === "string" &&
    UUID_RE.test(claims.cmcUuid) &&
    typeof claims.username === "string" &&
    USERNAME_RE.test(claims.username)
  );
}

export function signAccessToken(claims: AccessClaims, secret: string): string {
  return jwt.sign(claims, secret, { expiresIn: ACCESS_TOKEN_TTL_SECONDS });
}

export function verifyAccessToken(token: string, secret: string): AccessClaims {
  const decoded = jwt.verify(token, secret);

  if (!isValidAccessClaims(decoded)) {
    throw new Error("Access token claims are invalid");
  }

  return {
    sub: decoded.sub,
    username: decoded.username,
    cmcUuid: decoded.cmcUuid,
  };
}

function parseAccessTokenTtlSeconds(value: string | undefined): number {
  if (!value) {
    return 15 * 60;
  }

  const parsed = Number(value);
  if (!Number.isFinite(parsed) || !Number.isInteger(parsed) || parsed <= 0) {
    throw new Error("CMC_ACCESS_TOKEN_TTL must be a positive integer");
  }

  return parsed;
}
