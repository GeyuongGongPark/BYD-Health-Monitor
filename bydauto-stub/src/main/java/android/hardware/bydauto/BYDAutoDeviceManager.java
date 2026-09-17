package android.hardware.bydauto;

import android.content.Context;
import android.hardware.IBYDAutoDevice;

public abstract class BYDAutoDeviceManager {
    public static BYDAutoDeviceManager getInstance(Context con) { throw new RuntimeException("Stub!"); }
    public abstract void addDevice(IBYDAutoDevice device);
    public abstract void removeDevice(IBYDAutoDevice device);
    public int enableDevice(IBYDAutoDevice device) { throw new RuntimeException("Stub!"); }
    public int disableDevice(IBYDAutoDevice device) { throw new RuntimeException("Stub!"); }
    public int setInt(int device, int event, int value) { throw new RuntimeException("Stub!"); }
    public int getInt(int device, int event) { throw new RuntimeException("Stub!"); }
    public int setDouble(int device, int event, double value) { throw new RuntimeException("Stub!"); }
    public double getDouble(int device, int event) { throw new RuntimeException("Stub!"); }
    public int setIntArray(int device, int[] event, int[] value) { throw new RuntimeException("Stub!"); }
    public int[] getIntArray(int device, int[] event) { throw new RuntimeException("Stub!"); }
    public byte[] getBuffer(int device, int event) { throw new RuntimeException("Stub!"); }
    public int setDoubleArray(int device, int[] event, double[] value) { throw new RuntimeException("Stub!"); }
    public double[] getDoubleArray(int device, int[] event) { throw new RuntimeException("Stub!"); }
}
