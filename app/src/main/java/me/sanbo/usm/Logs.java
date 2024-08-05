package me.sanbo.usm;

import android.util.Log;

public class Logs {
    public static final String TAG = "USM";
    public static final boolean isDebug = true;

    public static void v(String info) {
        if (isDebug) {
            Log.println(Log.VERBOSE, TAG, info);
        }
    }

    public static void d(String info) {
        if (isDebug) {
            Log.println(Log.DEBUG, TAG, info);
        }
    }

    public static void i(String info) {
        if (isDebug) {
            Log.println(Log.INFO, TAG, info);
        }
    }

    public static void e(String info) {
        if (isDebug) {
            Log.println(Log.ERROR, TAG, info);
        }
    }

    public static void e(Throwable e) {
        if (isDebug) {
            Log.println(Log.ERROR, TAG, Log.getStackTraceString(e));
        }
    }

    public static void w(String info) {
        if (isDebug) {
            Log.println(Log.WARN, TAG, info);
        }
    }

    public static void wtf(String info) {
        if (isDebug) {
            Log.println(Log.ASSERT, TAG, info);
        }
    }
}
