package com.bandwidth.androidreference.activity;

import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;

import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.fragment.CallFragment;
import com.bandwidth.androidreference.intent.BWSipIntent;
import com.bandwidth.androidreference.utils.NotificationHelper;

public class CallActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);

            broadcastManager.sendBroadcast(getIntent());

            NotificationHelper.clearIncomingCallNotification(this);

            if (getIntent().getAction().equals(BWSipIntent.DECLINE_CALL)) {
                finish();
                overridePendingTransition(R.animator.animation_none, R.animator.animation_none);
            } else {
                setContentView(R.layout.activity_main);
                CallFragment callFragment = new CallFragment();
                callFragment.setPhoneNumber(getIntent().getStringExtra(BWSipIntent.PHONE_CALL));
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, callFragment)
                        .commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}
