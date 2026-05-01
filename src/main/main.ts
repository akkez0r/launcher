import { app, BrowserWindow, dialog, ipcMain, shell } from "electron";
import { autoUpdater } from "electron-updater";
import Store from "electron-store";
import path from "node:path";
import {
  cpSync,
  closeSync,
  existsSync,
  mkdirSync,
  openSync,
  readFileSync,
  readdirSync,
  rmSync,
  statSync,
  unlinkSync,
  writeFileSync,
  createWriteStream
} from "node:fs";
import { spawn, spawnSync, type ChildProcess } from "node:child_process";
import https from "node:https";
import http, { type IncomingMessage } from "node:http";
import { pipeline } from "node:stream/promises";
import AdmZip from "adm-zip";
import {
  IPC_CHANNELS,
  LauncherAppInfo,
  UpdateEventPayload
} from "../shared/ipc";

type LauncherStore = {
  updateChannel: string;
  lastSeenVersion: string;
  minecraftExePath: string;
};

const store = new Store<LauncherStore>({
  defaults: {
    updateChannel: "latest",
    lastSeenVersion: app.getVersion(),
    minecraftExePath: ""
  }
});

let mainWindow: BrowserWindow | null = null;
const DEFAULT_PROGRAM_ARGS =
  "--username Player --uuid - --session - --version 1.5.2 --gameDir . --assetsDir .\\assets --assetIndex 1.4 --accessToken - --userProperties {} --userType legacy --versionType snapshot --skinProxy pre-1.8";
const LOCKED_MINECRAFT_SOURCE_ZIP_URL =
  "https://github.com/akkez0r/launcher/releases/latest/download/minecraft-source.zip";

type LaunchConfig = {
  command: string;
  args: string[];
  cwd?: string;
};

type SourcePrepResult = {
  ok: boolean;
  message: string;
};

function sendUpdateEvent(payload: UpdateEventPayload): void {
  mainWindow?.webContents.send(IPC_CHANNELS.UPDATE_EVENT, payload);
}

function createWindow(): void {
  mainWindow = new BrowserWindow({
    width: 900,
    height: 580,
    minWidth: 760,
    minHeight: 520,
    autoHideMenuBar: true,
    webPreferences: {
      preload: path.join(__dirname, "preload.js"),
      contextIsolation: true,
      nodeIntegration: false
    }
  });

  const indexPath = path.join(__dirname, "../renderer/index.html");
  mainWindow.loadFile(indexPath);
  mainWindow.on("closed", () => {
    mainWindow = null;
  });
}

function wireUpdater(): void {
  autoUpdater.autoDownload = true;
  autoUpdater.autoInstallOnAppQuit = true;
  autoUpdater.allowPrerelease = store.get("updateChannel") !== "latest";

  autoUpdater.on("checking-for-update", () => {
    sendUpdateEvent({ type: "checking", message: "Checking for updates..." });
  });

  autoUpdater.on("update-available", (info) => {
    sendUpdateEvent({
      type: "available",
      message: `Update ${info.version} is available. Downloading...`
    });
  });

  autoUpdater.on("update-not-available", (info) => {
    sendUpdateEvent({
      type: "not-available",
      message: `You are up to date (${info.version}).`
    });
  });

  autoUpdater.on("download-progress", (progress) => {
    sendUpdateEvent({
      type: "progress",
      percent: progress.percent,
      bytesPerSecond: progress.bytesPerSecond,
      message: `Downloading ${progress.percent.toFixed(1)}%`
    });
  });

  autoUpdater.on("update-downloaded", (info) => {
    store.set("lastSeenVersion", info.version);
    sendUpdateEvent({
      type: "downloaded",
      message: `Update ${info.version} downloaded. Restart to install.`
    });
  });

  autoUpdater.on("error", (error) => {
    sendUpdateEvent({
      type: "error",
      message: `Update error: ${error.message}`
    });
  });
}

function wireIpc(): void {
  ipcMain.handle(IPC_CHANNELS.APP_INFO, (): LauncherAppInfo => {
    return {
      version: app.getVersion(),
      updateChannel: store.get("updateChannel"),
      minecraftExePath: store.get("minecraftExePath")
    };
  });

  ipcMain.handle(IPC_CHANNELS.SELECT_MINECRAFT_EXE, async () => {
    const result = await dialog.showOpenDialog({
      title: "Select Minecraft source folder or executable",
      properties: ["openFile", "openDirectory"],
      filters: [
        { name: "Launch files", extensions: ["exe"] },
        { name: "All files", extensions: ["*"] }
      ]
    });

    if (result.canceled || result.filePaths.length === 0) {
      return "";
    }

    const selected = result.filePaths[0] ?? "";
    if (selected) {
      store.set("minecraftExePath", selected);
    }
    return selected;
  });

  ipcMain.handle(IPC_CHANNELS.SET_MINECRAFT_EXE, async (_event, value: string) => {
    store.set("minecraftExePath", value.trim());
  });

  ipcMain.handle(IPC_CHANNELS.DOWNLOAD_MINECRAFT_FROM_GITHUB, async () => {
    return downloadMinecraftFromGithub();
  });

  ipcMain.handle(IPC_CHANNELS.OPEN_MINECRAFT_WORLDS, async () => {
    const sourceRoot = resolveStoredSourceRoot();
    if (!sourceRoot) {
      return {
        ok: false,
        message: "Minecraft source path is not set. Save/download Minecraft first."
      };
    }

    const worldsDir = path.join(sourceRoot, "game", "saves");
    mkdirSync(worldsDir, { recursive: true });
    const openResult = await shell.openPath(worldsDir);
    if (openResult) {
      return { ok: false, message: `Failed to open worlds folder: ${openResult}` };
    }
    return { ok: true, message: `Opened worlds folder: ${worldsDir}` };
  });

  ipcMain.handle(IPC_CHANNELS.LAUNCH_MINECRAFT, async () => {
    let targetPath = store.get("minecraftExePath").trim();
    if (!targetPath || !existsSync(targetPath)) {
      const bootstrap = await downloadMinecraftFromGithub();
      if (!bootstrap.ok) {
        return { ok: false, message: bootstrap.message };
      }
      targetPath = bootstrap.path ?? store.get("minecraftExePath").trim();
    }

    if (!targetPath) {
      return { ok: false, message: "Set Minecraft path first or download your Minecraft package." };
    }

    if (!existsSync(targetPath)) {
      return { ok: false, message: "Minecraft path does not exist after download attempt." };
    }

    try {
      const sourceRoot = resolveSourceRootFromTarget(targetPath);
      let launchNote = "";
      if (sourceRoot && !hasJavaRuntime()) {
        return {
          ok: false,
          message: "Java not found on PATH. Install a JDK/JRE so source launch can run."
        };
      }

      if (sourceRoot) {
        const prepResult = prepareSourceProjectForLaunch(sourceRoot);
        if (!prepResult.ok) {
          return { ok: false, message: prepResult.message };
        }
        launchNote = prepResult.message;
      }

      const launchConfig = resolveLaunchConfig(targetPath);
      if (!launchConfig) {
        return {
          ok: false,
          message:
            "No launch target found. Pick a Minecraft source folder (with Client.launch) or a .exe."
        };
      }

      const launchLogPath = createLaunchLogPath();
      const launchLogFd = openSync(launchLogPath, "a");
      const child = spawn(launchConfig.command, launchConfig.args, {
        cwd: launchConfig.cwd,
        detached: true,
        stdio: ["ignore", launchLogFd, launchLogFd]
      });
      closeSync(launchLogFd);
      const launchProbe = await waitForProcessStart(child, 1600);
      if (!launchProbe.ok) {
        const details = readLaunchLogSnippet(launchLogPath);
        return {
          ok: false,
          message: `Minecraft process exited immediately (${launchProbe.reason}). ${details}`
        };
      }

      child.unref();
      const message = launchNote ? `${launchNote} Minecraft launched.` : "Minecraft launched.";
      return { ok: true, message };
    } catch (error) {
      const message = error instanceof Error ? error.message : "Unknown launch error.";
      return { ok: false, message: `Failed to launch: ${message}` };
    }
  });

  ipcMain.handle(IPC_CHANNELS.CHECK_FOR_UPDATES, async () => {
    await autoUpdater.checkForUpdates();
  });

  ipcMain.handle(IPC_CHANNELS.INSTALL_UPDATE, async () => {
    autoUpdater.quitAndInstall();
  });
}

function resolveLaunchConfig(targetPath: string): LaunchConfig | null {
  const javaCommand = resolveJavaCommand();
  const stats = statSync(targetPath);
  if (stats.isFile()) {
    const extension = path.extname(targetPath).toLowerCase();
    if (extension === ".exe") {
      return { command: targetPath, args: [], cwd: path.dirname(targetPath) };
    }

    return null;
  }

  if (!stats.isDirectory()) {
    return null;
  }

  const sourceRoot = resolveSourceRoot(targetPath);
  if (sourceRoot) {
    if (!javaCommand) {
      return null;
    }
    const sourceLaunch = resolveSourceProjectLaunch(sourceRoot, javaCommand);
    if (sourceLaunch) {
      return sourceLaunch;
    }
  }

  const minecraftExe = path.join(targetPath, "MinecraftLauncher.exe");
  if (existsSync(minecraftExe)) {
    return { command: minecraftExe, args: [], cwd: targetPath };
  }

  return null;
}

function resolveSourceRootFromTarget(targetPath: string): string | null {
  const stats = statSync(targetPath);
  if (!stats.isDirectory()) {
    return null;
  }

  return resolveSourceRoot(targetPath);
}

function resolveStoredSourceRoot(): string | null {
  const configuredPath = store.get("minecraftExePath").trim();
  if (!configuredPath || !existsSync(configuredPath)) {
    return null;
  }

  try {
    const stats = statSync(configuredPath);
    if (stats.isDirectory()) {
      return resolveSourceRoot(configuredPath) ?? configuredPath;
    }
  } catch {
    return null;
  }

  return null;
}

function hasJavaRuntime(): boolean {
  return resolveJavaCommand() !== null;
}

function resolveJavaCommand(): string | null {
  const javaCheck = spawnSync("where", ["java"], {
    windowsHide: true,
    stdio: "ignore"
  });
  if (javaCheck.status === 0) {
    return "java";
  }

  const javawCheck = spawnSync("where", ["javaw"], {
    windowsHide: true,
    stdio: "ignore"
  });
  if (javawCheck.status === 0) {
    return "javaw";
  }

  return null;
}

function resolveJavacCommand(): string | null {
  const javacCheck = spawnSync("where", ["javac"], {
    windowsHide: true,
    stdio: "ignore"
  });
  if (javacCheck.status === 0) {
    return "javac";
  }

  return null;
}

function resolveSourceRoot(targetPath: string): string | null {
  const directLaunch = path.join(targetPath, "Client.launch");
  if (existsSync(directLaunch)) {
    return targetPath;
  }

  const nested = path.join(targetPath, "minecraft");
  if (existsSync(path.join(nested, "Client.launch"))) {
    return nested;
  }

  return null;
}

function resolveSourceProjectLaunch(
  sourceRoot: string,
  javaCommand: string
): LaunchConfig | null {
  const launchFile = path.join(sourceRoot, "Client.launch");
  const gameDir = path.join(sourceRoot, "game");
  const binDir = path.join(sourceRoot, "bin");
  if (!existsSync(launchFile) || !existsSync(gameDir) || !existsSync(binDir)) {
    return null;
  }

  const launchArgsLine = extractProgramArgs(launchFile) ?? DEFAULT_PROGRAM_ARGS;
  const launchArgs = ensureFullscreenDefault(tokenizeArgs(launchArgsLine));
  const vmArgsLine = extractVmArgs(launchFile) ?? "";
  const vmArgs = tokenizeArgs(vmArgsLine);
  const sanitizedVmArgs = vmArgs.filter(
    (arg) =>
      !/^-D(?:java\.library\.path|org\.lwjgl\.librarypath|net\.java\.games\.input\.librarypath)=/.test(
        arg
      )
  );
  const nativeVmArgs = resolveNativeVmArgs(sourceRoot);
  const runtimeClasspath = buildRuntimeClasspath(sourceRoot);
  const classpath = runtimeClasspath.length > 0 ? runtimeClasspath : binDir;

  return {
    command: javaCommand,
    args: [
      ...sanitizedVmArgs,
      ...nativeVmArgs,
      "-cp",
      classpath,
      "org.mcphackers.launchwrapper.Launch",
      ...launchArgs
    ],
    cwd: gameDir
  };
}

function extractProgramArgs(launchFile: string): string | null {
  try {
    const xml = readFileSync(launchFile, "utf8");
    const match = xml.match(
      /key="org\.eclipse\.jdt\.launching\.PROGRAM_ARGUMENTS"\s+value="([^"]*)"/
    );
    return match?.[1] ?? null;
  } catch {
    return null;
  }
}

function extractVmArgs(launchFile: string): string | null {
  try {
    const xml = readFileSync(launchFile, "utf8");
    const match = xml.match(/key="org\.eclipse\.jdt\.launching\.VM_ARGUMENTS"\s+value="([^"]*)"/);
    return match?.[1] ?? null;
  } catch {
    return null;
  }
}

function tokenizeArgs(argsLine: string): string[] {
  const matches = argsLine.match(/(?:[^\s"]+|"[^"]*")+/g) ?? [];
  return matches.map((item) => item.replace(/^"|"$/g, ""));
}

function ensureFullscreenDefault(args: string[]): string[] {
  const hasFullscreenFlag = args.some(
    (arg) => arg === "--fullscreen" || arg === "-fullscreen" || arg === "--windowed"
  );
  if (hasFullscreenFlag) {
    return args;
  }

  return [...args, "--fullscreen"];
}

function prepareSourceProjectForLaunch(sourceRoot: string): SourcePrepResult {
  const updateMessage = updateSourceFromGit(sourceRoot);
  const compileResult = compileSourceProject(sourceRoot);
  if (!compileResult.ok) {
    return compileResult;
  }

  return {
    ok: true,
    message: `${updateMessage} ${compileResult.message}`.trim()
  };
}

function updateSourceFromGit(sourceRoot: string): string {
  if (!existsSync(path.join(sourceRoot, ".git"))) {
    return "No git repo detected; skipping source update.";
  }

  const result = spawnSync("git", ["pull", "--ff-only"], {
    cwd: sourceRoot,
    windowsHide: true,
    encoding: "utf8"
  });

  const output = `${result.stdout ?? ""}\n${result.stderr ?? ""}`;
  if (result.status !== 0) {
    return `Git update failed (${firstLine(output)}). Launching local source.`;
  }

  if (/already up to date/i.test(output)) {
    return "Minecraft source already up to date.";
  }

  return "Minecraft source updated from git.";
}

function compileSourceProject(sourceRoot: string): SourcePrepResult {
  const javacCommand = resolveJavacCommand();
  if (!javacCommand) {
    return {
      ok: false,
      message: "JDK not found (javac missing on PATH). Install a JDK to run source builds."
    };
  }

  const srcDir = path.join(sourceRoot, "src");
  const binDir = path.join(sourceRoot, "bin");
  if (!existsSync(srcDir)) {
    if (existsSync(binDir)) {
      return {
        ok: true,
        message: "Using protected precompiled Minecraft build."
      };
    }
    return {
      ok: false,
      message: "Minecraft source package is missing both src and bin folders."
    };
  }

  mkdirSync(binDir, { recursive: true });

  const javaFiles = collectJavaFiles(srcDir, sourceRoot);
  if (javaFiles.length === 0) {
    return {
      ok: false,
      message: "No Java files found under src; cannot compile source project."
    };
  }

  const classpath = buildCompileClasspath(sourceRoot);
  const argsFile = path.join(sourceRoot, ".launcher-javac-args.txt");
  const lines = [
    "-cp",
    quoteArgfileValue(classpath),
    "-d",
    quoteArgfileValue("bin"),
    ...javaFiles.map((file) => quoteArgfileValue(file))
  ];

  try {
    writeFileSync(argsFile, lines.join("\n"), "utf8");
    const result = spawnSync(javacCommand, [`@${argsFile}`], {
      cwd: sourceRoot,
      windowsHide: true,
      encoding: "utf8"
    });
    if (result.status !== 0) {
      const failure = `${result.stdout ?? ""}\n${result.stderr ?? ""}`;
      return {
        ok: false,
        message: `Source compile failed: ${firstLine(failure)}`
      };
    }
  } finally {
    if (existsSync(argsFile)) {
      unlinkSync(argsFile);
    }
  }

  return { ok: true, message: "Source project compiled." };
}

function buildCompileClasspath(sourceRoot: string): string {
  const entries = ["bin", ...collectJarFiles(sourceRoot).map((jarPath) => normalizePathForJava(path.relative(sourceRoot, jarPath)))];
  return entries.join(path.delimiter);
}

function buildRuntimeClasspath(sourceRoot: string): string {
  const entries = [path.join(sourceRoot, "bin"), ...collectJarFiles(sourceRoot).map((jarPath) => normalizePathForJava(jarPath))];
  return entries.join(path.delimiter);
}

function collectJarFiles(sourceRoot: string): string[] {
  const includeRoots = resolveDependencyRoots(sourceRoot);

  const output: string[] = [];
  for (const root of includeRoots) {
    const stack = [root];
    while (stack.length > 0) {
      const currentDir = stack.pop();
      if (!currentDir) {
        continue;
      }

      const entries = readdirSync(currentDir, { withFileTypes: true });
      for (const entry of entries) {
        const fullPath = path.join(currentDir, entry.name);
        if (entry.isDirectory()) {
          stack.push(fullPath);
          continue;
        }

        if (entry.isFile() && fullPath.toLowerCase().endsWith(".jar")) {
          output.push(fullPath);
        }
      }
    }
  }

  return output.sort();
}

function normalizePathForJava(filePath: string): string {
  return filePath.replace(/\\/g, "/");
}

function resolveNativeVmArgs(sourceRoot: string): string[] {
  const nativesDir = resolveNativesDir(sourceRoot);
  if (!nativesDir) {
    return [];
  }

  const nativePath = normalizePathForJava(nativesDir);
  return [
    `-Djava.library.path=${nativePath}`,
    `-Dorg.lwjgl.librarypath=${nativePath}`,
    `-Dnet.java.games.input.librarypath=${nativePath}`
  ];
}

function resolveNativesDir(sourceRoot: string): string | null {
  const candidates: string[] = [];
  let current = sourceRoot;
  while (true) {
    candidates.push(
      path.join(current, "libraries", "natives"),
      path.join(current, "lib", "natives"),
      path.join(current, "jars", "natives"),
      path.join(current, "bin", "natives"),
      path.join(current, "natives"),
      path.join(current, "game", "bin", "natives"),
      path.join(current, "game", "natives")
    );
    const parent = path.dirname(current);
    if (parent === current) {
      break;
    }
    current = parent;
  }

  for (const dir of candidates) {
    if (!existsSync(dir) || !statSync(dir).isDirectory()) {
      continue;
    }

    const files = readdirSync(dir).map((name) => name.toLowerCase());
    const hasLwjglNative = files.some((name) =>
      ["lwjgl.dll", "lwjgl64.dll", "openal32.dll", "openal64.dll"].includes(name)
    );
    if (hasLwjglNative) {
      return dir;
    }
  }

  return null;
}

function resolveDependencyRoots(sourceRoot: string): string[] {
  const candidates: string[] = [];
  let current = sourceRoot;
  while (true) {
    candidates.push(current);
    const parent = path.dirname(current);
    if (parent === current) {
      break;
    }
    current = parent;
  }

  const roots: string[] = [];
  const seen = new Set<string>();

  for (const base of candidates) {
    if (!base || seen.has(base)) {
      continue;
    }
    seen.add(base);

    for (const folderName of ["jars", "libraries", "lib"]) {
      const dependencyRoot = path.join(base, folderName);
      if (existsSync(dependencyRoot) && statSync(dependencyRoot).isDirectory()) {
        roots.push(dependencyRoot);
      }
    }
  }

  return roots;
}

function collectJavaFiles(rootDir: string, sourceRoot: string): string[] {
  const output: string[] = [];
  const stack = [rootDir];

  while (stack.length > 0) {
    const currentDir = stack.pop();
    if (!currentDir) {
      continue;
    }

    const entries = readdirSync(currentDir, { withFileTypes: true });
    for (const entry of entries) {
      const fullPath = path.join(currentDir, entry.name);
      if (entry.isDirectory()) {
        stack.push(fullPath);
        continue;
      }

      if (entry.isFile() && fullPath.endsWith(".java")) {
        output.push(path.relative(sourceRoot, fullPath).replace(/\\/g, "/"));
      }
    }
  }

  return output.sort();
}

function firstLine(text: string): string {
  const trimmed = text.trim();
  if (!trimmed) {
    return "unknown error";
  }

  return trimmed.split(/\r?\n/)[0] ?? "unknown error";
}

function quoteArgfileValue(value: string): string {
  const escaped = value.replace(/\\/g, "\\\\").replace(/"/g, '\\"');
  return `"${escaped}"`;
}

function waitForProcessStart(
  child: ChildProcess,
  timeoutMs: number
): Promise<{ ok: true } | { ok: false; reason: string }> {
  return new Promise((resolve) => {
    let settled = false;
    const done = (result: { ok: true } | { ok: false; reason: string }) => {
      if (settled) {
        return;
      }
      settled = true;
      clearTimeout(timer);
      resolve(result);
    };

    const timer = setTimeout(() => {
      done({ ok: true });
    }, timeoutMs);

    child.once("error", (error) => {
      done({ ok: false, reason: error.message });
    });

    child.once("exit", (code, signal) => {
      if (signal) {
        done({ ok: false, reason: `signal ${signal}` });
        return;
      }

      const exitCode = typeof code === "number" ? String(code) : "unknown";
      done({ ok: false, reason: `exit code ${exitCode}` });
    });
  });
}

function createLaunchLogPath(): string {
  return path.join(app.getPath("userData"), "minecraft-launch.log");
}

function readLaunchLogSnippet(logPath: string): string {
  try {
    if (!existsSync(logPath)) {
      return `No launch log found at ${logPath}`;
    }

    const content = readFileSync(logPath, "utf8").trim();
    if (!content) {
      return `Launch log is empty (${logPath})`;
    }

    const lines = content.split(/\r?\n/).filter((line) => line.trim().length > 0);
    const interesting = lines.filter((line) =>
      /(Exception|Error|Caused by|UnsatisfiedLinkError)/i.test(line)
    );
    if (interesting.length > 0) {
      return `Log: ${interesting.slice(-3).join(" | ")}`;
    }

    const tail = lines.slice(-8).join(" | ");
    return `Log tail: ${tail}`;
  } catch (error) {
    const message = error instanceof Error ? error.message : "unknown log read error";
    return `Failed to read launch log: ${message}`;
  }
}

async function downloadMinecraftFromGithub(): Promise<{ ok: boolean; message: string; path?: string }> {
  const zipUrl = LOCKED_MINECRAFT_SOURCE_ZIP_URL;

  const installDir = path.join(app.getPath("userData"), "minecraft-source");
  const tempRoot = path.join(app.getPath("temp"), "akkez-launcher");
  const zipPath = path.join(tempRoot, "minecraft-source.zip");
  const extractDir = path.join(tempRoot, "minecraft-source-extract");

  try {
    mkdirSync(tempRoot, { recursive: true });
    if (existsSync(extractDir)) {
      rmSync(extractDir, { recursive: true, force: true });
    }
    if (existsSync(installDir)) {
      rmSync(installDir, { recursive: true, force: true });
    }

    await downloadFile(zipUrl, zipPath);
    const zip = new AdmZip(zipPath);
    zip.extractAllTo(extractDir, true);

    const projectRoot = findProjectRoot(extractDir);
    if (!projectRoot) {
      return {
        ok: false,
        message:
          "Downloaded package does not contain expected Minecraft project files (Client.launch)."
      };
    }

    cpSync(projectRoot, installDir, { recursive: true });
    store.set("minecraftExePath", installDir);
    return {
      ok: true,
      message: `Your Minecraft source downloaded to ${installDir}.`,
      path: installDir
    };
  } catch (error) {
    const message = error instanceof Error ? error.message : "Unknown download error";
    return {
      ok: false,
      message: `Failed to download your Minecraft source package: ${message}`
    };
  }
}

async function downloadFile(url: string, destinationPath: string): Promise<void> {
  const response = await fetchUrl(url);
  if (!response.ok || !response.body) {
    throw new Error(`HTTP ${response.status} while downloading ${url}`);
  }

  const output = createWriteStream(destinationPath);
  await pipeline(response.body, output);
}

function fetchUrl(url: string): Promise<{ ok: boolean; status: number; body: IncomingMessage | null }> {
  return new Promise((resolve, reject) => {
    const client = url.startsWith("https://") ? https : http;
    const request = client.get(
      url,
      {
        headers: {
          "user-agent": "Akkez-Launcher"
        }
      },
      (response) => {
        const statusCode = response.statusCode ?? 0;
        const location = response.headers.location;
        if (
          location &&
          statusCode >= 300 &&
          statusCode < 400 &&
          (location.startsWith("http://") || location.startsWith("https://"))
        ) {
          response.resume();
          fetchUrl(location).then(resolve).catch(reject);
          return;
        }

        if (statusCode < 200 || statusCode >= 300) {
          response.resume();
          resolve({ ok: false, status: statusCode, body: null });
          return;
        }

        resolve({ ok: true, status: statusCode, body: response });
      }
    );

    request.on("error", reject);
  });
}

function findProjectRoot(extractDir: string): string | null {
  const stack = [extractDir];
  while (stack.length > 0) {
    const current = stack.pop();
    if (!current) {
      continue;
    }

    const hasDirectClient = existsSync(path.join(current, "Client.launch"));
    const hasNestedClient = existsSync(path.join(current, "minecraft", "Client.launch"));
    if ((hasDirectClient || hasNestedClient) && existsSync(path.join(current, "libraries"))) {
      return current;
    }

    if (hasDirectClient && existsSync(path.join(current, "src"))) {
      return current;
    }

    const entries = readdirSync(current, { withFileTypes: true });
    for (const entry of entries) {
      if (entry.isDirectory()) {
        stack.push(path.join(current, entry.name));
      }
    }
  }

  return null;
}

app.whenReady().then(async () => {
  if (!store.get("minecraftExePath")) {
    const defaultPath = detectDefaultMinecraftPath();
    if (defaultPath) {
      store.set("minecraftExePath", defaultPath);
    }
  }

  createWindow();
  wireUpdater();
  wireIpc();
  sendUpdateEvent({ type: "idle", message: "Launcher ready." });

  try {
    await autoUpdater.checkForUpdatesAndNotify();
  } catch (error) {
    const message =
      error instanceof Error ? error.message : "Unknown auto-update startup error";
    sendUpdateEvent({ type: "error", message });
  }
});

function detectDefaultMinecraftPath(): string {
  const bases = [
    process.cwd(),
    path.resolve(process.cwd(), ".."),
    app.getAppPath(),
    path.resolve(app.getAppPath(), "..")
  ];

  for (const base of bases) {
    const sourceRoot = resolveSourceRoot(base);
    if (sourceRoot) {
      return sourceRoot;
    }
  }

  return "";
}

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") {
    app.quit();
  }
});

app.on("activate", () => {
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow();
  }
});
