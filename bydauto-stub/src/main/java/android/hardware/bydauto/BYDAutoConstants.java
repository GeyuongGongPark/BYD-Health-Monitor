package android.hardware.bydauto;

public class BYDAutoConstants {
    // Command result codes
    public static final int BYDAUTO_COMMAND_RESULT_SUCCESS = 0;
    public static final int BYDAUTO_COMMAND_RESULT_FAILED = 0x80000020;
    public static final int BYDAUTO_COMMAND_RESULT_TIMEOUT = 0x80000022;
    public static final int BYDAUTO_COMMAND_RESULT_BUSY = 0x80000021;
    public static final int BYDAUTO_COMMAND_RESULT_INVALID_VALUE = 0x80000023;
    public static final int UNKNOWN_ERROR = Integer.MIN_VALUE;

    // Device type IDs
    public static final int BYDAUTO_DEVICE_AC = 1000;
    public static final int BYDAUTO_DEVICE_BODYWORK = 1001;
    public static final int BYDAUTO_DEVICE_AUDIO = 1002;
    public static final int BYDAUTO_DEVICE_LIGHT = 1004;
    public static final int BYDAUTO_DEVICE_ENERGY = 1006;
    public static final int BYDAUTO_DEVICE_INSTRUMENT = 1007;
    public static final int BYDAUTO_DEVICE_PM2P5 = 1008;
    public static final int BYDAUTO_DEVICE_CHARGING = 1009;
    public static final int BYDAUTO_DEVICE_GEARBOX = 1011;
    public static final int BYDAUTO_DEVICE_ENGINE = 1012;
    public static final int BYDAUTO_DEVICE_SPEED = 1013;
    public static final int BYDAUTO_DEVICE_STATISTIC = 1014;
    public static final int BYDAUTO_DEVICE_TYRE = 1016;
    public static final int BYDAUTO_DEVICE_LOCATION = 1017;
    public static final int BYDAUTO_DEVICE_SETTING = 1023;
    public static final int BYDAUTO_DEVICE_TIME = 1024;
    public static final int BYDAUTO_DEVICE_RADAR = 1025;
    public static final int BYDAUTO_DEVICE_PANORAMA = 1031;
    public static final int BYDAUTO_DEVICE_DOOR_LOCK = 1041;
    public static final int BYDAUTO_DEVICE_SAFETY_BELT = 1042;
    public static final int BYDAUTO_DEVICE_SENSOR = 1043;
    public static final int BYDAUTO_DEVICE_WIPER = 1046;
    public static final int BYDAUTO_DEVICE_REAR_VIEW_MIRROR = 1047;
}
