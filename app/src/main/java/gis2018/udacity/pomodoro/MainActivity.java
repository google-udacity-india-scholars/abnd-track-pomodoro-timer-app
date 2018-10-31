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
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String LOG_TAG = "pomodoro Issue-1 Test";
    private static final long TIME_PERIOD = 5000; // Time Period is 5 seconds
    private static final long TIME_INTERVAL = 1000; // Time Interval is 1 second
    private static final String COUNTDOWN_FINISHED_MESSAGE = "CountDown Finished";
    BroadcastReceiver receiver;

    @BindView(R.id.settings_button_main)
    Button settingsButton;
    @BindView(R.id.task_change_button_main)
    Button changeButton;
    @BindView(R.id.timer_button_main)
    ToggleButton timerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        settingsButton.setOnClickListener(this);
        changeButton.setOnClickListener(this);
        timerButton.setOnCheckedChangeListener(this);

        if(isServiceRunning(CountDownTimerService.class)){
            // service is running
            // enable the button
            timerButton.setChecked(true);
        }
        else{
            timerButton.setChecked(false);
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timerButton.setChecked(false);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(CountDownTimerService.STOP_ACTION_BROADCAST));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }
    @Override
    public void onClick(View v) {
        // switch case to handle different button clicks
        switch (v.getId()) {
            // Settings button is clicked
            case R.id.settings_button_main:
                // launch SettingsActivity
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.task_change_button_main:
                // Todo: Define on task change
                break;
            default:

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            // start timer
            Intent serviceIntent = new Intent(this, CountDownTimerService.class);
            serviceIntent.putExtra("time_period", TIME_PERIOD);
            serviceIntent.putExtra("time_interval", TIME_INTERVAL);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(serviceIntent);
            else
                startService(serviceIntent);
        } else {
            // stop timer
            Log.v("Triggered", String.valueOf(isChecked));
            Intent serviceIntent = new Intent(this, CountDownTimerService.class);
            stopService(serviceIntent);
        }
    }

    /**
     * Checks if a service is running or not.
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