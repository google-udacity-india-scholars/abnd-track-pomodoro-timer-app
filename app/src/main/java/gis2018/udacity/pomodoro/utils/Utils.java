package gis2018.udacity.pomodoro.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import gis2018.udacity.pomodoro.R;

import static gis2018.udacity.pomodoro.utils.Constants.LONG_BREAK;
import static gis2018.udacity.pomodoro.utils.Constants.POMODORO;
import static gis2018.udacity.pomodoro.utils.Constants.SHORT_BREAK;

public class Utils {

    /**
     * Updates value of WorkSessionCount by 1 and Writes the same to SharedPreferences.
     *
     * @param preferences Injected SharedPreferences instance from caller
     * @param context     Injected Context from caller
     * @return updated value of WorkSessionCount
     */
    public static int updateWorkSessionCount(SharedPreferences preferences, Context context) {
        // Retrieving value of workSessionCount (Current value of workSessionCount) from SharedPreference.
        int oldWorkSessionCount = preferences.getInt(context.getString(R.string.work_session_count_key), 0);

        // Updating oldWorkSessionCount by 1.
        int newWorkSessionCount = ++oldWorkSessionCount;

        // Writing value of workSessionCount after a session is completed (New value of workSessionCount) in SharedPreference.
        preferences
                .edit()
                .putInt(context.getString(R.string.work_session_count_key), newWorkSessionCount)
                .apply();

        return newWorkSessionCount;
    }

    /**
     * Returns type of break, user should take according to value of workSessionCount
     *
     * @param preferences Injected SharedPreferences instance from caller
     * @param context     Injected Context from caller
     * @return type of break, user should take
     */
    public static int getTypeOfBreak(SharedPreferences preferences, Context context) {
        int currentWorkSessionCount = preferences.getInt(context.getString(R.string.work_session_count_key), 0);
        if (currentWorkSessionCount % 4 == 0)
            return LONG_BREAK;
        return SHORT_BREAK;
    }

    /**
     * Writes new value for currentlyRunningService in SharedPreferences.
     * Value of currentlyRunningService determines values for textOn & textOff of ToggleButton in MainActivity.
     *
     * @param preferences                 Injected SharedPreferences instance from caller
     * @param context                     Injected Context from caller
     * @param currentlyRunningServiceType can be POMODORO, SHORT_BREAK or LONG_BREAK
     */
    public static void updateCurrentlyRunningServiceType(SharedPreferences preferences, Context context, int currentlyRunningServiceType) {
        preferences
                .edit()
                .putInt(context.getString(R.string.currently_running_service_type_key), currentlyRunningServiceType)
                .apply();
    }

    /**
     * @param preferences Injected SharedPreferences instance from caller
     * @param context     Injected Context from caller
     * @return current value of currentlyRunningService from SharedPreferences.
     */
    public static int retrieveCurrentlyRunningServiceType(SharedPreferences preferences, Context context) {
        return preferences.getInt(context.getString(R.string.currently_running_service_type_key), 0);
    }

    /**
     * Retrieving current value of Duration for POMODORO, SHORT_BREAK and LONG_BREAK from SharedPreferences.
     *
     * @param preferences                 Injected SharedPreferences instance from caller
     * @param context                     Injected Context from caller
     * @param currentlyRunningServiceType current value of currentlyRunningService (Can be POMODORO, SHORT_BREAK, or LONG_BREAK).
     * @return duration of CountDown for a service in milliSeconds according to value of currentlyRunningServiceType.
     */
    public static long getCurrentDurationPreferenceOf(SharedPreferences preferences, Context context, int currentlyRunningServiceType) {
        if (currentlyRunningServiceType == POMODORO) {
            // Current value of work duration stored in shared-preference
            int currentWorkDurationPreference = preferences.getInt(context.getString(R.string.work_duration_key), 1);

            // Switch case to return appropriate minute value of work duration according value stored in shared-preference.
            switch (currentWorkDurationPreference) {
                case 0:
                    return 20 * 60000; // 20 minutes
                case 1:
                    return 25 * 60000; // 25 minutes
                case 2:
                    return 30 * 60000; // 30 minutes
                case 3:
                    return 40 * 60000; // 40 minutes
                case 4:
                    return 55 * 60000; // 55 minutes
            }
        } else if (currentlyRunningServiceType == SHORT_BREAK) {
            // Current value of short-break duration stored in shared-preference
            int currentShortBreakDurationPreference = preferences.getInt(context.getString(R.string.short_break_duration_key), 1);

            // Switch case to return appropriate minute value of short-break duration according value stored in shared-preference.
            switch (currentShortBreakDurationPreference) {
                case 0:
                    return 3 * 60000; // 3 minutes
                case 1:
                    return 5 * 60000; // 5 minutes
                case 2:
                    return 10 * 60000; // 10 minutes
                case 3:
                    return 15 * 60000; // 15 minutes
            }
        } else if (currentlyRunningServiceType == LONG_BREAK) {
            // Current value of long-break duration stored in shared-preference
            int currentLongBreakDurationPreference = preferences.getInt(context.getString(R.string.long_break_duration_key), 1);

            // Switch case to return appropriate minute value of long-break duration according value stored in shared-preference.
            switch (currentLongBreakDurationPreference) {
                case 0:
                    return 10 * 60000; // 10 minutes
                case 1:
                    return 15 * 60000; // 15 minutes
                case 2:
                    return 20 * 60000; // 20 minutes
                case 3:
                    return 25 * 60000; // 25 minutes
            }
        }
        return 0;
    }

    /**
     * @param duration of currentlyRunningService (CountDown value) in milliSeconds
     * @return duration in mm:ss format from duration value in milliSeconds.
     */
    public static String getCurrentDurationPreferenceStringFor(long duration) {
        // https://stackoverflow.com/a/41589025/8411356
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration) % 60,
                TimeUnit.MILLISECONDS.toSeconds(duration) % 60);
    }
}