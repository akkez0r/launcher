export type ParsedPngDims =
  | {
      ok: true;
      width: number;
      height: number;
    }
  | {
      ok: false;
      reason:
        | "file_too_small"
        | "not_png"
        | "invalid_png"
        | "dimensions_too_large"
        | "unsupported_dimensions";
      width?: number;
      height?: number;
    };
