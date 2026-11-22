# Roadmap & Feature Parity

The target is a Grimfall-specific handheld launcher and companion inspired by the feature breadth of DS Forever. We will implement original Grimfall integration, not copy DS Forever's code or assume its WoW-specific internals work here.

## 0.2 — Startup mode selector
- [x] Start screen with Browser Client and Local Client choices
- [x] Remember the last selected mode
- [x] Browser mode opens existing WebView client
- [x] Local mode has a clearly marked integration/setup placeholder
- [x] Switch modes from browser overlay

## 0.3 — Browser client
- [ ] Confirm Grimfall mobile client and official play URL
- [ ] Test WebView touch movement, camera, action buttons, login and scaling
- [ ] Preserve native touch controls and avoid obstructing UI
- [ ] Determine if Gamepad API input is supported
- [ ] Verify APK build and test on AYN Thor

## 0.4 — Local client and required addon bundle
- [ ] Integrate a compatible 3.3.5a local runtime (investigate Android/Wine/Box64 route)
- [x] Add in-app ConsolePortLK installer/repair downloader (pinned 1.5.0-rc2 archive)
- [ ] Confirm upstream license notices and verify installer archive integrity
- [x] Folder picker and Interface/AddOns validation\n- [ ] Local client detection and setup wizard
- [ ] User selects client folder; validate expected files
- [ ] Back up existing Interface/AddOns before addon installation
- [ ] Install/repair ConsolePortLK automatically; replace only its own addon folders and preserve unrelated addons
- [ ] Verify addon files and report installed version
- [ ] Configure Thor mappings / ConsolePort setup for the Thor layout
- [ ] Launch, patch/update and error recovery flow where supported
- [ ] Never bundle proprietary WoW game files; user supplies them

## 0.5 — Controller room
- [ ] Button, trigger and stick mapping
- [ ] Analogue movement and camera, if supported by the input route
- [ ] Deadzones, sensitivity, invert axes and response curves
- [ ] Profile presets and per-character bindings
- [ ] Keyboard/gamepad/touch input diagnostics
- [ ] Reliable game input bridge for local and browser modes

## 0.6 — Gameplay companion
- [ ] Multi-page action bars and modifier layers
- [ ] Radial spell/ability/item/mount selection
- [ ] Cooldown and cast tracking
- [ ] Target, focus, assist and party selection
- [ ] Pet command wheel
- [ ] Macro manager and binding editor
- [ ] Quick chat and push-to-talk/voice typing investigation

## 0.7 — Character & inventory tools
- [ ] Character-specific profiles
- [ ] Gear slots and stat comparison
- [ ] Bags, item search, tooltip helper and equipment actions where client APIs permit
- [ ] Spell/talent/ability browser
- [ ] Mounts, pets, professions and utility shortcuts

## 0.8 — Azeroth navigation
- [ ] World map and minimap companion
- [ ] Quest list and tracking
- [ ] Waypoints/navigation overlay where permitted
- [ ] Party/raid status and dungeon utility

## 0.9 — Thor dual-screen UI
- [ ] Dedicated second-screen layout
- [ ] Movable/resizable widgets
- [ ] Game status, cast bar, party, buffs and cooldown panels
- [ ] Touch-friendly companion rooms and tabs
- [ ] Layouts that adapt to Thor orientation and screen dimensions

## 1.0 — Settings, themes and quality
- [ ] Themes and OLED-friendly mode
- [ ] Layout editor and per-character preferences
- [ ] Safe reset options and backups
- [ ] Import/export profiles
- [ ] Crash/error diagnostics with privacy controls
- [ ] Release build, install/update flow and full Thor test checklist

## Current limitations
- Browser mode is implemented as a WebView shell with experimental synthetic-key controls.
- Local Client mode is a navigation placeholder, not a working game launcher.
- ConsolePortLK is a required dependency for the intended Local Client mode, but it is not embedded in the APK yet.
- Neither controller gameplay nor second-screen functionality is hardware-verified.
