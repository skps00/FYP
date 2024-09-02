package com.calendar.fyp;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LanguageHelper {
    private static final String PREFERENCE_KEY_LANG = "language_preference";
    private static final String DEFAULT_LANG = "en"; // 默认语言为英语

    public static void setLanguage(Context context, String language) {
        SharedPreferences preferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCE_KEY_LANG, language);
        editor.apply(); // 或者使用 editor.commit();
    }

    public static String getLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        return preferences.getString(PREFERENCE_KEY_LANG, DEFAULT_LANG);
    }

    public static void applyLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String language = preferences.getString(PREFERENCE_KEY_LANG, "zh");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}