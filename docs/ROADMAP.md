# Roadmap & Feature Parity

The target is a Grimfall-specific handheld launcher and companion inspired by the feature breadth of DS Forever. We will implement original Grimfall integration, not copy DS Forever's code or assume its WoW-specific internals work here.

## 0.2 — Startup and mode selection
- [x] Start screen with Browser Client and Local Client choices
- [x] Remember the last selected mode
- [x] Browser mode opens the Grimfall WebView client
- [x] Local mode shows a separate ConsolePortLK setup route
- [x] Switch modes from browser overlay

## 0.3 — Browser client and input bridge
- [x] Confirm official Grimfall Play URL and read the mobile input announcement
- [x] Record the documented mobile movement-stick, camera-gesture and action-button model
- [x] Keep ordinary screen touch input passing through to the game
- [x] Capture Android controller buttons with KeyEvent
- [x] Capture analogue sticks, trigger axes and D-pad hats with MotionEvent
- [x] Prototype calibrated native multi-touch for movement and camera
- [x] Add calibrated face-button touch taps and keyboard fallback
- [x] Add radial stick deadzone, live diagnostics and release-all handling
- [x] Add a practical calibration and device-test guide
- [ ] Verify that Grimfall accepts the injected multi-touch events
- [ ] Test login, movement, camera, simultaneous inputs, buttons and scaling on AYN Thor
- [ ] Check actual WebView Gamepad API availability; native controller sampling is the current route
- [ ] Verify a successful APK build and archive the build artifact

## 0.4 — Local client and required addon installer
- [ ] Integrate a compatible 3.3.5a local runtime (investigate Android/Wine/Box64 route)
- [x] Add in-app ConsolePortLK installer/repair downloader (pinned 1.5.0-rc2 archive)
- [x] Folder picker and Interface/AddOns validation
- [x] Back up existing ConsolePort-prefixed folders before replacing them
- [x] Replace only ConsolePort-prefixed addon folders and preserve unrelated addons
- [x] Record upstream ConsolePortLK license and attribution requirements
- [ ] Bundle a reviewed ConsolePortLK source snapshot and license text in the APK
- [ ] Verify installer archive integrity and installed addon version
- [ ] Detect/validate the supplied 3.3.5a build and set up its Grimfall realm configuration
- [ ] Configure Thor mappings / ConsolePort setup for the Thor layout
- [ ] Launch, patch/update and error recovery flow where supported
- [ ] Never bundle proprietary WoW game files; user supplies them

## 0.5 — Controller configuration and profiles
- [x] Basic button, trigger and stick mapping in Browser Client prototype
- [x] Analogue movement and camera input bridge prototype
- [x] Deadzone default and input diagnostics
- [ ] User controls for sensitivity, invert axes and response curves
- [ ] Editable button mapping and hold / tap / repeat behaviour
- [ ] Browser and local-client profile presets
- [ ] Per-character bindings and saved layouts
- [ ] Validate all mappings against real gameplay and avoid stuck input

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
- The browser controller is a code prototype. Its native multi-touch events, camera drag and action taps have not been tested against a live Grimfall session or on AYN Thor hardware.
- The official Grimfall announcement documents mobile touch controls and desktop keyboard/mouse, but does not advertise native gamepad / Gamepad API support.
- The prototype's fallback keyboard events are synthetic DOM events and may be ignored by the page.
- Local Client mode can download and install ConsolePortLK into a selected client folder, but it cannot launch a game: the Wine/Box64 runtime and Grimfall client setup remain unimplemented.
- ConsolePortLK is downloaded from a pinned upstream release when requested; it is not embedded in the current APK.
- No successful Android APK build or AYN Thor gameplay test is claimed until a current GitHub Actions run and device test pass.
