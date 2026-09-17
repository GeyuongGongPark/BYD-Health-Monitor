package android.hardware.bydauto.ac;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoAcDevice extends AbsBYDAutoDevice {
    // Power state constants
    public static final int AC_POWER_OFF = 0;
    public static final int AC_POWER_ON = 1;

    // Control mode constants
    public static final int AC_CTRLMODE_AUTO = 0;
    public static final int AC_CTRLMODE_MANUAL = 1;

    // Cycle mode constants
    public static final int AC_CYCLEMODE_OUTLOOP = 0;
    public static final int AC_CYCLEMODE_INLOOP = 1;

    // Temperature unit constants
    public static final int AC_TEMPERATURE_UNIT_OF = 0;
    public static final int AC_TEMPERATURE_UNIT_OC = 1;

    // Wind level constants
    public static final int AC_WINDLEVEL_0 = 0;
    public static final int AC_WINDLEVEL_1 = 1;
    public static final int AC_WINDLEVEL_2 = 2;
    public static final int AC_WINDLEVEL_3 = 3;
    public static final int AC_WINDLEVEL_4 = 4;
    public static final int AC_WINDLEVEL_5 = 5;
    public static final int AC_WINDLEVEL_6 = 6;
    public static final int AC_WINDLEVEL_7 = 7;

    // Wind mode constants
    public static final int AC_WINDMODE_FACE = 1;
    public static final int AC_WINDMODE_FOOT = 3;
    public static final int AC_WINDMODE_DEFROST = 5;

    // Temperature range constants
    public static final int AC_TEMP_IN_CELSIUS_MIN = 17;
    public static final int AC_TEMP_IN_CELSIUS_MAX = 33;
    public static final int AC_TEMP_OUT_CELSIUS_MIN = -40;
    public static final int AC_TEMP_OUT_CELSIUS_MAX = 50;

    // Defrost area constants
    public static final int AC_DEFROST_AREA_FRONT = 1;
    public static final int AC_DEFROST_AREA_REAR = 2;

    // Control source constants
    public static final int AC_CTRL_SOURCE_UI_KEY = 0;
    public static final int AC_CTRL_SOURCE_VOICE = 1;

    // Command result
    public static final int AC_COMMAND_SUCCESS = 0;

    public static BYDAutoAcDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int start(int setSource) { throw new RuntimeException("Stub!"); }
    public int stop(int setSource) { throw new RuntimeException("Stub!"); }
    public int getAcStartState() { throw new RuntimeException("Stub!"); }
    public int startRearAc(int setSource) { throw new RuntimeException("Stub!"); }
    public int stopRearAc(int setSource) { throw new RuntimeException("Stub!"); }
    public int getRearAcStartState() { throw new RuntimeException("Stub!"); }
    public int setAcControlMode(int setSource, int mode) { throw new RuntimeException("Stub!"); }
    public int getAcControlMode() { throw new RuntimeException("Stub!"); }
    public int setAcCycleMode(int setSource, int mode) { throw new RuntimeException("Stub!"); }
    public int getAcCycleMode() { throw new RuntimeException("Stub!"); }
    public int setAcVentilationState(int setSource, int state) { throw new RuntimeException("Stub!"); }
    public int getAcVentilationState() { throw new RuntimeException("Stub!"); }
    public int setAcTemperatureControlMode(int setSource, int mode) { throw new RuntimeException("Stub!"); }
    public int getAcTemperatureControlMode() { throw new RuntimeException("Stub!"); }
    public int setAcDefrostState(int setSource, int area, int state) { throw new RuntimeException("Stub!"); }
    public int getAcDefrostState(int area) { throw new RuntimeException("Stub!"); }
    public int getAcCompressorManualSign() { throw new RuntimeException("Stub!"); }
    public int getAcCompressorMode() { throw new RuntimeException("Stub!"); }
    public int getAcWindModeManualSign() { throw new RuntimeException("Stub!"); }
    public int setAcWindMode(int setSource, int mode) { throw new RuntimeException("Stub!"); }
    public int getAcWindMode() { throw new RuntimeException("Stub!"); }
    public int getAcWindLevelManualSign() { throw new RuntimeException("Stub!"); }
    public int setAcWindLevel(int setSource, int level) { throw new RuntimeException("Stub!"); }
    public int getAcWindLevel() { throw new RuntimeException("Stub!"); }
    public int getTemperatureUnit() { throw new RuntimeException("Stub!"); }
    public int setAcTemperature(int type, int value, int tempSource, int unit) { throw new RuntimeException("Stub!"); }
    public int getTemprature(int area) { throw new RuntimeException("Stub!"); }
}
