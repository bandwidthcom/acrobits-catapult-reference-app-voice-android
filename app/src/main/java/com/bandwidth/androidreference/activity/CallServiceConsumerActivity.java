package com.bandwidth.androidreference.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;

import com.bandwidth.androidreference.CallBackgroundService;

public class CallServiceConsumerActivity extends ActionBarActivity {

    protected CallBackgroundService callService;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CallBackgroundService.LocalBinder binder = (CallBackgroundService.LocalBinder) service;
            callService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            callService = null;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, CallBackgroundService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (callService != null) {
            unbindService(serviceConnection);
        }
    }

    public CallBackgroundService getCallService() {
        return callService;
    }
}
