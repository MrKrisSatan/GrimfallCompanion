# Grimfall Companion

Android browser-first controller companion for **Grimfall WoW (3.3.5)**, aimed at the AYN Thor dual-screen handheld.

**Status: 0.1.1 source prototype.** This is not yet a verified APK. Controller integration is experimental and has not been tested on Thor hardware.

## Prototype features
- Fullscreen Android WebView shell.
- Loads Grimfall's site by default.
- URL dialog: load a specific webclient address and persist it between launches.
- Controller button-to-key mapping (A/B/X/Y → 1/2/3/4, L1/R1/L2/R2 → Q/E/R/F).
- On-screen key buttons.
- Overlay hide/show.
- Status/debug feedback for button events and analog stick axes.

## Important input caveat
The app emits synthetic JavaScript keyboard events in the WebView. Many web games ignore synthetic keyboard events because they do not represent trusted hardware input, and the Grimfall webclient may use touch or canvas-specific input. The controller event status only confirms Android received the input; it does **not** confirm the game acted on it. Analog movement and camera mapping are not implemented.

## Build
1. Install Android Studio and a supported Android SDK.
2. Open the `android/` directory.
3. Allow Gradle sync to complete.
4. Build > Build APK(s).
5. Install the debug APK on the AYN Thor and test with the checklist in `docs/TESTING.md`.

## Project site
Sources are in `docs/`. GitHub Actions deploys the `docs` folder to GitHub Pages.

## Roadmap
- [x] Project website and Android source scaffold
- [x] Configurable webclient URL
- [x] Controller event status / stick-axis diagnostic
- [ ] Build and install APK on Thor
- [ ] Validate browser-client navigation/login
- [ ] Confirm whether Grimfall accepts keyboard events
- [ ] Implement reliable movement and camera bridge if feasible
- [ ] Configurable key mappings and saved profiles
- [ ] Dual-screen companion UI (action bars, radial selector, targeting, map/quest shortcuts)

## Project independence
Grimfall Companion is an independent community project, not affiliated with Grimfall. It does not store user credentials or bundle a WoW client.