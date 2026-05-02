/**
 * Creates dist-artifacts/minecraft-source.zip from a local Minecraft MCP project tree
 * (Client.launch + src + game/, etc.) for attaching to launcher GitHub releases.
 *
 * Resolves MINECRAFT_PROJECT_DIR when set; otherwise tries ../minecraft and ./minecraft
 * relative to the launcher repo cwd. Exits 0 without writing a zip when no valid tree exists
 * (e.g. CI checkout that only contains the Electron app).
 */
"use strict";

const fs = require("fs");
const path = require("path");
const AdmZip = require("adm-zip");

/** @returns {readonly string[]} */
function splitPosix(rel) {
  return rel.split("/").filter(Boolean);
}

/**
 * @param {string} relPosix Relative path within project ( posix, no leading slash )
 * @returns {boolean}
 */
function shouldExclude(relPosix) {
  const seg = splitPosix(relPosix);
  const base = seg[0] ?? "";
  if (!base) {
    return true;
  }
  if (
    base === ".git" ||
    base === ".idea" ||
    base === ".settings" ||
    base === ".vscode" ||
    base === "bin" ||
    base === "out" ||
    base === "src_original" ||
    base === "md5" ||
    base === "__pycache__"
  ) {
    return true;
  }
  const leaf = seg[seg.length - 1];
  if (leaf.endsWith(".log")) {
    return true;
  }
  if (seg.includes("saves") || seg.includes("crash-reports")) {
    return true;
  }
  if (seg[0] === "game" && seg[1] === "stats") {
    return true;
  }
  if (leaf === "options.txt" || leaf === "options.cfg" || leaf.startsWith(".launcher-javac")) {
    return true;
  }
  return false;
}

/**
 * @param {string} projectDir
 */
function zipMinecraftProject(projectDir) {
  const clientLaunch = path.join(projectDir, "Client.launch");
  const srcDir = path.join(projectDir, "src");
  if (!fs.existsSync(clientLaunch)) {
    throw new Error(`Missing Client.launch under ${projectDir}`);
  }
  if (!fs.existsSync(srcDir) || !fs.statSync(srcDir).isDirectory()) {
    throw new Error(`Missing src/ under ${projectDir}`);
  }

  const zip = new AdmZip();
  const ROOT = "minecraft-source";

  /** @type {{ abs: string, relPosix: string }[]} */
  const stack = [{ abs: projectDir, relPosix: "" }];

  while (stack.length > 0) {
    const item = stack.pop();
    if (!item) {
      continue;
    }
    const entries = fs.readdirSync(item.abs, { withFileTypes: true });
    for (const ent of entries) {
      const name = ent.name;
      const nextRelPosix =
        item.relPosix === "" ? name.replace(/\\/g, "/") : `${item.relPosix}/${name}`.replace(/\\/g, "/");
      if (shouldExclude(nextRelPosix)) {
        continue;
      }

      const full = path.join(item.abs, name);
      if (ent.isDirectory()) {
        stack.push({ abs: full, relPosix: nextRelPosix });
        continue;
      }
      if (ent.isFile()) {
        const entryPath = `${ROOT}/${nextRelPosix}`.replace(/\\/g, "/");
        zip.addFile(entryPath, fs.readFileSync(full));
      }
    }
  }

  return zip;
}

function resolveProjectDir() {
  const cwd = process.cwd();
  /** @type {string[]} */
  const candidates = [];
  const env = process.env.MINECRAFT_PROJECT_DIR?.trim();
  if (env) {
    candidates.push(path.resolve(env));
  }
  candidates.push(path.resolve(cwd, "..", "minecraft"), path.resolve(cwd, "minecraft"));

  for (const dir of candidates) {
    const cl = path.join(dir, "Client.launch");
    const src = path.join(dir, "src");
    if (fs.existsSync(cl) && fs.existsSync(src)) {
      return dir;
    }
  }
  return null;
}

function main() {
  const projectDir = resolveProjectDir();
  if (!projectDir) {
    process.stdout.write(
      "package-minecraft-source: no Minecraft project found (set MINECRAFT_PROJECT_DIR or place ../minecraft). Skipping.\n"
    );
    process.exit(0);
  }

  const outDir = path.join(process.cwd(), "dist-artifacts");
  const outFile = path.join(outDir, "minecraft-source.zip");
  fs.mkdirSync(outDir, { recursive: true });

  const zip = zipMinecraftProject(projectDir);
  zip.writeZip(outFile);

  const mb = (fs.statSync(outFile).size / (1024 * 1024)).toFixed(2);
  process.stdout.write(
    `package-minecraft-source: wrote ${outFile} (${mb} MiB) from ${projectDir}\n`
  );
  process.exit(0);
}

main();
