package gis2018.udacity.pomodoro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.work_duration_spinner)
    Spinner workDurationSpinner;
    @BindView(R.id.short_break_duration_spinner)
    Spinner shortBreakDurationSpinner;
    @BindView(R.id.long_break_duration_spinner)
    Spinner longBreakDurationSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

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
        workDurationSpinner.setSelection(1);
        shortBreakDurationSpinner.setSelection(1);
        longBreakDurationSpinner.setSelection(1);
    }
}
