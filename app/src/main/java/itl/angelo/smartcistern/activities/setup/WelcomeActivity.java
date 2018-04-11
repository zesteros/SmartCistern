package itl.angelo.smartcistern.activities.setup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import itl.angelo.smartcistern.R;
import itl.angelo.smartcistern.activities.MainActivity;
import itl.angelo.smartcistern.util.connection.ConnectionThread;
import itl.angelo.smartcistern.util.connection.GlobalData;
import itl.angelo.smartcistern.util.ui.UserInteraction;
import itl.angelo.smartcistern.util.volumecomputer.VolumeCalculator;

import static itl.angelo.smartcistern.fragments.ConnectFragment.IP_PREF_DEFAULT;
import static itl.angelo.smartcistern.fragments.ConnectFragment.IP_PREF_KEY;
import static itl.angelo.smartcistern.fragments.ConnectFragment.PORT_PREF_DEFAULT;
import static itl.angelo.smartcistern.fragments.ConnectFragment.PORT_PREF_KEY;

/**
 * Created by Angelo on 20/04/2017.
 */

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SETUP_PREFS_FILE = "setup_prefs";
    public static final String FIRST_RUN_KEY = "setup_prefs";
    public static final String IS_CONFIGURED = "setup_configured";
    private ProgressDialog mProgressDialog;
    private TextView mTitle, mSubtitle, mIpTitle, mPortTitle;
    private Button mContinueButton, mSkipButton;
    private SetupPhase mPhase;
    private UserInteraction mUi;
    private SharedPreferences settings;
    private EditText mIpEditText, mPortEditText;
    private SharedPreferences customPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mTitle = (TextView) findViewById(R.id.welcome_title);
        mIpTitle = (TextView) findViewById(R.id.ip_title);
        mPortTitle = (TextView) findViewById(R.id.port_title);
        mSubtitle = (TextView) findViewById(R.id.welcome_subtitle);
        mIpEditText = (EditText) findViewById(R.id.ip_setup);
        mPortEditText = (EditText) findViewById(R.id.port_setup);
        mContinueButton = (Button) findViewById(R.id.continue_button);
        mSkipButton = (Button) findViewById(R.id.skip_button);
        mUi = new UserInteraction(this);

        settings = getSharedPreferences(SETUP_PREFS_FILE, 0);

        boolean firstRun = settings.getBoolean(FIRST_RUN_KEY, false);
        boolean isConfigured = settings.getBoolean(IS_CONFIGURED, false);

        if (!firstRun || !isConfigured) {//if running for first time
            //Splash will load for first time
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(FIRST_RUN_KEY, true);
            editor.commit();
            mPhase = SetupPhase.INITIAL;
            mContinueButton.setOnClickListener(this);
            mSkipButton.setOnClickListener(this);

        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean updatePrefs(String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("value", value);
        if (value != null)
            if (!value.equals(""))
                prefs
                        .edit()
                        .putString(key, value)
                        .apply();
            else return true;
        else return true;
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continue_button) {
            switch (mPhase) {
                case INITIAL:
                    if (!updatePrefs(IP_PREF_KEY, String.valueOf(mIpEditText.getText()))
                            && !updatePrefs(PORT_PREF_KEY, String.valueOf(mPortEditText.getText())))
                        new TestConnection().execute();
                    else
                        Toast.makeText(this, getString(R.string.input_error), Toast.LENGTH_SHORT).show();
                    break;
                case EMPTY:
                    new GetHeight().execute(new Boolean[]{true});
                    break;
                case FULL:
                    new GetHeight().execute(new Boolean[]{false});
                    break;
                case FINISHED:
                    startActivity(new Intent(WelcomeActivity.this, SizeSetupActivity.class));
                    finish();
                    break;
            }
        } else {
            customPrefs = getSharedPreferences(SETUP_PREFS_FILE, 0);
            SharedPreferences.Editor editor = customPrefs.edit();
            editor.putBoolean(IS_CONFIGURED, true);
            editor.commit();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public class TestConnection extends AsyncTask<Void, Integer, Integer> {


        private SharedPreferences mPrefs;
        private ConnectionThread connectionThread;
        private UserInteraction mUi;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(WelcomeActivity.this);
            mProgressDialog.setMessage(WelcomeActivity.this.getString(R.string.connecting_to_cistern));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            mPrefs = PreferenceManager.getDefaultSharedPreferences(WelcomeActivity.this);
            connectionThread =
                    new ConnectionThread(
                            WelcomeActivity.this,
                            mPrefs.getString(IP_PREF_KEY, IP_PREF_DEFAULT),
                            mPrefs.getString(PORT_PREF_KEY, PORT_PREF_DEFAULT)
                    );
            connectionThread.setNeedUI(false);
            GlobalData.getInstance().setThread(connectionThread);
            mUi = new UserInteraction(WelcomeActivity.this);
            if (connectionThread.testConnection()) {
                /*If the connection was successful*/
                //show connection succeed message to user
                WelcomeActivity.this.runOnUiThread(() ->
                        mUi.showToast(R.string.connection_to_server_success)
                );
                connectionThread.start();
                GlobalData.getInstance().isConnected(true);
            } else {
                WelcomeActivity.this.runOnUiThread(() ->
                        mUi.showToast(R.string.connection_to_server_failed)
                );
                GlobalData.getInstance().isConnected(false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog.dismiss();
            if (GlobalData.getInstance().isConnected()) {
                mTitle.setText(getString(R.string.distance_setup_title));
                mSubtitle.setText(getString(R.string.distance_empty_setup_subtitle));
                mContinueButton.setText(getString(R.string.is_already_empty));
                mIpEditText.setVisibility(View.GONE);
                mPortEditText.setVisibility(View.GONE);
                mPortTitle.setVisibility(View.GONE);
                mIpTitle.setVisibility(View.GONE);
                mPhase = SetupPhase.EMPTY;
            }

        }
    }

    public class GetHeight extends AsyncTask<Boolean, Integer, Integer> {
        private float avg = 0;
        private SharedPreferences mPrefs;
        private boolean isEmpty;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(WelcomeActivity.this);
            mProgressDialog.setMessage(WelcomeActivity.this.getString(R.string.obtaining_height));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected Integer doInBackground(Boolean... params) {
            isEmpty = params[0];
            long startTime = System.currentTimeMillis();
            float distance = 0;
            int i = 1;
            while (System.currentTimeMillis() < startTime + 15000) {
                distance += GlobalData.getInstance().getDistance();
                avg = distance / i;
                i++;
                if (!GlobalData.getInstance().isConnected()) break;
            }
            String key =
                    isEmpty ?
                            VolumeCalculator.EMPTY_HEIGHT_PREF_KEY :
                            VolumeCalculator.FULL_HEIGHT_PREF_KEY;

            mPrefs = PreferenceManager.getDefaultSharedPreferences(WelcomeActivity.this);
            mPrefs
                    .edit()
                    .putString(
                            key,
                            String.valueOf(avg)
                    )
                    .apply();
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog.dismiss();
            Toast.makeText(
                    WelcomeActivity.this,
                    getString(R.string.height_established_success) + avg + getString(R.string.cm_height),
                    Toast.LENGTH_LONG
            ).show();
            if (isEmpty) {
                mSubtitle.setText(getString(R.string.distance_full_setup_subtitle));
                mContinueButton.setText(getString(R.string.is_already_full));
                mPhase = SetupPhase.FULL;
            } else {
                mTitle.setText(getString(R.string.setup_finished));
                mSubtitle.setText(getString(R.string.finished_setup_subtitle));
                mContinueButton.setText(getString(R.string.continue_button));
                mPhase = SetupPhase.FINISHED;
            }
        }
    }
}
