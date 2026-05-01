import { contextBridge, ipcRenderer } from "electron";
import {
  IPC_CHANNELS,
  LauncherAppInfo,
  UpdateEventPayload
} from "../shared/ipc";

contextBridge.exposeInMainWorld("launcherApi", {
  getAppInfo: (): Promise<LauncherAppInfo> => ipcRenderer.invoke(IPC_CHANNELS.APP_INFO),
  checkForUpdates: (): Promise<void> => ipcRenderer.invoke(IPC_CHANNELS.CHECK_FOR_UPDATES),
  installUpdate: (): Promise<void> => ipcRenderer.invoke(IPC_CHANNELS.INSTALL_UPDATE),
  onUpdateEvent: (handler: (payload: UpdateEventPayload) => void) => {
    const wrapped = (_event: Electron.IpcRendererEvent, payload: UpdateEventPayload) =>
      handler(payload);
    ipcRenderer.on(IPC_CHANNELS.UPDATE_EVENT, wrapped);
    return () => ipcRenderer.removeListener(IPC_CHANNELS.UPDATE_EVENT, wrapped);
  }
});
