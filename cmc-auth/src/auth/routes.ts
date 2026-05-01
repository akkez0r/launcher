import express, { type Request } from "express";
import { ZodError, z } from "zod";

import { AuthError, AuthService, type AuthConfig } from "./service";

const usernameRe = /^[a-zA-Z0-9_]{3,32}$/;

const registerSchema = z.object({
  username: z.string().regex(usernameRe),
  email: z.string().email(),
  password: z.string().min(8),
});

const loginSchema = z.object({
  emailOrUsername: z.string().min(1),
  password: z.string().min(1),
});

const refreshSchema = z.object({
  refreshToken: z.string().min(1),
});

export function createAuthRouter(config: AuthConfig): express.Router {
  const router = express.Router();
  const service = new AuthService(config);

  router.post("/register", async (req, res) => {
    try {
      const body = registerSchema.parse(req.body);
      const result = await service.register(body);
      res.status(200).json(result);
    } catch (error) {
      respondWithError(res, error);
    }
  });

  router.post("/login", async (req, res) => {
    try {
      const body = loginSchema.parse(req.body);
      const result = await service.login(body);
      res.status(200).json(result);
    } catch (error) {
      respondWithError(res, error);
    }
  });

  router.post("/refresh", async (req, res) => {
    try {
      const body = refreshSchema.parse(req.body);
      const result = await service.refresh(body.refreshToken);
      res.status(200).json(result);
    } catch (error) {
      respondWithError(res, error);
    }
  });

  router.get("/me", (req, res) => {
    try {
      const token = extractBearerToken(req);
      const user = service.me(token);
      res.status(200).json({ user });
    } catch (error) {
      respondWithError(res, error);
    }
  });

  router.post("/logout", (req, res) => {
    try {
      const body = refreshSchema.parse(req.body);
      service.logout(body.refreshToken);
      res.status(200).json({ ok: true });
    } catch (error) {
      respondWithError(res, error);
    }
  });

  return router;
}

function extractBearerToken(req: Request): string {
  const authorizationHeader = req.header("authorization");
  if (!authorizationHeader) {
    throw new AuthError("INVALID_TOKEN", "missing authorization header");
  }

  const [scheme, token] = authorizationHeader.split(" ");
  if (scheme?.toLowerCase() !== "bearer" || !token) {
    throw new AuthError("INVALID_TOKEN", "invalid authorization header");
  }

  return token;
}

function respondWithError(
  res: express.Response,
  error: unknown,
): express.Response {
  if (error instanceof ZodError) {
    return res.status(400).json({
      error: "invalid_request",
      details: error.issues,
    });
  }

  if (error instanceof AuthError) {
    if (error.code === "EMAIL_TAKEN" || error.code === "USERNAME_TAKEN") {
      return res.status(409).json({ error: error.code.toLowerCase() });
    }
    if (error.code === "INVALID_CREDENTIALS") {
      return res.status(401).json({ error: "invalid_credentials" });
    }
    if (error.code === "INVALID_TOKEN") {
      return res.status(401).json({ error: "invalid_token" });
    }
  }

  return res.status(500).json({ error: "internal_error" });
}
