package gis2018.udacity.tametu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import gis2018.udacity.tametu.utils.Utils;

import static gis2018.udacity.tametu.utils.Constants.INTENT_NAME_ACTION;
import static gis2018.udacity.tametu.utils.Constants.INTENT_VALUE_LONG_BREAK;
import static gis2018.udacity.tametu.utils.Constants.INTENT_VALUE_SHORT_BREAK;
import static gis2018.udacity.tametu.utils.Constants.INTENT_VALUE_START;
import static gis2018.udacity.tametu.utils.Constants.LONG_BREAK;
import static gis2018.udacity.tametu.utils.Constants.SHORT_BREAK;
import static gis2018.udacity.tametu.utils.Constants.TAMETU;
import static gis2018.udacity.tametu.utils.StartTimerUtils.startTimer;

public class StartTimerActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String receivedIntent = intent.getStringExtra(INTENT_NAME_ACTION);
        SharedPreferences prefences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (receivedIntent) {
            case INTENT_VALUE_START:
                long workDuration = Utils.getCurrentDurationPreferenceOf(prefences, context,
                        TAMETU);
                startTimer(workDuration, context);
                Log.d("TIMER was started with", String.valueOf(workDuration));
                break;
            case INTENT_VALUE_SHORT_BREAK:
                long shortBreakDuration = Utils.getCurrentDurationPreferenceOf(prefences, context,
                        SHORT_BREAK);
                startTimer(shortBreakDuration, context);
                Log.d("SHRT_BRK started with", String.valueOf(shortBreakDuration));
                break;
            case INTENT_VALUE_LONG_BREAK:
                long longBreakDuration = Utils.getCurrentDurationPreferenceOf(prefences, context,
                        LONG_BREAK);
                startTimer(longBreakDuration, context);
                Log.d("LONG_BRK started with", String.valueOf(longBreakDuration));
        }
    }
}
