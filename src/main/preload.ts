import { contextBridge, ipcRenderer } from "electron";
import {
  HostStatus,
  IPC_CHANNELS,
  LauncherAppInfo,
  UpdateEventPayload
} from "../shared/ipc";

contextBridge.exposeInMainWorld("launcherApi", {
  getAppInfo: (): Promise<LauncherAppInfo> => ipcRenderer.invoke(IPC_CHANNELS.APP_INFO),
  selectMinecraftExe: (): Promise<string> =>
    ipcRenderer.invoke(IPC_CHANNELS.SELECT_MINECRAFT_EXE),
  setMinecraftExe: (value: string): Promise<void> =>
    ipcRenderer.invoke(IPC_CHANNELS.SET_MINECRAFT_EXE, value),
  downloadMinecraftFromGithub: (): Promise<{ ok: boolean; message: string; path?: string }> =>
    ipcRenderer.invoke(IPC_CHANNELS.DOWNLOAD_MINECRAFT_FROM_GITHUB),
  launchMinecraft: (): Promise<{ ok: boolean; message: string }> =>
    ipcRenderer.invoke(IPC_CHANNELS.LAUNCH_MINECRAFT),
  openMinecraftWorlds: (): Promise<{ ok: boolean; message: string }> =>
    ipcRenderer.invoke(IPC_CHANNELS.OPEN_MINECRAFT_WORLDS),
  getHostStatus: (): Promise<HostStatus> => ipcRenderer.invoke(IPC_CHANNELS.HOST_GET_STATUS),
  startHostServer: (): Promise<{ ok: boolean; message: string }> =>
    ipcRenderer.invoke(IPC_CHANNELS.HOST_START_SERVER),
  stopHostServer: (): Promise<{ ok: boolean; message: string }> =>
    ipcRenderer.invoke(IPC_CHANNELS.HOST_STOP_SERVER),
  startHostTunnel: (): Promise<{ ok: boolean; message: string }> =>
    ipcRenderer.invoke(IPC_CHANNELS.HOST_START_TUNNEL),
  stopHostTunnel: (): Promise<{ ok: boolean; message: string }> =>
    ipcRenderer.invoke(IPC_CHANNELS.HOST_STOP_TUNNEL),
  checkForUpdates: (): Promise<void> => ipcRenderer.invoke(IPC_CHANNELS.CHECK_FOR_UPDATES),
  installUpdate: (): Promise<void> => ipcRenderer.invoke(IPC_CHANNELS.INSTALL_UPDATE),
  onUpdateEvent: (handler: (payload: UpdateEventPayload) => void) => {
    const wrapped = (_event: Electron.IpcRendererEvent, payload: UpdateEventPayload) =>
      handler(payload);
    ipcRenderer.on(IPC_CHANNELS.UPDATE_EVENT, wrapped);
    return () => ipcRenderer.removeListener(IPC_CHANNELS.UPDATE_EVENT, wrapped);
  }
});
