package com.example.androidappmusic.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.androidappmusic.models.Song;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataLocalManager {

    private static DataLocalManager instance;
    private SharedPreferencesManager sharedPreferencesManager;

    private static final String USER_ID = "USER_ID";
    private static final String USER_AVATAR = "USER_AVATAR";

    public static void init(Context context) {
        instance = new DataLocalManager();
        instance.sharedPreferencesManager = new SharedPreferencesManager(context);
    }

    public static DataLocalManager getInstance() {
        if (instance == null) {
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void deleteAllData() {
        DataLocalManager.getInstance().sharedPreferencesManager.deleteAllData();
    }

    public static void deleteUserID() {
        DataLocalManager.getInstance().sharedPreferencesManager.deleteUserID(USER_ID);
    }

    public static void deleteUserAvatar() {
        DataLocalManager.getInstance().sharedPreferencesManager.deleteUserAvatar(USER_AVATAR);
    }

    public static void setUserID(String userID) {
        DataLocalManager.getInstance().sharedPreferencesManager.putStringValue(USER_ID, userID);
    }

    public static String getUserID() {
        return DataLocalManager.getInstance().sharedPreferencesManager.getStringValue(USER_ID);
    }


    public static void setUserAvatar(String userAvatar) {
        DataLocalManager.getInstance().sharedPreferencesManager.putStringValue(USER_AVATAR, userAvatar);
    }

    public static String getUserAvatar() {
        return DataLocalManager.getInstance().sharedPreferencesManager.getStringValue(USER_AVATAR);
    }



}
