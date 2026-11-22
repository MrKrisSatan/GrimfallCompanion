# Grimfall Companion

A two-mode Android launcher and companion project for GrimfallWoW, aimed at the AYN Thor dual-screen handheld.

**Status: 0.2 prototype.** The app now starts with a mode selector:
- **Browser Client:** launches Grimfall in a fullscreen WebView with experimental controller/key overlay.
- **Local Client:** opens a setup placeholder for the future local-client workflow. It is not yet a working game launcher.

## Feature goal

Build Grimfall-specific feature parity with the breadth of DS Forever: controller configuration, multi-page action bars, radial ability menus, character-aware profiles, maps/quests, party and pet controls, inventory/character utilities, second-screen widgets, streaming and local-client support. We will implement and validate our own Grimfall integration rather than copy DS Forever's code.

## Browser mode prototype
- Fullscreen Android WebView with Grimfall homepage by default.
- Custom webclient URL dialog and saved URL.
- A/B/X/Y → 1/2/3/4 and L1/R1/L2/R2 → Q/E/R/F experimental mapping.
- On-screen key taps, debug display, hide/show controls and mode switch.
- Synthetic JS keyboard events may be ignored by the browser game.

## Local mode
Local-client discovery, installation/setup, launch, updates, controller input, and PC streaming remain unimplemented. The mode selection and placeholder screen are only a navigation foundation.

## Build APK
GitHub Actions builds a debug APK when Android source changes. Open the repository's Actions tab and download the artifact after a successful workflow. The build and device test are not confirmed here.

## Project website
GitHub Pages source lives in `docs/`.

## Independence
Grimfall Companion is an independent community project, not affiliated with Grimfall. It does not store login credentials or bundle game client files.
