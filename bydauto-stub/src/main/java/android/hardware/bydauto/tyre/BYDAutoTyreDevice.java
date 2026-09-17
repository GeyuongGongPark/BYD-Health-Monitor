package android.hardware.bydauto.tyre;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoTyreDevice extends AbsBYDAutoDevice {
    // Tyre area constants
    public static final int TYRE_COMMAND_AREA_LEFT_FRONT = 1;
    public static final int TYRE_COMMAND_AREA_RIGHT_FRONT = 2;
    public static final int TYRE_COMMAND_AREA_LEFT_REAR = 3;
    public static final int TYRE_COMMAND_AREA_RIGHT_REAR = 4;

    // System state constants
    public static final int TYRE_SYSTEM_STATE_NORMAL = 0;
    public static final int TYRE_SYSTEM_STATE_SELF_CHECKING = 1;
    public static final int TYRE_SYSTEM_STATE_SIGNAL_ANOMAL = 2;
    public static final int TYRE_SYSTEM_STATE_BREAKDOWN = 3;
    public static final int TYRE_SYSTEM_STATE_MASKED = 4;

    // Pressure state constants
    public static final int TYRE_PRESSURE_STATE_NORMAL = 0;
    public static final int TYRE_PRESSURE_STATE_OVERPRESSURE = 1;
    public static final int TYRE_PRESSURE_STATE_UNDERPRESSURE = 2;

    // Temperature state constants
    public static final int TYRE_TEMPERATURE_STATE_NORMAL = 0;
    public static final int TYRE_TEMPERATURE_STATE_SUPER_HIGH = 1;
    public static final int TYRE_TEMPERATURE_STATE_HIGH = 2;
    public static final int TYRE_TEMPERATURE_STATE_SLEEP = 3;

    // Air leak state constants
    public static final int TYRE_AIR_LEAK_STATE_NORMAL = 0;
    public static final int TYRE_AIR_LEAK_STATE_QUICK = 1;
    public static final int TYRE_AIR_LEAK_STATE_SLOW = 2;

    public static BYDAutoTyreDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int getTyreSystemState() { throw new RuntimeException("Stub!"); }
    public int getTyreTemperatureState() { throw new RuntimeException("Stub!"); }
    public int getTyreBatteryState() { throw new RuntimeException("Stub!"); }
    public int getTyreAirLeakState(int area) { throw new RuntimeException("Stub!"); }
    public int getTyreSignalState(int area) { throw new RuntimeException("Stub!"); }
    public int getTyrePressureState(int area) { throw new RuntimeException("Stub!"); }
    public int getTyrePressureValue(int area) { throw new RuntimeException("Stub!"); }
}
