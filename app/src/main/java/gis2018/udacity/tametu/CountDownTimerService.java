package gis2018.udacity.tametu;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import gis2018.udacity.tametu.utils.Utils;

import static gis2018.udacity.tametu.utils.Constants.CHANNEL_ID;
import static gis2018.udacity.tametu.utils.Constants.COUNTDOWN_BROADCAST;
import static gis2018.udacity.tametu.utils.Constants.INTENT_NAME_ACTION;
import static gis2018.udacity.tametu.utils.Constants.INTENT_VALUE_CANCEL;
import static gis2018.udacity.tametu.utils.Constants.INTENT_VALUE_COMPLETE;
import static gis2018.udacity.tametu.utils.Constants.STOP_ACTION_BROADCAST;
import static gis2018.udacity.tametu.utils.Constants.TAMETU;
import static gis2018.udacity.tametu.utils.Constants.TASK_INFORMATION_NOTIFICATION_ID;
import static gis2018.udacity.tametu.utils.Constants.TASK_MESSAGE;
import static gis2018.udacity.tametu.utils.Utils.ringID;
import static gis2018.udacity.tametu.utils.Utils.soundPool;
import static gis2018.udacity.tametu.utils.Utils.tickID;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.floatRingingVolumeLevel;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.floatTickingVolumeLevel;

public class CountDownTimerService extends Service {
    public static final int ID = 1;
    LocalBroadcastManager broadcaster;
    private CountDownTimer countDownTimer;
    private SharedPreferences preferences;
    private int newWorkSessionCount;
    private int currentlyRunningServiceType;

    public CountDownTimerService() {
    }

    @Override
    public void onCreate() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long TIME_PERIOD = intent.getLongExtra("time_period", 0);
        long TIME_INTERVAL = intent.getLongExtra("time_interval", 0);
        currentlyRunningServiceType = Utils.retrieveCurrentlyRunningServiceType(preferences, this);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        //For "Complete" button - Intent and PendingIntent
        Intent completeIntent = new Intent(this, StopTimerActionReceiver.class)
                .putExtra(INTENT_NAME_ACTION, INTENT_VALUE_COMPLETE);
        PendingIntent completeActionPendingIntent = PendingIntent.getBroadcast(this,
                1, completeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //For "Cancel" button - Intent and PendingIntent
        Intent cancelIntent = new Intent(this, StopTimerActionReceiver.class)
                .putExtra(INTENT_NAME_ACTION, INTENT_VALUE_CANCEL);
        PendingIntent cancelActionPendingIntent = PendingIntent.getBroadcast(this,
                0, cancelIntent, 0);

        Notification.Builder notificationBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(this, CHANNEL_ID);

        } else {
            notificationBuilder = new Notification.Builder(this);
        }

        notificationBuilder = notificationBuilder
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setOngoing(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationBuilder = notificationBuilder
                    .setWhen(System.currentTimeMillis() + TIME_PERIOD)
                    .setUsesChronometer(true)
                    .setChronometerCountDown(true);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder = notificationBuilder
                    .setColor(getResources().getColor(R.color.colorPrimary));

        }

        //adding separate cases for both service types to ensure the order of the action
        //buttons is preserved in the notification
        switch (currentlyRunningServiceType) {
            case 0:
                notificationBuilder
                        .addAction(R.drawable.complete, "Complete", completeActionPendingIntent)
                        .addAction(R.drawable.cancel, "Cancel", cancelActionPendingIntent)
                        .setContentTitle("Tametu Countdown Timer")
                        .setContentText(getContentText());
                break;
            case 1:
            case 2:
                notificationBuilder
                        .addAction(R.drawable.cancel, "Cancel", cancelActionPendingIntent)
                        .setContentTitle("On a break")
                        .setContentText("Break timer is running");
                break;
        }

        Notification notification = notificationBuilder
                .build();

        // Clearing any previous notifications.
        NotificationManagerCompat
                .from(this)
                .cancel(TASK_INFORMATION_NOTIFICATION_ID);

        startForeground(ID, notification);
        countDownTimerBuilder(TIME_PERIOD, TIME_INTERVAL).start();
        return START_REDELIVER_INTENT;
    }

    private String getContentText() {
        String contentText;
        String taskMessage = preferences.getString(TASK_MESSAGE, null);
        assert taskMessage != null;
        if (!taskMessage.equals("")) {
            contentText = taskMessage + " is running";
        } else {
            contentText = "Countdown timer is running";
        }
        return contentText;
    }

    /**
     * @return a CountDownTimer which ticks every 1 second for given Time period.
     */
    private CountDownTimer countDownTimerBuilder(long TIME_PERIOD, long TIME_INTERVAL) {
        currentlyRunningServiceType = Utils.retrieveCurrentlyRunningServiceType(preferences, getApplicationContext());
        countDownTimer = new CountDownTimer(TIME_PERIOD, TIME_INTERVAL) {
            @Override
            public void onTick(long timeInMilliSeconds) {
                soundPool.play(tickID,
                        floatTickingVolumeLevel,
                        floatTickingVolumeLevel,
                        1, 0, 1f);

                String countDown = Utils.getCurrentDurationPreferenceStringFor(timeInMilliSeconds);

                broadcaster.sendBroadcast(
                        new Intent(COUNTDOWN_BROADCAST)
                                .putExtra("countDown", countDown));
            }

            @Override
            public void onFinish() {
                // Updates and Retrieves new value of WorkSessionCount.
                if (currentlyRunningServiceType == TAMETU) {
                    newWorkSessionCount = Utils.updateWorkSessionCount(preferences, getApplicationContext());
                    // Getting type of break user should take, and updating type of currently running service
                    currentlyRunningServiceType = Utils.getTypeOfBreak(preferences, getApplicationContext());
                } else {
                    // If last value of currentlyRunningServiceType was SHORT_BREAK or LONG_BREAK then set it back to POMODORO
                    currentlyRunningServiceType = TAMETU;
                }

                newWorkSessionCount = preferences.getInt(getString(R.string.work_session_count_key), 0);
                // Updating value of currentlyRunningServiceType in SharedPreferences.
                Utils.updateCurrentlyRunningServiceType(preferences, getApplicationContext(), currentlyRunningServiceType);
                //Ring once ticking ends.
                soundPool.play(ringID,
                        floatRingingVolumeLevel,
                        floatRingingVolumeLevel,
                        2, 0, 1f);
                stopSelf();
                stoppedBroadcastIntent();
            }
        };
        return countDownTimer;
    }

    // Broadcasts intent that the timer has stopped.
    protected void stoppedBroadcastIntent() {
        broadcaster.sendBroadcast(
                new Intent(STOP_ACTION_BROADCAST)
                        .putExtra("workSessionCount", newWorkSessionCount));
    }
}
