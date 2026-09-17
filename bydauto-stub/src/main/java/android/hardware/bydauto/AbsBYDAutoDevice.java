package android.hardware.bydauto;

import android.hardware.IBYDAutoDevice;
import android.hardware.IBYDAutoEvent;
import android.hardware.IBYDAutoListener;

public abstract class AbsBYDAutoDevice implements IBYDAutoDevice {
    public int set(int device, int event, int value) { throw new RuntimeException("Stub!"); }
    public int set(int device, int event, double value) { throw new RuntimeException("Stub!"); }
    public int set(int device, int event, byte[] value) { throw new RuntimeException("Stub!"); }
    public int set(int device, int[] event, int[] params) { throw new RuntimeException("Stub!"); }
    public int set(int device, int[] event, double[] params) { throw new RuntimeException("Stub!"); }
    public int get(int device, int event) { throw new RuntimeException("Stub!"); }
    public int[] getIntArray(int device, int[] event) { throw new RuntimeException("Stub!"); }
    public byte[] getBuffer(int device, int event) { throw new RuntimeException("Stub!"); }
    public double getDouble(int device, int event) { throw new RuntimeException("Stub!"); }
    public double[] getDoubleArray(int device, int[] event) { throw new RuntimeException("Stub!"); }

    @Override public int getType() { throw new RuntimeException("Stub!"); }
    @Override public boolean postEvent(int devType, int evtType, int val, Object data) { throw new RuntimeException("Stub!"); }
    @Override public boolean postEvent(int devType, int evtType, double val, Object data) { throw new RuntimeException("Stub!"); }
    @Override public boolean postEvent(int devType, int evtType, byte[] val, Object data) { throw new RuntimeException("Stub!"); }
    @Override public boolean onPostEvent(IBYDAutoEvent event) { throw new RuntimeException("Stub!"); }
    @Override public void registerListener(IBYDAutoListener l) { throw new RuntimeException("Stub!"); }
    @Override public void unregisterListener(IBYDAutoListener l) { throw new RuntimeException("Stub!"); }
}
