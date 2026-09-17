package android.hardware.bydauto.multimedia;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoMultimediaDevice extends AbsBYDAutoDevice {
    public static final int MULTIMEDIA_TYPE_AM = 0;
    public static final int MULTIMEDIA_TYPE_FM = 1;
    public static final int MULTIMEDIA_TYPE_CD = 2;
    public static final int MULTIMEDIA_TYPE_VCD = 3;
    public static final int MULTIMEDIA_TYPE_DVD = 4;
    public static final int MULTIMEDIA_TYPE_TV = 5;
    public static final int MULTIMEDIA_TYPE_AUX = 7;
    public static final int MULTIMEDIA_TYPE_LOCAL_AUDIO = 8;
    public static final int MULTIMEDIA_TYPE_LOCAL_VIDEO = 9;
    public static final int MULTIMEDIA_TYPE_USB_AUDIO = 10;
    public static final int MULTIMEDIA_TYPE_USB_VIDEO = 11;
    public static final int MULTIMEDIA_TYPE_SD_AUDIO = 12;
    public static final int MULTIMEDIA_TYPE_SD_VIDEO = 13;
    public static final int MULTIMEDIA_TYPE_HD_AUDIO = 14;
    public static final int MULTIMEDIA_TYPE_HD_VIDEO = 15;
    public static final int MULTIMEDIA_TYPE_BT = 16;
    public static final int MULTIMEDIA_TYPE_ROBOT = 17;
    public static final int ACTION_ENTER = 0;
    public static final int ACTION_PLAY = 1;
    public static final int ACTION_PAUSE = 2;
    public static final int ACTION_PLAY_PRE = 3;
    public static final int ACTION_PLAY_NEXT = 4;
    public static final int ACTION_SET_PLAY_PATTERN = 5;
    public static final int ACTION_AUTO_SEARCH = 6;
    public static final int ACTION_PLAY_PRE_FREQ = 7;
    public static final int ACTION_PLAY_NEXT_FREQ = 8;
    public static final int ACTION_CANCEL_RADIO_SEARCH = 9;

    public static BYDAutoMultimediaDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public int getMediaType() { throw new RuntimeException("Stub!"); }
    public int getPlayMode() { throw new RuntimeException("Stub!"); }
    public int getPlayState() { throw new RuntimeException("Stub!"); }
    public MediaInfo getPlayMediaInfo() { throw new RuntimeException("Stub!"); }
    public int controlMedia(int mode, int action, MediaControlParam param) { throw new RuntimeException("Stub!"); }
}
