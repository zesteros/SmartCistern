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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import itl.angelo.smartcistern.R;
import itl.angelo.smartcistern.activities.MainActivity;
import itl.angelo.smartcistern.util.connection.ConnectionThread;
import itl.angelo.smartcistern.util.connection.GlobalData;
import itl.angelo.smartcistern.util.ui.UserInteraction;
import itl.angelo.smartcistern.util.volumecomputer.VolumeCalculator;

import static itl.angelo.smartcistern.activities.setup.WelcomeActivity.IS_CONFIGURED;
import static itl.angelo.smartcistern.activities.setup.WelcomeActivity.SETUP_PREFS_FILE;

/**
 * Created by Angelo on 20/04/2017.
 */

public class SizeSetupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private TextView[] labels;
    private EditText[] edit;
    private int containerType;
    private SharedPreferences prefs, customPrefs;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        customPrefs = getSharedPreferences(SETUP_PREFS_FILE, 0);

        Spinner spinner = (Spinner) findViewById(R.id.container_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.container_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        int labelRes[] = getLabelsResource();
        int editTextRes[] = getEditsResource();

        labels = new TextView[labelRes.length];
        edit = new EditText[editTextRes.length];

        for (int i = 0; i < labelRes.length; i++) {
            labels[i] = (TextView) findViewById(labelRes[i]);
            edit[i] = (EditText) findViewById(editTextRes[i]);
        }
        Button continueButton = (Button) findViewById(R.id.finish_button);
        continueButton.setOnClickListener(this);
    }

    public int[] getLabelsResource() {
        return new int[]{
                R.id.radio_title,
                R.id.edge_title,
                R.id.width_title,
                R.id.length_title,
                R.id.height_title
        };
    }

    public int[] getEditsResource() {
        return new int[]{
                R.id.radio_edit_text,
                R.id.edge_edit_text,
                R.id.width_edit_text,
                R.id.length_edit_text,
                R.id.height_edit_text
        };
    }

    public String[] getPreferencesKeys() {
        return new String[]{

        };
    }

    public void enableDisableElements(boolean[] options) {
        for (int i = 0; i < labels.length - 1; i++) {
            labels[i].setEnabled(options[i]);
            edit[i].setEnabled(options[i]);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        prefs
                .edit()
                .putString(
                        VolumeCalculator.FIGURE_TYPE_KEY,
                        String.valueOf(position)
                )
                .apply();
        switch (position) {
            case 0:
                enableDisableElements(new boolean[]{true, false, false, false});
                break;
            case 1:
                enableDisableElements(new boolean[]{false, false, true, true});
                break;
            case 2:
                enableDisableElements(new boolean[]{false, true, false, false});
                break;
        }
        setContainerType(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public boolean updatePrefs(String key, String value) {
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

        boolean error = false;
        for (int i = 0; i < edit.length; i++) {
            Log.d("EDIT_CONTENT " + i, String.valueOf(edit[i].getText()));
        }
        switch (getContainerType()) {
            case 0:
                error = updatePrefs(VolumeCalculator.RADIO_PREF_KEY, String.valueOf(edit[0].getText()))
                        && updatePrefs(VolumeCalculator.STATIC_HEIGHT_PREF_KEY, String.valueOf(edit[4].getText()));
                break;
            case 1:
                error = updatePrefs(VolumeCalculator.WIDTH_PREF_KEY, String.valueOf(edit[2].getText()))
                        && updatePrefs(VolumeCalculator.LENGTH_PREF_KEY, String.valueOf(edit[3].getText()))
                        && updatePrefs(VolumeCalculator.STATIC_HEIGHT_PREF_KEY, String.valueOf(edit[4].getText()));
                break;
            case 2:
                error = updatePrefs(VolumeCalculator.EDGE_PREF_KEY, String.valueOf(edit[1].getText()))
                        && updatePrefs(VolumeCalculator.STATIC_HEIGHT_PREF_KEY, String.valueOf(edit[4].getText()));
                break;

        }
        if (!error) {
            SharedPreferences.Editor editor = customPrefs.edit();
            editor.putBoolean(IS_CONFIGURED, true);
            editor.commit();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else
            Toast.makeText(this, "Por favor, introduce valores adecuados", Toast.LENGTH_SHORT).show();
    }

    public int getContainerType() {
        return containerType;
    }

    public void setContainerType(int containerType) {
        this.containerType = containerType;
    }
}
