# Architecture

Aqua Surface Drift is a native Android app with a `DreamService`, a remote-friendly settings activity, and a local settings provider. A hardware-accelerated Android `View` draws the animated surface using Canvas paths and a bounded grid of light glints. It bundles no video, texture pack, font, or runtime library.

`WaterSurfaceMath` samples low-cost, time-varying wave and highlight functions. `AquaSurfaceSceneView` draws a water-color gradient, softly drifting ambient light, layered wave contours, and small surface reflections. It adapts to the display size supplied by Android and does not request a forced 4K buffer. Scheduled redraws stop when the DreamService or preview pauses.

`AquaSurfaceOptions` resolves validated local choices and independent Random values. The exported `SettingsProvider` reports the supported schema and current preferences to host apps. Neither the app nor provider makes network requests.

The standalone Python installer is separate from runtime behavior. It fetches GitHub release metadata and signed APK bytes only after an explicit install command, restricts download hosts, verifies SHA-256, and invokes ADB.
