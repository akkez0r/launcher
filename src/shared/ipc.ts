export const IPC_CHANNELS = {
  APP_INFO: "launcher:app-info",
  AUTH_REGISTER: "launcher:auth-register",
  AUTH_LOGIN: "launcher:auth-login",
  AUTH_LOGOUT: "launcher:auth-logout",
  AUTH_ME: "launcher:auth-me",
  SELECT_MINECRAFT_EXE: "launcher:select-minecraft-exe",
  SET_MINECRAFT_EXE: "launcher:set-minecraft-exe",
  DOWNLOAD_MINECRAFT_FROM_GITHUB: "launcher:download-minecraft-from-github",
  LAUNCH_MINECRAFT: "launcher:launch-minecraft",
  OPEN_MINECRAFT_WORLDS: "launcher:open-minecraft-worlds",
  HOST_GET_STATUS: "launcher:host-get-status",
  HOST_START_SERVER: "launcher:host-start-server",
  HOST_STOP_SERVER: "launcher:host-stop-server",
  HOST_START_TUNNEL: "launcher:host-start-tunnel",
  HOST_STOP_TUNNEL: "launcher:host-stop-tunnel",
  CHECK_FOR_UPDATES: "launcher:check-for-updates",
  INSTALL_UPDATE: "launcher:install-update",
  UPDATE_EVENT: "launcher:update-event"
} as const;

export type UpdateEventType =
  | "idle"
  | "checking"
  | "available"
  | "not-available"
  | "progress"
  | "downloaded"
  | "error";

export interface LauncherAppInfo {
  version: string;
  updateChannel: string;
  minecraftExePath: string;
  isLoggedIn?: boolean;
  cmcUsername?: string;
  cmcUuid?: string;
}

export interface UpdateEventPayload {
  type: UpdateEventType;
  message?: string;
  percent?: number;
  bytesPerSecond?: number;
}

export interface HostStatus {
  serverRunning: boolean;
  tunnelRunning: boolean;
  serverRoot: string;
  publicAddress: string;
  serverLogTail: string[];
  tunnelLogTail: string[];
}
