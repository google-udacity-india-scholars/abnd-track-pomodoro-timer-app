package gis2018.udacity.pomodoro;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "pomodoro Issue-1 Test";
    private static final long TIME_PERIOD = 5000; // Time Period is 5 seconds
    private static final long TIME_INTERVAL = 1000; // Time Interval is 1 second
    private static final String COUNTDOWN_FINISHED_MESSAGE = "CountDown Finished";
    private CountDownTimer countDownTimer;
    private long timeCountInMilliSeconds = 1 * 60000;
    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    @BindView(R.id.settings_button_main)
    Button settingsButton;
    @BindView(R.id.task_change_button_main)
    Button changeButton;
    private ProgressBar progressBarCircle;
    private ImageView imageViewStartStop;
    private TextView textViewTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBarCircle=(ProgressBar)findViewById(R.id.progressBarCircle);
        imageViewStartStop = (ImageView) findViewById(R.id.imageViewStartStop);
        textViewTime = (TextView) findViewById(R.id.countdown_textview_main);

        ButterKnife.bind(this);
        settingsButton.setOnClickListener(this);
        changeButton.setOnClickListener(this);
        imageViewStartStop.setOnClickListener(this);
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
            case R.id.imageViewStartStop:
//                Intent serviceIntent = new Intent(this, CountDownTimerService.class);
//                serviceIntent.putExtra("time_period", TIME_PERIOD);
//                serviceIntent.putExtra("time_interval", TIME_INTERVAL);
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                    startForegroundService(serviceIntent);
//                else
//                    startService(serviceIntent);
                startStop();
                break;
            default:

        }
    }
    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {

            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
            // showing the reset icon
//            imageViewReset.setVisibility(View.VISIBLE);
            // changing play icon to stop icon
            imageViewStartStop.setImageResource(R.drawable.icon_stop);
            // making edit text not editable
           // editTextMinute.setEnabled(false);
            // changing the timer status to started
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {

            // hiding the reset icon
            //imageViewReset.setVisibility(View.GONE);
            // changing stop icon to start icon
            imageViewStartStop.setImageResource(R.drawable.icon_start);
            // making edit text editable
           // editTextMinute.setEnabled(true);
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();

        }

    }
    private void setTimerValues() {
        int time = 2;
        //For the started i have given the fixed value to the timer what we have to do is fetch the value from the shared preference from the setting
        //Activity ,Work duration and give time to this time variable and then it's done.

//        if (!editTextMinute.getText().toString().isEmpty()) {
//            // fetching value from edit text and type cast to integer
//            time = Integer.parseInt(editTextMinute.getText().toString().trim());
//        } else {
//            // toast message to fill edit text
//           // Toast.makeText(getApplicationContext(), getString(R.string.message_minutes), Toast.LENGTH_LONG).show();
//        }
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time * 60 * 1000;
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {

                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                setProgressBarValues();
                // hiding the reset icon
                //imageViewReset.setVisibility(View.GONE);
                // changing stop icon to start icon
                imageViewStartStop.setImageResource(R.drawable.icon_start);
                // making edit text editable
               // editTextMinute.setEnabled(true);
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;
            }

        }.start();
        countDownTimer.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }


    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }
}
