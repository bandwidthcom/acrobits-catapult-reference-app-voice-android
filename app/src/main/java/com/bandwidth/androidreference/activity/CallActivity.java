package com.bandwidth.androidreference.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.fragment.CallFragment;
import com.bandwidth.androidreference.intent.BWSipIntent;

public class CallActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            Intent intent = getIntent();

            CallFragment callFragment = new CallFragment();
            callFragment.setPhoneNumber(intent.getStringExtra(BWSipIntent.PHONE_CALL));
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, callFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
    }
}
