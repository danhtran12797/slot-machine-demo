package com.nimoon.games.slotmachine;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Ultil {

    public static String NAME_SHARED_PREFERENCES = "shared preferences";
    public static String USERNAME = "username";
    public static String COIN = "coin";

    public static int getPrefCoin(Context context) {
        SharedPreferences pref = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        int coin = pref.getInt(COIN, -1);
        return coin;
    }

    public static void setPrefsCoin(Context context, int coin) {
        SharedPreferences pref = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(COIN, coin);
        editor.commit();
    }

    public static String getPrefUsername(Context context) {
        SharedPreferences pref = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        String username = pref.getString(USERNAME, "");
        return username;
    }

    public static void setPrefsUsername(Context context, String username) {
        SharedPreferences pref = context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USERNAME, username);
        editor.commit();
    }
}
