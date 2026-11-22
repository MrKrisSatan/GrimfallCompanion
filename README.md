# Grimfall Companion

Android browser-first controller companion for **Grimfall WoW (3.3.5)**, aimed at the AYN Thor dual-screen handheld.

> Early prototype. Browser input integration is experimental and not hardware-tested. It does not modify the server or bundle a WoW client.

## Goals
- Open Grimfall's web site/client in fullscreen Android WebView.
- Capture physical gamepad button input and provide experimental key mappings.
- Offer an on-screen key overlay.
- Grow toward a dual-screen action-bar / radial-wheel companion.

## Prototype limitations
- The initial build loads the Grimfall homepage. If the browser client is hosted at a separate route, navigate to it in the browser or edit the `home` URL in `MainActivity.java`.
- This prototype forwards synthetic JavaScript keyboard events. Grimfall may not accept synthetic events, may require focus, or rely on touch/canvas controls. Direct touch injection, Gamepad API integration, and second-screen support are not implemented yet.
- Controller buttons currently map to key taps/events (A=1, B=2, X=3, Y=4, L1=Q, R1=E, L2=R, R2=F). Mapping settings are not implemented.
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