package me.sanbo.usm;

public class Eventss {
    public static final int NONE = 0;
    public static final int MOVE_TO_FOREGROUND = 1;
    public static final int ACTIVITY_RESUMED = MOVE_TO_FOREGROUND;
    public static final int MOVE_TO_BACKGROUND = 2;
    public static final int ACTIVITY_PAUSED = MOVE_TO_BACKGROUND;
    public static final int END_OF_DAY = 3;
    public static final int CONTINUE_PREVIOUS_DAY = 4;
    public static final int CONFIGURATION_CHANGE = 5;
    public static final int SYSTEM_INTERACTION = 6;
    public static final int USER_INTERACTION = 7;
    public static final int SHORTCUT_INVOCATION = 8;
    public static final int CHOOSER_ACTION = 9;
    public static final int NOTIFICATION_SEEN = 10;
    public static final int STANDBY_BUCKET_CHANGED = 11;
    public static final int NOTIFICATION_INTERRUPTION = 12;
    public static final int SLICE_PINNED_PRIV = 13;
    public static final int SLICE_PINNED = 14;
    public static final int SCREEN_INTERACTIVE = 15;
    public static final int SCREEN_NON_INTERACTIVE = 16;
    public static final int KEYGUARD_SHOWN = 17;
    public static final int KEYGUARD_HIDDEN = 18;
    public static final int FOREGROUND_SERVICE_START = 19;
    public static final int FOREGROUND_SERVICE_STOP = 20;
    public static final int CONTINUING_FOREGROUND_SERVICE = 21;
    public static final int ROLLOVER_FOREGROUND_SERVICE = 22;
    public static final int ACTIVITY_STOPPED = 23;
    public static final int ACTIVITY_DESTROYED = 24;
    public static final int FLUSH_TO_DISK = 25;
    public static final int DEVICE_SHUTDOWN = 26;
    public static final int DEVICE_STARTUP = 27;
    public static final int USER_UNLOCKED = 28;
    public static final int USER_STOPPED = 29;
    public static final int LOCUS_ID_SET = 30;
    public static final int FLAG_IS_PACKAGE_INSTANT_APP = 1 << 0;
}
