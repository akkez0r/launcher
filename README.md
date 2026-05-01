# Akkez Launcher

Electron-based Windows launcher with GitHub Releases auto-updates.

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
