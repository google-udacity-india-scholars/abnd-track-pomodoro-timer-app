package gis2018.udacity.pomodoro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import gis2018.udacity.pomodoro.utils.Utils;


/**
 * ActionReceiver is used when notification
 * action button is click
 *
 */
public class ActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Utils.isCancelActionClick = true; // setting cancel function trigger
        Toast.makeText(context, context.getResources().getString(R.string.cancel_notification_toast),
                Toast.LENGTH_SHORT).show();  // showing Toast on clicking "Cancel"

        Intent cancelIntent = new Intent();
        cancelIntent.setClassName(context.getPackageName(), context.getPackageName()+".MainActivity");
        cancelIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(cancelIntent);

    }
}
