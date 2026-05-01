export const IPC_CHANNELS = {
  APP_INFO: "launcher:app-info",
  SELECT_MINECRAFT_EXE: "launcher:select-minecraft-exe",
  SET_MINECRAFT_EXE: "launcher:set-minecraft-exe",
  SET_MINECRAFT_REPO_URL: "launcher:set-minecraft-repo-url",
  DOWNLOAD_MINECRAFT_FROM_GITHUB: "launcher:download-minecraft-from-github",
  LAUNCH_MINECRAFT: "launcher:launch-minecraft",
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
  minecraftRepoUrl: string;
}

export interface UpdateEventPayload {
  type: UpdateEventType;
  message?: string;
  percent?: number;
  bytesPerSecond?: number;
}
