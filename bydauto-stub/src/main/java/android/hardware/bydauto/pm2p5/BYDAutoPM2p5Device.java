package android.hardware.bydauto.pm2p5;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoPM2p5Device extends AbsBYDAutoDevice {
    public static final int PM2P5_LEVEL_INVALID = 0;
    public static final int PM2P5_LEVEL_EXCELLENT = 1;
    public static final int PM2P5_LEVEL_GOOD = 2;
    public static final int PM2P5_LEVEL_LOW_GRADE = 3;
    public static final int PM2P5_LEVEL_MIDDLE = 4;
    public static final int PM2P5_LEVEL_HEAVY = 5;
    public static final int PM2P5_LEVEL_SERIOUS = 6;

    public static BYDAutoPM2p5Device getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int getPM2p5OnlineState() { throw new RuntimeException("Stub!"); }
    public int[] getPM2p5CheckState() { throw new RuntimeException("Stub!"); }
    public int[] getPM2p5Value() { throw new RuntimeException("Stub!"); }
    public int[] getPM2p5Level() { throw new RuntimeException("Stub!"); }
}
