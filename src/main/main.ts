import { app, BrowserWindow, ipcMain } from "electron";
import { autoUpdater } from "electron-updater";
import Store from "electron-store";
import path from "node:path";
import {
  IPC_CHANNELS,
  LauncherAppInfo,
  UpdateEventPayload
} from "../shared/ipc";

type LauncherStore = {
  updateChannel: string;
  lastSeenVersion: string;
};

const store = new Store<LauncherStore>({
  defaults: {
    updateChannel: "latest",
    lastSeenVersion: app.getVersion()
  }
});

let mainWindow: BrowserWindow | null = null;

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
      updateChannel: store.get("updateChannel")
    };
  });

  ipcMain.handle(IPC_CHANNELS.CHECK_FOR_UPDATES, async () => {
    await autoUpdater.checkForUpdates();
  });

  ipcMain.handle(IPC_CHANNELS.INSTALL_UPDATE, async () => {
    autoUpdater.quitAndInstall();
  });
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
