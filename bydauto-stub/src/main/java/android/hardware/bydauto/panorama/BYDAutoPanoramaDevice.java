package android.hardware.bydauto.panorama;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoPanoramaDevice extends AbsBYDAutoDevice {
    public static final int DISPLAY_MODE_PANORAMA = 0;
    public static final int DISPLAY_MODE_FULL_SCREEN = 1;
    public static final int DISPLAY_MODE_WIDGET = 3;
    public static final int DISPLAY_MODE_RF_REVERSE = 4;
    public static final int DISPLAY_MODE_REVERSE = 5;
    public static final int PANORAMA_OUTPUT_OFF = 1;
    public static final int PANORAMA_OUTPUT_FRONT = 2;
    public static final int PANORAMA_OUTPUT_REAR = 3;
    public static final int PANORAMA_OUTPUT_LEFT = 4;
    public static final int PANORAMA_OUTPUT_RIGHT = 5;
    public static final int PANORAMA_OUTPUT_COMPOSE = 6;

    public static BYDAutoPanoramaDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int getPanoWorkState() { throw new RuntimeException("Stub!"); }
    public int getPanoOutputState() { throw new RuntimeException("Stub!"); }
    public int getPanoOutputSignal() { throw new RuntimeException("Stub!"); }
    public int getBackLineConfig() { throw new RuntimeException("Stub!"); }
    public int getPanoramaOnlineState() { throw new RuntimeException("Stub!"); }
    public int getPanoRotation() { throw new RuntimeException("Stub!"); }
    public int getDisplayMode() { throw new RuntimeException("Stub!"); }
}
