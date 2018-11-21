/*
 *  Audio Files downloaded from soundjay.com
 *  Enhanced at http://www.mp3smaller.com/ & http://www.mp3louder.com/
 */

package gis2018.udacity.pomodoro;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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

import static gis2018.udacity.pomodoro.utils.Constants.CHANNEL_ID;
import static gis2018.udacity.pomodoro.utils.Constants.LONG_BREAK;
import static gis2018.udacity.pomodoro.utils.Constants.POMODORO;
import static gis2018.udacity.pomodoro.utils.Constants.SHORT_BREAK;
import static gis2018.udacity.pomodoro.utils.Constants.TASK_INFORMATION_NOTIFICATION_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final long TIME_INTERVAL = 1000; // Time Interval is 1 second

    public static SoundPool soundPool;
    public static int tickID, ringID;

    BroadcastReceiver stoppedIntentReceiver;
    BroadcastReceiver countDownReceiver;
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

    private int currentlyRunningServiceType; // Type of Service can be POMODORO, SHORT_BREAK or LONG_BREAK
    private long workDuration; // Time Period for Pomodoro (Work-Session)
    private String workDurationString; // Time Period for Pomodoro in String
    private long shortBreakDuration; // Time Period for Short-Break
    private String shortBreakDurationString; // Time Period for Short-Break in String
    private long longBreakDuration; // Time Period for Long-Break
    private String longBreakDurationString; // Time Period for Long-Break in String
    private SharedPreferences preferences;
    private int workSessionCount = 0; // Number of Completed Work-Sessions
    private AlertDialog alertDialog;
    private boolean isAppVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isAppVisible = true;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        ButterKnife.bind(this);
        settingsImageView.setOnClickListener(this);
        changeButton.setOnClickListener(this);
        timerButton.setOnClickListener(this);
        finishImageView.setOnClickListener(this);

        //Preparing SoundPool to play ticking sounds
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        tickID = soundPool.load(this, R.raw.clockticking, 1);
        ringID = soundPool.load(this, R.raw.bellringing, 2);

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
                currentlyRunningServiceType = Utils.retrieveCurrentlyRunningServiceType(preferences, getApplicationContext());
                // Changing textOn & textOff according to value of currentlyRunningServiceType.
                changeToggleButtonStateText(currentlyRunningServiceType);
                unregisterLocalBroadcastReceivers();
                alertDialog = createPomodoroCompletionAlertDialog();
                displayPomodoroCompletionAlertDialog();
                displayTaskInformationNotification();
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

        alertDialog = createPomodoroCompletionAlertDialog();
        displayPomodoroCompletionAlertDialog();
    }

    @Override
    protected void onStart() {
        isAppVisible = true;
        currentlyRunningServiceType = Utils.retrieveCurrentlyRunningServiceType(preferences, this);
        super.onStart();
    }

    @Override
    protected void onResume() {
        isAppVisible = true;
        registerLocalBroadcastReceivers();
        // Creates new Alert Dialog.
        alertDialog = createPomodoroCompletionAlertDialog();
        displayPomodoroCompletionAlertDialog();
        super.onResume();
    }

    @Override
    protected void onPause() {
        isAppVisible = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        isAppVisible = false;
        if (!isServiceRunning(CountDownTimerService.class)) {
            unregisterLocalBroadcastReceivers();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        isAppVisible = false;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        registerLocalBroadcastReceivers();

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
                        switchToPomodoro();
                    }
                } else if (currentlyRunningServiceType == SHORT_BREAK) {
                    if (timerButton.isChecked()) {
                        startTimer(shortBreakDuration);
                    } else {
                        // When "Skip Short Break" is clicked, service is stopped and toggleButton is reset to "Start Pomodoro".
                        switchToPomodoro();
                    }
                } else if (currentlyRunningServiceType == LONG_BREAK) {
                    if (timerButton.isChecked()) {
                        startTimer(longBreakDuration);
                    } else {
                        // When "Skip Long Break" is clicked, service is stopped and toggleButton is reset to "Start Pomodoro".
                        switchToPomodoro();
                    }
                }
                break;

            case R.id.finish_imageview_main:
                if (timerButton.isChecked()) {
                    // Finish (Complete Button) stops service and sets currentlyRunningServiceType to SHORT_BREAK or LONG_BREAK and updates number of completed WorkSessions.
                    if (currentlyRunningServiceType == POMODORO) {

                        // Updates newWorkSessionCount in SharedPreferences and displays it on TextView.
                        int newWorkSessionCount = Utils.updateWorkSessionCount(preferences, this);
                        workSessionCompletedTextView.setText(String.valueOf(newWorkSessionCount));

                        // Retrieves type of break user should take, either SHORT_BREAK or LONG_BREAK, and updates value of currentlyRunningService in SharedPreferences.
                        currentlyRunningServiceType = Utils.getTypeOfBreak(preferences, this);
                        Utils.updateCurrentlyRunningServiceType(preferences, this, currentlyRunningServiceType);

                        long duration = Utils.getCurrentDurationPreferenceOf(preferences, this, currentlyRunningServiceType);
                        stopTimer(Utils.getCurrentDurationPreferenceStringFor(duration));
                        soundPool.play(ringID, 0.5f, 0.5f, 1, 0, 1f);
                        changeToggleButtonStateText(currentlyRunningServiceType);
                        unregisterLocalBroadcastReceivers();
                        alertDialog = createPomodoroCompletionAlertDialog();
                        displayPomodoroCompletionAlertDialog();
                        displayTaskInformationNotification();
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
     * @param duration is Time Period for which timer should tick.
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
     * Registers LocalBroadcastReceivers.
     */
    private void registerLocalBroadcastReceivers() {
        LocalBroadcastManager.getInstance(this).registerReceiver((stoppedIntentReceiver),
                new IntentFilter(CountDownTimerService.STOP_ACTION_BROADCAST));
        LocalBroadcastManager.getInstance(this).registerReceiver((countDownReceiver),
                new IntentFilter(CountDownTimerService.COUNTDOWN_BROADCAST));
    }

    /**
     * Unregisters LocalBroadcastReceivers.
     */
    private void unregisterLocalBroadcastReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stoppedIntentReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(countDownReceiver);
    }

    /**
     * Switch to Pomodoro (Work-Session) after completion of Short-Break or Long-Break.
     */
    private void switchToPomodoro() {
        stopTimer(workDurationString);

        // Clearing any previous notifications.
        NotificationManagerCompat
                .from(getApplicationContext())
                .cancel(TASK_INFORMATION_NOTIFICATION_ID);

        if (alertDialog != null)
            alertDialog.cancel();
        currentlyRunningServiceType = POMODORO;
        Utils.updateCurrentlyRunningServiceType(preferences, this, currentlyRunningServiceType);
        currentlyRunningServiceType = Utils.retrieveCurrentlyRunningServiceType(preferences, this);
        changeToggleButtonStateText(currentlyRunningServiceType);
        unregisterLocalBroadcastReceivers();
    }

    /**
     * Checks if a service is running or not.
     *
     * @param serviceClass name of the Service class.
     * @return true if service is running, otherwise false.
     */
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates layout for alert-dialog, which is shown when Pomodoro (Work-Session) is completed.
     *
     * @return alert-dialog
     */
    private AlertDialog createPomodoroCompletionAlertDialog() {
        if (alertDialog != null)
            alertDialog.cancel();

        View alertDialogLayout = View.inflate(getApplicationContext(), R.layout.layout_alert_dialog, null);
        final Button startBreakLargeButton = alertDialogLayout.findViewById(R.id.start_break);
        final Button startOtherBreakMediumButton = alertDialogLayout.findViewById(R.id.start_other_break);
        Button skipBreakSmallButton = alertDialogLayout.findViewById(R.id.skip_break);

        if (currentlyRunningServiceType == SHORT_BREAK) {
            startBreakLargeButton.setText(R.string.start_short_break);
            startOtherBreakMediumButton.setText(R.string.start_long_break);
        } else if (currentlyRunningServiceType == LONG_BREAK) {
            startBreakLargeButton.setText(R.string.start_long_break);
            startOtherBreakMediumButton.setText(R.string.start_short_break);
        }

        startBreakLargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentButtonText = startBreakLargeButton.getText().toString();
                startBreakFromAlertDialog(currentButtonText);
            }
        });

        startOtherBreakMediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentButtonText = startOtherBreakMediumButton.getText().toString();
                startBreakFromAlertDialog(currentButtonText);
            }
        });

        skipBreakSmallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToPomodoro();
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(alertDialogLayout);
        alertDialogBuilder.setCancelable(false);
        return alertDialogBuilder.create();
    }

    /**
     * Displays alert dialog when a Pomodoro (Work-Session) is finished.
     */
    private void displayPomodoroCompletionAlertDialog() {
        if (currentlyRunningServiceType != POMODORO && isAppVisible && !alertDialog.isShowing() && !isServiceRunning(CountDownTimerService.class)) {
            alertDialog.show();
        }
    }

    /**
     * Sets appropriate values for medium and large button, and starts service; either SHORT_BREAK or LONG_BREAK.
     *
     * @param currentButtonText button text of either medium button or large button.
     */
    private void startBreakFromAlertDialog(String currentButtonText) {
        long breakDuration = 0;
        if (currentButtonText.equals(getString(R.string.start_long_break))) {
            Utils.updateCurrentlyRunningServiceType(preferences, getApplicationContext(), LONG_BREAK);
            breakDuration = longBreakDuration;
        } else if (currentButtonText.equals(getString(R.string.start_short_break))) {
            Utils.updateCurrentlyRunningServiceType(preferences, getApplicationContext(), SHORT_BREAK);
            breakDuration = shortBreakDuration;
        }

        currentlyRunningServiceType = Utils.retrieveCurrentlyRunningServiceType(preferences, getApplicationContext());
        if (alertDialog != null)
            alertDialog.cancel();
        registerLocalBroadcastReceivers();
        changeToggleButtonStateText(currentlyRunningServiceType);
        startTimer(breakDuration);
        timerButton.setChecked(isServiceRunning(CountDownTimerService.class));
    }

    /**
     * Creates structure for a notification which is shown when a task is Completed.
     * Task can be POMODORO, SHORT_BREAK, LONG_BREAK
     *
     * @return notification.
     */
    private NotificationCompat.Builder createTaskInformationNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        String notificationContentText;

        if (currentlyRunningServiceType == POMODORO)
            notificationContentText = getString(R.string.start_pomodoro);
        else
            notificationContentText = getString(R.string.pomodoro_completion_alert_message);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("Pomodoro Countdown Timer")
                .setContentIntent(pendingIntent)
                .setContentText(notificationContentText)
                .setAutoCancel(true);
    }

    /**
     * Displays a notification when foreground service is finished.
     */
    private void displayTaskInformationNotification() {
        Notification notification = createTaskInformationNotification().build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat
                .from(this);

        // Clearing any previous notifications.
        notificationManagerCompat
                .cancel(TASK_INFORMATION_NOTIFICATION_ID);

        // Displays a notification.
        if (!isServiceRunning(CountDownTimerService.class)) {
            notificationManagerCompat
                    .notify(TASK_INFORMATION_NOTIFICATION_ID, notification);
        }
    }
}
