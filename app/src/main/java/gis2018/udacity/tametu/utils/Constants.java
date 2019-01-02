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
    public static final String START_ACTION_BROADCAST = "com.gis2018.start.action";

    //Intent names and values
    public static final String INTENT_NAME_ACTION = "action";
    public static final String INTENT_VALUE_START = "start";
    public static final String INTENT_VALUE_COMPLETE = "complete";
    public static final String INTENT_VALUE_CANCEL = "cancel";
    public static final String INTENT_VALUE_SHORT_BREAK = "short";
    public static final String INTENT_VALUE_LONG_BREAK = "long";

    public static final long TIME_INTERVAL = 1000; // Time Interval is 1 second

    //SharePreferences Keys
    public static final String WORK_DURATION_KEY = "WORK_DURATION";
    public static final String SHORT_BREAK_DURATION_KEY = "SHORT_BREAK_DURATION";
    public static final String LONG_BREAK_DURATION_KEY = "LONG_BREAK_DURATION";
    public static final String START_LONG_BREAK_AFTER_KEY = "LONG_BREAK_AFTER_DURATION";
    public static final String WORK_SESSION_COUNT_KEY = "WORK_SESSION_COUNT";
    public static final String TASK_ON_HAND_COUNT_KEY = "TASK_ON_HAND_COUNT";
    public static final String TICKING_VOLUME_LEVEL_KEY = "TICKING_VOLUME_LEVEL";
    public static final String RINGING_VOLUME_LEVEL_KEY = "RINGING_VOLUME_LEVEL";
}
