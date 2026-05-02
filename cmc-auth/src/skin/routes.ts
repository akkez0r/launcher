import type { Request } from "express";
import express, { Router } from "express";
import { ZodError, z } from "zod";

import { AuthError } from "../auth/service";

import { lookupSkinBytesByHyphenlessUuid, SkinUploadError, upsertAuthenticatedUserSkin } from "./service";

const skinUploadBodySchema = z.object({
  skinBase64: z.string().min(1),
});

export function createAuthenticatedSkinRoutes(params: {
  accessTokenSecret: string;
}): Router {
  const router = Router();
  router.post("/skins", async (req, res) => {
    try {
      const body = skinUploadBodySchema.parse(req.body);
      const token = extractBearerToken(req);
      const saved = await upsertAuthenticatedUserSkin({
        accessTokenSecret: params.accessTokenSecret,
        accessToken: token,
        skinBase64: body.skinBase64,
      });

      res.status(200).json({
        ok: true,
        width: saved.width,
        height: saved.height,
      });
    } catch (error) {
      respondSkinError(res, error);
    }
  });

  return router;
}

export function attachPublicSkinGetRoute(app: express.Express): void {
  app.get("/skins/:skinAsset", async (req, res) => {
    const asset = typeof req.params.skinAsset === "string" ? req.params.skinAsset : "";
    const lower = asset.toLowerCase();
    const match = /^([0-9a-f]{32})\.png$/.exec(lower);
    if (!match) {
      res.status(400).send("Bad request");
      return;
    }

    try {
      const bytes = await lookupSkinBytesByHyphenlessUuid(match[1]);
      if (!bytes) {
        res.status(404).send("Not found");
        return;
      }

      res.setHeader("Content-Type", "image/png");
      res.setHeader("Cache-Control", "public, max-age=300");
      res.status(200).send(bytes);
    } catch {
      res.status(500).send("Internal error");
    }
  });
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

function respondSkinError(res: express.Response, error: unknown): express.Response {
  if (error instanceof ZodError) {
    return res.status(400).json({
      error: "invalid_request",
      details: error.issues,
    });
  }

  if (error instanceof SkinUploadError) {
    return res.status(400).json({
      error: "invalid_skin",
      reason: error.reason,
    });
  }

  if (error instanceof AuthError && error.code === "INVALID_TOKEN") {
    return res.status(401).json({ error: "invalid_token" });
  }

  // eslint-disable-next-line no-console
  console.error("skin_route_error", error);
  return res.status(500).json({ error: "internal_error" });
}
