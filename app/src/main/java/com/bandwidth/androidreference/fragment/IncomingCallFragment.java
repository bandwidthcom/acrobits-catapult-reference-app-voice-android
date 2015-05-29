package com.bandwidth.androidreference.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.activity.CallActivity;
import com.bandwidth.androidreference.activity.IncomingCallActivity;
import com.bandwidth.androidreference.intent.BWSipIntent;

public class IncomingCallFragment extends Fragment {

    private TextView textViewIncomingNumber;
    private String callerNumber;
    private Ringtone ringtone;
    Vibrator vibrator;
    private long[] vibrationPattern = {0, 1000, 1000, 1000, 1000, 1000, 1000};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final IncomingCallActivity activity = (IncomingCallActivity) this.getActivity();
        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(activity);

        View rootView = inflater.inflate(R.layout.fragment_incoming_call, container, false);

        Button buttonAnswer = (Button) rootView.findViewById(R.id.button_answer);
        Button buttonDecline = (Button) rootView.findViewById(R.id.button_decline);

        buttonAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtone.stop();
                vibrator.cancel();

                Intent intent = new Intent(activity, CallActivity.class);
                intent.setAction(BWSipIntent.PHONE_CALL);
                intent.putExtra(BWSipIntent.PHONE_CALL, textViewIncomingNumber.getText().toString());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();

            }
        });

        buttonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtone.stop();
                vibrator.cancel();

                Intent intent = new Intent(activity, CallActivity.class);
                intent.setAction(BWSipIntent.DECLINE_CALL);
                intent.putExtra(BWSipIntent.DECLINE_CALL, textViewIncomingNumber.getText().toString());
                broadcastManager.sendBroadcast(intent);

                activity.finish();
            }
        });

        textViewIncomingNumber = (TextView) rootView.findViewById(R.id.textViewIncomingNumber);
        if (callerNumber != null) {
            textViewIncomingNumber.setText(callerNumber);
        }

        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(activity, uri);

        vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            vibrator.vibrate(vibrationPattern, 0);
            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE) {
                ringtone.play();
            }
        }

        setFlags();

        return rootView;
    }

    private void setFlags() {
        this.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        this.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        this.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void setFromNumber(String number) {
        callerNumber = number;
        if (textViewIncomingNumber != null) {
            textViewIncomingNumber.setText(callerNumber);
        }
    }
}
