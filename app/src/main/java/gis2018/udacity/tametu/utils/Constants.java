package gis2018.udacity.tametu.utils;

public class Constants {

    public static final int TAMETU = 0;
    public static final int SHORT_BREAK = 1;
    public static final int LONG_BREAK = 2;

    public static final int TASK_INFORMATION_NOTIFICATION_ID = 10;
    public static final String CHANNEL_ID = "TAMETU";

    //Broadcast ID
    public static final String COUNTDOWN_BROADCAST = "com.gis2018.countdown";
    public static final String STOP_ACTION_BROADCAST = "com.gis2018.stop.action";
    public static final String COMPLETE_ACTION_BROADCAST = "com.gis2018.complete.action";

    //Intent names and values
    public static final String INTENT_NAME_ACTION = "action";
    public static final String INTENT_VALUE_START = "start";
    public static final String INTENT_VALUE_COMPLETE = "complete";
    public static final String INTENT_VALUE_CANCEL = "cancel";
    public static final String INTENT_VALUE_SHORT_BREAK = "short";
    public static final String INTENT_VALUE_LONG_BREAK = "long";

    public static final long TIME_INTERVAL = 1000; // Time Interval is 1 second
}