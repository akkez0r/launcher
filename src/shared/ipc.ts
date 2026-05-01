export const IPC_CHANNELS = {
  APP_INFO: "launcher:app-info",
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
}

export interface UpdateEventPayload {
  type: UpdateEventType;
  message?: string;
  percent?: number;
  bytesPerSecond?: number;
}
