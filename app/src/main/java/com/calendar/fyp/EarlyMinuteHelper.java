package com.calendar.fyp;

import android.content.Context;
import android.content.SharedPreferences;

public class EarlyMinuteHelper {
    private static final String PREFERENCE_KEY_MINUTES = "early_minutes";
    private static final int DEFAULT_MINUTES = 0; // 默认提前分钟数为10分钟

    public static void setEarlyMinutes(Context context, int minutes) {
        SharedPreferences preferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREFERENCE_KEY_MINUTES, minutes);
        editor.apply(); // 或者使用 editor.commit();
    }

    public static int getEarlyMinutes(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        return preferences.getInt(PREFERENCE_KEY_MINUTES, DEFAULT_MINUTES);
    }
}