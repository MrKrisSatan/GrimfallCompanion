# Browser controller prototype

## Why the controller bridge uses touch

**Confirmed from Grimfall's official announcement (5 November 2026):** its browser client describes keyboard and mouse on desktop, and a mobile control layout with an on-screen movement stick, camera gestures and action buttons. The announcement does not document native controller input or the browser Gamepad API.

- Official announcement: https://www.grimfallwow.com/news/webclient-announcement/40
- Play page: https://www.grimfallwow.com/play
- Android controller event model: https://developer.android.com/games/sdk/game-controller/controller-input

Android exposes physical gamepad buttons as key events and analogue sticks, triggers and hats as motion events. The Companion therefore samples those Android events in its Activity and translates them into the input model Grimfall already documents for mobile: native WebView touch events at calibrated points. Keyboard events remain a fallback for buttons and for movement when selected.

This is a prototype based on documented input paths, not a claim that Grimfall accepts every injected event. The game and AYN Thor still need to be tested together.

## Current input map

| Physical input | Browser prototype output |
|---|---|
| Left stick | Holds a virtual finger on the mobile movement stick and moves it relative to the calibrated centre |
| Left stick, with Move: KEYS selected | Holds/releases W, A, S and D keyboard events |
| Right stick | Holds a virtual finger at the calibrated camera start point and continuously drags it to rotate the view |
| A / B / X / Y | Brief native touch at the individually calibrated on-screen action-button centres; before calibration, falls back to keyboard 1 / 2 / 3 / 4 |
| L1 / R1 | Q / E key down/up |
| L2 / R2 buttons or analogue triggers | Shift / Control while held |
| D-pad | Arrow keys |
| Start | Escape |
| Select | Tab |

The left stick uses a radial 0.18 deadzone with rescaling outside the deadzone. The camera drag integrates right-stick velocity at a prototype speed of 560 density-independent pixels per second and clamps the virtual pointer inside the WebView. The values are starter defaults, not tuned measurements from a Thor.

The movement and camera contacts can remain down at the same time. Action-button taps use additional pointer IDs, so they can be sent while moving or turning. All active contacts and held keys are released when the app pauses, closes the browser mode or destroys the Activity.

## Calibrate the mobile controls

1. Open **Browser Client** and wait for the Grimfall Play page to finish loading.
2. Sign in and enter the world. Use the mobile control layout if Grimfall offers a choice.
3. Open the Companion overlay and press **Calibrate**.
4. Tap the centre of the on-screen movement stick.
5. Tap the centre of the A, B, X and Y action buttons in order.
6. Tap an unobstructed place in the 3D world where a camera drag should begin.
7. Test both sticks before using combat. The calibration points are stored as normalised screen coordinates, so they survive ordinary resolution changes, but recalibrate after changing the mobile layout, orientation or UI scale.

Use **Move: TOUCH / Move: KEYS** to switch left-stick movement between the mobile pad and keyboard fallback. **Debug** displays sampled stick and trigger values. Hide collapses the overlay to a small **GC** restore button.

## Practical test checklist

Record each item as pass, fail or not tested. Test away from enemies first.

- [ ] The Play page loads, login works and the character enters the world.
- [ ] Left stick moves forward, backwards, left and right.
- [ ] Diagonal movement is smooth and the character stops when the stick is released.
- [ ] Right stick rotates the camera in both directions and stops when released.
- [ ] Movement and camera can be held together.
- [ ] A / B / X / Y activate their calibrated on-screen controls.
- [ ] L1 / R1 and L2 / R2 apply and release the expected bindings.
- [ ] D-pad, Start and Select produce the intended actions.
- [ ] Hiding the overlay does not stop controller input; the GC button restores it.
- [ ] Backgrounding the app or leaving the browser mode does not leave movement or camera stuck.
- [ ] Recalibration after a UI scale / layout change restores accurate control positions.

Start with movement and camera. Then verify one harmless ability, then modifiers, then combat. Do not test action taps against a dangerous target until you know which button each touch activates.

## Known limitations and risks

- No AYN Thor hardware or live Grimfall game session was available during implementation. There is no hardware compatibility result yet.
- The six-point calibration and default positions are implemented, but the browser game has not been confirmed to respond to the injected multi-touch events.
- The prototype sends touch events into WebView through Android's view input path. Grimfall may reject, ignore or interpret injected events differently from physical finger input; this must be checked in-game.
- A / B / X / Y are short taps, not sustained virtual presses. A future profile editor should let users bind other actions and choose tap / hold / repeat behaviour.
- Keyboard events are synthetic DOM events. The page may ignore them, so keyboard fallback is not guaranteed.
- Camera drag speed and movement radius are provisional. Add sensitivity, invert-axis and response-curve controls only after collecting real-device observations.
- WebView behaviour, full-screen scaling, account login and any Grimfall-specific browser restrictions remain unverified.
- Browser mode does not install addons. ConsolePortLK belongs to the separate Local Client route.

## Next acceptance milestone

The next milestone is a repeatable AYN Thor test: install the APK, enter Grimfall, calibrate, then complete movement, camera, simultaneous-input and action-button tests. Only after that should the project expand the controller profile editor or start the second-screen action-bar / targeting UI.

## Architecture notes for the later companion

The controller prototype stays deliberately small:

1. Android KeyEvent / MotionEvent capture.
2. A virtual multi-pointer touch adapter for Grimfall's documented mobile controls.
3. A small input profile layer for mappings, deadzones and future sensitivities.
4. A later second-screen companion surface for action bars, modifiers, radial menus, targeting, inventory, maps and quests.

For the separate local-client direction, the AYN Thor-specific Warmane DS project is a useful reference for a full Winlator / Box64 runtime and external-display companion, but it is a much larger GPL-3 / LGPL-2.1 integration than this browser controller: https://github.com/nicho-n/warmane-ayn-thor. Its runtime is not part of Grimfall Companion.
