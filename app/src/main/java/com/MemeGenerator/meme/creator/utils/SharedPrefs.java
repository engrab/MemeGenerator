package com.MemeGenerator.meme.creator.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    private final static String SHARED_PREFERENCE_FILE = "com.example.memegenerator_android";

    public static void putString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFERENCE_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFERENCE_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFERENCE_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFERENCE_FILE, 0);
        return settings.getString(key, defValue);
    }

    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFERENCE_FILE, 0);
        return settings.getInt(key, defValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFERENCE_FILE, 0);
        return settings.getBoolean(key, defValue);
    }
}
