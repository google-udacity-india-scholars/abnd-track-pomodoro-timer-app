package gis2018.udacity.tametu.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import gis2018.udacity.tametu.R;
import gis2018.udacity.tametu.StartTimerActionReceiver;

import static gis2018.udacity.tametu.utils.Constants.INTENT_NAME_ACTION;
import static gis2018.udacity.tametu.utils.Constants.INTENT_VALUE_LONG_BREAK;
import static gis2018.udacity.tametu.utils.Constants.INTENT_VALUE_SHORT_BREAK;
import static gis2018.udacity.tametu.utils.Constants.INTENT_VALUE_START;
import static gis2018.udacity.tametu.utils.Constants.LONG_BREAK;
import static gis2018.udacity.tametu.utils.Constants.SHORT_BREAK;
import static gis2018.udacity.tametu.utils.Constants.TAMETU;

public class NotificationActionUtils {
    /**
     * @param currentlyRunningServiceType The next service that shall be run
     * @return Returns Action Buttons and assigns pendingIntents to Actions
     */
    public static NotificationCompat.Action getIntervalAction(int currentlyRunningServiceType,
                                                              Context context) {

        switch (currentlyRunningServiceType) {
            case TAMETU:
                return new NotificationCompat.Action(
                        R.drawable.complete,
                        context.getString(R.string.start_tametu),
                        getPendingIntent(INTENT_VALUE_START, context));
            case SHORT_BREAK:
                return new NotificationCompat.Action(
                        R.drawable.complete,
                        context.getString(R.string.start_short_break),
                        getPendingIntent(INTENT_VALUE_SHORT_BREAK, context));
            case LONG_BREAK:
                return new NotificationCompat.Action(
                        R.drawable.complete,
                        context.getString(R.string.start_long_break),
                        getPendingIntent(INTENT_VALUE_LONG_BREAK, context));
            default:
                return null;
        }

    }

    private static PendingIntent getPendingIntent(String INTENT_VALUE, Context context) {
        Intent startIntent = new Intent(context, StartTimerActionReceiver.class)
                .putExtra(INTENT_NAME_ACTION, INTENT_VALUE);
        return PendingIntent.getBroadcast(
                context,
                1,
                startIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
