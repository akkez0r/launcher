import cors from "cors";
import dotenv from "dotenv";
import express from "express";
import { createAuthRouter } from "./auth/routes";

dotenv.config();

export type AppConfig = {
  auth?: {
    accessTokenSecret?: string;
    refreshTokenSecret?: string;
    refreshTokenTtlMs?: number;
  };
};

export function createApp(config: AppConfig = {}): express.Express {
  const app = express();
  const allowedOrigins = new Set(
    (process.env.CORS_ALLOWED_ORIGINS ?? "")
      .split(",")
      .map((origin) => origin.trim())
      .filter(Boolean),
  );
  const accessTokenSecret =
    config.auth?.accessTokenSecret ?? process.env.CMC_ACCESS_TOKEN_SECRET;
  const refreshTokenSecret =
    config.auth?.refreshTokenSecret ?? process.env.CMC_REFRESH_TOKEN_SECRET;
  const refreshTokenTtlMsInput =
    config.auth?.refreshTokenTtlMs ?? process.env.CMC_REFRESH_TOKEN_TTL_MS;
  const refreshTokenTtlMs = parseRefreshTokenTtlMs(refreshTokenTtlMsInput);

  if (!accessTokenSecret) {
    throw new Error(
      "CMC_ACCESS_TOKEN_SECRET is required to initialize auth routes",
    );
  }
  if (!refreshTokenSecret) {
    throw new Error(
      "CMC_REFRESH_TOKEN_SECRET is required to initialize auth routes",
    );
  }

  app.use(
    cors({
      origin(origin, callback) {
        if (!origin || allowedOrigins.has(origin)) {
          callback(null, true);
          return;
        }

        callback(new Error("Origin not allowed by CORS"));
      },
    }),
  );
  app.use(express.json());

  app.get("/health", (_req, res) => {
    res.json({ ok: true });
  });

  app.use(
    "/auth",
    createAuthRouter({
      accessTokenSecret,
      refreshTokenSecret,
      refreshTokenTtlMs,
    }),
  );

  return app;
}

if (require.main === module) {
  const app = createApp();
  const port = Number(process.env.PORT ?? 8787);

  app.listen(port, () => {
    console.log(`cmc-auth listening on ${port}`);
  });
}

function parseRefreshTokenTtlMs(value: number | string | undefined): number {
  const parsed =
    typeof value === "number"
      ? value
      : typeof value === "string"
        ? Number(value)
        : Number.NaN;

  if (!Number.isFinite(parsed) || !Number.isInteger(parsed) || parsed <= 0) {
    throw new Error("CMC_REFRESH_TOKEN_TTL_MS must be a positive integer");
  }

  return parsed;
}
