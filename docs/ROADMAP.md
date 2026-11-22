# Roadmap
## Prototype 0.1
- [x] WebView fullscreen shell and controller-key overlay
- [ ] Compile APK (requires Android SDK / Gradle)
- [ ] Validate webclient URL and login flow
- [ ] Verify physical controller mapping against Grimfall
## 0.2
- Persisted mappings and profiles
- Key-down/key-up handling and controller debug HUD
- Investigate touch/accessibility bridge and Gamepad API integration
## 0.3
- Dual-screen companion layout
- Radial ability wheel and modifier action pages
- Targeting, map/quest shortcuts
## Input limitation
Synthetic JavaScript keyboard events are not guaranteed to be accepted by browser games. This prototype must be validated on the device; direct touch injection is not yet implemented.