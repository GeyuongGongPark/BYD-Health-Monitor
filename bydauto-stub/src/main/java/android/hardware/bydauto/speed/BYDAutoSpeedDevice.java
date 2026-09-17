package android.hardware.bydauto.speed;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoSpeedDevice extends AbsBYDAutoDevice {
    public static BYDAutoSpeedDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public double getCurrentSpeed() { throw new RuntimeException("Stub!"); }
    public int getAccelerateDeepness() { throw new RuntimeException("Stub!"); }
    public int getBrakeDeepness() { throw new RuntimeException("Stub!"); }
}
