package com.jeremykenedy.aquasurfacedrift;

public final class WaterSurfaceMath {
  private WaterSurfaceMath() {}

  public static float wave(float x, float y, float time, int rippleMode) {
    float detail = rippleMode == 0 ? 0.35f : rippleMode == 2 ? 1.15f : 0.72f;
    float broad = (float) Math.sin(x * 5.2f + y * 2.1f + time * 0.48f) * (0.58f + y * 0.18f);
    float cross = (float) Math.sin(x * 9.3f - y * 5.4f - time * 0.31f) * 0.3f;
    float fine = (float) Math.sin(x * 16.7f + y * 12.8f + time * 0.22f) * 0.12f * detail;
    return broad + cross + fine;
  }

  public static float shimmer(float x, float y, float time, int mode) {
    if (mode == 0) return 0f;
    float phase =
        (float) Math.sin(x * 17.1f + y * 8.4f + time * 0.57f)
            * (float) Math.sin(x * 5.7f - y * 13.2f - time * 0.36f);
    float intensity = mode == 2 ? 1f : 0.72f;
    return Math.min(1f, Math.max(0f, phase + 0.18f) * intensity);
  }

  public static float perspective(float y, int view) {
    return view == 0 ? 0.62f + y * 0.46f : 0.38f + y * 0.92f;
  }
}
