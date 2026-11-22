# Roadmap & Milestones

## 0.1.1 — Input diagnostics
- [x] Fullscreen WebView
- [x] On-screen mapped-key test buttons
- [x] Experimental physical-controller key mapping
- [x] Saved client URL
- [x] Hide/show overlay
- [x] Button and stick-axis diagnostics
- [ ] Confirm automated Android build succeeds

## Verified browser-client direction
Grimfall's official browser announcement (5 November 2026) describes dedicated mobile touch controls: movement stick, camera gestures and action buttons. Mobile touch controls are therefore the primary play route. Companion overlay buttons and synthetic keyboard events are fallback experiments, not assumed to be the mobile client's control mechanism.

## 0.2 — Mobile client validation
- [ ] Confirm the official Play/client URL from the user's own browser, avoiding account/session URLs in reports
- [ ] Load the mobile touch interface inside WebView
- [ ] Verify touch movement, camera gestures, action buttons and UI scaling
- [ ] Test AYN Thor landscape layout, screen sizing and performance
- [ ] Check fullscreen/immersive mode and browser back navigation

## 0.3 — Controller investigation
- [ ] Determine whether the browser client itself reads Gamepad API input
- [ ] Test controller event detection independently from in-game response
- [ ] Research safe, feasible input routes: hardware key events, gamepad API, Android system remapping, or accessibility/touch bridge
- [ ] Only implement analogue movement/camera after the input route is proven
- [ ] Configurable mappings and saved profiles

## 0.4 — Thor companion layout
- [ ] AYN Thor second-screen capability and orientation research
- [ ] Action-bar pages/modifier layers
- [ ] Radial ability selector
- [ ] Target/focus shortcuts
- [ ] Quest, map and inventory shortcuts

## Input caveat
Android receiving a controller event does not prove the game receives it. Synthetic JavaScript keyboard events are untrusted and may be ignored. The browser client has its own touch UI; don't replace or obscure it until testing shows a reason.
