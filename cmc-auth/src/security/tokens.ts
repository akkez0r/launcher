import jwt from "jsonwebtoken";

export type AccessTokenPayload = {
  userId: string;
  email: string;
};

const ACCESS_TOKEN_TTL = process.env.CMC_ACCESS_TOKEN_TTL ?? "15m";

function getJwtSecret(): string {
  const jwtSecret = process.env.CMC_JWT_SECRET;

  if (!jwtSecret) {
    throw new Error("CMC_JWT_SECRET is required to sign and verify tokens");
  }

  return jwtSecret;
}

export function signAccessToken(payload: AccessTokenPayload): string {
  return jwt.sign(payload, getJwtSecret(), { expiresIn: ACCESS_TOKEN_TTL });
}

export function verifyAccessToken(token: string): AccessTokenPayload {
  const decoded = jwt.verify(token, getJwtSecret());

  if (
    typeof decoded === "string" ||
    typeof decoded.userId !== "string" ||
    typeof decoded.email !== "string"
  ) {
    throw new Error("Access token payload is invalid");
  }

  return {
    userId: decoded.userId,
    email: decoded.email,
  };
}
