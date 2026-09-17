package android.hardware;

public interface IBYDAutoDevice {
    int getType();
    boolean postEvent(int devType, int evtType, int val, Object data);
    boolean postEvent(int devType, int evtType, double val, Object data);
    boolean postEvent(int devType, int evtType, byte[] val, Object data);
    boolean onPostEvent(IBYDAutoEvent event);
    void registerListener(IBYDAutoListener l);
    void unregisterListener(IBYDAutoListener l);
}
