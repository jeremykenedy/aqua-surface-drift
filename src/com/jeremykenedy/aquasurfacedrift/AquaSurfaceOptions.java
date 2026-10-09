package com.jeremykenedy.aquasurfacedrift;

import java.util.Random;

public final class AquaSurfaceOptions {
  public final int water;
  public final int time;
  public final int ripples;
  public final int speed;
  public final int shimmer;
  public final int view;

  private AquaSurfaceOptions(int water, int time, int ripples, int speed, int shimmer, int view) {
    this.water = water;
    this.time = time;
    this.ripples = ripples;
    this.speed = speed;
    this.shimmer = shimmer;
    this.view = view;
  }

  public static AquaSurfaceOptions resolve(
      String water,
      String time,
      String ripples,
      String speed,
      String shimmer,
      String view,
      boolean randomizeAll,
      Random random) {
    String selectedWater = choose(water, randomizeAll, random, "ocean", "lagoon", "pool");
    String selectedTime = choose(time, randomizeAll, random, "day", "dusk", "night");
    String selectedRipples = choose(ripples, randomizeAll, random, "natural", "calm", "lively");
    String selectedSpeed = choose(speed, randomizeAll, random, "natural", "slow", "quick");
    String selectedShimmer = choose(shimmer, randomizeAll, random, "soft", "off", "bright");
    String selectedView = choose(view, randomizeAll, random, "wide", "close");
    return new AquaSurfaceOptions(
        waterFor(selectedWater),
        timeFor(selectedTime),
        ripplesFor(selectedRipples),
        speedFor(selectedSpeed),
        shimmerFor(selectedShimmer),
        viewFor(selectedView));
  }

  private static String choose(
      String selected, boolean randomizeAll, Random random, String... choices) {
    if (randomizeAll || "random".equals(selected)) return choices[random.nextInt(choices.length)];
    for (String choice : choices) if (choice.equals(selected)) return selected;
    return choices[0];
  }

  static int waterFor(String water) {
    if ("lagoon".equals(water)) return 1;
    if ("pool".equals(water)) return 2;
    return 0;
  }

  static int timeFor(String time) {
    if ("dusk".equals(time)) return 1;
    if ("night".equals(time)) return 2;
    return 0;
  }

  static int ripplesFor(String ripples) {
    if ("calm".equals(ripples)) return 0;
    if ("lively".equals(ripples)) return 2;
    return 1;
  }

  static int speedFor(String speed) {
    if ("slow".equals(speed)) return 0;
    if ("quick".equals(speed)) return 2;
    return 1;
  }

  static int shimmerFor(String shimmer) {
    if ("off".equals(shimmer)) return 0;
    if ("bright".equals(shimmer)) return 2;
    return 1;
  }

  static int viewFor(String view) {
    return "close".equals(view) ? 0 : 1;
  }
}
