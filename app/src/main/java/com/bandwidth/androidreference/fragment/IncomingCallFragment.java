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
import android.widget.Button;
import android.widget.TextView;

import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.activity.CallActivity;
import com.bandwidth.androidreference.activity.IncomingCallActivity;
import com.bandwidth.androidreference.intent.BWSipIntent;

public class IncomingCallFragment extends Fragment {

    private TextView textViewIncomingNumber;
    //private CallBackgroundService callService;
    private String callerNumber;
    private Ringtone ringtone;
    Vibrator vibrator;
    private long[] vibrationPattern = {0, 1000, 1000};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final IncomingCallActivity activity = (IncomingCallActivity) this.getActivity();

        View rootView = inflater.inflate(R.layout.fragment_incoming_call, container, false);

        Button buttonAnswer = (Button) rootView.findViewById(R.id.button_answer);
        Button buttonDecline = (Button) rootView.findViewById(R.id.button_decline);

        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this.getActivity());

        buttonAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtone.stop();
                vibrator.cancel();

                Intent callIntent = new Intent(activity, CallActivity.class);
                callIntent.setAction(BWSipIntent.INCOMING_CALL);
                callIntent.putExtra(BWSipIntent.INCOMING_CALL, textViewIncomingNumber.getText().toString());
                activity.startActivity(callIntent);

                Intent answerIntent = new Intent();
                answerIntent.setAction(BWSipIntent.ANSWER_CALL);
                broadcastManager.sendBroadcast(answerIntent);

                activity.finish();
            }
        });

        buttonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtone.stop();
                vibrator.cancel();
                broadcastManager.sendBroadcast(new Intent(BWSipIntent.DECLINE_CALL));
                activity.finish();
            }
        });

        textViewIncomingNumber = (TextView) rootView.findViewById(R.id.textViewIncomingNumber);

        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(activity, uri);

        vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            ringtone.play();
            vibrator.vibrate(vibrationPattern, 0);
        }

        return rootView;
    }

    public void setFromNumber(String number) {
        callerNumber = number;
        if (textViewIncomingNumber != null) {
            textViewIncomingNumber.setText(callerNumber);
        }
    }
}
