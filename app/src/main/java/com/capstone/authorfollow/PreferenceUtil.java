package com.capstone.authorfollow;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtil {
    public static final String PREF_HOME_PAGE_CHOICE = "homepage";
    public static final String PREF_SHOW_RECENT = "showRecent";
    public static final String PREF_SHOW_RESULTS_FOR_DAYS = "searchInDays";

    public static void savePrefs(Context context, String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key, defaultValue);
    }

    public static int getPrefs(Context context, String key, int defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String prefValueStr = sp.getString(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(prefValueStr);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean getPrefs(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key, defaultValue);
    }
}
