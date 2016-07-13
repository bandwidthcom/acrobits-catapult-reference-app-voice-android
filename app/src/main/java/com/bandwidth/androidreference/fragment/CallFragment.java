package com.bandwidth.androidreference.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bandwidth.androidreference.CallService;
import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.activity.CallActivity;
import com.bandwidth.androidreference.intent.BWSipIntent;
import com.bandwidth.androidreference.utils.NumberUtils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cz.acrobits.libsoftphone.Instance;
import cz.acrobits.libsoftphone.data.Call;

public class CallFragment extends Fragment {

    private TextView textViewNumber;
    private TextView textViewCallDuration;
    private String number;
    private Timer timer;
    private CallDurationTimer callDurationTimer;
    private PowerManager.WakeLock wakeLock;
    private LocalBroadcastManager broadcastManager;
    private IntentReceiver intentReceiver;
    private AudioManager audioManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final CallActivity activity = (CallActivity) getActivity();
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        broadcastManager = LocalBroadcastManager.getInstance(this.getActivity());
        intentReceiver = new IntentReceiver();
        PowerManager powerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "active_call");
        wakeLock.setReferenceCounted(false);

        View rootView = inflater.inflate(R.layout.fragment_call, container, false);

        RelativeLayout button0 = (RelativeLayout) rootView.findViewById(R.id.button0);
        RelativeLayout button1 = (RelativeLayout) rootView.findViewById(R.id.button1);
        RelativeLayout button2 = (RelativeLayout) rootView.findViewById(R.id.button2);
        RelativeLayout button3 = (RelativeLayout) rootView.findViewById(R.id.button3);
        RelativeLayout button4 = (RelativeLayout) rootView.findViewById(R.id.button4);
        RelativeLayout button5 = (RelativeLayout) rootView.findViewById(R.id.button5);
        RelativeLayout button6 = (RelativeLayout) rootView.findViewById(R.id.button6);
        RelativeLayout button7 = (RelativeLayout) rootView.findViewById(R.id.button7);
        RelativeLayout button8 = (RelativeLayout) rootView.findViewById(R.id.button8);
        RelativeLayout button9 = (RelativeLayout) rootView.findViewById(R.id.button9);
        RelativeLayout buttonPound = (RelativeLayout) rootView.findViewById(R.id.buttonPound);
        RelativeLayout buttonStar = (RelativeLayout) rootView.findViewById(R.id.buttonStar);

        button0.setOnTouchListener(dialerButtonTouchListener);
        button1.setOnTouchListener(dialerButtonTouchListener);
        button2.setOnTouchListener(dialerButtonTouchListener);
        button3.setOnTouchListener(dialerButtonTouchListener);
        button4.setOnTouchListener(dialerButtonTouchListener);
        button5.setOnTouchListener(dialerButtonTouchListener);
        button6.setOnTouchListener(dialerButtonTouchListener);
        button7.setOnTouchListener(dialerButtonTouchListener);
        button8.setOnTouchListener(dialerButtonTouchListener);
        button9.setOnTouchListener(dialerButtonTouchListener);
        buttonStar.setOnTouchListener(dialerButtonTouchListener);
        buttonPound.setOnTouchListener(dialerButtonTouchListener);

        final Button buttonEndCall = (Button) rootView.findViewById(R.id.buttonEndCall);
        final ToggleButton buttonMute = (ToggleButton) rootView.findViewById(R.id.buttonMute);
        final ToggleButton buttonSpeaker = (ToggleButton) rootView.findViewById(R.id.buttonSpeaker);
        textViewNumber = (TextView) rootView.findViewById(R.id.textViewNumber);
        textViewCallDuration = (TextView) rootView.findViewById(R.id.textViewCallDuration);

        if (number != null) {
            textViewNumber.setText(number);
        }

        buttonMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (buttonMute.isChecked()) {
                    intent.setAction(BWSipIntent.MUTE);
                }
                else {
                    intent.setAction(BWSipIntent.UNMUTE);
                }
                broadcastManager.sendBroadcast(intent);
            }
        });

        buttonSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (buttonSpeaker.isChecked()) {
                    intent.setAction(BWSipIntent.SPEAKER);
                }
                else {
                    intent.setAction(BWSipIntent.EARPIECE);
                }
                broadcastManager.sendBroadcast(intent);
            }
        });

        buttonEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcastManager.sendBroadcast(new Intent(BWSipIntent.END_CALL));
                activity.finish();
            }
        });

        return rootView;
    }

    View.OnTouchListener dialerButtonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT &&
                    audioManager.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Instance.Audio.dtmfOn(v.getTag().toString().charAt(0));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                        motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    Instance.Audio.dtmfOff();
                }
            }

            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                textViewNumber.setText(NumberUtils.getPrettyPhoneNumber(textViewNumber.getText() + v.getTag().toString()));
            }

            return false;
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        if (timer == null) {
            timer = new Timer();
            callDurationTimer = new CallDurationTimer();
            timer.schedule(callDurationTimer, 0, 1000);
        }
        wakeLock.acquire();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BWSipIntent.CALL_STATE);
        broadcastManager.registerReceiver(intentReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        callDurationTimer.cancel();
        timer.purge();
        timer = null;

        if (wakeLock.isHeld()) {
            wakeLock.release();
        }

        broadcastManager.unregisterReceiver(intentReceiver);
    }

    public void setPhoneNumber(String number) {
        this.number = number;
        if (textViewNumber != null) {
            textViewNumber.setText(number);
        }
    }

    private class IntentReceiver extends BroadcastReceiver
    {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BWSipIntent.CALL_STATE)) {
                Call.State callState = (Call.State) intent.getSerializableExtra(BWSipIntent.CALL_STATE);
                if (callState.isTerminal()) {
                    getActivity().finish();
                }
            }
        }
    }

    private class CallDurationTimer extends TimerTask {
        @Override
        public void run() {
            long duration;
            if (CallService.getCallStartTime() != null ) {
                duration = new Date().getTime() - CallService.getCallStartTime();
            }
            else {
                duration = 0;
            }
            final double seconds = Math.floor(duration / 1000) % 60;
            final double minutes = Math.floor(duration / 60000);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewCallDuration.setText(String.format("%02d", (long)minutes) + ":" + String.format("%02d", (long)seconds));
                }
            });
        }
    }
}
