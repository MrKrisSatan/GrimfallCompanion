# Third-party component: ConsolePortLK

Grimfall Companion intends to bundle ConsolePortLK for Local Client mode.

Upstream project: https://github.com/leoaviana/ConsolePortLK
Upstream license: Artistic License 2.0 (see upstream LICENSE.md)
Upstream purpose: controller interface addon backported for World of Warcraft 3.3.5a.

The addon is NOT embedded in the current APK/source prototype yet. Bundling is a required implementation milestone and must include the upstream addon source folders and license text verbatim. Do not claim this APK currently installs ConsolePortLK.


## Required integration behaviour
- Bundle a pinned, reviewed ConsolePortLK release/source snapshot in app assets.
- Include and preserve LICENSE.md and upstream notices.
- On Local Client setup, back up existing AddOns, then install/replace only the ConsolePortLK addon folders owned by the bundle.
- Preserve unrelated addons and user settings; never wipe WTF wholesale.
- Detect version and repair missing addon files on launch/setup.
- Display the bundled addon version and upstream link in the UI.
- Do not install addons in Browser Client mode.
