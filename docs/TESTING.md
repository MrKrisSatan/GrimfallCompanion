# Device Test Checklist — 0.1.1

## Before testing
- Use the official Grimfall browser client.
- Install a debug APK built locally.
- If the webclient uses a separate URL, open Controls → URL and paste it. Avoid sharing URLs containing session or token parameters.

## WebView
- [ ] App opens in fullscreen landscape
- [ ] Grimfall page loads
- [ ] Navigation and login work
- [ ] Browser client enters game
- [ ] Keyboard doesn't unexpectedly pop up

## Controller diagnostics
- [ ] A/B/X/Y display mapped keys 1/2/3/4
- [ ] L1/R1/L2/R2 display Q/E/R/F
- [ ] Left-stick axis values update in the status text
- [ ] Hide overlay; reopen using Controls button

**Important:** These status changes only confirm Android received the input. They do not prove the game reacts to synthetic JavaScript key events.

## Report
Please note Android version, the sanitized client URL, which tests pass/fail, and screenshots/logs with credentials, email, tokens, or private account details redacted.

## Known limitations
- Analog movement and camera control are not mapped.
- Controller bindings are hard-coded.
- Synthetic key events may be ignored.
- No second-screen companion UI yet.