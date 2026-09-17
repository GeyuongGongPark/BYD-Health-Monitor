package android.hardware.bydauto.statistic;

import android.hardware.IBYDAutoEvent;
import android.hardware.IBYDAutoListener;

public abstract class AbsBYDAutoStatisticListener implements IBYDAutoListener {
    @Override public void onDataChanged(IBYDAutoEvent event) { }
}
