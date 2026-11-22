# Grimfall Companion

A two-mode Android launcher and companion project for GrimfallWoW, aimed at the AYN Thor dual-screen handheld.

**Status: 0.3 prototype.** The app starts with:
- **Browser Client:** launches Grimfall in a fullscreen WebView with experimental controller/key overlay.
- **Local Client:** includes a folder-picker based ConsolePortLK installer/repair flow (network download, backup and folder installation). Local client runtime remains unimplemented.

## Local Client dependency (mandatory)

Local Client mode currently downloads the pinned ConsolePortLK **1.5.0-rc2** archive from upstream (https://github.com/leoaviana/ConsolePortLK) when the user presses Install/Repair, and installs its ConsolePort-prefixed addon folders into the selected client. The addon source is not embedded in the APK. The installer creates a timestamped backup directory under AddOns and replaces only ConsolePort-prefixed addon folders. Other addon folders are preserved. The install flow requires network access and the user to grant folder access via Android's system picker. This is an early installer and has not been tested on an actual Thor or WoW client.

Setup will validate the user's own client files, back up existing AddOns, install only ConsolePortLK-owned folders, preserve unrelated addons, and avoid destructive WTF-folder resets.

## Feature goal

Build Grimfall-specific feature parity with the breadth of DS Forever: controller configuration, multi-page action bars, radial ability menus, character-aware profiles, maps/quests, party and pet controls, inventory/character utilities, second-screen widgets, streaming and local-client support. We will implement and validate our own Grimfall integration rather than copy DS Forever's code.

## Browser mode prototype
- Fullscreen Android WebView with Grimfall homepage by default.
- Custom webclient URL dialog and saved URL.
- A/B/X/Y → 1/2/3/4 and L1/R1/L2/R2 → Q/E/R/F experimental mapping.
- On-screen key taps, debug display, hide/show controls and mode switch.
- Synthetic JS keyboard events may be ignored by the browser game.

## Build APK
GitHub Actions builds a debug APK when Android source changes. Open the repository's Actions tab and download the artifact after a successful workflow. The build and device test are not confirmed here.

## Project website
GitHub Pages source lives in `docs/`.

## Independence
Grimfall Companion is an independent community project, not affiliated with Grimfall. It does not store login credentials or bundle proprietary WoW game client files.
