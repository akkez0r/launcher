# Akkez Launcher

Electron-based Windows launcher with GitHub Releases auto-updates.

## Minecraft Source Launch

- Set the Minecraft path to your source project folder that contains `Client.launch`.
- Or use **Download Minecraft** in the launcher to fetch your locked package from:
  - `https://github.com/akkez0r/launcher/releases/latest/download/minecraft-source.zip`
- If launch path is missing, launcher auto-downloads this package before launch.
- On launch, the launcher tries to run `git pull --ff-only` in that source folder (if it is a git repo).
- Then it compiles Java source with `javac` and starts the game via Java.
- Requirements: `java` and `javac` available on `PATH` (JDK installed).

## Quick Start

1. Install dependencies:
   - `npm install`
2. Build and run locally:
   - `npm run start`

## CMC Auth Setup (Backend + Launcher)

The packaged launcher defaults to **`https://auth.craviorsmp.com`** for register/login and for **Host tab** `cmc-api-base-url` unless you set **`CMC_API_BASE_URL`**. Players who install the EXE normally need **no PowerShell setup**.

> Note: unless specified, commands in this section are run from the launcher repo root.

1. Install launcher and backend dependencies:
   - `npm install`
   - `npm --prefix cmc-auth install`
2. Create backend env file:
   - PowerShell: `Copy-Item cmc-auth/.env.example cmc-auth/.env`
3. Apply auth schema to Postgres (uses `CMC_DB_URL` from `cmc-auth/.env`):
   - `psql "$env:CMC_DB_URL" -f cmc-auth/src/schema.sql`
4. Start the auth backend:
   - `npm --prefix cmc-auth run dev`
5. In a second terminal, point the launcher at your **local** auth API (defaults to production HTTPS if you skip this):
   - `$env:CMC_API_BASE_URL="http://127.0.0.1:4000"`
   - `npm run start`

## CMC Login Quick Test Checklist

- [ ] Open launcher and register a new account from the CMC auth UI.
- [ ] Log out, then log in with the same account and confirm login succeeds.
- [ ] Close and reopen the launcher; verify the session is restored automatically.
- [ ] Verify backend identity injection is applied on launch: after clicking **Launch**, open Task Manager -> **Details** -> enable the **Command line** column, then confirm the game process command line contains `--username` and `--uuid` values matching `/auth/me` (CMC profile).
- [ ] Start Minecraft from the launcher and confirm launch still succeeds.

## Manual Run Checklist (Backend + Launcher)

- [ ] Backend `npm --prefix cmc-auth run dev` is running without startup errors.
- [ ] Launcher starts with `CMC_API_BASE_URL` pointing at the backend URL.
- [ ] `/health` responds with `{ "ok": true }` from the auth service.
- [ ] Register/login/refresh flow works in launcher without 401/500 errors.
- [ ] Game launch works after authenticated session is established.

## Build EXE

- `npm run dist:win`

Output is produced in `release/`.

## Configure GitHub Auto-Update

Edit `package.json` build publish config:

- `build.publish[0].owner`
- `build.publish[0].repo`

Then create a GitHub release using CI and include updater artifacts (`latest.yml`, installer).

## Release Commands

- Build + publish from local (not recommended): `npm run release`
- CI release is configured in `.github/workflows/release.yml`.

## Update Verification

Run:

- `npm run test:update-checklist`

This prints the manual validation checklist for update testing.
