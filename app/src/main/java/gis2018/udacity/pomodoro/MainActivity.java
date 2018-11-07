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

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private long workDuration; // Time Period
    private String workDurationString; // Time Period String
    private static final long TIME_INTERVAL = 1000; // Time Interval is 1 second
    BroadcastReceiver stopppedIntentReceiver;
    BroadcastReceiver countDownReceiver;
    private SharedPreferences preferences;

    @BindView(R.id.settings_imageview_main)
    ImageView settingsImageView;
    @BindView(R.id.task_change_button_main)
    Button changeButton;
    @BindView(R.id.timer_button_main)
    ToggleButton timerButton;
    @BindView(R.id.countdown_textview_main)
    TextView countDownTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        settingsImageView.setOnClickListener(this);
        changeButton.setOnClickListener(this);
        timerButton.setOnClickListener(this);

        // Set button as checked if the service is already running
        timerButton.setChecked(isServiceRunning(CountDownTimerService.class));

        // Receives broadcast that the timer has stopped
        stopppedIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timerButton.setChecked(false);
            }
        };

        // Receiver broadcast for countDown at every tick
        countDownReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null)
                    countDownTextView.setText(intent.getExtras().getString("countDown"));
            }
        };

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        workDuration = getCurrentWorkDurationPreference();

        // https://stackoverflow.com/a/41589025/8411356
        workDurationString = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(this.workDuration) % 60,
                TimeUnit.MILLISECONDS.toSeconds(this.workDuration) % 60);

        countDownTextView.setText(workDurationString);
    }

    private long getCurrentWorkDurationPreference() {
        // current value of work duration stored in shared-preference
        int currentWorkDurationPreference = preferences.getInt(getString(R.string.work_duration_key), -1);

        // switch case to return appropriate minute value of work duration according value stored in shared-preference.
        switch (currentWorkDurationPreference) {
            case 0:
                return 20 * 60000; // 20 minutes
            case 1:
                return 25 * 60000; // 25 minutes
            case 2:
                return 30 * 60000; // 30 minutes
            case 3:
                return 40 * 60000; // 40 minutes
            case 4:
                return 55 * 60000; // 55 minutes
        }
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((stopppedIntentReceiver),
                new IntentFilter(CountDownTimerService.STOP_ACTION_BROADCAST));
        LocalBroadcastManager.getInstance(this).registerReceiver((countDownReceiver),
                new IntentFilter(CountDownTimerService.COUNTDOWN_BROADCAST));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stopppedIntentReceiver);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        // switch case to handle different button clicks
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
                if (timerButton.isChecked()) {
                    // start timer
                    Intent serviceIntent = new Intent(this, CountDownTimerService.class);
                    serviceIntent.putExtra("time_period", workDuration);
                    serviceIntent.putExtra("time_interval", TIME_INTERVAL);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        startForegroundService(serviceIntent);
                    else
                        startService(serviceIntent);
                } else {
                    // stop timer
                    Intent serviceIntent = new Intent(this, CountDownTimerService.class);
                    stopService(serviceIntent);
                    countDownTextView.setText(workDurationString);
                }
            default:

        }
    }

    /**
     * Checks if a service is running or not.
     *
     * @param serviceClass
     * @return
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