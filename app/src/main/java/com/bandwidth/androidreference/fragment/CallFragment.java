package com.bandwidth.androidreference.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.activity.CallActivity;
import com.bandwidth.androidreference.activity.IncomingCallActivity;
import com.bandwidth.androidreference.intent.BWSipIntent;

/**
 * Created by nguyer on 4/7/15.
 */
public class CallFragment extends Fragment {

    TextView textViewNumber;
    String number;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final CallActivity activity = (CallActivity) this.getActivity();

        View rootView = inflater.inflate(R.layout.fragment_call, container, false);

        Button buttonEndCall = (Button) rootView.findViewById(R.id.buttonEndCall);
        textViewNumber = (TextView) rootView.findViewById(R.id.textViewNumber);
        if (number != null) {
            textViewNumber.setText(number);
        }

        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this.getActivity());

        buttonEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcastManager.sendBroadcast(new Intent(BWSipIntent.END_CALL));
                activity.finish();
            }
        });

        return rootView;
    }

    public void setFromNumber(String number) {
        this.number = number;
        if (textViewNumber != null) {
            textViewNumber.setText(number);
        }
    }
}
