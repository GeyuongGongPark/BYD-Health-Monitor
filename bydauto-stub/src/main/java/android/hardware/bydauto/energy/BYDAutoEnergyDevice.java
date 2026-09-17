package android.hardware.bydauto.energy;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoEnergyDevice extends AbsBYDAutoDevice {
    // Energy mode constants
    public static final int ENERGY_MODE_STOP = 0;
    public static final int ENERGY_MODE_EV = 1;
    public static final int ENERGY_MODE_FORCE_EV = 2;
    public static final int ENERGY_MODE_HEV = 3;
    public static final int ENERGY_MODE_FUEL = 4;
    public static final int ENERGY_MODE_KEEP = 5;

    // Operation mode constants
    public static final int ENERGY_OPERATION_ECONOMY = 1;
    public static final int ENERGY_OPERATION_SPORT = 2;

    // Road surface mode constants
    public static final int ENERGY_ROAD_SURFACE_COMMON = 1;
    public static final int ENERGY_ROAD_SURFACE_SNOW = 2;
    public static final int ENERGY_ROAD_SURFACE_MUDDY = 3;
    public static final int ENERGY_ROAD_SURFACE_SAND = 4;

    public static BYDAutoEnergyDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int getEnergyMode() { throw new RuntimeException("Stub!"); }
    public int getOperationMode() { throw new RuntimeException("Stub!"); }
    public int getPowerGenerationState() { throw new RuntimeException("Stub!"); }
    public int getPowerGenerationValue() { throw new RuntimeException("Stub!"); }
    public int getRoadSurfaceMode() { throw new RuntimeException("Stub!"); }
}
