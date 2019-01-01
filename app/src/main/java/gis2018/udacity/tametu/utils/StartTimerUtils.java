package gis2018.udacity.tametu.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import gis2018.udacity.tametu.CountDownTimerService;

import static gis2018.udacity.tametu.utils.Constants.RINGING_VOLUME_LEVEL_KEY;
import static gis2018.udacity.tametu.utils.Constants.TICKING_VOLUME_LEVEL_KEY;
import static gis2018.udacity.tametu.utils.Constants.TIME_INTERVAL;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.convertToFloat;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.floatRingingVolumeLevel;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.floatTickingVolumeLevel;
import static gis2018.udacity.tametu.utils.VolumeSeekBarUtils.maxVolume;

public class StartTimerUtils {

    /**
     * Starts service and CountDownTimer according to duration value.
     * Duration can be initial value of either POMODORO, SHORT_BREAK or LONG_BREAK.
     *
     * @param duration is Time Period for which timer should tick
     */
    public static void startTimer(long duration, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        floatTickingVolumeLevel = convertToFloat(preferences
                .getInt(TICKING_VOLUME_LEVEL_KEY, maxVolume), maxVolume); //set ticking volume
        floatRingingVolumeLevel = convertToFloat(preferences
                .getInt(RINGING_VOLUME_LEVEL_KEY, maxVolume), maxVolume); //also set ringing volume
        Intent serviceIntent = new Intent(context, CountDownTimerService.class);
        serviceIntent.putExtra("time_period", duration);
        serviceIntent.putExtra("time_interval", TIME_INTERVAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(serviceIntent);
        else
            context.startService(serviceIntent);
    }
}
