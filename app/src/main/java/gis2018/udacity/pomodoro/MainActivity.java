/*
 *  Audio Files downloaded from soundjay.com
 *  Enhanced at http://www.mp3smaller.com/ & http://www.mp3louder.com/
 */

package gis2018.udacity.pomodoro;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import gis2018.udacity.pomodoro.utils.Utils;

import static gis2018.udacity.pomodoro.utils.Constants.LONG_BREAK;
import static gis2018.udacity.pomodoro.utils.Constants.POMODORO;
import static gis2018.udacity.pomodoro.utils.Constants.SHORT_BREAK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int currentlyRunningServiceType; // Type of Service can be POMODORO, SHORT_BREAK or LONG_BREAK
    private long workDuration; // Time Period for Pomodoro (Work-Session)
    private String workDurationString; // Time Period for Pomodoro in String
    private long shortBreakDuration; // Time Period for Short-Break
    private String shortBreakDurationString; // Time Period for Short-Break in String
    private long longBreakDuration; // Time Period for Long-Break
    private String longBreakDurationString; // Time Period for Long-Break in String

    private static final long TIME_INTERVAL = 1000; // Time Interval is 1 second
    BroadcastReceiver stoppedIntentReceiver;
    BroadcastReceiver countDownReceiver;
    private SharedPreferences preferences;
    private int workSessionCount = 0; // Number of Completed Work-Sessions

    @BindView(R.id.settings_imageview_main)
    ImageView settingsImageView;
    @BindView(R.id.task_change_button_main)
    Button changeButton;
    @BindView(R.id.timer_button_main)
    ToggleButton timerButton;
    @BindView(R.id.countdown_textview_main)
    TextView countDownTextView;
    @BindView(R.id.session_completed_value_textview_main)
    TextView workSessionCompletedTextView;
    @BindView(R.id.finish_imageview_main)
    ImageView finishImageView; // (Complete Button)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        settingsImageView.setOnClickListener(this);
        changeButton.setOnClickListener(this);
        timerButton.setOnClickListener(this);
        finishImageView.setOnClickListener(this);

        // Set button as checked if the service is already running.
        timerButton.setChecked(isServiceRunning(CountDownTimerService.class));

        // Receives broadcast that the timer has stopped.
        stoppedIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timerButton.setChecked(false);

                // Setting new value of workSessionCount for workSessionCompletedTextView after a session is completed.
                if (intent.getExtras() != null) {
                    workSessionCount = intent.getExtras().getInt("workSessionCount");
                    workSessionCompletedTextView.setText(String.valueOf(workSessionCount));
                }

                // Retrieving value of currentlyRunningServiceType from SharedPreferences.
                currentlyRunningServiceType = Utils.retrieveCurrentlyRunningServiceType(preferences, context);

                if (currentlyRunningServiceType == POMODORO) {
                    // Getting type of break user should take, and updating type of currently running service
                    currentlyRunningServiceType = Utils.getTypeOfBreak(preferences, getApplicationContext());
                } else {
                    // If last value of currentlyRunningServiceType was SHORT_BREAK or LONG_BREAK then set it back to POMODORO
                    currentlyRunningServiceType = POMODORO;
                }

                // Updating value of currentlyRunningServiceType in SharedPreferences.
                Utils.updateCurrentlyRunningServiceType(preferences, getApplicationContext(), currentlyRunningServiceType);

                // Changing textOn & textOff according to value of currentlyRunningServiceType.
                changeToggleButtonStateText(currentlyRunningServiceType);
            }
        };

        // Receives broadcast for countDown at every tick.
        countDownReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null)
                    countDownTextView.setText(intent.getExtras().getString("countDown"));
            }
        };

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieving current value of Duration for POMODORO, SHORT_BREAK and LONG_BREAK from SharedPreferences.
        workDuration = Utils.getCurrentDurationPreferenceOf(preferences, this, POMODORO);
        shortBreakDuration = Utils.getCurrentDurationPreferenceOf(preferences, this, SHORT_BREAK);
        longBreakDuration = Utils.getCurrentDurationPreferenceOf(preferences, this, LONG_BREAK);

        // Retrieving duration in mm:ss format from duration value in milliSeconds.
        workDurationString = Utils.getCurrentDurationPreferenceStringFor(workDuration);
        shortBreakDurationString = Utils.getCurrentDurationPreferenceStringFor(shortBreakDuration);
        longBreakDurationString = Utils.getCurrentDurationPreferenceStringFor(longBreakDuration);

        // Changing textOn & textOff according to value of currentlyRunningServiceType.
        currentlyRunningServiceType = Utils.retrieveCurrentlyRunningServiceType(preferences, this);
        changeToggleButtonStateText(currentlyRunningServiceType);

        // Retrieving value of workSessionCount (Current value of workSessionCount) from SharedPreference.
        workSessionCount = preferences.getInt(getString(R.string.work_session_count_key), 0);
        workSessionCompletedTextView.setText(String.valueOf(workSessionCount));
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((stoppedIntentReceiver),
                new IntentFilter(CountDownTimerService.STOP_ACTION_BROADCAST));
        LocalBroadcastManager.getInstance(this).registerReceiver((countDownReceiver),
                new IntentFilter(CountDownTimerService.COUNTDOWN_BROADCAST));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stoppedIntentReceiver);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        // Retrieving value of currentlyRunningServiceType from SharedPreferences.
        currentlyRunningServiceType = Utils.retrieveCurrentlyRunningServiceType(preferences, this);

        // Switch case to handle different button clicks
        switch (v.getId()) {
            // Settings button is clicked
            case R.id.settings_imageview_main:
                // launch SettingsActivity
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.task_change_button_main:
                // Todo: Define on task change
                break;

            case R.id.timer_button_main:
                if (currentlyRunningServiceType == POMODORO) {
                    if (timerButton.isChecked()) {
                        startTimer(workDuration);
                    } else {
                        // When "Cancel Pomodoro" is clicked, service is stopped and toggleButton is reset to "Start Pomodoro".
                        stopTimer(workDurationString);
                        Utils.updateCurrentlyRunningServiceType(preferences, this, currentlyRunningServiceType);
                        currentlyRunningServiceType = Utils.retrieveCurrentlyRunningServiceType(preferences, this);
                        changeToggleButtonStateText(currentlyRunningServiceType);
                    }
                } else if (currentlyRunningServiceType == SHORT_BREAK) {
                    if (timerButton.isChecked()) {
                        startTimer(shortBreakDuration);
                    } else {
                        // When "Skip Short Break" is clicked, service is stopped and toggleButton is reset to "Start Pomodoro".
                        stopTimer(workDurationString);
                        currentlyRunningServiceType = POMODORO;
                        Utils.updateCurrentlyRunningServiceType(preferences, this, currentlyRunningServiceType);
                        currentlyRunningServiceType = Utils.retrieveCurrentlyRunningServiceType(preferences, this);
                        changeToggleButtonStateText(currentlyRunningServiceType);
                    }
                } else if (currentlyRunningServiceType == LONG_BREAK) {
                    if (timerButton.isChecked()) {
                        startTimer(longBreakDuration);
                    } else {
                        // When "Skip Long Break" is clicked, service is stopped and toggleButton is reset to "Start Pomodoro".
                        stopTimer(workDurationString);
                        currentlyRunningServiceType = POMODORO;
                        Utils.updateCurrentlyRunningServiceType(preferences, this, currentlyRunningServiceType);
                        currentlyRunningServiceType = Utils.retrieveCurrentlyRunningServiceType(preferences, this);
                        changeToggleButtonStateText(currentlyRunningServiceType);
                    }
                }
                break;

            case R.id.finish_imageview_main:
                if (timerButton.isChecked()) {
                    // Finish (Complete Button) stops service and sets currentlyRunningServiceType to SHORT_BREAK or LONG_BREAK and updates number of completed WorkSessions.
                    if (currentlyRunningServiceType == POMODORO) {
                        int newWorkSessionCount = Utils.updateWorkSessionCount(preferences, this);
                        workSessionCompletedTextView.setText(String.valueOf(newWorkSessionCount));
                        currentlyRunningServiceType = Utils.getTypeOfBreak(preferences, this);
                        Utils.updateCurrentlyRunningServiceType(preferences, this, currentlyRunningServiceType);
                        long duration = Utils.getCurrentDurationPreferenceOf(preferences, this, currentlyRunningServiceType);
                        stopTimer(Utils.getCurrentDurationPreferenceStringFor(duration));
                        changeToggleButtonStateText(currentlyRunningServiceType);
                    }
                }
                break;
        }
    }

    /**
     * Starts service and CountDownTimer according to duration value.
     * Duration can be initial value of either POMODORO, SHORT_BREAK or LONG_BREAK.
     *
     * @param duration is Time Period for which timer should tick
     */
    private void startTimer(long duration) {
        Intent serviceIntent = new Intent(this, CountDownTimerService.class);
        serviceIntent.putExtra("time_period", duration);
        serviceIntent.putExtra("time_interval", TIME_INTERVAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(serviceIntent);
        else
            startService(serviceIntent);
    }

    /**
     * Stops service and resets CountDownTimer to initial value.
     * Duration can be initial value of either POMODORO, SHORT_BREAK or LONG_BREAK.
     *
     * @param duration is Time Period for which timer should tick
     */
    private void stopTimer(String duration) {
        Intent serviceIntent = new Intent(getApplicationContext(), CountDownTimerService.class);
        stopService(serviceIntent);
        countDownTextView.setText(duration);
    }

    /**
     * Changes textOn, textOff for Toggle Button & Resets CountDownTimer to initial value, according to value of currentlyRunningServiceType.
     *
     * @param currentlyRunningServiceType can be POMODORO, SHORT_BREAK or LONG_BREAK.
     */
    private void changeToggleButtonStateText(int currentlyRunningServiceType) {
        //
        timerButton.setChecked(isServiceRunning(CountDownTimerService.class));
        if (currentlyRunningServiceType == POMODORO) {
            timerButton.setTextOn(getString(R.string.cancel_pomodoro));
            timerButton.setTextOff(getString(R.string.start_pomodoro));
            countDownTextView.setText(workDurationString);
        } else if (currentlyRunningServiceType == SHORT_BREAK) {
            timerButton.setTextOn(getString(R.string.skip_short_break));
            timerButton.setTextOff(getString(R.string.start_short_break));
            countDownTextView.setText(shortBreakDurationString);
        } else if (currentlyRunningServiceType == LONG_BREAK) {
            timerButton.setTextOn(getString(R.string.skip_long_break));
            timerButton.setTextOff(getString(R.string.start_long_break));
            countDownTextView.setText(longBreakDurationString);
        }

        /*
         https://stackoverflow.com/a/3792554/4593315
         While changing textOn, textOff programmatically, button doesn't redraw so I used this hack.
          */
        timerButton.setChecked(timerButton.isChecked());
    }

    /**
     * Checks if a service is running or not.
     *
     * @param serviceClass name of the Service class
     * @return true if service is running, otherwise false
     */
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}