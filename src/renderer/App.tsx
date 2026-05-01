import React, { useEffect, useMemo, useState } from "react";
import { LauncherAppInfo, UpdateEventPayload } from "../shared/ipc";

type LauncherApi = {
  getAppInfo: () => Promise<LauncherAppInfo>;
  selectMinecraftExe: () => Promise<string>;
  setMinecraftExe: (value: string) => Promise<void>;
  launchMinecraft: () => Promise<{ ok: boolean; message: string }>;
  checkForUpdates: () => Promise<void>;
  installUpdate: () => Promise<void>;
  onUpdateEvent: (handler: (payload: UpdateEventPayload) => void) => () => void;
};

declare global {
  interface Window {
    launcherApi: LauncherApi;
  }
}

const cardStyle: React.CSSProperties = {
  maxWidth: 780,
  margin: "42px auto",
  padding: "28px",
  borderRadius: "14px",
  background: "linear-gradient(160deg, #1f2237, #202843)",
  color: "#eef2ff",
  boxShadow: "0 10px 28px rgba(0,0,0,0.32)"
};

const buttonStyle: React.CSSProperties = {
  border: "none",
  padding: "10px 16px",
  borderRadius: "10px",
  cursor: "pointer",
  fontWeight: 600
};

export function App(): JSX.Element {
  const [appInfo, setAppInfo] = useState<LauncherAppInfo>({
    version: "unknown",
    updateChannel: "latest"
  });
  const [event, setEvent] = useState<UpdateEventPayload>({
    type: "idle",
    message: "Launcher initializing..."
  });
  const [busy, setBusy] = useState(false);
  const [launching, setLaunching] = useState(false);
  const [minecraftExePath, setMinecraftExePath] = useState("");
  const [launchStatus, setLaunchStatus] = useState("");

  useEffect(() => {
    window.launcherApi.getAppInfo().then(setAppInfo).catch(() => {
      setEvent({ type: "error", message: "Failed to read app info." });
    });
    window.launcherApi
      .getAppInfo()
      .then((info) => setMinecraftExePath(info.minecraftExePath ?? ""))
      .catch(() => undefined);

    const unsubscribe = window.launcherApi.onUpdateEvent((payload) => {
      setBusy(payload.type === "checking" || payload.type === "progress");
      setEvent(payload);
    });

    return () => unsubscribe();
  }, []);

  const percentLabel = useMemo(() => {
    if (typeof event.percent !== "number") {
      return null;
    }
    return `${Math.max(0, Math.min(100, event.percent)).toFixed(1)}%`;
  }, [event.percent]);

  return (
    <main style={cardStyle}>
      <h1 style={{ marginTop: 0 }}>Akkez Launcher</h1>
      <p style={{ opacity: 0.9 }}>
        Version <strong>{appInfo.version}</strong> · Channel{" "}
        <strong>{appInfo.updateChannel}</strong>
      </p>

      <section
        style={{
          marginTop: 20,
          padding: 14,
          borderRadius: 10,
          background: "rgba(255,255,255,0.06)"
        }}
      >
        <h2 style={{ marginTop: 0, fontSize: 18 }}>Updater</h2>
        <p style={{ marginBottom: 8 }}>
          {event.message ?? "Use Check for updates to query GitHub releases."}
        </p>

        {percentLabel ? (
          <div style={{ marginBottom: 12 }}>
            <div
              style={{
                width: "100%",
                height: 12,
                borderRadius: 999,
                background: "rgba(255,255,255,0.2)",
                overflow: "hidden"
              }}
            >
              <div
                style={{
                  width: percentLabel,
                  height: "100%",
                  background: "#4f8cff"
                }}
              />
            </div>
            <small>{percentLabel}</small>
          </div>
        ) : null}

        <div style={{ display: "flex", gap: 12, flexWrap: "wrap" }}>
          <button
            style={{ ...buttonStyle, background: "#4f8cff", color: "#fff" }}
            disabled={busy}
            onClick={async () => {
              setBusy(true);
              try {
                await window.launcherApi.checkForUpdates();
              } finally {
                setBusy(false);
              }
            }}
          >
            Check for updates
          </button>
          <button
            style={{ ...buttonStyle, background: "#4edca8", color: "#0d1b18" }}
            disabled={event.type !== "downloaded"}
            onClick={() => window.launcherApi.installUpdate()}
          >
            Restart and install update
          </button>
        </div>
      </section>

      <section
        style={{
          marginTop: 16,
          padding: 14,
          borderRadius: 10,
          background: "rgba(255,255,255,0.06)"
        }}
      >
        <h2 style={{ marginTop: 0, fontSize: 18 }}>Minecraft</h2>
        <p style={{ marginBottom: 8 }}>
          Use your Minecraft source folder (with `Client.launch`). Launcher will auto-update from git and compile source before launch.
        </p>
        <input
          value={minecraftExePath}
          onChange={(e) => setMinecraftExePath(e.target.value)}
          placeholder="C:\\path\\to\\minecraft source folder (or optional C:\\path\\to\\Minecraft.exe)"
          style={{
            width: "100%",
            borderRadius: 8,
            border: "1px solid rgba(255,255,255,0.25)",
            background: "rgba(0,0,0,0.25)",
            color: "#eef2ff",
            padding: "10px 12px",
            boxSizing: "border-box",
            marginBottom: 10
          }}
        />
        <div style={{ display: "flex", gap: 12, flexWrap: "wrap" }}>
          <button
            style={{ ...buttonStyle, background: "#5f6bff", color: "#fff" }}
            onClick={async () => {
              const selected = await window.launcherApi.selectMinecraftExe();
              if (selected) {
                setMinecraftExePath(selected);
                setLaunchStatus("Minecraft path selected.");
              }
            }}
          >
            Browse Source
          </button>
          <button
            style={{ ...buttonStyle, background: "#8b8fa8", color: "#fff" }}
            onClick={async () => {
              await window.launcherApi.setMinecraftExe(minecraftExePath);
              setLaunchStatus("Minecraft path saved.");
            }}
          >
            Save Path
          </button>
          <button
            style={{ ...buttonStyle, background: "#4edca8", color: "#0d1b18" }}
            disabled={launching}
            onClick={async () => {
              setLaunching(true);
              try {
                await window.launcherApi.setMinecraftExe(minecraftExePath);
                const result = await window.launcherApi.launchMinecraft();
                setLaunchStatus(result.message);
              } finally {
                setLaunching(false);
              }
            }}
          >
            Launch Minecraft
          </button>
        </div>
        {launchStatus ? <p style={{ marginTop: 10 }}>{launchStatus}</p> : null}
      </section>
    </main>
  );
}
