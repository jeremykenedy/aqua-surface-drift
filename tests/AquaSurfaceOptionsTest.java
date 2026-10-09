package com.jeremykenedy.aquasurfacedrift;

import java.util.Random;

public final class AquaSurfaceOptionsTest {
  public static void main(String[] args) {
    verifyExplicitSettings();
    verifyRandomSelections();
    verifySafeDefaults();
    verifyProviderChoices();
    verifyWaterMotion();
  }

  private static void verifyExplicitSettings() {
    AquaSurfaceOptions options =
        AquaSurfaceOptions.resolve(
            "pool", "dusk", "lively", "quick", "bright", "close", false, new Random(2));
    check(
        options.water == 2
            && options.time == 1
            && options.ripples == 2
            && options.speed == 2
            && options.shimmer == 2
            && options.view == 0,
        "explicit choices reach the scene options");
    AquaSurfaceOptions ocean =
        AquaSurfaceOptions.resolve(
            "ocean", "day", "natural", "natural", "soft", "wide", false, new Random(3));
    check(ocean.water == 0 && ocean.time == 0 && ocean.view == 1, "default options resolve");
  }

  private static void verifyRandomSelections() {
    AquaSurfaceOptions random =
        AquaSurfaceOptions.resolve(
            "random", "random", "random", "random", "random", "random", false, new Random(41));
    check(random.water >= 0 && random.water <= 2, "water random selection is bounded");
    check(random.time >= 0 && random.time <= 2, "light random selection is bounded");
    check(random.ripples >= 0 && random.ripples <= 2, "ripple random selection is bounded");
    check(random.speed >= 0 && random.speed <= 2, "speed random selection is bounded");
    check(random.shimmer >= 0 && random.shimmer <= 2, "shimmer random selection is bounded");
    check(random.view >= 0 && random.view <= 1, "view random selection is bounded");
    AquaSurfaceOptions all =
        AquaSurfaceOptions.resolve(
            "pool", "night", "calm", "slow", "off", "close", true, new Random(87));
    check(all.water >= 0 && all.water <= 2, "randomize all selects a water environment");
    check(all.time >= 0 && all.time <= 2, "randomize all selects a lighting mode");
    check(all.ripples >= 0 && all.ripples <= 2, "randomize all selects ripple density");
    check(all.speed >= 0 && all.speed <= 2, "randomize all selects a speed");
    check(all.shimmer >= 0 && all.shimmer <= 2, "randomize all selects shimmer");
    check(all.view >= 0 && all.view <= 1, "randomize all selects a view");
  }

  private static void verifySafeDefaults() {
    AquaSurfaceOptions defaults =
        AquaSurfaceOptions.resolve(
            "unknown", "unknown", "unknown", "unknown", "unknown", "unknown", false, new Random(1));
    check(
        defaults.water == 0
            && defaults.time == 0
            && defaults.ripples == 1
            && defaults.speed == 1
            && defaults.shimmer == 1
            && defaults.view == 1,
        "invalid saved choices fall back to safe defaults");
    check(AquaSurfaceOptions.waterFor("lagoon") == 1, "lagoon selection maps to its palette");
    check(AquaSurfaceOptions.waterFor("pool") == 2, "pool selection maps to its palette");
    check(AquaSurfaceOptions.timeFor("dusk") == 1, "dusk selection maps to warm light");
    check(AquaSurfaceOptions.timeFor("night") == 2, "night selection maps to dark light");
    check(AquaSurfaceOptions.ripplesFor("calm") == 0, "calm ripple setting maps");
    check(AquaSurfaceOptions.ripplesFor("lively") == 2, "lively ripple setting maps");
    check(AquaSurfaceOptions.speedFor("slow") == 0, "slow movement maps");
    check(AquaSurfaceOptions.speedFor("quick") == 2, "quick movement maps");
    check(AquaSurfaceOptions.shimmerFor("off") == 0, "shimmer can be disabled");
    check(AquaSurfaceOptions.shimmerFor("bright") == 2, "bright shimmer maps");
    check(AquaSurfaceOptions.viewFor("close") == 0, "close view maps");
    check(AquaSurfaceOptions.viewFor("wide") == 1, "wide view maps");
  }

  private static void verifyProviderChoices() {
    String[][] choices = {
      {"water", "ocean", "lagoon", "pool", "random"},
      {"time", "day", "dusk", "night", "random"},
      {"ripples", "calm", "natural", "lively", "random"},
      {"speed", "slow", "natural", "quick", "random"},
      {"shimmer", "off", "soft", "bright", "random"},
      {"view", "close", "wide", "random"},
      {"randomize_all", "true", "false"}
    };
    for (String[] setting : choices) {
      for (int index = 1; index < setting.length; index++) {
        check(
            SettingsValues.isSupported(setting[0], setting[index]),
            "provider accepts listed choice");
      }
    }
    check(!SettingsValues.isSupported("water", "river"), "unknown water rejected");
    check(!SettingsValues.isSupported("time", "sunset"), "unknown light rejected");
    check(!SettingsValues.isSupported("ripples", "storm"), "unknown ripple setting rejected");
    check(!SettingsValues.isSupported("speed", "instant"), "unknown speed rejected");
    check(!SettingsValues.isSupported("shimmer", "flicker"), "unknown shimmer rejected");
    check(!SettingsValues.isSupported("view", "aerial"), "unknown view rejected");
    check(!SettingsValues.isSupported("randomize_all", "yes"), "invalid boolean rejected");
    check(!SettingsValues.isSupported("unknown", "value"), "unknown key rejected");
    check(!SettingsValues.isSupported(null, "ocean"), "null key rejected");
    check(!SettingsValues.isSupported("water", null), "null value rejected");
  }

  private static void verifyWaterMotion() {
    float calmMin = Float.POSITIVE_INFINITY;
    float calmMax = Float.NEGATIVE_INFINITY;
    boolean changedOverTime = false;
    for (int yi = 0; yi <= 20; yi++) {
      for (int xi = 0; xi <= 20; xi++) {
        float x = xi / 20f;
        float y = yi / 20f;
        float calm = WaterSurfaceMath.wave(x, y, 0f, 0);
        float natural = WaterSurfaceMath.wave(x, y, 0f, 1);
        float lively = WaterSurfaceMath.wave(x, y, 0f, 2);
        calmMin = Math.min(calmMin, calm);
        calmMax = Math.max(calmMax, calm);
        if (Math.abs(calm - WaterSurfaceMath.wave(x, y, 2f, 0)) > 0.01f) changedOverTime = true;
        check(natural >= -1.2f && natural <= 1.2f, "natural wave motion stays bounded");
        check(lively >= -1.2f && lively <= 1.2f, "lively wave motion stays bounded");
      }
    }
    check(calmMax - calmMin > 0.5f, "water surface has visible variation");
    check(changedOverTime, "water surface changes as animation time advances");
    check(WaterSurfaceMath.shimmer(0.31f, 0.58f, 0.8f, 0) == 0f, "disabled shimmer remains off");
    float soft = WaterSurfaceMath.shimmer(0.31f, 0.58f, 0.8f, 1);
    float bright = WaterSurfaceMath.shimmer(0.31f, 0.58f, 0.8f, 2);
    check(
        bright >= soft && bright <= 1f,
        "bright shimmer stays brighter without exceeding its bound");
    check(
        Math.abs(WaterSurfaceMath.perspective(0.5f, 0) - 0.85f) < 0.001f,
        "close view uses its shallow perspective");
    check(
        Math.abs(WaterSurfaceMath.perspective(0.5f, 1) - 0.84f) < 0.001f,
        "wide view uses its broader perspective");
  }

  private static void check(boolean result, String message) {
    if (!result) throw new AssertionError(message);
  }
}
