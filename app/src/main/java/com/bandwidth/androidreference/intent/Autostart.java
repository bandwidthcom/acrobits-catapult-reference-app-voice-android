package com.bandwidth.androidreference.intent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.bandwidth.androidreference.CallService;

public class Autostart extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, CallService.class);
        context.startService(serviceIntent);
    }
}