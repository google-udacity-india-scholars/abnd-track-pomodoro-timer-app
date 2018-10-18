package gis2018.udacity.pomodoro;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();
    @BindView(R.id.work_duration_spinner)
    Spinner workDurationSpinner;
    @BindView(R.id.short_break_duration_spinner)
    Spinner shortBreakDurationSpinner;
    @BindView(R.id.long_break_duration_spinner)
    Spinner longBreakDurationSpinner;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        initSpinner();
    }

    /**
     * Separate method to populate the spinners
     * This will keep the onCreate() method clean
     */
    private void initSpinner() {
        // Create an array adapter for all three spinners using the string array
        ArrayAdapter<CharSequence> workDurationAdapter = ArrayAdapter.createFromResource(this,
                R.array.work_duration_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> shortBreakDurationAdapter = ArrayAdapter.createFromResource(this,
                R.array.short_break_duration_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> longBreakDurationAdapter = ArrayAdapter.createFromResource(this,
                R.array.long_break_duration_array, android.R.layout.simple_spinner_item);

        // Layout to use when list of choices appears
        workDurationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shortBreakDurationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        longBreakDurationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        workDurationSpinner.setAdapter(workDurationAdapter);
        shortBreakDurationSpinner.setAdapter(shortBreakDurationAdapter);
        longBreakDurationSpinner.setAdapter(longBreakDurationAdapter);

        // Set the default selection
        workDurationSpinner.setSelection(preferences.getInt(getString(R.string.work_duration_key), 1));
        shortBreakDurationSpinner.setSelection(preferences.getInt(getString(R.string.short_break_duration_key), 1));
        longBreakDurationSpinner.setSelection(preferences.getInt(getString(R.string.long_break_duration_key), 1));

        workDurationSpinner.setOnItemSelectedListener(this);
        shortBreakDurationSpinner.setOnItemSelectedListener(this);
        longBreakDurationSpinner.setOnItemSelectedListener(this);
    }

    /**
     * Save the latest selected item position from the spinners
     * into SharedPreferences
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // initialize the editor
        SharedPreferences.Editor editor = preferences.edit();
        // switch case to handle different spinners
        switch (parent.getId()) {
            // item selected in work duration spinner
            case R.id.work_duration_spinner:
                Log.v(LOG_TAG, (String) parent.getItemAtPosition(position));
                // save the corresponding item position
                editor.putInt(getString(R.string.work_duration_key), position);
                break;
            // item selected in short break duration spinner
            case R.id.short_break_duration_spinner:
                Log.v(LOG_TAG, (String) parent.getItemAtPosition(position));
                // save the corresponding item position
                editor.putInt(getString(R.string.short_break_duration_key), position);
                break;
            // item selected in long break duration spinner
            case R.id.long_break_duration_spinner:
                Log.v(LOG_TAG, (String) parent.getItemAtPosition(position));
                // save the corresponding item position
                editor.putInt(getString(R.string.long_break_duration_key), position);
        }
        editor.apply();

        // Print the saved preferences in logs
        Log.v(LOG_TAG, String.valueOf(preferences.getInt(getString(R.string.work_duration_key), -1)));
        Log.v(LOG_TAG, String.valueOf(preferences.getInt(getString(R.string.short_break_duration_key), -1)));
        Log.v(LOG_TAG, String.valueOf(preferences.getInt(getString(R.string.long_break_duration_key), -1)));
    }

    /**
     * When nothing is selected from the spinner
     *
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
