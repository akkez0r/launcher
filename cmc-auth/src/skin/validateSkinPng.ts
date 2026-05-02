import type { ParsedPngDims } from "./types";

export const SKIN_DIMENSIONS_ALLOWED: ReadonlyArray<readonly [number, number]> = [
  [64, 32],
  [64, 64],
];

const PNG_SIGNATURE = Buffer.from([0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]);

export function readPngDimensions(buffer: Buffer): ParsedPngDims {
  if (buffer.length < 24) {
    return { ok: false, reason: "file_too_small" };
  }

  if (!buffer.subarray(0, PNG_SIGNATURE.length).equals(PNG_SIGNATURE)) {
    return { ok: false, reason: "not_png" };
  }

  const chunkLength = buffer.readUInt32BE(8);
  if (chunkLength !== 13) {
    return { ok: false, reason: "invalid_png" };
  }

  const ihdrChars = buffer.toString("ascii", 12, 16);
  if (ihdrChars !== "IHDR") {
    return { ok: false, reason: "invalid_png" };
  }

  const width = buffer.readUInt32BE(16);
  const height = buffer.readUInt32BE(20);

  if (!Number.isFinite(width) || !Number.isFinite(height) || width <= 0 || height <= 0) {
    return { ok: false, reason: "invalid_png" };
  }

  if (width > 1024 || height > 1024) {
    return { ok: false, reason: "dimensions_too_large" };
  }

  return { ok: true, width, height };
}

export function validateMinecraftSkinPng(buffer: Buffer): ParsedPngDims {
  const dims = readPngDimensions(buffer);
  if (!dims.ok) {
    return dims;
  }

  const allowed = SKIN_DIMENSIONS_ALLOWED.some(
    ([w, h]) => w === dims.width && h === dims.height,
  );
  if (!allowed) {
    return {
      ok: false,
      reason: "unsupported_dimensions",
      width: dims.width,
      height: dims.height,
    };
  }

  return dims;
}
