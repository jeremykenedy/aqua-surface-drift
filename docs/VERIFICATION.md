# Verification

## Test scope

Host tests cover scene option resolution, random selection bounds, water-wave motion and shimmer behavior, settings validation, installer URL trust, SHA-256 verification, cancellation, device selection, update/install/uninstall arguments, and cleanup. Java line and branch coverage is enforced for the scene-option resolver, water-motion math, and provider value validation. Python line and branch coverage is enforced for the installer. Android framework rendering and DreamService lifecycle are verified outside host coverage.

## Device evidence

| Platform | Device | Result |
| --- | --- | --- |
| Android TV emulator | `sdk_google_atv64_arm64`, Android 12/API 31, 1920x1080 | APK installed using the standalone installer. Settings and preview activities opened; animated water was allowed to run for 45 seconds, captured, and inspected. Settings provider schema and writes were checked. System Dream picker was unavailable, so idle selection and activation remain unverified. |
| Physical Fire TV | Not tested | We are looking for a Fire TV owner to test installation, selection, idle activation, remote settings, and wake/resume. Please report model, Fire OS/API, resolution, test steps, and results in the [issue tracker](https://github.com/jeremykenedy/aqua-surface-drift/issues). |
| Physical Google TV | Not tested | We are looking for a Google TV owner to test installation, system selection, idle activation, remote settings, and wake/resume. Please report model, OS/API, resolution, test steps, and results in the [issue tracker](https://github.com/jeremykenedy/aqua-surface-drift/issues). |

The emulator's screensaver component, enabled state, and screen-off timeout were recorded before and after the test and restored to their original values. The app was uninstalled after validation. No physical TV was used.

## Captures

`docs/screenshots/aqua-surface-drift-preview.png` and `aqua-surface-drift-settings.png` are unretouched captures from the app running on the Android TV emulator. The preview capture followed a 45-second settling period and shows the animated scene without menus or system UI. The smaller `res/drawable-nodpi/screensaver_preview.jpg` is derived from the inspected preview capture. The captures verify emulator rendering only, not physical 4K composition or vendor-specific DreamService activation.

## Optional clock regression, 2026-10-22

An isolated Android TV emulator fixture compiled the production scene and provider classes from this change. The combined five-app run passed 123 actual Canvas/provider assertions. It checked the existing appearance default (OFF), explicit ON/OFF under scene randomization, portable boolean storage and schema, and invalid-write retention. OFF produced zero time/date/seconds text draws while the scene remained drawn.

This isolated regression does not establish production DreamService binding, physical-TV performance or acceptance of the existing artwork. Signed release and both-TV installation gates remain pending.
