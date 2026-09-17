package android.hardware.bydauto.speed;

import android.hardware.IBYDAutoEvent;
import android.hardware.IBYDAutoListener;

public abstract class AbsBYDAutoSpeedListener implements IBYDAutoListener {
    @Override public void onDataChanged(IBYDAutoEvent event) { }
}
