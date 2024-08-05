package me.sanbo.usm;


import android.annotation.TargetApi;
import android.app.usage.EventStats;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class USMHelper {
    /**
     * 打开辅助功能设置界面
     *
     * @param context
     */
    public static void openUSMSetting(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                context.startActivity(intent);
            }
        } catch (Throwable e) {
        }
    }

    /**
     * 获取USM信息
     *
     * @param context
     * @param start
     * @param end
     * @return
     */
    public static JSONArray getUSMInfo(Context context, long start, long end) {
        JSONArray arr = new JSONArray();
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                return arr;
            }
            arr = getInfoByUE(context, start, end);

            if (arr == null || arr.length() == 0) {
                arr = getInfoByUS(context, start, end);
            }
        } catch (Throwable igone) {

        }
        return arr;
    }

    @TargetApi(28)
    public static JSONArray getEventStats(Context context, long beginTime, long endTime) {
        JSONArray arr = new JSONArray();
        //1. get ue
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<EventStats> usageStatsList = manager.queryEventStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime);

        for (EventStats eventStats : usageStatsList) {
            DateFormat format = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss", Locale.getDefault());

            int count = eventStats.getCount();
            int eventType = eventStats.getEventType();
            String beginningTime = format.format(new Date(eventStats.getFirstTimeStamp()));
            String enddingTime = format.format(new Date(eventStats.getLastTimeStamp()));
            String lastEventTime = format.format(new Date(eventStats.getLastEventTime()));
            long totalTime = eventStats.getTotalTime() / 1000;
            Logs.d("| " + count + " | " + eventType + " | " + beginningTime + " | " + enddingTime + " | " + lastEventTime + " | " + totalTime + " | ");
        }
        return arr;
    }


    public static JSONArray getInfoByUE(Context context, long beginTime, long endTime) {
        JSONArray arr = new JSONArray();
        //1. get ue
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents usageEvents = manager.queryEvents(beginTime, endTime);
//        UsageEvents usageEvents = manager.queryEventsForSelf(beginTime, endTime);

        if (usageEvents == null) {
            return arr;
        }

        UsageEvents.Event event = null;
        Usm openEvent = null;
        PackageManager packageManager = context.getPackageManager();
        while (usageEvents.hasNextEvent()) {
            event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);


//            printUE(packageManager, event);
            /**
             * 闭合数据
             */
            if (openEvent == null) {
                // 首个
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND || event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                    openEvent = openUsm(packageManager, event);
                } else {
                    // 以为切割时间内，直接第一非开启事件  o---|--c-o----c---o----c (可能切割时间为竖划线位置)
                }
            } else {
                String packageName = event.getPackageName();
                if (!packageName.equals(openEvent.pkgName)) {
                    openEvent.closeTime = event.getTimeStamp();
//                    // 大于3秒的才算做oc,一闪而过的不算
//                    if (openEvent.closeTime - openEvent.openTime >= 3000) {
//                        arr.put(openEvent.toJson());
//                    }
                    arr.put(openEvent.toJson());

                    if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND || event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                        openEvent = openUsm(packageManager, event);
                    } else {
                        openEvent = null;
                    }
                }
            }
        }
        return arr;
    }

    private static void printUE(PackageManager packageManager, UsageEvents.Event event) {
        try {
            // just print
//            DateFormat format = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss:SSS", Locale.getDefault());
            String packageName = event.getPackageName();
            String className = event.getClassName();
            int eventType = event.getEventType();
//            int standbyBucket = event.getAppStandbyBucket();
//            String shortcutId = event.getShortcutId();
//            String time = format.format(new Date(event.getTimeStamp()));

            String versionCode = "";
            String appName = "";
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                versionCode = packageInfo.versionName + "|" + packageInfo.versionCode;
                CharSequence lb = applicationInfo.loadLabel(packageManager);
                if (!TextUtils.isEmpty(lb)) {
                    appName = String.valueOf(lb);
                }
            } catch (Throwable igone) {
            }

//            EL.i("| " + appName + "| " + packageName + " | " + versionCode + " | " + className + " | " + eventType + " | " + getEventString(eventType) + " | " + time + " |");

            if (eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                Logs.i("| " + appName + "| " + packageName + " | " + versionCode + " | " + className + " | 前台 | " + event.getTimeStamp() + " |");
            } else if (eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                Logs.i("| " + appName + "| " + packageName + " | " + versionCode + " | " + className + " | 后台 | " + event.getTimeStamp() + " |");
//            } else if (eventType == UsageEvents.Event.ACTIVITY_STOPPED) {
//                Logs.i("| " + appName + "| " + packageName + " | " + versionCode + " | " + className + " | 停止 | " + time + " |");
//            } else if (eventType == 24) {
//                Logs.i("| " + appName + "| " + packageName + " | " + versionCode + " | " + className + " | 销毁 | " + time + " |");
            }

        } catch (Throwable e) {
            Logs.e(e);
        }
    }


    public static JSONArray getInfoByUS(Context context, long beginTime, long endTime) {
        JSONArray arr = new JSONArray();
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        planA(context, beginTime, endTime, manager);

//        EL.i("--------------------------------------------------------------------------");
//        Map<String, UsageStats> map = manager.queryAndAggregateUsageStats(beginTime, endTime);
//        for (Map.Entry<String, UsageStats> entry : map.entrySet()) {
////            EL.i("kkkk-------------------->" + entry.getKey());
//            printUS(entry.getValue());
//        }
        return arr;
    }

    private static void planA(Context context, long beginTime, long endTime, UsageStatsManager manager) {
        List<UsageStats> usageStatsList = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime);
        PackageManager packageManager = context.getPackageManager();
        for (UsageStats usageStats : usageStatsList) {
            try {
                printUS(usageStats);
                long lastUsedTime = usageStats.getLastTimeUsed();
                long durTime = usageStats.getTotalTimeInForeground();
                if (lastUsedTime > 0) {
                    String pkgName = usageStats.getPackageName();
                    long openTime = lastUsedTime - durTime;
                    Usm usm = new Usm();
                    usm.pkgName = pkgName;
                    usm.openTime = openTime;

                    PackageInfo packageInfo = packageManager.getPackageInfo(usm.pkgName, 0);
                    ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                    usm.versionCode = packageInfo.versionName + "|" + packageInfo.versionCode;
                    try {
                        CharSequence lb = applicationInfo.loadLabel(packageManager);
                        if (!TextUtils.isEmpty(lb)) {
                            usm.appName = String.valueOf(lb);
                        }
                    } catch (Throwable igone) {
                    }

                    Logs.i(usm.toJson().toString());

                }


            } catch (Throwable e) {

            }
        }
    }

    private static void printUS(UsageStats usageStats) {
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss:SSS", Locale.getDefault());
        String packageName = usageStats.getPackageName();
        String beginningTime = format.format(new Date(usageStats.getFirstTimeStamp()));
        String enddingTime = format.format(new Date(usageStats.getLastTimeStamp()));
        String lastUsedTime = format.format(new Date(usageStats.getLastTimeUsed())); //上次使用时间
        long foregroundTime = usageStats.getTotalTimeInForeground() / 1000; //前台总共运行的时间

        int appLaunchCount = 0; //应用被拉起启动次数
        int launchCount = 0;//应用前台启动次数(包括自己启动其他activity)
        try {
            appLaunchCount = (int) usageStats.getClass().getDeclaredMethod("getAppLaunchCount").invoke(usageStats);
            launchCount = usageStats.getClass().getDeclaredField("mLaunchCount").getInt(usageStats);
        } catch (Throwable e) {
            Logs.e(e);
        }
        if (packageName.equals("com.device")) {
            Logs.i("| " + packageName + " | " + beginningTime + " | " + enddingTime + " | " + lastUsedTime + " | " + foregroundTime + " | " + appLaunchCount + " | " + launchCount + " |");

        }
    }

    private static String getEventString(int eventType) {
        switch (eventType) {
            case Eventss.NONE:
                return "NONE";
            case Eventss.MOVE_TO_FOREGROUND:
                return "MOVE_TO_FOREGROUND";
//            case Eventss.ACTIVITY_RESUMED:
//                return "ACTIVITY_RESUMED";
            case Eventss.MOVE_TO_BACKGROUND:
                return "MOVE_TO_BACKGROUND";
//            case Eventss.ACTIVITY_PAUSED:
//                return "ACTIVITY_PAUSED";
            case Eventss.END_OF_DAY:
                return "END_OF_DAY";
            case Eventss.CONTINUE_PREVIOUS_DAY:
                return "CONTINUE_PREVIOUS_DAY";
            case Eventss.CONFIGURATION_CHANGE:
                return "CONFIGURATION_CHANGE";
            case Eventss.SYSTEM_INTERACTION:
                return "SYSTEM_INTERACTION";
            case Eventss.USER_INTERACTION:
                return "USER_INTERACTION";
            case Eventss.SHORTCUT_INVOCATION:
                return "SHORTCUT_INVOCATION";
            case Eventss.CHOOSER_ACTION:
                return "CHOOSER_ACTION";
            case Eventss.NOTIFICATION_SEEN:
                return "NOTIFICATION_SEEN";
            case Eventss.STANDBY_BUCKET_CHANGED:
                return "STANDBY_BUCKET_CHANGED";
            case Eventss.NOTIFICATION_INTERRUPTION:
                return "NOTIFICATION_INTERRUPTION";
            case Eventss.SLICE_PINNED_PRIV:
                return "SLICE_PINNED_PRIV";
            case Eventss.SLICE_PINNED:
                return "SLICE_PINNED";
            case Eventss.SCREEN_INTERACTIVE:
                return "SCREEN_INTERACTIVE";
            case Eventss.SCREEN_NON_INTERACTIVE:
                return "SCREEN_NON_INTERACTIVE";
            case Eventss.KEYGUARD_SHOWN:
                return "KEYGUARD_SHOWN";
            case Eventss.KEYGUARD_HIDDEN:
                return "KEYGUARD_HIDDEN";
            case Eventss.FOREGROUND_SERVICE_START:
                return "FOREGROUND_SERVICE_START";
            case Eventss.FOREGROUND_SERVICE_STOP:
                return "FOREGROUND_SERVICE_STOP";
            case Eventss.CONTINUING_FOREGROUND_SERVICE:
                return "CONTINUING_FOREGROUND_SERVICE";
            case Eventss.ROLLOVER_FOREGROUND_SERVICE:
                return "ROLLOVER_FOREGROUND_SERVICE";
            case Eventss.ACTIVITY_STOPPED:
                return "ACTIVITY_STOPPED";
            case Eventss.ACTIVITY_DESTROYED:
                return "ACTIVITY_DESTROYED";
            case Eventss.FLUSH_TO_DISK:
                return "FLUSH_TO_DISK";
            case Eventss.DEVICE_SHUTDOWN:
                return "DEVICE_SHUTDOWN";
            case Eventss.DEVICE_STARTUP:
                return "DEVICE_STARTUP";
            case Eventss.USER_UNLOCKED:
                return "USER_UNLOCKED";
            case Eventss.USER_STOPPED:
                return "USER_STOPPED";
            case Eventss.LOCUS_ID_SET:
                return "LOCUS_ID_SET";
            default:
                return "NONE";
        }
    }

    private static Usm openUsm(PackageManager packageManager, UsageEvents.Event event) {
        try {
            Usm usm = new Usm();
            usm.openTime = event.getTimeStamp();
            usm.pkgName = event.getPackageName();
            PackageInfo packageInfo = packageManager.getPackageInfo(usm.pkgName, 0);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            usm.versionCode = packageInfo.versionName + "|" + packageInfo.versionCode;
            try {
                CharSequence lb = applicationInfo.loadLabel(packageManager);
                if (!TextUtils.isEmpty(lb)) {
                    usm.appName = String.valueOf(lb);
                }
            } catch (Throwable igone) {
            }

            return usm;
        } catch (Throwable e) {
            Logs.e(e);
        }
        return null;
    }


}



