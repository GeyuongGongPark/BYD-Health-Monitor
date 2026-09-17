package android.hardware.bydauto.light;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoLightDevice extends AbsBYDAutoDevice {
    public static final int LIGHT_OFF = 0;
    public static final int LIGHT_ON = 1;
    public static final int LIGHT_SIDE = 1;
    public static final int LIGHT_LOW_BEAM = 2;
    public static final int LIGHT_HIGH_BEAM = 3;
    public static final int LIGHT_LEFT_TURN_SIGNAL = 4;
    public static final int LIGHT_RIGHT_TURN_SIGNAL = 5;
    public static final int LIGHT_FRONT_FOG = 6;
    public static final int LIGHT_REAR_FOG = 7;
    public static final int LIGHT_FOOT = 8;
    public static final int LIGHT_GROUP_LEFT = 0;
    public static final int LIGHT_GROUP_RIGHT = 1;

    public static BYDAutoLightDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int getLightStatus(int type) { throw new RuntimeException("Stub!"); }
    public int getLightAutoStatus() { throw new RuntimeException("Stub!"); }
    public int getAFSSwitch() { throw new RuntimeException("Stub!"); }
}
