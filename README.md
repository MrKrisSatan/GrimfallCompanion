# Grimfall Companion

An Android launcher and controller-first companion for Grimfall WoW, aimed at the AYN Thor dual-screen handheld.

**Status: browser input prototype.** The app has two entry points:

- **Browser Client:** opens the official Grimfall Play page in a fullscreen WebView. The current prototype samples physical controller buttons, sticks, triggers and D-pad input, then drives Grimfall's documented mobile controls through calibrated native multi-touch events.
- **Local Client:** provides a folder-picker based ConsolePortLK installer/repair flow. A compatible 3.3.5a runtime is not yet included, so this is not a playable local client.

## Browser controller prototype

The official Grimfall browser announcement (5 November 2026) documents an on-screen movement stick, camera gestures and action buttons on mobile, and keyboard/mouse on desktop. It does not document native gamepad or Gamepad API support. The prototype therefore bridges the AYN Thor controller to the mobile touch layout rather than assuming the browser game has gamepad support.

| Thor input | Prototype mapping |
|---|---|
| Left stick | Calibrated mobile movement-stick touch; optional WASD fallback |
| Right stick | Continuous camera drag from a calibrated world-screen point |
| A / B / X / Y | Calibrated touch taps on the four mobile action buttons; 1 / 2 / 3 / 4 fallback before calibration |
| L1 / R1 | Q / E |
| L2 / R2 or trigger axes | Shift / Control |
| D-pad | Arrow keys |
| Start / Select | Escape / Tab |

Press **Calibrate** in the overlay and tap the movement pad centre, A, B, X and Y action centres, then an unobstructed camera-drag start point. The positions are saved as normalised screen coordinates. **Move: TOUCH / Move: KEYS** switches the left stick between touch and keyboard movement. **Debug** shows sampled axes and triggers.

The touch bridge is a prototype, not a verified compatibility claim. It has not yet been tested on an AYN Thor or against a live Grimfall session. Synthetic keyboard events may be ignored by the game, and the game may treat injected WebView touch events differently from physical finger input.

See [Browser Input Prototype and Test Guide](docs/BROWSER_INPUT.md) for calibration, detailed mappings, acceptance tests and limitations.

## Local Client dependency

Local Client mode currently downloads the pinned ConsolePortLK **1.5.0-rc2** archive from upstream (https://github.com/leoaviana/ConsolePortLK) when the user presses Install/Repair, then installs its ConsolePort-prefixed addon folders into the selected client. The source is downloaded at install time and is not embedded in the APK. The installer makes a backup under AddOns and replaces only ConsolePort-prefixed folders, preserving unrelated addon folders. It requires network access and folder access through Android's system picker.

This is only an addon installer. The local game runtime, client validation, launch flow and Grimfall realm configuration remain future work. Users must supply their own lawful game client files. Do not delete the WTF folder or unrelated AddOns.

## Feature goal

Build Grimfall-specific feature parity with the breadth of DS Forever: controller profiles, modifier layers, multi-page action bars, radial ability menus, character-aware layouts, maps/quests, party and pet controls, inventory utilities and second-screen widgets. Implement original Grimfall integration rather than copying DS Forever's code.

## Build APK

A debug APK was built successfully in GitHub Actions on 23 November 2026 from commit 3a1e13c. See the [verified build run](https://github.com/MrKrisSatan/GrimfallCompanion/actions/runs/45178871447) and download its **GrimfallCompanion-debug-apk** artifact. A successful build is not proof of game compatibility; AYN Thor and live Grimfall input testing are still outstanding.

## Project website

GitHub Pages source lives in docs/. To publish it, open the repository's **Settings → Pages** and choose **GitHub Actions** under Build and deployment. The Actions token cannot enable Pages for this repository automatically, so the site remains unpublished until that one-time repository setting is selected.

## Independence

Grimfall Companion is an independent community project, not affiliated with Grimfall. It does not store login credentials or bundle proprietary WoW game client files.
