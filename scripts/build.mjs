import { build } from "esbuild";
import { cpSync, existsSync, mkdirSync, rmSync } from "node:fs";
import path from "node:path";

const watchMode = process.argv.includes("--watch");
const root = process.cwd();
const distDir = path.join(root, "dist");
const rendererDir = path.join(distDir, "renderer");

if (!watchMode && existsSync(distDir)) {
  rmSync(distDir, { recursive: true, force: true });
}

mkdirSync(path.join(distDir, "main"), { recursive: true });
mkdirSync(rendererDir, { recursive: true });

const common = {
  bundle: true,
  sourcemap: true,
  logLevel: "info"
};

await build({
  entryPoints: ["src/main/main.ts"],
  outfile: "dist/main/main.js",
  platform: "node",
  format: "cjs",
  target: "node20",
  external: ["electron", "electron-updater", "electron-store"],
  ...common
});

await build({
  entryPoints: ["src/main/preload.ts"],
  outfile: "dist/main/preload.js",
  platform: "node",
  format: "cjs",
  target: "node20",
  external: ["electron"],
  ...common
});

await build({
  entryPoints: ["src/renderer/index.tsx"],
  outfile: "dist/renderer/index.js",
  platform: "browser",
  format: "iife",
  target: "chrome120",
  jsx: "automatic",
  ...common
});

cpSync("src/renderer/index.html", path.join(rendererDir, "index.html"));
if (existsSync("src/renderer/assets")) {
  cpSync("src/renderer/assets", path.join(rendererDir, "assets"), {
    recursive: true
  });
}

if (watchMode) {
  console.log("Watching for changes...");
}
