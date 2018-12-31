package gis2018.udacity.tametu.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import gis2018.udacity.tametu.R;

import static gis2018.udacity.tametu.utils.Constants.TASK_ON_HAND_COUNT_KEY;

public class CheckMarkUtils {
    public CheckMarkUtils() {
        //Empty
    }

    public static void updateCheckMarkCount(Activity activity) {
        //Inflating Views inside the insert point linear layout
        LinearLayout insertPoint = activity.findViewById(R.id.insert_point);
        TextView workSessionCountTextView = activity
                .findViewById(R.id.session_completed_value_textview_main);


        insertPoint.removeAllViews(); //Clear out old views before adding

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        int n = preferences.getInt(TASK_ON_HAND_COUNT_KEY, 0);

        workSessionCountTextView.setText(String.valueOf(preferences.getInt(
                activity.getString(R.string.task_on_hand_count_key), 0)));
        for (int i = 0; i < n; i += 1) {
            LayoutInflater layoutInflater = activity.getLayoutInflater();
            View check_mark_view = layoutInflater.inflate(R.layout.check_mark, null, false);
            insertPoint.addView(check_mark_view);
        }
    }
}
