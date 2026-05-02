import type { QueryResultRow } from "pg";

import { AuthError } from "../auth/service";
import { query } from "../db";
import { verifyAccessToken } from "../security/tokens";

import type { ParsedPngDims } from "./types";
import { validateMinecraftSkinPng } from "./validateSkinPng";

const MAX_SKIN_DECODED_BYTES = 600_000;

export class SkinUploadError extends Error {
  constructor(public readonly reason: string) {
    super(`invalid_skin:${reason}`);
    this.name = "SkinUploadError";
  }
}

export function stripDataUrlBase64(input: string): string {
  const trimmed = input.trim();
  const comma = trimmed.indexOf("base64,");
  if (comma !== -1) {
    return trimmed.slice(comma + "base64,".length).trim();
  }
  return trimmed;
}

export async function upsertAuthenticatedUserSkin(params: {
  accessTokenSecret: string;
  accessToken: string;
  skinBase64: string;
}): Promise<{ ok: true; width: number; height: number }> {
  let claims;
  try {
    claims = verifyAccessToken(params.accessToken, params.accessTokenSecret);
  } catch {
    throw new AuthError("INVALID_TOKEN", "access token is invalid");
  }

  const rawB64 = stripDataUrlBase64(params.skinBase64);
  let buffer: Buffer;
  try {
    buffer = Buffer.from(rawB64, "base64");
  } catch {
    throw new SkinUploadError("invalid_base64");
  }

  if (buffer.length === 0 || buffer.length > MAX_SKIN_DECODED_BYTES) {
    throw new SkinUploadError("size_out_of_bounds");
  }

  const dims: ParsedPngDims = validateMinecraftSkinPng(buffer);
  if (!dims.ok) {
    throw new SkinUploadError(dims.reason);
  }

  const { rows: idRows } = await query<{ cmc_uuid: string }>(
    `
      SELECT cmc_uuid::text AS cmc_uuid
      FROM users
      WHERE id = $1
      LIMIT 1
    `,
    [claims.sub],
  );

  if (idRows.length === 0) {
    throw new AuthError("INVALID_TOKEN", "unknown user");
  }

  const cmcUuidHex = idRows[0].cmc_uuid.toLowerCase().replace(/-/g, "");

  await query("DELETE FROM user_skins WHERE user_id = $1", [claims.sub]);
  await query(
    `
      INSERT INTO user_skins (user_id, cmc_uuid_hex, skin_png_base64, width, height, updated_at)
      VALUES ($1, $2, $3, $4, $5, NOW())
    `,
    [claims.sub, cmcUuidHex, buffer.toString("base64"), dims.width, dims.height],
  );

  return { ok: true, width: dims.width, height: dims.height };
}

type SkinRow = QueryResultRow & {
  skin_png_base64: string;
};

export async function lookupSkinBytesByHyphenlessUuid(hex: string): Promise<Buffer | null> {
  const id = hex.toLowerCase().trim();
  if (!/^[0-9a-f]{32}$/.test(id)) {
    return null;
  }

  const { rows } = await query<SkinRow>(
    `
      SELECT skin_png_base64
      FROM user_skins
      WHERE cmc_uuid_hex = $1
      LIMIT 1
    `,
    [id],
  );

  if (rows.length === 0) {
    return null;
  }

  try {
    return Buffer.from(rows[0].skin_png_base64, "base64");
  } catch {
    return null;
  }
}
