package gis2018.udacity.pomodoro;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "pomodoro Issue-1 Test";
    private static final long TIME_PERIOD = 5000; // Time Period is 5 seconds
    private static final long TIME_INTERVAL = 1000; // Time Interval is 1 second
    private static final String COUNTDOWN_FINISHED_MESSAGE = "CountDown Finished";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        countDownTimerBuilder().start(); // start method starts the CountDownTimer
    }

    /**
     *
     * @return a CountDownTimer which ticks every 1 second for a fixed 5 seconds period.
     */
    private CountDownTimer countDownTimerBuilder() {
        return new CountDownTimer(TIME_PERIOD, TIME_INTERVAL) {
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
                Log.v(LOG_TAG, COUNTDOWN_FINISHED_MESSAGE);
            }
        };
    }
}