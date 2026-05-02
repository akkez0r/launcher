import React, { useCallback, useEffect, useMemo, useState } from "react";
import { HostStatus, LauncherAppInfo, UpdateEventPayload } from "../shared/ipc";

type LauncherApi = {
  getAppInfo: () => Promise<LauncherAppInfo>;
  register: (payload: {
    username: string;
    email: string;
    password: string;
  }) => Promise<{ user: { username: string; cmcUuid: string } }>;
  login: (payload: {
    emailOrUsername: string;
    password: string;
  }) => Promise<{ user: { username: string; cmcUuid: string } }>;
  logout: () => Promise<{ ok: boolean }>;
  me: () => Promise<{ user: { username: string; cmcUuid: string } }>;
  selectMinecraftExe: () => Promise<string>;
  setMinecraftExe: (value: string) => Promise<void>;
  downloadMinecraftFromGithub: () => Promise<{ ok: boolean; message: string; path?: string }>;
  launchMinecraft: () => Promise<{ ok: boolean; message: string }>;
  openMinecraftWorlds: () => Promise<{ ok: boolean; message: string }>;
  getHostStatus: () => Promise<HostStatus>;
  startHostServer: () => Promise<{ ok: boolean; message: string }>;
  stopHostServer: () => Promise<{ ok: boolean; message: string }>;
  startHostTunnel: () => Promise<{ ok: boolean; message: string }>;
  stopHostTunnel: () => Promise<{ ok: boolean; message: string }>;
  checkForUpdates: () => Promise<void>;
  installUpdate: () => Promise<void>;
  onUpdateEvent: (handler: (payload: UpdateEventPayload) => void) => () => void;
};

declare global {
  interface Window {
    launcherApi: LauncherApi;
  }
}

const wrapperStyle: React.CSSProperties = {
  minHeight: "100vh",
  padding: "32px 22px",
  boxSizing: "border-box",
  background:
    "radial-gradient(circle at 10% 10%, rgba(79,140,255,0.26), rgba(0,0,0,0) 34%), radial-gradient(circle at 90% 20%, rgba(170,98,255,0.2), rgba(0,0,0,0) 30%), #111425",
  color: "#eef2ff"
};

const cardStyle: React.CSSProperties = {
  maxWidth: 980,
  margin: "0 auto",
  padding: "28px",
  borderRadius: "20px",
  background: "linear-gradient(160deg, rgba(31,34,55,0.92), rgba(23,29,50,0.94))",
  color: "#eef2ff",
  boxShadow: "0 16px 48px rgba(0,0,0,0.4)",
  border: "1px solid rgba(255,255,255,0.08)",
  backdropFilter: "blur(4px)"
};

const gridStyle: React.CSSProperties = {
  display: "grid",
  gridTemplateColumns: "repeat(auto-fit, minmax(300px, 1fr))",
  gap: 16
};

const buttonStyle: React.CSSProperties = {
  border: "none",
  padding: "10px 16px",
  borderRadius: "10px",
  cursor: "pointer",
  fontWeight: 600,
  transition: "transform 140ms ease, opacity 140ms ease, box-shadow 180ms ease"
};

const inputStyle: React.CSSProperties = {
  width: "100%",
  borderRadius: 10,
  border: "1px solid rgba(255,255,255,0.25)",
  background: "rgba(0,0,0,0.25)",
  color: "#eef2ff",
  padding: "10px 12px",
  boxSizing: "border-box",
  marginBottom: 10,
  outline: "none"
};

const changelogEntries = [
  {
    version: "1.5.0",
    date: "2026-05-02",
    notes: [
      "CMC auth defaults to production (https://auth.craviorsmp.com) — installers do not need PowerShell/env setup.",
      "Host tab writes cmc-api-base-url + cmc-require-account and keeps online-mode offline automatically.",
      "Launcher passes your CMC access token into the client so multiplayer servers can require a logged-in Akkez/CMC session.",
      "Use the latest minecraft-source.zip from releases with this build for enforced CMC-only servers."
    ]
  },
  {
    version: "1.4.0",
    date: "2026-05-02",
    notes: [
      "CMC login tab, encrypted session storage, and game launch injection for username/uuid.",
      "Host tab: dedicated server + Playit tunnel helpers."
    ]
  },
  {
    version: "1.2.0",
    date: "2026-05-01",
    notes: [
      "Refreshed launcher UI with smoother animations and improved layout.",
      "Added Open Worlds Folder action so players can access saves quickly.",
      "Supports protected precompiled Minecraft packages (launch without src)."
    ]
  },
  {
    version: "1.1.1",
    date: "2026-05-01",
    notes: [
      "Locked Minecraft download source to your official release asset.",
      "Added minecraft-source.zip packaging in release workflow.",
      "Removed arbitrary source URL entry to prevent wrong project downloads."
    ]
  },
  {
    version: "1.1.0",
    date: "2026-05-01",
    notes: [
      "Major update: launcher can bootstrap Minecraft source from GitHub.",
      "Added one-click Download Minecraft flow in launcher UI.",
      "Launch auto-downloads package when local source is missing."
    ]
  },
  {
    version: "1.0.9",
    date: "2026-05-01",
    notes: [
      "Fixed RetroMCP native path resolution for libraries/natives.",
      "Overrode stale native VM args to ensure lwjgl64 loads correctly."
    ]
  }
];

export function App(): JSX.Element {
  const [activeTab, setActiveTab] = useState<"play" | "host" | "account">("play");
  const [appInfo, setAppInfo] = useState<LauncherAppInfo>({
    version: "unknown",
    updateChannel: "latest",
    minecraftExePath: "",
    isLoggedIn: false,
    cmcUsername: "",
    cmcUuid: ""
  });
  const [event, setEvent] = useState<UpdateEventPayload>({
    type: "idle",
    message: "Launcher initializing..."
  });
  const [busy, setBusy] = useState(false);
  const [launching, setLaunching] = useState(false);
  const [downloadingSource, setDownloadingSource] = useState(false);
  const [minecraftExePath, setMinecraftExePath] = useState("");
  const [launchStatus, setLaunchStatus] = useState("");
  const [authBusy, setAuthBusy] = useState(false);
  const [authStatus, setAuthStatus] = useState("");
  const [registerUsername, setRegisterUsername] = useState("");
  const [registerEmail, setRegisterEmail] = useState("");
  const [registerPassword, setRegisterPassword] = useState("");
  const [loginEmailOrUsername, setLoginEmailOrUsername] = useState("");
  const [loginPassword, setLoginPassword] = useState("");
  const [hostBusy, setHostBusy] = useState(false);
  const [hostMessage, setHostMessage] = useState("");
  const [hostStatus, setHostStatus] = useState<HostStatus>({
    serverRunning: false,
    tunnelRunning: false,
    serverRoot: "",
    publicAddress: "",
    serverLogTail: [],
    tunnelLogTail: []
  });

  const refreshAppInfo = useCallback(async () => {
    const info = await window.launcherApi.getAppInfo();
    let nextInfo = info;
    if (info.isLoggedIn) {
      try {
        const me = await window.launcherApi.me();
        nextInfo = {
          ...info,
          cmcUsername: me.user.username ?? info.cmcUsername,
          cmcUuid: me.user.cmcUuid ?? info.cmcUuid
        };
      } catch {
        try {
          // Session might have been cleared in main; re-read app info immediately.
          nextInfo = await window.launcherApi.getAppInfo();
        } catch {
          nextInfo = {
            ...info,
            isLoggedIn: false,
            cmcUsername: "",
            cmcUuid: ""
          };
        }
      }
    }
    if (!nextInfo.isLoggedIn) {
      nextInfo = {
        ...nextInfo,
        cmcUsername: "",
        cmcUuid: ""
      };
    }
    setAppInfo(nextInfo);
    setMinecraftExePath(nextInfo.minecraftExePath ?? "");
    return nextInfo;
  }, []);

  useEffect(() => {
    refreshAppInfo().catch(() => {
      setEvent({ type: "error", message: "Failed to read app info." });
    });

    const unsubscribe = window.launcherApi.onUpdateEvent((payload) => {
      setBusy(payload.type === "checking" || payload.type === "progress");
      setEvent(payload);
    });

    const refreshHost = () => {
      window.launcherApi
        .getHostStatus()
        .then(setHostStatus)
        .catch(() => undefined);
    };
    refreshHost();
    const timer = window.setInterval(refreshHost, 2000);

    return () => {
      window.clearInterval(timer);
      unsubscribe();
    };
  }, [refreshAppInfo]);

  const percentLabel = useMemo(() => {
    if (typeof event.percent !== "number") {
      return null;
    }
    return `${Math.max(0, Math.min(100, event.percent)).toFixed(1)}%`;
  }, [event.percent]);

  const getErrorMessage = (error: unknown): string =>
    error instanceof Error ? error.message : "Unknown authentication error.";

  return (
    <main style={wrapperStyle}>
      <style>{`
        .float-in { animation: floatIn 450ms ease; }
        .pulse-dot { width: 8px; height: 8px; border-radius: 999px; background: #4edca8; box-shadow: 0 0 0 0 rgba(78,220,168,0.6); animation: pulse 1.7s infinite; }
        .a-btn:hover { transform: translateY(-1px); box-shadow: 0 8px 18px rgba(0,0,0,0.22); }
        .a-btn:active { transform: translateY(0); }
        @keyframes pulse {
          0% { box-shadow: 0 0 0 0 rgba(78,220,168,0.6); }
          100% { box-shadow: 0 0 0 12px rgba(78,220,168,0); }
        }
        @keyframes floatIn {
          0% { opacity: 0; transform: translateY(8px); }
          100% { opacity: 1; transform: translateY(0); }
        }
      `}</style>
      <section style={cardStyle} className="float-in">
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 12 }}>
        <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
        <img
          src="./assets/logo.svg"
          alt="Akkez Launcher logo"
          style={{ width: 46, height: 46, borderRadius: 10 }}
        />
        <div>
          <h1 style={{ margin: 0 }}>Akkez Launcher</h1>
          <p style={{ margin: "4px 0 0", opacity: 0.8 }}>Protected build delivery + one-click launch</p>
        </div>
        </div>
        <div style={{ display: "flex", alignItems: "center", gap: 8, opacity: 0.9 }}>
          <span className="pulse-dot" />
          <small>Online</small>
        </div>
      </div>
      <p style={{ opacity: 0.9 }}>
        Version <strong>{appInfo.version}</strong> · Channel{" "}
        <strong>{appInfo.updateChannel}</strong>
      </p>

      <div style={{ display: "flex", gap: 8, marginBottom: 8 }}>
        <button
          className="a-btn"
          style={{
            ...buttonStyle,
            background: activeTab === "play" ? "#4f8cff" : "#313a57",
            color: "#fff"
          }}
          onClick={() => setActiveTab("play")}
        >
          Play
        </button>
        <button
          className="a-btn"
          style={{
            ...buttonStyle,
            background: activeTab === "host" ? "#4f8cff" : "#313a57",
            color: "#fff"
          }}
          onClick={() => setActiveTab("host")}
        >
          Host
        </button>
        <button
          className="a-btn"
          style={{
            ...buttonStyle,
            background: activeTab === "account" ? "#4f8cff" : "#313a57",
            color: "#fff"
          }}
          onClick={() => setActiveTab("account")}
        >
          Account
        </button>
      </div>

      <div style={gridStyle}>
      {activeTab === "play" ? (
      <>
      <section
        style={{
          marginTop: 8,
          padding: 14,
          borderRadius: 12,
          background: "rgba(255,255,255,0.06)",
          border: "1px solid rgba(255,255,255,0.08)"
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
                background: "rgba(255,255,255,0.15)",
                overflow: "hidden"
              }}
            >
              <div
                style={{
                  width: percentLabel,
                  height: "100%",
                  background: "linear-gradient(90deg, #4f8cff, #7f6cff)"
                }}
              />
            </div>
            <small>{percentLabel}</small>
          </div>
        ) : null}

        <div style={{ display: "flex", gap: 12, flexWrap: "wrap" }}>
          <button
            className="a-btn"
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
            className="a-btn"
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
          marginTop: 8,
          padding: 14,
          borderRadius: 12,
          background: "rgba(255,255,255,0.06)",
          border: "1px solid rgba(255,255,255,0.08)"
        }}
      >
        <h2 style={{ marginTop: 0, fontSize: 18 }}>Minecraft</h2>
        <p style={{ marginBottom: 8 }}>
          Protected package mode: players download your official build, launch it, and can still access their worlds/saves.
        </p>
        <input
          value={minecraftExePath}
          onChange={(e) => setMinecraftExePath(e.target.value)}
          placeholder="C:\\path\\to\\minecraft source folder"
          style={inputStyle}
        />
        <div style={{ display: "flex", gap: 12, flexWrap: "wrap" }}>
          <button
            className="a-btn"
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
            className="a-btn"
            style={{ ...buttonStyle, background: "#8b8fa8", color: "#fff" }}
            onClick={async () => {
              await window.launcherApi.setMinecraftExe(minecraftExePath);
              setLaunchStatus("Minecraft path saved.");
            }}
          >
            Save Path
          </button>
          <button
            className="a-btn"
            style={{ ...buttonStyle, background: "#c9a24d", color: "#1b1300" }}
            disabled={launching || downloadingSource}
            onClick={async () => {
              setDownloadingSource(true);
              try {
                const result = await window.launcherApi.downloadMinecraftFromGithub();
                if (result.path) {
                  setMinecraftExePath(result.path);
                }
                setLaunchStatus(result.message);
              } finally {
                setDownloadingSource(false);
              }
            }}
          >
            {downloadingSource ? "Downloading..." : "Download Minecraft"}
          </button>
          <button
            className="a-btn"
            style={{ ...buttonStyle, background: "#4edca8", color: "#0d1b18" }}
            disabled={launching || downloadingSource}
            onClick={async () => {
              if (!appInfo.isLoggedIn) {
                setLaunchStatus("Launch blocked: please log in from the Account tab first.");
                return;
              }
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
          <button
            className="a-btn"
            style={{ ...buttonStyle, background: "#3c4667", color: "#eef2ff" }}
            onClick={async () => {
              const result = await window.launcherApi.openMinecraftWorlds();
              setLaunchStatus(result.message);
            }}
          >
            Open Worlds Folder
          </button>
        </div>
        {launchStatus ? <p style={{ marginTop: 10, opacity: 0.92 }}>{launchStatus}</p> : null}
      </section>
      </>
      ) : activeTab === "host" ? (
      <section
        style={{
          marginTop: 8,
          padding: 14,
          borderRadius: 12,
          background: "rgba(255,255,255,0.06)",
          border: "1px solid rgba(255,255,255,0.08)"
        }}
      >
        <h2 style={{ marginTop: 0, fontSize: 18 }}>Host Server</h2>
        <p style={{ marginBottom: 8, opacity: 0.9 }}>
          Runs local `minecraft_server` source and exposes multiplayer using Playit.
        </p>
        <p style={{ margin: "0 0 8px", opacity: 0.86 }}>
          Server root: <strong>{hostStatus.serverRoot || "not detected"}</strong>
        </p>
        <p style={{ margin: "0 0 8px", opacity: 0.86 }}>
          Tunnel: <strong>{hostStatus.publicAddress || "not connected yet"}</strong>
        </p>
        <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
          <button
            className="a-btn"
            style={{ ...buttonStyle, background: "#4edca8", color: "#0d1b18" }}
            disabled={hostBusy || hostStatus.serverRunning}
            onClick={async () => {
              setHostBusy(true);
              try {
                const result = await window.launcherApi.startHostServer();
                setHostMessage(result.message);
              } finally {
                setHostBusy(false);
              }
            }}
          >
            Start Server
          </button>
          <button
            className="a-btn"
            style={{ ...buttonStyle, background: "#8b8fa8", color: "#fff" }}
            disabled={hostBusy || !hostStatus.serverRunning}
            onClick={async () => {
              setHostBusy(true);
              try {
                const result = await window.launcherApi.stopHostServer();
                setHostMessage(result.message);
              } finally {
                setHostBusy(false);
              }
            }}
          >
            Stop Server
          </button>
          <button
            className="a-btn"
            style={{ ...buttonStyle, background: "#c9a24d", color: "#1b1300" }}
            disabled={hostBusy || hostStatus.tunnelRunning}
            onClick={async () => {
              setHostBusy(true);
              try {
                const result = await window.launcherApi.startHostTunnel();
                setHostMessage(result.message);
              } finally {
                setHostBusy(false);
              }
            }}
          >
            Start Playit Tunnel
          </button>
          <button
            className="a-btn"
            style={{ ...buttonStyle, background: "#3c4667", color: "#eef2ff" }}
            disabled={hostBusy || !hostStatus.tunnelRunning}
            onClick={async () => {
              setHostBusy(true);
              try {
                const result = await window.launcherApi.stopHostTunnel();
                setHostMessage(result.message);
              } finally {
                setHostBusy(false);
              }
            }}
          >
            Stop Tunnel
          </button>
        </div>
        {hostMessage ? <p style={{ marginTop: 10, opacity: 0.92 }}>{hostMessage}</p> : null}
        <div style={{ marginTop: 10, display: "grid", gap: 10 }}>
          <div
            style={{
              padding: 10,
              borderRadius: 10,
              background: "rgba(0,0,0,0.24)",
              border: "1px solid rgba(255,255,255,0.08)",
              maxHeight: 120,
              overflowY: "auto",
              fontFamily: "Consolas, monospace",
              fontSize: 12
            }}
          >
            {(hostStatus.serverLogTail.length > 0 ? hostStatus.serverLogTail : ["[host] No server logs yet."]).map(
              (line, idx) => (
                <div key={`${line}-${idx}`}>{line}</div>
              )
            )}
          </div>
          <div
            style={{
              padding: 10,
              borderRadius: 10,
              background: "rgba(0,0,0,0.24)",
              border: "1px solid rgba(255,255,255,0.08)",
              maxHeight: 120,
              overflowY: "auto",
              fontFamily: "Consolas, monospace",
              fontSize: 12
            }}
          >
            {(hostStatus.tunnelLogTail.length > 0
              ? hostStatus.tunnelLogTail
              : ["[playit] No tunnel logs yet."]).map((line, idx) => (
              <div key={`${line}-${idx}`}>{line}</div>
            ))}
          </div>
        </div>
      </section>
      ) : (
      <section
        style={{
          marginTop: 8,
          padding: 14,
          borderRadius: 12,
          background: "rgba(255,255,255,0.06)",
          border: "1px solid rgba(255,255,255,0.08)"
        }}
      >
        <h2 style={{ marginTop: 0, fontSize: 18 }}>CMC Account</h2>
        {!appInfo.isLoggedIn ? (
          <>
            <p style={{ marginBottom: 12, opacity: 0.9 }}>
              Log in or create a CMC account to enable Minecraft launch.
            </p>
            <div style={{ display: "grid", gap: 12, gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))" }}>
              <div
                style={{
                  padding: 12,
                  borderRadius: 10,
                  background: "rgba(0,0,0,0.22)",
                  border: "1px solid rgba(255,255,255,0.08)"
                }}
              >
                <h3 style={{ marginTop: 0, marginBottom: 10, fontSize: 15 }}>Register</h3>
                <input
                  value={registerUsername}
                  onChange={(e) => setRegisterUsername(e.target.value)}
                  placeholder="Username"
                  style={inputStyle}
                />
                <input
                  value={registerEmail}
                  onChange={(e) => setRegisterEmail(e.target.value)}
                  placeholder="Email"
                  style={inputStyle}
                />
                <input
                  type="password"
                  value={registerPassword}
                  onChange={(e) => setRegisterPassword(e.target.value)}
                  placeholder="Password"
                  style={inputStyle}
                />
                <button
                  className="a-btn"
                  style={{ ...buttonStyle, background: "#5f6bff", color: "#fff", width: "100%" }}
                  disabled={
                    authBusy ||
                    !registerUsername.trim() ||
                    !registerEmail.trim() ||
                    !registerPassword
                  }
                  onClick={async () => {
                    setAuthBusy(true);
                    setAuthStatus("");
                    try {
                      const result = await window.launcherApi.register({
                        username: registerUsername.trim(),
                        email: registerEmail.trim(),
                        password: registerPassword
                      });
                      await refreshAppInfo();
                      setRegisterPassword("");
                      setLoginPassword("");
                      setAuthStatus(`Registered and logged in as ${result.user.username}.`);
                      setLaunchStatus("Account connected. You can now launch Minecraft.");
                    } catch (error) {
                      setAuthStatus(getErrorMessage(error));
                    } finally {
                      setAuthBusy(false);
                    }
                  }}
                >
                  {authBusy ? "Please wait..." : "Create account"}
                </button>
              </div>

              <div
                style={{
                  padding: 12,
                  borderRadius: 10,
                  background: "rgba(0,0,0,0.22)",
                  border: "1px solid rgba(255,255,255,0.08)"
                }}
              >
                <h3 style={{ marginTop: 0, marginBottom: 10, fontSize: 15 }}>Login</h3>
                <input
                  value={loginEmailOrUsername}
                  onChange={(e) => setLoginEmailOrUsername(e.target.value)}
                  placeholder="Email or username"
                  style={inputStyle}
                />
                <input
                  type="password"
                  value={loginPassword}
                  onChange={(e) => setLoginPassword(e.target.value)}
                  placeholder="Password"
                  style={inputStyle}
                />
                <button
                  className="a-btn"
                  style={{ ...buttonStyle, background: "#4f8cff", color: "#fff", width: "100%" }}
                  disabled={authBusy || !loginEmailOrUsername.trim() || !loginPassword}
                  onClick={async () => {
                    setAuthBusy(true);
                    setAuthStatus("");
                    try {
                      const result = await window.launcherApi.login({
                        emailOrUsername: loginEmailOrUsername.trim(),
                        password: loginPassword
                      });
                      await refreshAppInfo();
                      setLoginPassword("");
                      setRegisterPassword("");
                      setAuthStatus(`Logged in as ${result.user.username}.`);
                      setLaunchStatus("Account connected. You can now launch Minecraft.");
                    } catch (error) {
                      setAuthStatus(getErrorMessage(error));
                    } finally {
                      setAuthBusy(false);
                    }
                  }}
                >
                  {authBusy ? "Please wait..." : "Login"}
                </button>
              </div>
            </div>
          </>
        ) : (
          <div
            style={{
              padding: 12,
              borderRadius: 10,
              background: "rgba(0,0,0,0.22)",
              border: "1px solid rgba(255,255,255,0.08)"
            }}
          >
            <p style={{ margin: "0 0 8px", opacity: 0.9 }}>
              Signed in as <strong>{appInfo.cmcUsername || "unknown user"}</strong>
            </p>
            <p style={{ margin: "0 0 12px", opacity: 0.9 }}>
              CMC UUID: <strong>{appInfo.cmcUuid || "unknown"}</strong>
            </p>
            <button
              className="a-btn"
              style={{ ...buttonStyle, background: "#8b8fa8", color: "#fff" }}
              disabled={authBusy}
              onClick={async () => {
                setAuthBusy(true);
                setAuthStatus("");
                try {
                  await window.launcherApi.logout();
                  await refreshAppInfo();
                  setRegisterUsername("");
                  setRegisterEmail("");
                  setLoginEmailOrUsername("");
                  setRegisterPassword("");
                  setLoginPassword("");
                  setAuthStatus("Logged out successfully.");
                  setLaunchStatus("Launch blocked until you log in again.");
                } catch (error) {
                  setAuthStatus(getErrorMessage(error));
                } finally {
                  setAuthBusy(false);
                }
              }}
            >
              {authBusy ? "Please wait..." : "Logout"}
            </button>
          </div>
        )}
        {authStatus ? <p style={{ marginTop: 10, opacity: 0.92 }}>{authStatus}</p> : null}
      </section>
      )}
      <section
        style={{
          marginTop: 16,
          padding: 14,
          borderRadius: 12,
          background: "rgba(255,255,255,0.06)",
          border: "1px solid rgba(255,255,255,0.08)"
        }}
      >
        <h2 style={{ marginTop: 0, fontSize: 18 }}>Changelog</h2>
        <div style={{ maxHeight: 220, overflowY: "auto", paddingRight: 4 }}>
          {changelogEntries.map((entry) => (
            <div
              key={entry.version}
              style={{
                marginBottom: 12,
                paddingBottom: 10,
                borderBottom: "1px solid rgba(255,255,255,0.08)"
              }}
            >
              <p style={{ margin: "0 0 6px", fontWeight: 700 }}>
                v{entry.version} <span style={{ opacity: 0.7, fontWeight: 500 }}>({entry.date})</span>
              </p>
              {entry.notes.map((note) => (
                <p key={note} style={{ margin: "3px 0", opacity: 0.9 }}>
                  - {note}
                </p>
              ))}
            </div>
          ))}
        </div>
      </section>
      </div>
      </section>
    </main>
  );
}
