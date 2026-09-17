package android.hardware.bydauto.sensor;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoSensorDevice extends AbsBYDAutoDevice {
    public static final int LIGHT_INTENSITY_LEVEL1 = 1;
    public static final int LIGHT_INTENSITY_LEVEL2 = 2;
    public static final int LIGHT_INTENSITY_LEVEL3 = 3;
    public static final int LIGHT_INTENSITY_LEVEL4 = 4;
    public static final int LIGHT_INTENSITY_LEVEL5 = 5;

    public static BYDAutoSensorDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int getLightIntensity() { throw new RuntimeException("Stub!"); }
}
