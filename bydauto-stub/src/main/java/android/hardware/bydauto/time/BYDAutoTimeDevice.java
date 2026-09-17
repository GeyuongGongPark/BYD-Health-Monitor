package android.hardware.bydauto.time;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoTimeDevice extends AbsBYDAutoDevice {
    public static final int TIME_FORMAT_24H_OFF = 0;
    public static final int TIME_FORMAT_24H_ON = 1;
    public static final int TIME_YEAR_MIN = 2001;
    public static final int TIME_YEAR_MAX = 2255;
    public static final int TIME_MONTH_MIN = 1;
    public static final int TIME_MONTH_MAX = 12;
    public static final int TIME_DAY_MIN = 1;
    public static final int TIME_DAY_MAX = 31;
    public static final int TIME_HOUR_MIN = 0;
    public static final int TIME_HOUR_MAX = 23;
    public static final int TIME_MINUTE_MIN = 0;
    public static final int TIME_MINUTE_MAX = 59;
    public static final int TIME_SECOND_MIN = 0;
    public static final int TIME_SECOND_MAX = 59;

    public static BYDAutoTimeDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int setDate(int year, int month, int day, int weekday) { throw new RuntimeException("Stub!"); }
    public int setTime(int hour, int minute, int second) { throw new RuntimeException("Stub!"); }
    public int[] getTime() { throw new RuntimeException("Stub!"); }
    public int setTimeFormat(int value) { throw new RuntimeException("Stub!"); }
    public int getTimeFormat() { throw new RuntimeException("Stub!"); }
}
