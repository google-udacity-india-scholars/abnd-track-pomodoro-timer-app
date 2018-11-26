package gis2018.udacity.pomodoro;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import gis2018.udacity.pomodoro.utils.Utils;

import static gis2018.udacity.pomodoro.MainActivity.ringID;
import static gis2018.udacity.pomodoro.MainActivity.soundPool;
import static gis2018.udacity.pomodoro.MainActivity.tickID;
import static gis2018.udacity.pomodoro.utils.Constants.CHANNEL_ID;
import static gis2018.udacity.pomodoro.utils.Constants.POMODORO;
import static gis2018.udacity.pomodoro.utils.Constants.TASK_INFORMATION_NOTIFICATION_ID;

public class CountDownTimerService extends Service {
    public static final int ID = 1;
    public static final String COUNTDOWN_BROADCAST = "com.gis2018.countdown";
    public static final String STOP_ACTION_BROADCAST = "com.gis2018.stop.action";
    LocalBroadcastManager broadcaster;
    private CountDownTimer countDownTimer;
    private SharedPreferences preferences;
    private int newWorkSessionCount;
    private int currentlyRunningServiceType;

    public CountDownTimerService() {
    }

    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onDestroy() {
        countDownTimer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long TIME_PERIOD = intent.getLongExtra("time_period", 0);
        long TIME_INTERVAL = intent.getLongExtra("time_interval", 0);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        //For "Complete" button - Intent and PendingIntent
        Intent completeIntent = new Intent(this,MainActivity.class);
        completeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent completeActionPendingIntent = PendingIntent.getActivity(this,
                0,completeIntent,0);
        Utils.isCompleteActionClick = true;

        //For "Cancel" button - Intent and PendingIntent
        Intent cancelIntent = new Intent(this,ActionReceiver.class);
        PendingIntent cancelActionPendingIntent = PendingIntent.getBroadcast(this,
                0,cancelIntent,0);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("Pomodoro Countdown Timer")
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentIntent(pendingIntent)
                .setContentText("Countdown timer is running")
                .addAction(R.drawable.complete,"Complete",completeActionPendingIntent).setColor(Color.GREEN)
                .addAction(R.drawable.cancel,"Cancel",cancelActionPendingIntent).setColor(Color.RED)
                .build();

        // Clearing any previous notifications.
        NotificationManagerCompat
                .from(this)
                .cancel(TASK_INFORMATION_NOTIFICATION_ID);

        startForeground(ID, notification);
        countDownTimerBuilder(TIME_PERIOD, TIME_INTERVAL).start();
        return START_REDELIVER_INTENT;
    }

    /**
     * @return a CountDownTimer which ticks every 1 second for given Time period.
     */
    private CountDownTimer countDownTimerBuilder(long TIME_PERIOD, long TIME_INTERVAL) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentlyRunningServiceType = Utils.retrieveCurrentlyRunningServiceType(preferences, getApplicationContext());
        countDownTimer = new CountDownTimer(TIME_PERIOD, TIME_INTERVAL) {
            @Override
            public void onTick(long timeInMilliSeconds) {
                soundPool.play(tickID, 0.5f, 0.5f, 1, 0, 1f);

                String countDown = Utils.getCurrentDurationPreferenceStringFor(timeInMilliSeconds);

                broadcaster.sendBroadcast(
                        new Intent(COUNTDOWN_BROADCAST)
                                .putExtra("countDown", countDown));
            }

            @Override
            public void onFinish() {
                // Updates and Retrieves new value of WorkSessionCount.
                if (currentlyRunningServiceType == POMODORO) {
                    newWorkSessionCount = Utils.updateWorkSessionCount(preferences, getApplicationContext());
                    // Getting type of break user should take, and updating type of currently running service
                    currentlyRunningServiceType = Utils.getTypeOfBreak(preferences, getApplicationContext());
                } else {
                    // If last value of currentlyRunningServiceType was SHORT_BREAK or LONG_BREAK then set it back to POMODORO
                    currentlyRunningServiceType = POMODORO;
                }

                newWorkSessionCount = preferences.getInt(getString(R.string.work_session_count_key), 0);
                // Updating value of currentlyRunningServiceType in SharedPreferences.
                Utils.updateCurrentlyRunningServiceType(preferences, getApplicationContext(), currentlyRunningServiceType);
                //Ring once ticking ends.
                soundPool.play(ringID, 0.5f, 0.5f, 1, 0, 1f);

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
