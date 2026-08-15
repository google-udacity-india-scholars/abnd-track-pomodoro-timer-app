package gis2018.udacity.tametu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import gis2018.udacity.tametu.databinding.ActivitySettingsBinding;

import static gis2018.udacity.tametu.utils.Constants.RINGING_VOLUME_LEVEL_KEY;
import static gis2018.udacity.tametu.utils.Constants.TICKING_VOLUME_LEVEL_KEY;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.convertToFloat;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.floatRingingVolumeLevel;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.floatTickingVolumeLevel;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.initializeSeekBar;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.maxVolume;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    private ActivitySettingsBinding binding;
    Spinner workDurationSpinner;
    Spinner shortBreakDurationSpinner;
    Spinner longBreakDurationSpinner;
    Spinner startlongbreakafterSpinner;
    SeekBar tickingSeekBar;
    SeekBar ringingSeekBar;
    TextView aboutUsTextView;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        workDurationSpinner = binding.workDurationSpinner;
        shortBreakDurationSpinner = binding.shortBreakDurationSpinner;
        longBreakDurationSpinner = binding.longBreakDurationSpinner;
        startlongbreakafterSpinner = binding.startLongBreakAfterSpinner;
        tickingSeekBar = binding.tickingSeekBar;
        ringingSeekBar = binding.ringingSeekBar;
        aboutUsTextView = binding.aboutUsText;

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        initSpinner();

        seekBarInitialization();
        aboutUsTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void seekBarInitialization() {
        tickingSeekBar = initializeSeekBar(this, tickingSeekBar);
        ringingSeekBar = initializeSeekBar(this, ringingSeekBar);
        tickingSeekBar.setOnSeekBarChangeListener(this);
        ringingSeekBar.setOnSeekBarChangeListener(this);
    }

    /**
     * Separate method to populate the spinners
     * This will keep the onCreate() method clean
     */
    private void initSpinner() {
        // Create an array adapter for all three spinners using the string array
        ArrayAdapter<CharSequence> workDurationAdapter = ArrayAdapter.createFromResource(this,
                R.array.work_duration_array, R.layout.spinner_item);
        ArrayAdapter<CharSequence> shortBreakDurationAdapter = ArrayAdapter.createFromResource(this,
                R.array.short_break_duration_array, R.layout.spinner_item);
        ArrayAdapter<CharSequence> longBreakDurationAdapter = ArrayAdapter.createFromResource(this,
                R.array.long_break_duration_array, R.layout.spinner_item);
        ArrayAdapter<CharSequence> startlongbreakafterAdapter = ArrayAdapter.createFromResource(this,
                R.array.start_long_break_after_array, R.layout.spinner_item);

        // Layout to use when list of choices appears
        workDurationAdapter.setDropDownViewResource(R.layout.spinner_item);
        shortBreakDurationAdapter.setDropDownViewResource(R.layout.spinner_item);
        longBreakDurationAdapter.setDropDownViewResource(R.layout.spinner_item);
        startlongbreakafterAdapter.setDropDownViewResource(R.layout.spinner_item);

        // Apply the adapter to the spinner
        workDurationSpinner.setAdapter(workDurationAdapter);
        shortBreakDurationSpinner.setAdapter(shortBreakDurationAdapter);
        longBreakDurationSpinner.setAdapter(longBreakDurationAdapter);
        startlongbreakafterSpinner.setAdapter(startlongbreakafterAdapter);

        // Set the default selection
        workDurationSpinner.setSelection(preferences.getInt(getString(R.string.work_duration_key), 1));
        shortBreakDurationSpinner.setSelection(preferences.getInt(getString(R.string.short_break_duration_key), 1));
        longBreakDurationSpinner.setSelection(preferences.getInt(getString(R.string.long_break_duration_key), 1));
        startlongbreakafterSpinner.setSelection(preferences.getInt(getString((R.string.start_long_break_after_key)), 2));

        workDurationSpinner.setOnItemSelectedListener(this);
        shortBreakDurationSpinner.setOnItemSelectedListener(this);
        longBreakDurationSpinner.setOnItemSelectedListener(this);
        startlongbreakafterSpinner.setOnItemSelectedListener(this);

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
        // chain of ifs to handle different spinners. (Can't be a switch: view IDs are no longer
        // compile-time constants once android.nonFinalResIds defaults to true.)
        int parentId = parent.getId();
        if (parentId == R.id.work_duration_spinner) {
            // item selected in work duration spinner: save the corresponding item position
            editor.putInt(getString(R.string.work_duration_key), position);
        } else if (parentId == R.id.short_break_duration_spinner) {
            // item selected in short break duration spinner: save the corresponding item position
            editor.putInt(getString(R.string.short_break_duration_key), position);
        } else if (parentId == R.id.long_break_duration_spinner) {
            // item selected in long break duration spinner: save the corresponding item position
            editor.putInt(getString(R.string.long_break_duration_key), position);
        } else if (parentId == R.id.start_long_break_after_spinner) {
            // save the corresponding item position
            editor.putInt(getString(R.string.start_long_break_after_key), position);
        }
        editor.apply();
    }

    /**
     * When nothing is selected from the spinner
     *
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();
        if (id == R.id.ticking_seek_bar) {
            preferences.edit().putInt(TICKING_VOLUME_LEVEL_KEY, progress).apply(); //Save volume level to SharedPreferences
            floatTickingVolumeLevel = convertToFloat(preferences.getInt(TICKING_VOLUME_LEVEL_KEY, maxVolume), maxVolume);
        } else if (id == R.id.ringing_seek_bar) {
            preferences.edit().putInt(RINGING_VOLUME_LEVEL_KEY, progress).apply(); //Save value to SharedPreferences
            floatRingingVolumeLevel = convertToFloat(preferences.getInt(RINGING_VOLUME_LEVEL_KEY, maxVolume), maxVolume);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}