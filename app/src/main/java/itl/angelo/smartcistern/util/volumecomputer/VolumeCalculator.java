package itl.angelo.smartcistern.util.volumecomputer;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import itl.angelo.smartcistern.R;
import itl.angelo.smartcistern.activities.MainActivity;
import itl.angelo.smartcistern.util.connection.GlobalData;
import itl.angelo.smartcistern.util.ui.UserInteraction;

/**
 * Created by Angelo on 21/03/2017.
 * <p>
 * Class for calculate volume of cistern or another container
 * <p>
 * first set type, if the container is a cylinder:
 * 1° Set mRadio and mDynamicHeight
 * 2° Get the desired value (vol or liter)
 * <p>
 * if is a cube:
 * 1° Set the mEdge size
 * 2° Get the desired value (vol or liter)
 * <p>
 * if is a cuboid
 * 1° Set mWidth, mLength and mDynamicHeight
 * 2° Get the desired value (vol or liter)
 */

public class VolumeCalculator {

    private Context mContext;
    public static final String ERROR_MARGIN_KEY = "error_margin_pref_key";
    public static final String FIGURE_TYPE_KEY = "figure_type_key";
    public static final String RADIO_PREF_KEY = "radio_pref";
    public static final String STATIC_HEIGHT_PREF_KEY = "static_height_pref";
    public static final String FULL_HEIGHT_PREF_KEY = "full_height_pref";
    public static final String EMPTY_HEIGHT_PREF_KEY = "empty_height_pref";
    public static final String EDGE_PREF_KEY = "edge_pref";
    public static final String WIDTH_PREF_KEY = "width_pref_key";
    public static final String LENGTH_PREF_KEY = "length_pref_key";
    public static final String ERROR_MARGIN_DEFAULT = "2";
    public static final String EDGE_PREF_DEFAULT = "5";
    public static final String WIDTH_PREF_DEFAULT = "5";
    public static final String LENGTH_PREF_DEFAULT = "5";
    public static final String FIGURE_TYPE_DEFAULT = "0";
    public static final String RADIO_PREF_DEFAULT = "5";
    public static final String STATIC_HEIGHT_PREF_DEFAULT = "6.5";
    public static final String FULL_HEIGHT_PREF_DEFAULT = "4.5";
    public static final String EMPTY_HEIGHT_PREF_DEFAULT = "9";
    public static final String MILILITERS_LABEL = " ml";
    public static final String CENTIMETERS_BY_SECOND_LABEL = " cm/s";

    private static final int POWER_3 = 3;
    private static final int POWER_2 = 2;
    private static final int VOLUME_TO_LITERS_FACTOR = 1000;
    private FigureType mType;
    private float mRadio;
    private float mDynamicHeight;
    private float mHeight;
    private float mHeightWhenIsFull;
    private float mEdge;
    private float mLength;
    private float mWidth;
    private float mVolumeTotal;
    private float mHeightWhenIsEmpty;
    private float mSensorErrorMargin;
    private SharedPreferences mPrefs;
    private UserInteraction mUi;
    private boolean mShowNotification;

    /**
     * @param context the context of the app
     */
    public VolumeCalculator(Context context) {
        this.mContext = context;
        getPreferenceData();
        mUi = new UserInteraction(context);
    }

    public void getPreferenceData() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        setSensorErrorMargin(Float.parseFloat(mPrefs.getString(ERROR_MARGIN_KEY, ERROR_MARGIN_DEFAULT)));
        setType(Integer.parseInt(mPrefs.getString(FIGURE_TYPE_KEY, FIGURE_TYPE_DEFAULT)));
        setRadio(Float.parseFloat(mPrefs.getString(RADIO_PREF_KEY, RADIO_PREF_DEFAULT)));
        setEdge(Float.parseFloat(mPrefs.getString(EDGE_PREF_KEY, EDGE_PREF_DEFAULT)));
        setWidth(Float.parseFloat(mPrefs.getString(WIDTH_PREF_KEY, WIDTH_PREF_DEFAULT)));
        setLength(Float.parseFloat(mPrefs.getString(LENGTH_PREF_KEY, LENGTH_PREF_DEFAULT)));
        setStaticHeight(Float.parseFloat(mPrefs.getString(STATIC_HEIGHT_PREF_KEY, STATIC_HEIGHT_PREF_DEFAULT)));
        setHeightWhenIsFull(Float.parseFloat(mPrefs.getString(FULL_HEIGHT_PREF_KEY, FULL_HEIGHT_PREF_DEFAULT)));
        setHeightWhenIsEmpty(Float.parseFloat(mPrefs.getString(EMPTY_HEIGHT_PREF_KEY, EDGE_PREF_DEFAULT)));
        setShowNotification(mPrefs.getBoolean(mContext.getString(R.string.pref_notification_key), false));
    }

    /**
     * @return type of figure
     */
    public FigureType getType() {
        return mType;
    }

    /**
     * @param type the type of figure
     */
    public void setType(FigureType type) {
        this.mType = type;
    }

    public void setType(int type) {
        switch (type) {
            case 0:
                setType(FigureType.CYLINDER);
                break;
            case 1:
                setType(FigureType.CUBOID);
                break;
            case 2:
                setType(FigureType.CUBE);
                break;
        }

    }

    /**
     * @return the volume of container
     */
    public float getVolumeTotal() {
        FigureType type = getType();
        float volume = 0;
        switch (type) {
            case CYLINDER:
                volume = (float) (Math.PI * Math.pow(getRadio(), POWER_2) *
                        (getStaticHeight() - getSensorErrorMargin())
                );
                break;
            case CUBE:
                volume = (float) Math.pow(getEdge(), POWER_3);
                break;
            case CUBOID:
                volume = getLength() * getWidth() *
                        (getStaticHeight() - getSensorErrorMargin());
                break;
        }
        return volume;
    }

    /**
     * @return the height static
     */
    public float getStaticHeightFromSensor() {
        return getHeightWhenIsEmpty() - getHeightWhenIsFull();
    }

    /**
     * @return the height from sensor (inverted)
     */
    public float getDynamicHeightFromSensor() {
        float height = GlobalData.getInstance().getDistance() - getHeightWhenIsFull();
        if (height > 0)
            return height;
        return 0;
    }

    /**
     * @return the volume calculated from the user values and read from sensor
     */
    public float getVolume() {
        getPreferenceData();
        FigureType type = getType();
        float volume = 0;
        switch (type) {
            case CYLINDER:
                volume = (float) (Math.PI * Math.pow(getRadio(), POWER_2) *
                        (getDynamicHeightFromSensor()) - getSensorErrorMargin());
                break;
            case CUBE:
                volume = (float) Math.pow(getEdge(), POWER_2) * (getDynamicHeightFromSensor() - getSensorErrorMargin());
                break;
            case CUBOID:
                volume = getLength() * getWidth() * (getDynamicHeightFromSensor() - getSensorErrorMargin());
                break;
        }
        volume = getVolumeTotal() - volume;
        volume = volume < 0 || getDynamicHeightFromSensor() > getHeightWhenIsEmpty() ? 0 : volume;
        return volume;
    }

    /**
     * @param limit the limit of the plot (y coordinate when is full)
     * @param bottomLimit the y limit when is empty
     * @param readHeight the height from sensor
     * @return the value for graphic
     */
    public float getValueForGraphic(float limit, float bottomLimit, float readHeight) {
        //Log.d("value 4 plot", "limit:" + limit + ",bottomlimit:" + bottomLimit);
        //m=y2-y2/x2-x1
        getPreferenceData();
        /*Get the slope
        * m = y2 - y1
        *     -------
        *     x2 - x1
        *  where y2 is the bottom limit, y1 the top limit and
        *  x2 and x1 the total height when the container is empty or full
        */
        float m = (bottomLimit - limit) / (getHeightWhenIsEmpty() - getHeightWhenIsFull());
        //y - y1 = m (x-x1)
        /*Get the y coordinate according height from sensor*/
        float y = m * readHeight - (m * getHeightWhenIsFull() - limit);
        /*If the read height is superior than the height when is empty
        * return the bottom limit (empty)*/
        if (readHeight >= getHeightWhenIsEmpty())
            return bottomLimit;
        /*If the read height is inferior than the height when is full
        * return the top limit (full)*/
        else if (readHeight <= getHeightWhenIsFull())
            return limit;
        return y;
    }

    /**
     * @param type type of container
     * @return the volume in liters
     */
    public float getLiters(FigureType type) {
        return getVolume() * VOLUME_TO_LITERS_FACTOR;
    }

    /**
     * @return the mRadio of container
     */
    public float getRadio() {
        return mRadio;
    }

    public void setRadio(float radio) {
        this.mRadio = radio;
    }

    public float getDynamicHeight() {
        return mDynamicHeight;
    }

    public void setDynamicHeight(float dynamicHeight) {
        this.mDynamicHeight = dynamicHeight;
    }

    public float getEdge() {
        return mEdge;
    }

    public void setEdge(float edge) {
        this.mEdge = edge;
    }

    public float getLength() {
        return mLength;
    }

    public void setLength(float length) {
        this.mLength = length;
    }

    public float getWidth() {
        return mWidth;
    }

    public void setWidth(float width) {
        this.mWidth = width;
    }

    public float getStaticHeight() {
        return mHeight;
    }

    public void setStaticHeight(float height) {
        this.mHeight = height;
    }

    public void setVolumeTotal(float volumeTotal) {
        this.mVolumeTotal = volumeTotal;
    }

    public float getHeightWhenIsFull() {
        return mHeightWhenIsFull;
    }

    public void setHeightWhenIsFull(float heightWhenIsFull) {
        this.mHeightWhenIsFull = heightWhenIsFull;
    }

    public float getHeightWhenIsEmpty() {
        return mHeightWhenIsEmpty;
    }

    public void setHeightWhenIsEmpty(float height) {
        this.mHeightWhenIsEmpty = height;
    }

    public FigureType getFigureTypeFromPrefs(int val) {
        FigureType figureType = FigureType.CYLINDER;
        switch (val) {
            case 0:
                figureType = FigureType.CYLINDER;
                break;
            case 1:
                figureType = FigureType.CUBOID;
                break;
            case 2:
                figureType = FigureType.CUBE;
                break;
        }
        return figureType;
    }

    public float getSensorErrorMargin() {
        return mSensorErrorMargin;
    }

    public void setSensorErrorMargin(float sensorErrorMargin) {
        this.mSensorErrorMargin = sensorErrorMargin;
    }

    /**
     * @return the percentage of water in container
     */
    public float getPercentage() {
        getPreferenceData();
        float heightTotal = getHeightWhenIsEmpty() - getHeightWhenIsFull();
        float readHeight = getHeightWhenIsEmpty() - GlobalData.getInstance().getDistance();
        readHeight = readHeight > heightTotal ? heightTotal : readHeight;
        float percentage = 0;
        try {
            percentage = readHeight * 100 / heightTotal;
            percentage = percentage < 0 ? 0 : percentage;
        } catch (Exception e) {
            e.printStackTrace();
            percentage = 0.0f;
        }
        return percentage;
    }

    /**
     * @param full the condition for calculate speed (fill or emptying)
     * @return the speed in cm/s
     */
    public float getSpeed(boolean full) {
        float height = getHeightWhenIsEmpty() - getHeightWhenIsFull();
        long startTime = System.currentTimeMillis();
        long time = 0;
        if (full) {
            while (GlobalData.getInstance().getDistance() < getHeightWhenIsEmpty())
                time = System.currentTimeMillis() - startTime;
            GlobalData.getInstance().setDumpTime(time);
        } else {
            while (GlobalData.getInstance().getDistance() > getHeightWhenIsFull())
                time = System.currentTimeMillis() - startTime;
            GlobalData.getInstance().setFillTime(time);
        }
        return height / (time / 1000);

    }

    /**
     * @return the speed according empty or full container
     */
    public float getSpeed() {
        getPreferenceData();
        float distanceSensor = GlobalData.getInstance().getDistance();
        float speed;
        if (distanceSensor <= getHeightWhenIsFull() + 0.3f) {
            GlobalData.getInstance().setFillTimes(GlobalData.getInstance().getFillTimes() + 1);
            speed = getSpeed(true);
            GlobalData.getInstance().setDumpSpeed(speed);
            if (showNotification())
                showNotification(mContext.getString(R.string.container_empty));
            return speed;
        } else if (distanceSensor >= getHeightWhenIsEmpty()) {
            GlobalData.getInstance().setDumpTimes(GlobalData.getInstance().getDumpTimes() + 1);
            speed = getSpeed(false);
            GlobalData.getInstance().setFillingSpeed(speed);
            if (showNotification())
                showNotification(mContext.getString(R.string.container_full));
            return speed;
        }
        return 0;
    }

    /**
     * @param text the text to show in notification
     */
    public void showNotification(String text) {
        mUi.getNotificationManager().notify(0,
                mUi.createNotification(
                        R.string.app_name,
                        text,
                        R.drawable.ic_setting_container,
                        MainActivity.class,
                        MainActivity.class,
                        Notification.FLAG_AUTO_CANCEL
                ));
    }


    public boolean showNotification() {
        return mShowNotification;
    }

    public void setShowNotification(boolean showNotification) {
        this.mShowNotification = showNotification;
    }
}
