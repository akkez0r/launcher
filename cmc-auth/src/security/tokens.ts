import jwt from "jsonwebtoken";

export type AccessClaims = {
  sub: string;
  username: string;
  cmcUuid: string;
};

const ACCESS_TOKEN_TTL = process.env.CMC_ACCESS_TOKEN_TTL ?? "15m";

export function signAccessToken(claims: AccessClaims, secret: string): string {
  return jwt.sign(claims, secret, { expiresIn: ACCESS_TOKEN_TTL });
}

export function verifyAccessToken(token: string, secret: string): AccessClaims {
  const decoded = jwt.verify(token, secret);

  if (
    typeof decoded === "string" ||
    typeof decoded.sub !== "string" ||
    typeof decoded.username !== "string" ||
    typeof decoded.cmcUuid !== "string"
  ) {
    throw new Error("Access token claims are invalid");
  }

  return {
    sub: decoded.sub,
    username: decoded.username,
    cmcUuid: decoded.cmcUuid,
  };
}
