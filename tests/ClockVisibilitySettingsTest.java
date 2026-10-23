package com.jeremykenedy.aquasurfacedrift;

public final class ClockVisibilitySettingsTest {
  public static void main(String[] args) {
    if (!SettingsValues.isSupported("show_clock", "true")
        || !SettingsValues.isSupported("show_clock", "false")
        || SettingsValues.isSupported("show_clock", "random")
        || SettingsValues.isSupported("show_clock", "invalid")) {
      throw new AssertionError("Clock visibility must be an explicit boolean");
    }
    System.out.println("Clock visibility settings tests passed.");
  }
}
