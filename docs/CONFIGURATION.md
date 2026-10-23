# Configuration

Open Aqua Surface Drift from the TV launcher. Use the remote to choose settings. The app saves preferences locally and resolves Random values when a new DreamService scene starts.

| Setting key | Choices | Default | Effect |
| --- | --- | --- | --- |
| `water` | `ocean`, `lagoon`, `pool`, `random` | `ocean` | Changes the surface color palette. |
| `time` | `day`, `dusk`, `night`, `random` | `day` | Changes ambient water colors and reflection tones. |
| `ripples` | `calm`, `natural`, `lively`, `random` | `natural` | Changes the amount of fine wave movement. |
| `speed` | `slow`, `natural`, `quick`, `random` | `natural` | Changes how quickly the surface animation moves. |
| `shimmer` | `off`, `soft`, `bright`, `random` | `soft` | Controls reflected light glints on the water. |
| `view` | `close`, `wide`, `random` | `wide` | Changes wave spacing and screen-space perspective. |
| `randomize_all` | `true`, `false` | `false` | Chooses each setting independently whenever the saver starts. |

## Settings provider

Host apps can discover the choices through the exported local provider at `com.jeremykenedy.aquasurfacedrift.settings`:

- `content://com.jeremykenedy.aquasurfacedrift.settings/schema` returns `key`, `title`, `type`, `default`, `choices`, and `randomAllowed`.
- `content://com.jeremykenedy.aquasurfacedrift.settings/settings` returns the saved `key` and `value` pairs.
- Call `ContentResolver.update()` on the `settings` URI with text fields `key` and `value` to change one supported value. Invalid keys and choices are rejected.

All preferences remain on device. The provider does not make network requests. Hosts should read the schema rather than rely on a copied option list. Fire TV UI settings integration is maintained separately from this screensaver app.

## Optional clock

`show_clock` is an explicit boolean with default `false`, choices `true|false`, and `randomAllowed=false`. It enables a gently moving local-time overlay. Scene randomization preserves the chosen clock visibility.
