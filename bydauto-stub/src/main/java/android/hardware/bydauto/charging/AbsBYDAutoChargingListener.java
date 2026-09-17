package android.hardware.bydauto.charging;

import android.hardware.IBYDAutoEvent;
import android.hardware.IBYDAutoListener;

public abstract class AbsBYDAutoChargingListener implements IBYDAutoListener {
    @Override public void onDataChanged(IBYDAutoEvent event) { }
}
