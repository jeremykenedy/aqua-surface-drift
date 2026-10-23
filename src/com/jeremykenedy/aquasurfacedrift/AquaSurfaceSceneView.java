package com.jeremykenedy.aquasurfacedrift;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.View;
import java.util.Random;

final class AquaSurfaceSceneView extends View {
  private static final long FRAME_DELAY_MS = 40L;
  private static final int WAVE_ROWS = 19;
  private static final int WAVE_POINTS = 37;
  private static final int GLINT_ROWS = 17;
  private static final int GLINT_COLUMNS = 39;

  private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Path wavePath = new Path();
  private final float[] glintSeeds = new float[GLINT_ROWS * GLINT_COLUMNS * 3];
  private AquaSurfaceOptions options;
  private LinearGradient waterGradient;
  private RadialGradient sunlight;
  private long startedAt;
  private boolean running;
  private final boolean showClock;
  private final ClockOverlay clock;

  AquaSurfaceSceneView(Context context) {
    super(context);
    clock = new ClockOverlay(context);
    showClock =
        PreferenceManager.getDefaultSharedPreferences(context).getBoolean("show_clock", false);
    setLayerType(View.LAYER_TYPE_HARDWARE, null);
    loadOptions();
    seedGlints();
  }

  void start() {
    if (!running) {
      running = true;
      startedAt = SystemClock.uptimeMillis();
      postInvalidateOnAnimation();
    }
  }

  void stop() {
    running = false;
    removeCallbacks(invalidator);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (getWidth() <= 0 || getHeight() <= 0) return;
    float time = (SystemClock.uptimeMillis() - startedAt) / 1000f * speedFactor();
    drawWater(canvas, time);
    if (running) {
      drawWaves(canvas, time);
      drawSunlight(canvas, time);
      postDelayed(invalidator, FRAME_DELAY_MS);
    }
    if (showClock) clock.draw(canvas, getWidth(), getHeight(), time);
  }

  @Override
  protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
    super.onSizeChanged(width, height, oldWidth, oldHeight);
    int[] colors = waterColors();
    waterGradient =
        new LinearGradient(0, 0, 0, Math.max(height, 1), colors, null, Shader.TileMode.CLAMP);
    int glint = glintColor();
    sunlight =
        new RadialGradient(
            width * 0.62f,
            height * 0.18f,
            Math.max(width, height) * 0.75f,
            new int[] {withAlpha(glint, options.time == 2 ? 18 : 74), withAlpha(glint, 0)},
            null,
            Shader.TileMode.CLAMP);
  }

  private final Runnable invalidator =
      new Runnable() {
        @Override
        public void run() {
          if (running) invalidate();
        }
      };

  private void loadOptions() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    options =
        AquaSurfaceOptions.resolve(
            preferences.getString("water", "ocean"),
            preferences.getString("time", "day"),
            preferences.getString("ripples", "natural"),
            preferences.getString("speed", "natural"),
            preferences.getString("shimmer", "soft"),
            preferences.getString("view", "wide"),
            preferences.getBoolean("randomize_all", false),
            new Random(System.currentTimeMillis()));
  }

  private void seedGlints() {
    Random random = new Random(0xA91A5L);
    for (int index = 0; index < glintSeeds.length; index++) {
      glintSeeds[index] = random.nextFloat();
    }
  }

  private void drawWater(Canvas canvas, float time) {
    paint.setShader(waterGradient);
    canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    paint.setShader(sunlight);
    float drift = (float) Math.sin(time * 0.09f) * getWidth() * 0.11f;
    canvas.save();
    canvas.translate(drift, 0);
    canvas.drawRect(-drift, 0, getWidth() - drift, getHeight(), paint);
    canvas.restore();
    paint.setShader(null);
  }

  private void drawWaves(Canvas canvas, float time) {
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeCap(Paint.Cap.ROUND);
    float baseWidth = Math.max(1.5f, getHeight() * 0.0018f);
    float amplitude = getHeight() * (options.view == 0 ? 0.012f : 0.018f);
    int waveColor = waveColor();
    for (int row = 0; row < WAVE_ROWS; row++) {
      float rowFraction = row / (float) (WAVE_ROWS - 1);
      float y = getHeight() * (0.035f + rowFraction * rowFraction * 0.95f);
      float perspective = WaterSurfaceMath.perspective(rowFraction, options.view);
      wavePath.reset();
      for (int point = 0; point < WAVE_POINTS; point++) {
        float xFraction = point / (float) (WAVE_POINTS - 1);
        float wave = WaterSurfaceMath.wave(xFraction, rowFraction, time, options.ripples);
        float px = getWidth() * xFraction;
        float py = y + wave * amplitude * perspective;
        if (point == 0 || point % 9 == 0) wavePath.moveTo(px, py);
        else wavePath.lineTo(px, py);
      }
      int alpha = (int) (24 + rowFraction * 38 + options.time * 4);
      paint.setStrokeWidth(baseWidth * (0.44f + rowFraction * 0.76f));
      paint.setColor(withAlpha(waveColor, alpha));
      canvas.drawPath(wavePath, paint);
    }
    paint.setStyle(Paint.Style.FILL);
  }

  private void drawSunlight(Canvas canvas, float time) {
    if (options.shimmer == 0) return;
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeCap(Paint.Cap.ROUND);
    float cellWidth = getWidth() / (float) GLINT_COLUMNS;
    float cellHeight = getHeight() / (float) GLINT_ROWS;
    int color = glintColor();
    for (int row = 0; row < GLINT_ROWS; row++) {
      float yFraction = (row + 0.5f) / GLINT_ROWS;
      for (int column = 0; column < GLINT_COLUMNS; column++) {
        int seedIndex = (row * GLINT_COLUMNS + column) * 3;
        float xFraction = (column + glintSeeds[seedIndex]) / GLINT_COLUMNS;
        float localY = (row + glintSeeds[seedIndex + 1]) / GLINT_ROWS;
        float localTime = time + glintSeeds[seedIndex + 2] * 13f;
        float shimmer = WaterSurfaceMath.shimmer(xFraction, localY, localTime, options.shimmer);
        if (shimmer < 0.12f) continue;
        float perspective = WaterSurfaceMath.perspective(localY, options.view);
        float x = getWidth() * xFraction;
        float y = getHeight() * localY;
        float waveHeight = getHeight() * (options.view == 0 ? 0.012f : 0.018f);
        float length = cellWidth * (0.34f + shimmer * 0.92f);
        float leftFraction = Math.max(0f, xFraction - length / getWidth() * 0.5f);
        float rightFraction = Math.min(1f, xFraction + length / getWidth() * 0.5f);
        float leftWave = WaterSurfaceMath.wave(leftFraction, localY, localTime, options.ripples);
        float rightWave = WaterSurfaceMath.wave(rightFraction, localY, localTime, options.ripples);
        float nightScale = options.time == 2 ? 0.58f : 1f;
        paint.setColor(withAlpha(color, (int) ((68f + shimmer * 176f) * nightScale)));
        paint.setStrokeWidth(Math.max(1.5f, cellHeight * (0.022f + shimmer * 0.032f)));
        canvas.drawLine(
            getWidth() * leftFraction,
            y + leftWave * waveHeight * perspective,
            getWidth() * rightFraction,
            y + rightWave * waveHeight * perspective,
            paint);
      }
    }
    paint.setStyle(Paint.Style.FILL);
  }

  private int[] waterColors() {
    if (options.time == 2) return new int[] {0xff16435a, 0xff0a293c, 0xff061724};
    if (options.time == 1) {
      if (options.water == 1) return new int[] {0xffd58c82, 0xff47768b, 0xff173b55};
      return new int[] {0xffb8797c, 0xff3d6881, 0xff142e46};
    }
    if (options.water == 1) return new int[] {0xff71e6ce, 0xff149c98, 0xff075568};
    if (options.water == 2) return new int[] {0xff80def0, 0xff2098c4, 0xff075075};
    return new int[] {0xff3eb7c4, 0xff117c92, 0xff07394f};
  }

  private int waveColor() {
    if (options.time == 2) return 0xff80cde0;
    if (options.time == 1) return 0xffffc9aa;
    return options.water == 1 ? 0xff9af4d7 : 0xffa7e6f3;
  }

  private int glintColor() {
    if (options.time == 2) return 0xffb0eaff;
    if (options.time == 1) return 0xffffd5af;
    return options.water == 1 ? 0xffe3fff1 : 0xfff5ffff;
  }

  private float speedFactor() {
    if (options.speed == 0) return 0.48f;
    if (options.speed == 2) return 1.72f;
    return 1f;
  }

  private static int withAlpha(int color, int alpha) {
    return (color & 0x00ffffff) | (Math.max(0, Math.min(255, alpha)) << 24);
  }
}
