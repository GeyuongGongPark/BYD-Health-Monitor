package android.hardware.bydauto.radar;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoRadarDevice extends AbsBYDAutoDevice {
    public static final int RADAR_AREA_LEFT_FRONT = 1;
    public static final int RADAR_AREA_RIGHT_FRONT = 2;
    public static final int RADAR_AREA_LEFT_REAR = 3;
    public static final int RADAR_AREA_RIGHT_REAR = 4;
    public static final int RADAR_AREA_LEFT = 5;
    public static final int RADAR_AREA_RIGHT = 6;
    public static final int RADAR_AREA_FRONT_LEFT_MID = 7;
    public static final int RADAR_AREA_FRONT_RIGHT_MID = 8;
    public static final int RADAR_PROBE_STATE_ABNORMAL = 0;
    public static final int RADAR_PROBE_STATE_SAFE = 1;
    public static final int RADAR_PROBE_STATE_GREEN = 2;
    public static final int RADAR_PROBE_STATE_YELLOW = 3;
    public static final int RADAR_PROBE_STATE_RED = 4;

    public static BYDAutoRadarDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int getRadarProbeState(int area) { throw new RuntimeException("Stub!"); }
    public int[] getAllRadarProbeStates() { throw new RuntimeException("Stub!"); }
    public int getReverseRadarSwitchState() { throw new RuntimeException("Stub!"); }
}
