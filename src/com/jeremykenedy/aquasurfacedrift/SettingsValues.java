package com.jeremykenedy.aquasurfacedrift;

public final class SettingsValues {
  private SettingsValues() {}

  public static boolean isSupported(String key, String value) {
    if (key == null || value == null) return false;
    if ("water".equals(key)) return oneOf(value, "ocean", "lagoon", "pool", "random");
    if ("time".equals(key)) return oneOf(value, "day", "dusk", "night", "random");
    if ("ripples".equals(key)) return oneOf(value, "calm", "natural", "lively", "random");
    if ("speed".equals(key)) return oneOf(value, "slow", "natural", "quick", "random");
    if ("shimmer".equals(key)) return oneOf(value, "off", "soft", "bright", "random");
    if ("view".equals(key)) return oneOf(value, "close", "wide", "random");
    if ("show_clock".equals(key)) return oneOf(value, "true", "false");
    if ("randomize_all".equals(key)) return oneOf(value, "true", "false");
    return false;
  }

  private static boolean oneOf(String value, String... allowed) {
    for (String option : allowed) if (option.equals(value)) return true;
    return false;
  }
}
