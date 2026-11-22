# Grimfall Companion

Android browser-first controller companion for **Grimfall WoW (3.3.5)**, aimed at the AYN Thor dual-screen handheld.

> Early prototype. Browser input integration is experimental and not hardware-tested. It does not modify the server or bundle a WoW client.

## Goals
- Open Grimfall's web client in fullscreen Android WebView.
- Capture physical gamepad input and provide configurable keyboard mappings.
- Offer an optional on-screen button overlay.
- Grow toward a dual-screen action-bar / radial-wheel companion.

## Prototype limitations
- This prototype forwards synthetic keyboard events into the WebView. Grimfall may not respond to synthetic events, may require focus, or may rely on touch/canvas controls. Direct touch injection, Gamepad API integration, and second-screen support are not implemented yet.
- The URL is configurable in source for now; default is Grimfall's site. Enter the exact webclient URL if it differs.
- No credentials are stored by the app.

## Build
Open `android/` in Android Studio, allow Gradle sync, then Build > Build APK(s).

## Website
Project page sources are in `docs/`. GitHub Pages workflow is in `.github/workflows/pages.yml`.

## Roadmap
- [x] Initial source scaffold and project website
- [ ] Build APK and install on AYN Thor
- [ ] Test Grimfall webclient and controller response
- [ ] Identify reliable input transport (keyboard events, Gamepad API, touch/accessibility bridge)
- [ ] Custom mappings and profiles
- [ ] Dual-screen UI: action bars, radial wheel, targeting, map/quest shortcuts