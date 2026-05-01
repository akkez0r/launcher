# Akkez Launcher

Electron-based Windows launcher with GitHub Releases auto-updates.

## Minecraft Source Launch

- Set the Minecraft path to your source project folder that contains `Client.launch`.
- On launch, the launcher tries to run `git pull --ff-only` in that source folder (if it is a git repo).
- Then it compiles Java source with `javac` and starts the game via Java.
- Requirements: `java` and `javac` available on `PATH` (JDK installed).

## Quick Start

1. Install dependencies:
   - `npm install`
2. Build and run locally:
   - `npm run start`

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
