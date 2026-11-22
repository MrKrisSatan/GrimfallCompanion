# Grimfall Companion

A two-mode Android launcher and companion project for GrimfallWoW, aimed at the AYN Thor dual-screen handheld.

**Status: 0.2 prototype.** The app starts with:
- **Browser Client:** launches Grimfall in a fullscreen WebView with experimental controller/key overlay.
- **Local Client:** opens a setup placeholder. Local client runtime and addon installation are not implemented yet.

## Local Client dependency (mandatory)

The intended Local Client mode **must bundle ConsolePortLK** (upstream: https://github.com/leoaviana/ConsolePortLK) in the APK and automatically install/repair it in the user's selected 3.3.5a client folder. The bundled copy must preserve the upstream Artistic License 2.0 and notices. This is a required future milestone; it is **not embedded in the current prototype APK/source yet**.

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
