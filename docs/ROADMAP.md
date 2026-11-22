# Roadmap & Milestones

## 0.1.1 — Input diagnostics (source milestone)
- [x] Fullscreen WebView
- [x] Touch button row for mapped keyboard keys
- [x] Physical controller buttons mapped to keys
- [x] URL dialog with persisted webclient address
- [x] Hide/show overlay controls
- [x] Controller button and analog stick diagnostic status
- [ ] Build APK and install on AYN Thor

## 0.2 — Input validation
- [ ] Confirm Grimfall browser client URL and load path
- [ ] Verify Android WebView compatibility, login and gameplay
- [ ] Test physical button mapping and identify ignored synthetic keyboard events
- [ ] Capture device/client version and sanitized test notes
- [ ] Research direct Gamepad API, accessibility, touch dispatch and browser-specific approaches

## 0.3 — Mapping UX
- [ ] User-editable mappings
- [ ] Profiles saved locally
- [ ] Analog movement experiments (WASD thresholding, hold/release)
- [ ] Camera/mouse experiments and sensitivity
- [ ] Press/hold and key-up handling

## 0.4 — Thor dual-screen companion
- [ ] Layout research on AYN Thor
- [ ] Action bar pages and modifier layers
- [ ] Radial ability selector
- [ ] Target cycling / focus shortcuts
- [ ] Quest, map and inventory shortcuts

## Risk
Synthetic JavaScript KeyboardEvents are untrusted and frequently fail to trigger game controls. WebView controller event reception and game input acceptance are two separate tests. We will not describe controls as functional until tested against Grimfall itself.