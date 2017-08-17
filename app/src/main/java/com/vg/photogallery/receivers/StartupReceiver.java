package com.vg.photogallery.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vg.photogallery.service.PollService;
import com.vg.photogallery.util.QueryPreferences;

/**
 * Broadcast receiver is a component which gets intents like
 * activities and services
 */
public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";

    /**
     * this method will be called when receiver gets intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());

        boolean isOn = QueryPreferences.isAlarmOn(context);
        PollService.setServiceAlarm(context, isOn);
    }
}