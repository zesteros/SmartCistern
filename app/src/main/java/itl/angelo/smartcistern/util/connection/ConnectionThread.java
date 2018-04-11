package itl.angelo.smartcistern.util.connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;

import itl.angelo.smartcistern.activities.MainActivity;
import itl.angelo.smartcistern.R;
import itl.angelo.smartcistern.fragments.ConnectFragment;
import itl.angelo.smartcistern.util.ui.UserInteraction;
import itl.angelo.smartcistern.util.volumecomputer.VolumeCalculator;

/**
 * Created by Angelo on 24/03/2017.
 */

public class ConnectionThread extends Thread {
    private float sum;
    private Context mContext;
    private boolean keepRunning = true;
    private Socket mSocket;
    private String mIp;
    private String mPort;
    private UserInteraction mUserInteraction;
    private BufferedReader mInput;
    private SensorDataUpdater mUpdater;
    private String URL;
    private boolean needUI = true;
    private boolean error;
    private TextView[] textViews;
    public static final int ESP8266_FREQUENCY = 2412;
    private FloatingActionButton wifiIndicator;
    private int connectionFailedCounter;
    private ConnectFragment fragment;
    private int counter;
    private VolumeCalculator mVolumeCalculator;
    private boolean mCalculateValues;

    public ConnectionThread(ConnectFragment connectFragment, FloatingActionButton mWifiIndicator, TextView[] textViews, Context context, String ip, String port) {
        this.mContext = context;
        this.fragment = connectFragment;
        mUserInteraction = new UserInteraction(context);
        mUpdater = new SensorDataUpdater();
        this.mIp = ip;
        this.mPort = port;
        URL = "http://" + ip + ":" + port;
        this.setTextViews(textViews);
        this.setWifiIndicator(mWifiIndicator);
        mVolumeCalculator = new VolumeCalculator(mContext);
        counter = 1;
        sum = 0;
    }

    public ConnectionThread(Context context, String ip, String port) {
            this.mContext = context;
        this.mIp = ip;
        this.mPort = port;

        URL = "http://" + ip + ":" + port;
        mUserInteraction = new UserInteraction(context);
        mUpdater = new SensorDataUpdater();
    }

    /**
     * Method to run while app is open
     */
    @Override
    public void run() {
        try {
            //start the mCounter for obtain avg
            //start infinite loop
            while (isKeepRunning()) {
                //Log.d("ruunning", "true");
                if (testConnection()) {
                    try {
                        GlobalData.getInstance().isConnected(true);
                        mInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                        mUpdater.debugMessage(mInput.readLine());
                        if (isNeedUI()) updateUI();
                        if (calculateValues()) {
                            sum += mVolumeCalculator.getVolume();
                            GlobalData.getInstance().setVolumeAverage(sum / counter);
                        }
                        counter++;
                    } catch (Exception e) {
                        //if something went wrong notify to user almost the reason is
                        //the connection was lost
                        if (mInput != null) mInput.close();
                        mSocket.close();
                        e.printStackTrace();
                        notifyConnectionError(ConnectionStatus.LOST);
                        break;
                    }
                } else connectionFailedCounter++;
                sendData();
                Thread.sleep(500);
                if (connectionFailedCounter > 5) {
                    ((MainActivity) mContext).runOnUiThread(() -> {
                        fragment.showUIDisconnected();
                    });
                    GlobalData.getInstance().isConnected(false);
                    break;
                }
            }
        } catch (ConnectException ce) {
            ce.printStackTrace();
            notifyConnectionError(ConnectionStatus.IP_PORT_ERROR);
        } catch (Exception e) {
            notifyConnectionError(ConnectionStatus.LOST);
            e.printStackTrace();
        }
    }

    public String[] getKeys() {
        return new String[]{
                mContext.getString(R.string.pump_ip_key),
                mContext.getString(R.string.pump_port_key),
                VolumeCalculator.FULL_HEIGHT_PREF_KEY,
                VolumeCalculator.EMPTY_HEIGHT_PREF_KEY,
                mContext.getString(R.string.manual_mode_key)
        };
    }

    public String[] getDefaults() {
        return new String[]{
                mContext.getString(R.string.pref_default_ip_pump),
                mContext.getString(R.string.pref_default_port_pump),
                VolumeCalculator.FULL_HEIGHT_PREF_DEFAULT,
                VolumeCalculator.EMPTY_HEIGHT_PREF_DEFAULT,
        };
    }

    public String[] getData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String[] keys = getKeys();
        String[] defaults = getDefaults();
        String[] data = new String[getKeys().length];
        for (int i = 0; i < data.length - 1; i++)
            data[i] = prefs.getString(keys[i], defaults[i]);
        boolean manualModeActivated = prefs.getBoolean(keys[data.length - 1], false);
        data[data.length - 1] = manualModeActivated ? "1" : "0";
        return data;
    }

    public void sendData() {
        try {
            HttpClient httpclient = new DefaultHttpClient(); // create an HTTP client
            // define the URL e.g. http://myIpaddress:myport/?pin=13 (to toggle pin 13 for example)
            String[] data = getData();
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(mContext);
            boolean isOverLimits = prefs.getBoolean(
                    mContext.getString(R.string.over_limits_key),
                    false
            );
            float limitFull = Float.valueOf(
                    prefs.getString(
                            VolumeCalculator.FULL_HEIGHT_PREF_KEY,
                            VolumeCalculator.FULL_HEIGHT_PREF_DEFAULT)
            );
            String isWaterPumpOn =
                    GlobalData.getInstance().isWaterPumpOn() &&
                            GlobalData.getInstance().getDistance() >= limitFull ? "1" : "0";

            String url = "http://" + data[0] + ":" + data[1] + "/?" + data[2] + "," + data[3] + "," + data[4] +
                    "," + isWaterPumpOn;

            URI website = new URI(url);

            HttpGet getRequest = new HttpGet(); // create an HTTP GET object
            getRequest.setURI(website); // set the URL of the GET request
            HttpResponse response = httpclient.execute(getRequest); // execute the request
            InputStream content = response.getEntity().getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    content
            ));
            if (in.readLine() != null)
                if (in.readLine().equals("1"))
                    Log.d("success", "height established");
            // Close the connection
            content.close();
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        } catch (URISyntaxException e) {
        }
    }

    /**
     * @return if connection was succeed
     */
    public boolean testConnection() {
        /*Try the connection*/
        try {
            mSocket = new Socket(mIp, Integer.parseInt(mPort));
            mSocket.setSoTimeout(5000);
        } catch (ConnectException ce) {
            if (ce.toString().contains(mContext.getString(R.string.connection_refused_exception)))
                notifyConnectionError(ConnectionStatus.CANNOT_CONNECT);
                //if the exception contains some of this errors, send the notification
            else if (ce.toString().contains(mContext.getString(
                    R.string.network_unreachable_exception)
            )) notifyConnectionError(ConnectionStatus.INTERNET_DISCONNECTED);
            else if (ce.toString().contains(mContext.getString(
                    R.string.no_route_to_host_exception)
            )) notifyConnectionError(ConnectionStatus.NO_ROUTE_TO_HOST);
            ce.printStackTrace();
            Log.e("error", ce + "");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void notifyConnectionError(final ConnectionStatus status) {
        connectionFailedCounter++;
        GlobalData.getInstance().isConnected(false);
        ((MainActivity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mDataActivity.notification.getNotificationManager().cancel(NotificationHelper.RECEIVING_DATA);
                switch (status) {
                    case NO_ROUTE_TO_HOST:
                        mUserInteraction.showSnackbar(R.string.no_route_to_host);
                        break;
                    case LOST:
                        mUserInteraction.showSnackbar(R.string.conection_lost);
                        break;
                    case IP_PORT_ERROR:
                        mUserInteraction.showSnackbar(R.string.ip_port_error);
                        break;
                    case INPUT_ERROR:
                        mUserInteraction.showSnackbar(R.string.input_error);
                        break;
                    case INTERNET_DISCONNECTED:
                        mUserInteraction.showSnackbar(R.string.internet_disconnected);
                        break;
                }
            }
        });
    }

    private void updateUI() {
        ((MainActivity) mContext).runOnUiThread(() -> {
            float signalStrength = GlobalData.getInstance().getDb();
            int channel = GlobalData.getInstance().getChannel();
            getTextViews()[0]
                    .setText(
                            mContext.getString(R.string.signal_strength)
                                    .replace("$", String.valueOf(signalStrength) + " dbm")
                    );
            getTextViews()[1]
                    .setText(
                            mContext.getString(R.string.distance_calculated)
                                    .replace("$", String.valueOf(
                                            new DecimalFormat("0.00").format(calculateDistance(
                                                    signalStrength,
                                                    ESP8266_FREQUENCY))) + " m"
                                    )
                    );
            getTextViews()[2]
                    .setText(
                            mContext.getString(R.string.channel_setted)
                                    .replace("$", String.valueOf(channel))
                    );
            getTextViews()[3]
                    .setText(
                            mContext.getString(R.string.signal_quality)
                                    .replace("$",
                                            determineSignalQuality(signalStrength)
                                    )
                    );
        });
    }

    private String determineSignalQuality(float signalStrength) {
        float[] signalsStrengths = signalQualityType();
        String[] signalsTitles = signalQualityTypeTitle();
        int[] signalResource = wifiIndicatorResources();
        if (signalStrength > signalsStrengths[0]) {
            getWifiIndicator().setImageResource(signalResource[0]);
            return signalsTitles[0];
        } else
            for (int i = 1; i < signalsStrengths.length; i++)
                if (signalStrength >= signalsStrengths[i]) {
                    getWifiIndicator().setImageResource(signalResource[i]);
                    return signalsTitles[i];
                }
        return "";
    }

    private float[] signalQualityType() {
        return new float[]{
                -35f,
                -67f,
                -70f,
                -80f,
                -90f
        };
    }

    private String[] signalQualityTypeTitle() {
        return new String[]{
                mContext.getString(R.string.very_good_strength),
                mContext.getString(R.string.good_strength),
                mContext.getString(R.string.regular_strength),
                mContext.getString(R.string.bad_strength),
                mContext.getString(R.string.very_bad_strength)
        };
    }

    private int[] wifiIndicatorResources() {
        return new int[]{
                R.drawable.ic_wifi_very_good,
                R.drawable.ic_wifi_good,
                R.drawable.ic_wifi_regular,
                R.drawable.ic_wifi_bad,
                R.drawable.ic_wifi_very_bad
        };
    }

    public float calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return (float) Math.pow(10.0, exp);
    }

    public boolean isKeepRunning() {
        return keepRunning;
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    public boolean isNeedUI() {
        return needUI;
    }

    public void setNeedUI(boolean needUI) {
        this.needUI = needUI;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public TextView[] getTextViews() {
        return textViews;
    }

    public void setTextViews(TextView[] textViews) {
        this.textViews = textViews;
    }

    public FloatingActionButton getWifiIndicator() {
        return wifiIndicator;
    }

    public void setWifiIndicator(FloatingActionButton wifiIndicator) {
        this.wifiIndicator = wifiIndicator;
    }

    public boolean calculateValues() {
        return mCalculateValues;
    }

    public void calculateValues(boolean mCalculateValues) {
        this.mCalculateValues = mCalculateValues;
    }
}
