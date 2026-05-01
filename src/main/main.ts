import { app, BrowserWindow, dialog, ipcMain } from "electron";
import { autoUpdater } from "electron-updater";
import Store from "electron-store";
import path from "node:path";
import { existsSync, statSync } from "node:fs";
import { spawn, spawnSync } from "node:child_process";
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

type LaunchConfig = {
  command: string;
  args: string[];
  cwd?: string;
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
      title: "Select Minecraft executable or folder",
      properties: ["openFile", "openDirectory"],
      filters: [
        { name: "Launch files", extensions: ["exe", "jar"] },
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

  ipcMain.handle(IPC_CHANNELS.LAUNCH_MINECRAFT, async () => {
    const targetPath = store.get("minecraftExePath").trim();
    if (!targetPath) {
      return { ok: false, message: "Set Minecraft path first." };
    }

    if (!existsSync(targetPath)) {
      return { ok: false, message: "Minecraft path does not exist." };
    }

    try {
      if (isJavaLaunchTarget(targetPath) && !hasJavaRuntime()) {
        return {
          ok: false,
          message: "Java not found on PATH. Install Java or select a direct .exe."
        };
      }

      const launchConfig = resolveLaunchConfig(targetPath);
      if (!launchConfig) {
        return {
          ok: false,
          message:
            "No launch target found. Pick an .exe/.jar or a folder with RetroMCP-Java-GUI.jar."
        };
      }

      const child = spawn(launchConfig.command, launchConfig.args, {
        cwd: launchConfig.cwd,
        detached: true,
        stdio: "ignore"
      });
      child.unref();
      return { ok: true, message: "Minecraft launched." };
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

    if (extension === ".jar") {
      if (!javaCommand) {
        return null;
      }
      return {
        command: javaCommand,
        args: ["-jar", targetPath],
        cwd: path.dirname(targetPath)
      };
    }

    return null;
  }

  if (!stats.isDirectory()) {
    return null;
  }

  const retroJar = path.join(targetPath, "RetroMCP-Java-GUI.jar");
  if (existsSync(retroJar)) {
    if (!javaCommand) {
      return null;
    }
    return {
      command: javaCommand,
      args: ["-jar", retroJar],
      cwd: targetPath
    };
  }

  const minecraftExe = path.join(targetPath, "MinecraftLauncher.exe");
  if (existsSync(minecraftExe)) {
    return { command: minecraftExe, args: [], cwd: targetPath };
  }

  return null;
}

function isJavaLaunchTarget(targetPath: string): boolean {
  const stats = statSync(targetPath);
  if (stats.isFile()) {
    return path.extname(targetPath).toLowerCase() === ".jar";
  }

  if (!stats.isDirectory()) {
    return false;
  }

  return existsSync(path.join(targetPath, "RetroMCP-Java-GUI.jar"));
}

function hasJavaRuntime(): boolean {
  return resolveJavaCommand() !== null;
}

function resolveJavaCommand(): string | null {
  const javawCheck = spawnSync("where", ["javaw"], {
    windowsHide: true,
    stdio: "ignore"
  });
  if (javawCheck.status === 0) {
    return "javaw";
  }

  const javaCheck = spawnSync("where", ["java"], {
    windowsHide: true,
    stdio: "ignore"
  });
  if (javaCheck.status === 0) {
    return "java";
  }

  return null;
}

app.whenReady().then(async () => {
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
