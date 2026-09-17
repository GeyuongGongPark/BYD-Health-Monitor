package android.hardware.bydauto.bodywork;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoBodyworkDevice extends AbsBYDAutoDevice {
    // Window area constants
    public static final int BODYWORK_CMD_WINDOW_LEFT_FRONT = 1;
    public static final int BODYWORK_CMD_WINDOW_RIGHT_FRONT = 2;
    public static final int BODYWORK_CMD_WINDOW_LEFT_REAR = 3;
    public static final int BODYWORK_CMD_WINDOW_RIGHT_REAR = 4;

    // Door area constants
    public static final int BODYWORK_CMD_DOOR_LEFT_FRONT = 1;
    public static final int BODYWORK_CMD_DOOR_RIGHT_FRONT = 2;
    public static final int BODYWORK_CMD_DOOR_LEFT_REAR = 3;
    public static final int BODYWORK_CMD_DOOR_RIGHT_REAR = 4;
    public static final int BODYWORK_CMD_DOOR_HOOD = 5;
    public static final int BODYWORK_CMD_DOOR_LUGGAGE_DOOR = 6;

    // Power level constants
    public static final int POWER_LEVEL_OFF = 0;
    public static final int POWER_LEVEL_ACC = 1;
    public static final int POWER_LEVEL_ON = 2;

    // Battery voltage level constants
    public static final int BATTERY_VOLTAGE_LOW = 0;
    public static final int BATTERY_VOLTAGE_NORMAL = 1;

    public static BYDAutoBodyworkDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int getWindowState(int area) { throw new RuntimeException("Stub!"); }
    public int getDoorState(int area) { throw new RuntimeException("Stub!"); }
    public int getAutoSystemState() { throw new RuntimeException("Stub!"); }
    public double getSteeringWheelValue(int type) { throw new RuntimeException("Stub!"); }
    public int getPowerLevel() { throw new RuntimeException("Stub!"); }
    public int getBatteryVoltageLevel() { throw new RuntimeException("Stub!"); }
    public String getAutoVIN() { throw new RuntimeException("Stub!"); }
    public int getMoonRoofConfig() { throw new RuntimeException("Stub!"); }
    public int getFuelElecLowPower() { throw new RuntimeException("Stub!"); }
    public int getAlarmState() { throw new RuntimeException("Stub!"); }
    public int getWindowOpenPercent(int area) { throw new RuntimeException("Stub!"); }
    public int getAutoModelName() { throw new RuntimeException("Stub!"); }
    public int getBatteryCapacity() { throw new RuntimeException("Stub!"); }
}
