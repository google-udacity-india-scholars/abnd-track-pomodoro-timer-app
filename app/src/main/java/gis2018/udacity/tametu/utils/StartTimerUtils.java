package gis2018.udacity.tametu.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import gis2018.udacity.tametu.CountDownTimerService;

import static gis2018.udacity.tametu.utils.Constants.TIME_INTERVAL;

public class StartTimerUtils {

    public static void startTimer(long duration, Context context) {
        Intent serviceIntent = new Intent(context, CountDownTimerService.class);
        serviceIntent.putExtra("time_period", duration);
        serviceIntent.putExtra("time_interval", TIME_INTERVAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(serviceIntent);
        else
            context.startService(serviceIntent);
    }
}
