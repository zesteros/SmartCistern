package itl.angelo.smartcistern.activities;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;

import itl.angelo.smartcistern.R;
import itl.angelo.smartcistern.util.connection.GlobalData;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || ContainerPreferenceFragment.class.getName().equals(fragmentName)
                || ManualModePreference.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ManualModePreference extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private SwitchPreference mode, limits;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_manual_mode);
            setHasOptionsMenu(true);
            mode = (SwitchPreference) findPreference(getString(R.string.manual_mode_key));
            limits = (SwitchPreference) findPreference(getString(R.string.over_limits_key));
            mode.setOnPreferenceChangeListener(this);
            boolean manualModeActivated = PreferenceManager.
                    getDefaultSharedPreferences(getActivity())
                    .getBoolean(getString(R.string.manual_mode_key), false);
            ;
            disableFunction(manualModeActivated);
        }

        private void disableFunction(boolean activated) {
            if (activated)
                limits.setEnabled(true);

            else limits.setEnabled(false);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                ;
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            /*final boolean[] result = new boolean[1];
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog
                    .setTitle(R.string.restart)
                    .setMessage(R.string.restart_dialog_message)
                    .setPositiveButton(R.string.accept, (dialog1, which) -> {
                        Intent i = new Intent(getActivity().getApplicationContext(),
                                MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        getActivity().getFragmentManager().beginTransaction()
                                .remove(ManualModePreference.this).commit();
                        GlobalData.getInstance().isModeChanged(true);
                        getActivity().finish();
                        changeSwitchValue(preference);
                    })
                    .setNegativeButton(R.string.cancel, null);
            dialog.create().show();
            Log.d("result ", result[0] + "");
            ;*/
            disableFunction((Boolean) newValue);
            GlobalData.getInstance().isModeChanged(true);
            return true;
        }

        public void changeSwitchValue(Preference preference) {
            if (((SwitchPreference) preference).isChecked()) {
                ((SwitchPreference) preference).setChecked(false);
                preference
                        .getSharedPreferences()
                        .edit()
                        .putBoolean(getString(R.string.manual_mode_key), false)
                        .apply();
            } else {
                ((SwitchPreference) preference).setChecked(true);
                preference
                        .getSharedPreferences()
                        .edit()
                        .putBoolean(getString(R.string.manual_mode_key), true)
                        .apply();
            }
        }
    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                ;
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_connection);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                ;
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ContainerPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private EditTextPreference radioPref, edgePref, lengthPref, widthPref;
        private ListPreference containerTypePref;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_container);

            containerTypePref = (ListPreference) findPreference(getString(R.string.figure_type_pref_key));

            radioPref = (EditTextPreference) findPreference(getString(R.string.radio_pref_key));
            edgePref = (EditTextPreference) findPreference(getString(R.string.edge_pref_key));
            lengthPref = (EditTextPreference) findPreference(getString(R.string.length_pref_key));
            widthPref = (EditTextPreference) findPreference(getString(R.string.width_pref_key));
            disableFunctions(
                    Integer.parseInt(
                            containerTypePref.getSharedPreferences().getString(
                                    containerTypePref.getKey(), "0")
                    )
            );
            containerTypePref.setOnPreferenceChangeListener(this);
            /*containerTypePref.setOnPreferenceChangeListener((preference, newValue) -> {
                int option = Integer.parseInt(newValue.toString());
                disableFunctions(option);
                preference
                        .getSharedPreferences()
                        .edit()
                        .putString(preference.getKey(),newValue.toString())
                        .apply();
                return false;
            });
            */
            setHasOptionsMenu(true);
        }

        public void disableFunctions(int option) {
            switch (option) {
                case 0:
                    radioPref.setEnabled(true);
                    edgePref.setEnabled(false);
                    lengthPref.setEnabled(false);
                    widthPref.setEnabled(false);
                    break;
                case 1:
                    radioPref.setEnabled(false);
                    edgePref.setEnabled(false);
                    lengthPref.setEnabled(true);
                    widthPref.setEnabled(true);
                    break;
                case 2:
                    radioPref.setEnabled(false);
                    edgePref.setEnabled(true);
                    lengthPref.setEnabled(false);
                    widthPref.setEnabled(false);
                    break;
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                ;
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            disableFunctions(Integer.parseInt(newValue.toString()));
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
