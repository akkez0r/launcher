import crypto from "node:crypto";
import jwt from "jsonwebtoken";
import type { PoolClient, QueryResultRow } from "pg";

import { db, query } from "../db";
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

type DbUser = AuthUser & {
  passwordHash: string;
};

type DbRefreshToken = {
  tokenHash: string;
  userId: string;
  expiresAt: Date;
  revokedAt: Date | null;
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
  constructor(private readonly config: AuthConfig) {}

  async register(input: {
    username: string;
    email: string;
    password: string;
    cmcUuid?: string;
  }): Promise<AuthSession> {
    const email = input.email.trim().toLowerCase();
    const username = input.username.trim();
    const passwordHash = await hashPassword(input.password);
    const cmcUuid = input.cmcUuid ?? crypto.randomUUID();

    try {
      const { rows } = await query<UserRow>(
        `
          INSERT INTO users (username, email, cmc_uuid, password_hash)
          VALUES ($1, $2, $3, $4)
          RETURNING id, username, email, cmc_uuid, password_hash
        `,
        [username, email, cmcUuid, passwordHash],
      );
      const user = mapDbUser(rows[0]);
      return this.createSession(user);
    } catch (error) {
      if (isPgUniqueViolation(error)) {
        const target = String(error.constraint ?? "");
        if (target.includes("email")) {
          throw new AuthError("EMAIL_TAKEN", "email already exists");
        }
        if (target.includes("username")) {
          throw new AuthError("USERNAME_TAKEN", "username already exists");
        }
      }

      throw error;
    }
  }

  async login(input: {
    emailOrUsername: string;
    password: string;
  }): Promise<AuthSession> {
    const identity = input.emailOrUsername.trim();
    const normalizedEmail = identity.toLowerCase();
    const { rows } = await query<UserRow>(
      `
        SELECT id, username, email, cmc_uuid, password_hash
        FROM users
        WHERE email = $1 OR username = $2
        LIMIT 1
      `,
      [normalizedEmail, identity],
    );
    if (rows.length === 0) {
      throw new AuthError("INVALID_CREDENTIALS", "invalid credentials");
    }

    const user = mapDbUser(rows[0]);
    const matches = await verifyPassword(input.password, user.passwordHash);
    if (!matches) {
      throw new AuthError("INVALID_CREDENTIALS", "invalid credentials");
    }

    return this.createSession(user);
  }

  async refresh(refreshToken: string): Promise<AuthSession> {
    const claims = this.verifyRefreshToken(refreshToken);
    const tokenHash = hashToken(refreshToken);
    const client = await db.connect();

    try {
      await client.query("BEGIN");
      const { rows } = await client.query<RefreshWithUserRow>(
        `
          SELECT
            rt.token_hash,
            rt.user_id,
            rt.expires_at,
            rt.revoked_at,
            u.id,
            u.username,
            u.email,
            u.cmc_uuid,
            u.password_hash
          FROM refresh_tokens rt
          INNER JOIN users u ON u.id = rt.user_id
          WHERE rt.token_hash = $1
          FOR UPDATE
        `,
        [tokenHash],
      );

      if (rows.length === 0) {
        throw new AuthError("INVALID_TOKEN", "refresh token is invalid");
      }

      const tokenRecord = mapDbRefresh(rows[0]);
      if (
        tokenRecord.userId !== claims.sub ||
        tokenRecord.revokedAt !== null ||
        tokenRecord.expiresAt.getTime() <= Date.now()
      ) {
        throw new AuthError("INVALID_TOKEN", "refresh token is invalid");
      }

      await client.query(
        `
          UPDATE refresh_tokens
          SET revoked_at = NOW()
          WHERE token_hash = $1
        `,
        [tokenHash],
      );

      const user = mapDbUser(rows[0]);
      const session = await this.createSession(user, client);
      await client.query("COMMIT");
      return session;
    } catch (error) {
      await client.query("ROLLBACK");
      if (error instanceof AuthError) {
        throw error;
      }
      throw error;
    } finally {
      client.release();
    }
  }

  async logout(refreshToken: string): Promise<void> {
    const tokenHash = hashToken(refreshToken);
    await query(
      `
        UPDATE refresh_tokens
        SET revoked_at = NOW()
        WHERE token_hash = $1
      `,
      [tokenHash],
    );
  }

  async me(accessToken: string): Promise<AuthUser> {
    let claims;
    try {
      claims = verifyAccessToken(accessToken, this.config.accessTokenSecret);
    } catch {
      throw new AuthError("INVALID_TOKEN", "access token is invalid");
    }

    const { rows } = await query<AuthUserRow>(
      `
        SELECT id, username, email, cmc_uuid
        FROM users
        WHERE id = $1
        LIMIT 1
      `,
      [claims.sub],
    );
    if (rows.length === 0) {
      throw new AuthError("INVALID_TOKEN", "access token is invalid");
    }

    return mapAuthUser(rows[0]);
  }

  private async createSession(
    user: DbUser,
    client?: PoolClient,
  ): Promise<AuthSession> {
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

    const runner = client ?? db;
    await runner.query(
      `
        INSERT INTO refresh_tokens (token_hash, user_id, expires_at)
        VALUES ($1, $2, $3)
      `,
      [hashToken(refreshToken), user.id, new Date(expiresAtMs)],
    );

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

function toPublicUser(user: DbUser): AuthUser {
  return {
    id: user.id,
    username: user.username,
    email: user.email,
    cmcUuid: user.cmcUuid,
  };
}

type UserRow = QueryResultRow & {
  id: string;
  username: string;
  email: string;
  cmc_uuid: string;
  password_hash: string;
};

type AuthUserRow = QueryResultRow & {
  id: string;
  username: string;
  email: string;
  cmc_uuid: string;
};

type RefreshWithUserRow = UserRow & {
  token_hash: string;
  user_id: string;
  expires_at: Date;
  revoked_at: Date | null;
};

function mapDbUser(row: UserRow): DbUser {
  return {
    id: row.id,
    username: row.username,
    email: row.email,
    cmcUuid: row.cmc_uuid,
    passwordHash: row.password_hash,
  };
}

function mapAuthUser(row: AuthUserRow): AuthUser {
  return {
    id: row.id,
    username: row.username,
    email: row.email,
    cmcUuid: row.cmc_uuid,
  };
}

function mapDbRefresh(row: RefreshWithUserRow): DbRefreshToken {
  return {
    tokenHash: row.token_hash,
    userId: row.user_id,
    expiresAt: new Date(row.expires_at),
    revokedAt: row.revoked_at ? new Date(row.revoked_at) : null,
  };
}

type PgErrorLike = {
  code?: string;
  constraint?: string;
};

function isPgUniqueViolation(error: unknown): error is PgErrorLike {
  if (!error || typeof error !== "object") {
    return false;
  }

  return (error as PgErrorLike).code === "23505";
}

function hashToken(token: string): string {
  return crypto.createHash("sha256").update(token).digest("hex");
}
