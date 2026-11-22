# Roadmap & Milestones

## 0.1.1 — Input diagnostics
- [x] Fullscreen WebView
- [x] On-screen key buttons
- [x] Controller buttons mapped to keyboard events
- [x] Configurable browser-client URL (persisted)
- [x] Hide/show overlay
- [x] Button event and analog-axis diagnostic
- [ ] Compile APK and test on Thor

## 0.2 — Input validation
- [ ] Verify client URL and page navigation
- [ ] Test login/gameplay in WebView
- [ ] Determine whether synthetic JS keyboard events are accepted by Grimfall
- [ ] Identify whether client relies on touch/canvas controls
- [ ] Capture safe test results (no credentials)

## 0.3 — Controller mapping
- [ ] User-editable mappings and profiles
- [ ] Key-down/key-up and repeat/hold refinement
- [ ] Analog movement experiment
- [ ] Camera/mouse experiment
- [ ] Controller debug panel

## 0.4 — Thor dual-screen companion
- [ ] Layout test on AYN Thor
- [ ] Action-bar pages and modifiers
- [ ] Radial selector
- [ ] Target/focus shortcuts
- [ ] Quest/map/inventory shortcuts

## Input risk
Synthetic JavaScript KeyboardEvents are untrusted and may be ignored by games. Android receiving a controller event is a different test from Grimfall accepting it. Do not label gameplay controls functional until verified on-device.