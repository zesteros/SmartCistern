package itl.angelo.smartcistern.fragments;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import itl.angelo.smartcistern.R;
import itl.angelo.smartcistern.activities.MainActivity;
import itl.angelo.smartcistern.adapters.SectionsPagerAdapter;
import itl.angelo.smartcistern.util.connection.ConnectionThread;
import itl.angelo.smartcistern.util.connection.GlobalData;
import itl.angelo.smartcistern.util.ui.UserInteraction;
import itl.angelo.smartcistern.util.volumecomputer.VolumeCalculator;

/**
 * A placeholder fragment containing a simple view.
 */
public class ConnectFragment extends Fragment implements View.OnClickListener {

    private static final String ROTATION_ANIMATION = "rotation";
    private static final String ALPHA_ANIMATION = "alpha";
    private ConnectionThread connectionThread;
    private TextView test;
    private Button connectButton;
    private SharedPreferences mPrefs;
    private UserInteraction mUi;
    private ProgressDialog mProgressDialog;
    public static final String IP_PREF_KEY = "ip_address_key";
    public static final String IP_PREF_DEFAULT = "192.168.3.100";
    public static final String PORT_PREF_KEY = "port_key";
    public static final String PORT_PREF_DEFAULT = "10001";
    private TextView[] mConnectionDetails;
    private LinearLayout mConnectionDetailsLayout;
    private FloatingActionButton mWifiIndicator;
    private RefillThread mRefillThread;
    private boolean mShowScreens;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mShowScreens = mPrefs.getBoolean(getString(R.string.show_screens_key), false);
        if (mShowScreens) addPages();
        int[] textViewIds = getConnectionDetailsIds();
        mConnectionDetails = new TextView[textViewIds.length];
        mConnectionDetailsLayout = (LinearLayout) rootView.findViewById(R.id.connect_details_layout);
        mWifiIndicator = (FloatingActionButton) rootView.findViewById(R.id.wifi_indicator);
        for (int i = 0; i < textViewIds.length; i++)
            mConnectionDetails[i] = (TextView) rootView.findViewById(textViewIds[i]);
        connectButton = (Button) rootView.findViewById(R.id.connect_button);
        connectButton.setOnClickListener(this);
        mProgressDialog = new ProgressDialog(getActivity());
        mUi = new UserInteraction(getActivity());

        return rootView;
    }

    public int[] getConnectionDetailsIds() {
        return new int[]{
                R.id.signal,
                R.id.distance,
                R.id.channel,
                R.id.signal_quality
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        //if (GlobalData.getInstance().isConnected()) {
            showUIConnected();
          //  GlobalData.getInstance().getThread().setTextViews(mConnectionDetails);
            //GlobalData.getInstance().getThread().setWifiIndicator(mWifiIndicator);
        //} else showUIDisconnected();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mProgressDialog != null) mProgressDialog.dismiss();
        if (GlobalData.getInstance().isConnected()) {
            showUIConnected();
            GlobalData.getInstance().getThread().setTextViews(mConnectionDetails);
            GlobalData.getInstance().getThread().setWifiIndicator(mWifiIndicator);
        } else showUIDisconnected();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null) mProgressDialog.dismiss();
        if (GlobalData.getInstance().isConnected()) {
            showUIConnected();
            GlobalData.getInstance().getThread().setTextViews(mConnectionDetails);
            GlobalData.getInstance().getThread().setWifiIndicator(mWifiIndicator);
        } else showUIDisconnected();
    }

    @Override
    public void onClick(View v) {
        animView(v, ROTATION_ANIMATION, 500, 0f, 720f);
        animView(mConnectionDetailsLayout, ALPHA_ANIMATION, 3000, 0f, 1f);
        if (connectButton.getText().equals(getString(R.string.connect)))
            new TestConnection().execute();
        else showUIDisconnected();
    }

    public void showUIDisconnected() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mShowScreens = mPrefs.getBoolean(getString(R.string.show_screens_key), false);
        if (!mShowScreens) removePages();
        mConnectionDetailsLayout.setVisibility(View.INVISIBLE);
        mWifiIndicator.setVisibility(View.INVISIBLE);
        connectionThread = GlobalData.getInstance().getThread();
        if (mRefillThread != null) mRefillThread.keepRunning = false;
        if (connectionThread != null) connectionThread.setKeepRunning(false);
        connectButton.setText(getString(R.string.connect));
        connectButton.setBackground(
                getResources().getDrawable(R.drawable.round_button_connect)
        );
        GlobalData.getInstance().isConnected(false);
    }

    private void removePages() {
        ((MainActivity) getActivity()).removeInitialPages();
        ((MainActivity) getActivity()).checkManualMode();
    }

    private void addPages() {
        ((MainActivity) getActivity()).addInitialPages();
        ((MainActivity) getActivity()).checkManualMode();
    }

    public void showUIConnected() {
        addPages();
        mWifiIndicator.setVisibility(View.VISIBLE);
        GlobalData.getInstance().isConnected(true);
        mConnectionDetailsLayout.setVisibility(View.VISIBLE);
        connectButton.setText(getString(R.string.disconnect));
        connectButton.setBackground(
                getResources().getDrawable(R.drawable.round_button_disconnect)
        );
    }

    public class TestConnection extends AsyncTask<Void, Integer, Integer> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage(getActivity().getString(R.string.connecting_to_cistern));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

            connectionThread =
                    new ConnectionThread(
                            ConnectFragment.this,
                            mWifiIndicator,
                            mConnectionDetails,
                            getActivity(),
                            mPrefs.getString(IP_PREF_KEY, IP_PREF_DEFAULT),
                            mPrefs.getString(PORT_PREF_KEY, PORT_PREF_DEFAULT)
                    );
            mRefillThread = new RefillThread();
            GlobalData.getInstance().setThread(connectionThread);
            if (connectionThread.testConnection()) {
                /*If the connection was successful*/
                //show connection succeed message to user
                //getActivity().runOnUiThread(() ->
                //      mUi.showToast(R.string.connection_to_server_success)
                //);
                connectionThread.calculateValues(true);
                connectionThread.start();
                mRefillThread.start();
                GlobalData.getInstance().isConnected(true);
            } else {
                //getActivity().runOnUiThread(() ->
                //      mUi.showToast(R.string.connection_to_server_failed)
                //);
                GlobalData.getInstance().isConnected(false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog.dismiss();
            if (GlobalData.getInstance().isConnected())
                showUIConnected();
            else
                showUIDisconnected();
        }
    }

    /**
     * @param view     view to apply anim
     * @param type     type of anim
     * @param duration duration
     * @param from     form where
     * @param to       to where
     */
    public void animView(View view, String type, int duration, float from, float to) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, type, from, to);
        anim.setDuration(duration);
        anim.start();
    }

    private class RefillThread extends Thread {
        private VolumeCalculator mVolumeCalculator;
        public boolean keepRunning = true;

        public RefillThread() {
            mVolumeCalculator = new VolumeCalculator(getActivity());
        }

        @Override
        public void run() {
            while (keepRunning) mVolumeCalculator.getSpeed();
        }
    }
}
