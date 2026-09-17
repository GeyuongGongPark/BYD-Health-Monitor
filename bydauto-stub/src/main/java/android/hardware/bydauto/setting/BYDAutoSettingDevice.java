package android.hardware.bydauto.setting;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoSettingDevice extends AbsBYDAutoDevice {
    // Language constants
    public static final int SET_LANGUAGE_SIMPLE_CHINESE = 1;
    public static final int SET_LANGUAGE_COMPLEX_CHINESE = 2;
    public static final int SET_LANGUAGE_ENGLISH = 3;
    public static final int SET_LANGUAGE_RUSSIAN = 4;
    public static final int SET_LANGUAGE_ARABIC = 5;

    // Feature constants
    public static final String FEATURE_BACK_DOOR = "back_door";
    public static final String FEATURE_DRIVER_SEAT_HEATING = "driver_seat_heating";
    public static final String FEATURE_DRIVER_SEAT_VENTILATING = "driver_seat_ventilating";
    public static final String FEATURE_PASSENGER_SEAT_HEATING = "passenger_seat_heating";
    public static final String FEATURE_PASSENGER_SEAT_VENTILATING = "passenger_seat_ventilating";
    public static final String FEATURE_FOUR_WHEEL_DRIVE = "four_wheel_drive";
    public static final String FEATURE_INSIDE_LIGHT = "inside_light";
    public static final String FEATURE_INTERIOR_ATMOSPHERE_LAMP = "interior_atmosphere_lamp";
    public static final String FEATURE_OVERSPEED_LOCKING = "overspeed_locking";
    public static final String FEATURE_REARVIEW_MIRROR_FOLLOW_UP = "rearview_mirror_follow_up";

    // Feature support result
    public static final int DEVICE_HAS_THE_FEATURE = 1;
    public static final int DEVICE_NOT_HAS_THE_FEATURE = 0;

    public static BYDAutoSettingDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int setACBTWind(int value) { throw new RuntimeException("Stub!"); }
    public int getACBTWind() { throw new RuntimeException("Stub!"); }
    public int setEnergyFeedback(int value) { throw new RuntimeException("Stub!"); }
    public int getEnergyFeedback() { throw new RuntimeException("Stub!"); }
    public int setSOCTarget(int value) { throw new RuntimeException("Stub!"); }
    public int getSOCTarget() { throw new RuntimeException("Stub!"); }
    public int setSteerAssis(int value) { throw new RuntimeException("Stub!"); }
    public int getSteerAssis() { throw new RuntimeException("Stub!"); }
    public int setPM25Power(int value) { throw new RuntimeException("Stub!"); }
    public int getPM25Power() { throw new RuntimeException("Stub!"); }
    public int setBackHomeLightDelayValue(int value) { throw new RuntimeException("Stub!"); }
    public int getBackHomeLightDelayValue() { throw new RuntimeException("Stub!"); }
    public int setLockOff(int value) { throw new RuntimeException("Stub!"); }
    public int getLockOff() { throw new RuntimeException("Stub!"); }
    public int setOverspeedLock(int value) { throw new RuntimeException("Stub!"); }
    public int getOverspeedLock() { throw new RuntimeException("Stub!"); }
    public int setLanguage(int value) { throw new RuntimeException("Stub!"); }
    public int getLanguage() { throw new RuntimeException("Stub!"); }
    public int hasFeature(String feature) { throw new RuntimeException("Stub!"); }
    public int getEngineOilLevel() { throw new RuntimeException("Stub!"); }
    public int setBackDoorOpenedHeight(int height) { throw new RuntimeException("Stub!"); }
    public int getBackDoorOpenedHeight() { throw new RuntimeException("Stub!"); }
}
