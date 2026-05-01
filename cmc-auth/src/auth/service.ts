import crypto from "node:crypto";
import jwt from "jsonwebtoken";

import { hashPassword, verifyPassword } from "../security/password";
import { signAccessToken, verifyAccessToken } from "../security/tokens";

export type AuthConfig = {
  accessTokenSecret: string;
  refreshTokenSecret: string;
  refreshTokenTtlMs: number;
};

export type AuthUser = {
  id: string;
  username: string;
  email: string;
  cmcUuid: string;
};

type StoredUser = AuthUser & {
  passwordHash: string;
};

type StoredRefreshToken = {
  tokenHash: string;
  userId: string;
  expiresAtMs: number;
  revokedAtMs: number | null;
};

type AuthSession = {
  user: AuthUser;
  accessToken: string;
  refreshToken: string;
};

export class AuthError extends Error {
  constructor(
    public readonly code:
      | "USERNAME_TAKEN"
      | "EMAIL_TAKEN"
      | "INVALID_CREDENTIALS"
      | "INVALID_TOKEN",
    message: string,
  ) {
    super(message);
  }
}

export class AuthService {
  private readonly usersById = new Map<string, StoredUser>();
  private readonly userIdByEmail = new Map<string, string>();
  private readonly userIdByUsername = new Map<string, string>();
  private readonly refreshByHash = new Map<string, StoredRefreshToken>();

  constructor(private readonly config: AuthConfig) {}

  async register(input: {
    username: string;
    email: string;
    password: string;
    cmcUuid?: string;
  }): Promise<AuthSession> {
    const email = input.email.trim().toLowerCase();
    const username = input.username.trim();

    if (this.userIdByEmail.has(email)) {
      throw new AuthError("EMAIL_TAKEN", "email already exists");
    }
    if (this.userIdByUsername.has(username)) {
      throw new AuthError("USERNAME_TAKEN", "username already exists");
    }

    const passwordHash = await hashPassword(input.password);
    const user: StoredUser = {
      id: crypto.randomUUID(),
      username,
      email,
      cmcUuid: input.cmcUuid ?? crypto.randomUUID(),
      passwordHash,
    };

    this.usersById.set(user.id, user);
    this.userIdByEmail.set(user.email, user.id);
    this.userIdByUsername.set(user.username, user.id);

    return this.createSession(user);
  }

  async login(input: { email: string; password: string }): Promise<AuthSession> {
    const email = input.email.trim().toLowerCase();
    const userId = this.userIdByEmail.get(email);
    if (!userId) {
      throw new AuthError("INVALID_CREDENTIALS", "invalid credentials");
    }

    const user = this.usersById.get(userId);
    if (!user) {
      throw new AuthError("INVALID_CREDENTIALS", "invalid credentials");
    }

    const matches = await verifyPassword(input.password, user.passwordHash);
    if (!matches) {
      throw new AuthError("INVALID_CREDENTIALS", "invalid credentials");
    }

    return this.createSession(user);
  }

  async refresh(refreshToken: string): Promise<AuthSession> {
    const claims = this.verifyRefreshToken(refreshToken);
    const tokenHash = hashToken(refreshToken);
    const tokenRecord = this.refreshByHash.get(tokenHash);

    if (
      !tokenRecord ||
      tokenRecord.userId !== claims.sub ||
      tokenRecord.revokedAtMs !== null ||
      tokenRecord.expiresAtMs <= Date.now()
    ) {
      throw new AuthError("INVALID_TOKEN", "refresh token is invalid");
    }

    tokenRecord.revokedAtMs = Date.now();
    this.refreshByHash.set(tokenHash, tokenRecord);

    const user = this.usersById.get(claims.sub);
    if (!user) {
      throw new AuthError("INVALID_TOKEN", "refresh token is invalid");
    }

    return this.createSession(user);
  }

  logout(refreshToken: string): void {
    const tokenHash = hashToken(refreshToken);
    const tokenRecord = this.refreshByHash.get(tokenHash);
    if (!tokenRecord) {
      return;
    }

    tokenRecord.revokedAtMs = Date.now();
    this.refreshByHash.set(tokenHash, tokenRecord);
  }

  me(accessToken: string): AuthUser {
    let claims;
    try {
      claims = verifyAccessToken(accessToken, this.config.accessTokenSecret);
    } catch {
      throw new AuthError("INVALID_TOKEN", "access token is invalid");
    }

    const user = this.usersById.get(claims.sub);
    if (!user) {
      throw new AuthError("INVALID_TOKEN", "access token is invalid");
    }

    return toPublicUser(user);
  }

  private createSession(user: StoredUser): AuthSession {
    const accessToken = signAccessToken(
      {
        sub: user.id,
        username: user.username,
        cmcUuid: user.cmcUuid,
      },
      this.config.accessTokenSecret,
    );

    const expiresAtMs = Date.now() + this.config.refreshTokenTtlMs;
    const refreshJti = crypto.randomUUID();
    const refreshToken = jwt.sign(
      { sub: user.id, typ: "refresh", jti: refreshJti },
      this.config.refreshTokenSecret,
      {
        expiresIn: Math.max(1, Math.floor(this.config.refreshTokenTtlMs / 1000)),
      },
    );

    this.refreshByHash.set(hashToken(refreshToken), {
      tokenHash: hashToken(refreshToken),
      userId: user.id,
      expiresAtMs,
      revokedAtMs: null,
    });

    return {
      user: toPublicUser(user),
      accessToken,
      refreshToken,
    };
  }

  private verifyRefreshToken(token: string): { sub: string } {
    try {
      const decoded = jwt.verify(token, this.config.refreshTokenSecret);
      if (
        typeof decoded !== "object" ||
        decoded === null ||
        typeof decoded.sub !== "string"
      ) {
        throw new Error("refresh token claims invalid");
      }

      return { sub: decoded.sub };
    } catch {
      throw new AuthError("INVALID_TOKEN", "refresh token is invalid");
    }
  }
}

function toPublicUser(user: StoredUser): AuthUser {
  return {
    id: user.id,
    username: user.username,
    email: user.email,
    cmcUuid: user.cmcUuid,
  };
}

function hashToken(token: string): string {
  return crypto.createHash("sha256").update(token).digest("hex");
}
