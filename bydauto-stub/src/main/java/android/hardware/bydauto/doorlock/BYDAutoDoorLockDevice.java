package android.hardware.bydauto.doorlock;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoDoorLockDevice extends AbsBYDAutoDevice {
    public static final int DOOR_LOCK_AREA_LEFT_FRONT = 1;
    public static final int DOOR_LOCK_AREA_LEFT_REAR = 2;
    public static final int DOOR_LOCK_AREA_RIGHT_FRONT = 3;
    public static final int DOOR_LOCK_AREA_RIGHT_REAR = 4;
    public static final int DOOR_LOCK_AREA_BACK = 5;
    public static final int DOOR_LOCK_AREA_CHILDLOCK_LEFT = 6;
    public static final int DOOR_LOCK_AREA_CHILDLOCK_RIGHT = 7;
    public static final int DOOR_LOCK_STATE_INVALID = 0;
    public static final int DOOR_LOCK_STATE_UNLOCK = 1;
    public static final int DOOR_LOCK_STATE_LOCK = 2;

    public static BYDAutoDoorLockDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int getDoorLockStatus(int area) { throw new RuntimeException("Stub!"); }
}
