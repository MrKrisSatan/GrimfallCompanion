# Grimfall Companion

Android browser-first controller companion for **Grimfall WoW (3.3.5)**, aimed at the AYN Thor dual-screen handheld.

**Status: 0.1.1 source prototype.** Not a verified APK. Controller integration is experimental and not yet tested on Thor hardware.

## Prototype features
- Fullscreen Android WebView shell.
- Grimfall homepage loaded by default.
- URL dialog: set a specific webclient URL and persist it.
- Controller button-to-key mapping: A/B/X/Y → 1/2/3/4, L1/R1/L2/R2 → Q/E/R/F.
- On-screen key buttons.
- Overlay hide/show.
- Status text for button events and analog stick axes.

## Important input caveat
The app emits synthetic JavaScript keyboard events in WebView. Web games may ignore untrusted synthetic events, or use touch/canvas input. The status display confirms Android received a controller event, **not** that Grimfall acted on it. Analog movement and camera mapping are not implemented.

## Build
1. Install Android Studio and a supported Android SDK.
2. Open the `android/` directory.
3. Allow Gradle sync to complete.
4. Build > Build APK(s).
5. Install the debug APK on AYN Thor and test using `docs/TESTING.md`.

## Project website
Sources are in `docs/`. GitHub Actions deploys this folder to Pages.

## Roadmap
- [x] Initial source scaffold and site
- [x] Configurable webclient URL
- [x] Controller event / stick-axis diagnostic
- [ ] Build and install APK on Thor
- [ ] Validate browser-client navigation and login
- [ ] Confirm whether Grimfall accepts synthetic keyboard events
- [ ] Reliable movement/camera input
- [ ] Configurable mappings and profiles
- [ ] Dual-screen action bars, radial selector, targeting, map/quest shortcuts

## Independence
Grimfall Companion is an independent community prototype, not affiliated with Grimfall. It does not store login credentials or bundle a WoW client.