package com.example.androidappmusic.service;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.example.androidappmusic.sharedPreferences.DataLocalManager;

import java.util.Locale;

public class SettingLanguage {
    private  Context context;

    public SettingLanguage(Context context) {
        this.context = context;
        DataLocalManager.init(context);
    }

    public void Update_Language(String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        DataLocalManager.setLanguage(code); //Set o SharedPreferences
    }
}
