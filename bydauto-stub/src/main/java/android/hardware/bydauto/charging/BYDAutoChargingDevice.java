package android.hardware.bydauto.charging;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoChargingDevice extends AbsBYDAutoDevice {
    // Charging type constants
    public static final int CHARGING_TYPE_DEFAULT = 1;
    public static final int CHARGING_TYPE_AC = 2;
    public static final int CHARGING_TYPE_VTOG = 3;
    public static final int CHARGING_TYPE_GB_DC = 4;
    public static final int CHARGING_TYPE_GB_NON_DC = 5;

    // Charging gun state constants
    public static final int CHARGING_GUN_STATE_CONNECTED_NONE = 1;
    public static final int CHARGING_GUN_STATE_CONNECTED_AC = 2;
    public static final int CHARGING_GUN_STATE_CONNECTED_DC = 3;
    public static final int CHARGING_GUN_STATE_CONNECTED_AC_DC = 4;
    public static final int CHARGING_GUN_STATE_CONNECTED_VTOL = 5;

    public static BYDAutoChargingDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int getChargerFaultState() { throw new RuntimeException("Stub!"); }
    public int getChargerWorkState() { throw new RuntimeException("Stub!"); }
    public double getChargingCapacity() { throw new RuntimeException("Stub!"); }
    public int getChargingType() { throw new RuntimeException("Stub!"); }
    public int[] getChargingRestTime() { throw new RuntimeException("Stub!"); }
    public int getChargingCapState(int type) { throw new RuntimeException("Stub!"); }
    public int getChargingPortLockRebackState() { throw new RuntimeException("Stub!"); }
    public int getDischargeRequestState() { throw new RuntimeException("Stub!"); }
    public int getChargerState() { throw new RuntimeException("Stub!"); }
    public int getChargingGunState() { throw new RuntimeException("Stub!"); }
    public double getChargingPower() { throw new RuntimeException("Stub!"); }
    public int getBatteryManagementDeviceState() { throw new RuntimeException("Stub!"); }
    public int getChargingScheduleEnableState() { throw new RuntimeException("Stub!"); }
    public int getChargingScheduleState() { throw new RuntimeException("Stub!"); }
    public int getChargingGunNotInsertedState() { throw new RuntimeException("Stub!"); }
    public int[] getChargingScheduleTime() { throw new RuntimeException("Stub!"); }
}
