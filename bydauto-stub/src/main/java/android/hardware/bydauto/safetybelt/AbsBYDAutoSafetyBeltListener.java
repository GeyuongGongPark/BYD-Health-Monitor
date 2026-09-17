package android.hardware.bydauto.safetybelt;

import android.hardware.IBYDAutoEvent;
import android.hardware.IBYDAutoListener;

public abstract class AbsBYDAutoSafetyBeltListener implements IBYDAutoListener {
    @Override public void onDataChanged(IBYDAutoEvent event) { }
}
