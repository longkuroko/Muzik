package com.example.androidappmusic.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        context.sendBroadcast(new Intent("TRACKS_TRACkS").putExtra("actionname", action));
    }
}
