package gis2018.udacity.pomodoro;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import static gis2018.udacity.pomodoro.App.CHANNEL_ID;

public class CountDownTimerService extends Service {
    public static final int ID = 1;
    public static final String STOP_ACTION_BROADCAST = "com.gis2018.stop.action";
    private static final String LOG_TAG = "CntDwnTmrService_TAG";
    LocalBroadcastManager broadcaster;
    private CountDownTimer countDownTimer;

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

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Pomodoro Countdown Timer")
                .setContentIntent(pendingIntent)
                .setContentText("Countdown timer is running")
                .build();

        startForeground(ID, notification);
        countDownTimerBuilder(TIME_PERIOD, TIME_INTERVAL, "CountDown Finished").start();
        return START_REDELIVER_INTENT;
    }

    /**
     * @return a CountDownTimer which ticks every 1 second for a fixed 5 seconds period.
     */
    private CountDownTimer countDownTimerBuilder(long TIME_PERIOD, long TIME_INTERVAL,
                                                 final String END_MESSAGE) {
        countDownTimer = new CountDownTimer(TIME_PERIOD, TIME_INTERVAL) {
            @Override
            public void onTick(long timeInMilliSeconds) {
                long timeInSeconds = timeInMilliSeconds / 1000;
                if (timeInSeconds != 1) {
                    Log.v(LOG_TAG, String.valueOf(timeInMilliSeconds / 1000) + " seconds remaining");
                } else {
                    Log.v(LOG_TAG, String.valueOf(timeInMilliSeconds / 1000) + " second remaining");
                }
            }

            @Override
            public void onFinish() {
                Log.v(LOG_TAG, END_MESSAGE);
                stopSelf();
                stoppedBroadcastIntent();
            }
        };
        return countDownTimer;
    }

    //Broadcasts intent that the timer has stopped.
    protected void stoppedBroadcastIntent() {
        Intent localIntent = new Intent(STOP_ACTION_BROADCAST);
        broadcaster.sendBroadcast(localIntent);
    }
}
