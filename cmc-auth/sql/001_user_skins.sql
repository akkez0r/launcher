-- CMC auth: one PNG skin per user (64x32 or 64x64 Minecraft layout).
-- Apply on your PostgreSQL database used by cmc-auth.

CREATE TABLE IF NOT EXISTS user_skins (
  user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
  cmc_uuid_hex CHARACTER(32) NOT NULL UNIQUE,
  skin_png_base64 TEXT NOT NULL,
  width INTEGER NOT NULL,
  height INTEGER NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_user_skins_cmc_hex ON user_skins (cmc_uuid_hex);
