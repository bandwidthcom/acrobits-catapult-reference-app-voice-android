package com.bandwidth.androidreference.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.fragment.IncomingCallFragment;
import com.bandwidth.androidreference.intent.BWSipIntent;

public class IncomingCallActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            Intent intent = getIntent();

            IncomingCallFragment incomingCallFragment = new IncomingCallFragment();
            incomingCallFragment.setFromNumber(intent.getStringExtra(BWSipIntent.INCOMING_CALL));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, incomingCallFragment)
                    .commit();
        }
    }
}
