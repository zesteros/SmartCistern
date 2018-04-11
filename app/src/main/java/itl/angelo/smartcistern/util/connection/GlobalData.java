package itl.angelo.smartcistern.util.connection;

/**
 * Created by Angelo on 24/03/2017.
 */

public class GlobalData {
    private boolean isConnected;
    private static GlobalData instance;
    private float distance;
    private float db;
    private int channel;
    private ConnectionThread thread;
    private String itsWaterPumpOn;
    private boolean modeChanged;
    private boolean waterPumpOn;
    private float volumeAverage;
    private float dumpSpeed;
    private float fillingSpeed;
    private int fillTimes;
    private int dumpTimes;
    private long dumpTime;
    private long fillTime;

    public static synchronized GlobalData getInstance() {
        if (instance == null) instance = new GlobalData();
        return instance;
    }

    public void isConnected(boolean b) {
        this.isConnected = b;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }

    public void setThread(ConnectionThread thread) {
        this.thread = thread;
    }

    public ConnectionThread getThread() {
        return thread;
    }

    public float getDb() {
        return db;
    }

    public void setDb(float db) {
        this.db = db;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String itsWaterPumpOn() {
        return itsWaterPumpOn;
    }

    public void itsWaterPumpOn(String itsWaterPumpOn) {
        this.itsWaterPumpOn = itsWaterPumpOn;
    }

    public boolean isModeChanged() {
        return modeChanged;
    }

    public void isModeChanged(boolean modeChanged) {
        this.modeChanged = modeChanged;
    }

    public boolean isWaterPumpOn() {
        return waterPumpOn;
    }

    public void isWaterPumpOn(boolean b) {
        this.waterPumpOn = b;
    }

    public float getVolumeAverage() {
        return volumeAverage;
    }

    public void setVolumeAverage(float volumeAverage) {
        this.volumeAverage = volumeAverage;
    }

    public void setDumpSpeed(float dumpSpeed) {
        this.dumpSpeed = dumpSpeed;
    }

    public void setFillingSpeed(float fillingSpeed) {
        this.fillingSpeed = fillingSpeed;
    }

    public float getDumpSpeed() {
        return dumpSpeed;
    }

    public float getFillingSpeed() {
        return fillingSpeed;
    }

    public int getFillTimes() {
        return fillTimes;
    }

    public void setFillTimes(int fillTimes) {
        this.fillTimes = fillTimes;
    }

    public int getDumpTimes() {
        return dumpTimes;
    }

    public void setDumpTimes(int dumpTimes) {
        this.dumpTimes = dumpTimes;
    }

    public void setDumpTime(long dumpTime) {
        this.dumpTime = dumpTime;
    }

    public void setFillTime(long fillTime) {
        this.fillTime = fillTime;
    }

    public long getDumpTime() {
        return dumpTime;
    }

    public long getFillTime() {
        return fillTime;
    }
}
