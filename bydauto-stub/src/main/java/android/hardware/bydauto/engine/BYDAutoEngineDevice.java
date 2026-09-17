package android.hardware.bydauto.engine;

import android.content.Context;
import android.hardware.bydauto.AbsBYDAutoDevice;

public class BYDAutoEngineDevice extends AbsBYDAutoDevice {
    public static final String ENGINE_TYPE1 = "371QA";
    public static final String ENGINE_TYPE2 = "473QB";
    public static final String ENGINE_TYPE5 = "476ZQA";
    public static final String ENGINE_TYPE6 = "483QA";
    public static final String ENGINE_TYPE10 = "488QA";
    public static final String ENGINE_TYPE15 = "471ZQA";

    public static BYDAutoEngineDevice getInstance(Context context) { throw new RuntimeException("Stub!"); }

    public double getEngineDisplacement() { throw new RuntimeException("Stub!"); }
    public String getEngineCode() { throw new RuntimeException("Stub!"); }
    public int getEnginePower() { throw new RuntimeException("Stub!"); }
    public int getEngineSpeed() { throw new RuntimeException("Stub!"); }
    public int getEngineCoolantLevel() { throw new RuntimeException("Stub!"); }
    public int getOilLevel() { throw new RuntimeException("Stub!"); }
}
