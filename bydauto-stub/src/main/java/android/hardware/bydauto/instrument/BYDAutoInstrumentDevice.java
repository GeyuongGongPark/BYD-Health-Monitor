package android.hardware.bydauto.instrument;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoInstrumentDevice extends AbsBYDAutoDevice {
    // Malfunction type constants
    public static final int MALFUNCTION_INSTRUMENT_DISPLAY = 1;
    public static final int MALFUNCTION_MACHINE_OIL_LOW_PRESSURE = 2;
    public static final int MALFUNCTION_PARKING_BRAKE = 3;
    public static final int MALFUNCTION_CHARGING_SYSTEM = 4;
    public static final int MALFUNCTION_ENGINE = 5;
    public static final int MALFUNCTION_ABS_SYSTEM = 6;
    public static final int MALFUNCTION_ESP = 7;
    public static final int MALFUNCTION_QUICK_AIR_LEAK = 8;
    public static final int MALFUNCTION_HIGH_WATER_TEMPERATURE = 9;
    public static final int MALFUNCTION_ELECTRIC_PARKING_BRAKE = 10;
    public static final int MALFUNCTION_SRS = 11;
    public static final int MALFUNCTION_EPS = 12;
    public static final int MALFUNCTION_TYRE_PRESSURE = 13;
    public static final int MALFUNCTION_SVS = 14;
    public static final int MALFUNCTION_HIGH_MOTOR_TEMPERATURE = 15;
    public static final int MALFUNCTION_BATTERY = 16;
    public static final int MALFUNCTION_HIGH_BATTERY_TEMPERATURE = 17;
    public static final int MALFUNCTION_POWER_SYSTEM = 18;
    public static final int MALFUNCTION_OK = 19;
    public static final int MALFUNCTION_EV = 20;
    public static final int MALFUNCTION_HEV = 21;
    public static final int MALFUNCTION_SMART_KEY = 22;
    public static final int MALFUNCTION_FRONT_BELT = 23;

    // Unit name constants
    public static final int UNIT_SPEED = 1;
    public static final int UNIT_TEMPERATURE = 2;
    public static final int UNIT_PRESSURE = 3;
    public static final int UNIT_FUEL_CONSUMPTION = 4;

    public static BYDAutoInstrumentDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int getMalfunctionInfo(int typeName) { throw new RuntimeException("Stub!"); }
    public int getUnit(int unitName) { throw new RuntimeException("Stub!"); }
    public int setUnit(int unitName, int unitValue) { throw new RuntimeException("Stub!"); }
    public int getMaintenanceInfo(int typeName) { throw new RuntimeException("Stub!"); }
    public int setMaintenanceInfo(int typeName, int infoValue) { throw new RuntimeException("Stub!"); }
    public int sendMusicState(int state) { throw new RuntimeException("Stub!"); }
    public int sendMusicPlaybackProgress(int progress) { throw new RuntimeException("Stub!"); }
    public int sendMusicSource(int source) { throw new RuntimeException("Stub!"); }
    public int getAlarmBuzzleState() { throw new RuntimeException("Stub!"); }
    public double getExternalChargingPower() { throw new RuntimeException("Stub!"); }
    public int sendSimpleGuidanceInfo(int simpleType, int distance) { throw new RuntimeException("Stub!"); }
    public int sendCameraGuidanceInfo(int cameraType, int distance, int state) { throw new RuntimeException("Stub!"); }
    public int sendSafeGuidanceInfo(int safeType, int distance, int state) { throw new RuntimeException("Stub!"); }
    public int sendRestRouteInfo(int h, int m, long mileage) { throw new RuntimeException("Stub!"); }
    public int sendNextPathName(String name) { throw new RuntimeException("Stub!"); }
    public int sendAddressInfo(int target, String address) { throw new RuntimeException("Stub!"); }
    public int sendMusicName(String name) { throw new RuntimeException("Stub!"); }
    public int getNaviDestinationCommand() { throw new RuntimeException("Stub!"); }
    public int sendAutoNaviStatus(int status) { throw new RuntimeException("Stub!"); }
    public int getRoadNameCheckState() { throw new RuntimeException("Stub!"); }
}
