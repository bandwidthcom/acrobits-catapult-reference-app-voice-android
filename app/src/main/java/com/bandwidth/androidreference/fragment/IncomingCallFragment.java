package com.bandwidth.androidreference.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bandwidth.androidreference.CallService;
import com.bandwidth.androidreference.MainActivity;
import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.utils.NumberUtils;
import com.bandwidth.bwsip.BWCall;

public class IncomingCallFragment extends Fragment {

    private MainActivity mainActivity;
    private TextView textViewIncomingNumber;
    private BWCall bwCall;
    private CallService callService;
    private String callerNumber;
    private Ringtone ringtone;
    Vibrator vibrator;
    private long[] vibrationPattern = {0, 1000, 1000};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) this.getActivity();
        callService = CallService.getInstance(mainActivity);

        View rootView = inflater.inflate(R.layout.fragment_incoming_call, container, false);

        Button buttonAnswer = (Button) rootView.findViewById(R.id.button_answer);
        Button buttonDecline = (Button) rootView.findViewById(R.id.button_decline);

        buttonAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtone.stop();
                vibrator.cancel();
                callService.answerIncomingCall();
                mainActivity.onSupportNavigateUp();
                mainActivity.getDialerFragment().setActiveCall(bwCall);
            }
        });

        buttonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtone.stop();
                vibrator.cancel();
                mainActivity.onSupportNavigateUp();
                callService.declineIncomingCall();
            }
        });

        textViewIncomingNumber = (TextView) rootView.findViewById(R.id.textViewIncomingNumber);
        if (bwCall != null) {
            textViewIncomingNumber.setText(callerNumber);
        }

        mainActivity.setMenuVisible(false);

        AudioManager audioManager = (AudioManager) mainActivity.getSystemService(Context.AUDIO_SERVICE);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(mainActivity, uri);

        vibrator = (Vibrator) mainActivity.getSystemService(Context.VIBRATOR_SERVICE);

        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
            ringtone.play();
            vibrator.vibrate(vibrationPattern, 0);
        }

        return rootView;
    }

    public void setCall(BWCall bwCall) {
        this.bwCall = bwCall;
        callerNumber = NumberUtils.fromSipUri(bwCall.getRemoteUri());
        if (textViewIncomingNumber != null) {
            textViewIncomingNumber.setText(callerNumber);
        }
    }
}
