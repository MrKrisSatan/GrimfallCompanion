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

## Mobile-first strategy\nGrimfall's official browser-client announcement describes mobile touch controls (movement stick, camera gestures and action buttons). The native touch interface is the primary path to validate; the overlay's keyboard events are only an experimental fallback.\n\n## Important input caveat
The app emits synthetic JavaScript keyboard events in WebView. Web games may ignore untrusted synthetic events, or use touch/canvas input. The status display confirms Android received a controller event, **not** that Grimfall acted on it. Analog movement and camera mapping are not implemented.

## Build APK

**Automatic build:** GitHub Actions now builds a debug APK when Android source changes. Open the repository's **Actions** tab, select the Android APK workflow, wait for a successful run, then download the `GrimfallCompanion-debug-apk` artifact. The workflow also supports manual dispatch.


**Local build:** Install Android Studio and a supported SDK, open `android/`, allow sync, then Build > Build APK(s).

Install the debug APK on AYN Thor and follow `docs/TESTING.md`.

## Project website
Sources are in `docs/`. GitHub Actions deploys this folder to Pages.

## Roadmap
- [x] Initial source scaffold and site
- [x] Configurable webclient URL
- [x] Controller event / stick-axis diagnostic
- [x] GitHub Actions debug APK build workflow
- [ ] Build and install APK on Thor
- [ ] Validate browser-client navigation and login
- [ ] Confirm whether Grimfall accepts synthetic keyboard events
- [ ] Reliable movement/camera input
- [ ] Configurable mappings and profiles
- [ ] Dual-screen action bars, radial selector, targeting, map/quest shortcuts

## Independence
Grimfall Companion is an independent community prototype, not affiliated with Grimfall. It does not store login credentials or bundle a WoW client.