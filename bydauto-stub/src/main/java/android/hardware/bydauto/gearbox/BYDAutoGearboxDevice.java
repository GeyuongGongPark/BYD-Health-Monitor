package android.hardware.bydauto.gearbox;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoGearboxDevice extends AbsBYDAutoDevice {
    // Gearbox type constants
    public static final int GEARBOX_TYPE_MT = 0;
    public static final int GEARBOX_TYPE_AT = 2;
    public static final int GEARBOX_TYPE_CVT = 3;
    public static final int GEARBOX_TYPE_DCT = 4;

    // Auto mode constants
    public static final int GEARBOX_AUTO_MODE_P = 1;
    public static final int GEARBOX_AUTO_MODE_R = 2;
    public static final int GEARBOX_AUTO_MODE_N = 3;
    public static final int GEARBOX_AUTO_MODE_D = 4;
    public static final int GEARBOX_AUTO_MODE_M = 5;
    public static final int GEARBOX_AUTO_MODE_S = 6;

    public static BYDAutoGearboxDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int getGearboxType() { throw new RuntimeException("Stub!"); }
    public int getGearboxAutoModeType() { throw new RuntimeException("Stub!"); }
    public int getGearboxManualModeLevel() { throw new RuntimeException("Stub!"); }
    public String getGearboxCode() { throw new RuntimeException("Stub!"); }
    public int getBrakeFluidLevel() { throw new RuntimeException("Stub!"); }
    public int getParkBrakeSwitch() { throw new RuntimeException("Stub!"); }
    public int getBrakePedalState() { throw new RuntimeException("Stub!"); }
}
