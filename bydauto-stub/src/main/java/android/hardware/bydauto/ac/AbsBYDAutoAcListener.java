package android.hardware.bydauto.ac;

import android.hardware.IBYDAutoEvent;
import android.hardware.IBYDAutoListener;

public abstract class AbsBYDAutoAcListener implements IBYDAutoListener {
    public void onAcStarted() { }
    public void onAcStoped() { }
    public void onTemperatureChanged(int area, int value) { }
    @Override public void onDataChanged(IBYDAutoEvent event) { }
}
