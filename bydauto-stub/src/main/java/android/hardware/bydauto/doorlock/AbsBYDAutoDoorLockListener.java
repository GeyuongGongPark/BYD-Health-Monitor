package android.hardware.bydauto.doorlock;

import android.hardware.IBYDAutoEvent;
import android.hardware.IBYDAutoListener;

public abstract class AbsBYDAutoDoorLockListener implements IBYDAutoListener {
    @Override public void onDataChanged(IBYDAutoEvent event) { }
}
