package android.hardware.bydauto.safetybelt;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoSafetyBeltDevice extends AbsBYDAutoDevice {
    public static final int SAFETY_BELT_AREA_MAIN = 1;
    public static final int SAFETY_BELT_AREA_DEPUTY = 2;
    public static final int SAFETY_BELT_STATE_UNLOCK = 0;
    public static final int SAFETY_BELT_STATE_LOCK = 1;
    public static final int SAFETY_BELT_STATE_INVALID = 2;
    public static final int SAFETY_BELT_PASSENGER_STATE_NOBODY = 0;
    public static final int SAFETY_BELT_PASSENGER_STATE_SOMEBODY = 1;
    public static final int SAFETY_BELT_PASSENGER_STATE_INVALID = 2;

    public static BYDAutoSafetyBeltDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int getSafetyBeltStatus(int area) { throw new RuntimeException("Stub!"); }
    public int getPassengerStatus(int area) { throw new RuntimeException("Stub!"); }
}
