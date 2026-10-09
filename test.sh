#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
OUT="$ROOT/build/tests"
mkdir -p "$OUT"
javac -source 8 -target 8 -d "$OUT" \
  "$ROOT/src/com/jeremykenedy/aquasurfacedrift/AquaSurfaceOptions.java" \
  "$ROOT/src/com/jeremykenedy/aquasurfacedrift/WaterSurfaceMath.java" \
  "$ROOT/src/com/jeremykenedy/aquasurfacedrift/SettingsValues.java" \
  "$ROOT/tests/AquaSurfaceOptionsTest.java"
java -ea -cp "$OUT" com.jeremykenedy.aquasurfacedrift.AquaSurfaceOptionsTest
python3 -m unittest -v tests.test_installer
