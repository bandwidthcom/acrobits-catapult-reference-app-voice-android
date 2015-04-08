package com.bandwidth.androidreference.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bandwidth.androidreference.CallService;
import com.bandwidth.androidreference.MainActivity;
import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.utils.NumberUtils;
import com.bandwidth.bwsip.BWCall;
import com.bandwidth.bwsip.BWTone;


public class DialerFragment extends Fragment {

    private MainActivity mainActivity;
    private CallService callService;


    private PowerManager.WakeLock wl;

    private RelativeLayout button0;
    private RelativeLayout button1;
    private RelativeLayout button2;
    private RelativeLayout button3;
    private RelativeLayout button4;
    private RelativeLayout button5;
    private RelativeLayout button6;
    private RelativeLayout button7;
    private RelativeLayout button8;
    private RelativeLayout button9;
    private RelativeLayout buttonStar;
    private RelativeLayout buttonPound;
    private ImageButton buttonBackspace;
    private EditText editTextNumber;
    private Button buttonCall;

    private BWCall activeCall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) this.getActivity();
        PowerManager pm = (PowerManager) mainActivity.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "tag");
        wl.setReferenceCounted(false);

        View rootView = inflater.inflate(R.layout.fragment_dialer, container, false);

        button0 = (RelativeLayout) rootView.findViewById(R.id.button0);
        button1 = (RelativeLayout) rootView.findViewById(R.id.button1);
        button2 = (RelativeLayout) rootView.findViewById(R.id.button2);
        button3 = (RelativeLayout) rootView.findViewById(R.id.button3);
        button4 = (RelativeLayout) rootView.findViewById(R.id.button4);
        button5 = (RelativeLayout) rootView.findViewById(R.id.button5);
        button6 = (RelativeLayout) rootView.findViewById(R.id.button6);
        button7 = (RelativeLayout) rootView.findViewById(R.id.button7);
        button8 = (RelativeLayout) rootView.findViewById(R.id.button8);
        button9 = (RelativeLayout) rootView.findViewById(R.id.button9);
        buttonStar = (RelativeLayout) rootView.findViewById(R.id.buttonStar);
        buttonPound = (RelativeLayout) rootView.findViewById(R.id.buttonPound);
        buttonBackspace = (ImageButton) rootView.findViewById(R.id.buttonBackspace);
        editTextNumber = (EditText) rootView.findViewById(R.id.editTextNumber);
        buttonCall = (Button) rootView.findViewById(R.id.buttonCall);

        button0.setOnClickListener(dialerButtonClickListener);
        button1.setOnClickListener(dialerButtonClickListener);
        button2.setOnClickListener(dialerButtonClickListener);
        button3.setOnClickListener(dialerButtonClickListener);
        button4.setOnClickListener(dialerButtonClickListener);
        button5.setOnClickListener(dialerButtonClickListener);
        button6.setOnClickListener(dialerButtonClickListener);
        button7.setOnClickListener(dialerButtonClickListener);
        button8.setOnClickListener(dialerButtonClickListener);
        button9.setOnClickListener(dialerButtonClickListener);
        button0.setOnClickListener(dialerButtonClickListener);
        buttonStar.setOnClickListener(dialerButtonClickListener);
        buttonPound.setOnClickListener(dialerButtonClickListener);
        buttonBackspace.setOnClickListener(buttonBackspaceClickListener);
        buttonCall.setOnClickListener(buttonCallClickListener);

        callService = CallService.getInstance(mainActivity);

        mainActivity.setMenuVisible(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activeCall != null) {
            callStarted();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (wl.isHeld()) {
            wl.release();
        }
    }

    View.OnClickListener dialerButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editTextNumber.setText(NumberUtils.getPrettyPhoneNumber(editTextNumber.getText() + v.getTag().toString()));
            if (activeCall!= null) {
                BWTone.playDigit(v.getTag().toString());
            }
            buttonCall.requestFocus();
        }
    };

    View.OnClickListener buttonBackspaceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (editTextNumber.getText().toString().length() > 0) {
                String number = NumberUtils.removeExtraCharacters(editTextNumber.getText().toString());
                editTextNumber.setText(NumberUtils.getPrettyPhoneNumber(number.substring(0, number.length() - 1)));
            }
        }
    };

    View.OnClickListener buttonCallClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (activeCall != null) {
                callService.endCall();
                callEnded();
            }
            else {
                activeCall = callService.makeCall(editTextNumber.getText().toString());
                callStarted();
            }
        }
    };

    public void callStarted() {
        if (!wl.isHeld()) {
            wl.acquire();
        }
        buttonBackspace.setVisibility(View.INVISIBLE);
        buttonCall.setBackgroundResource(R.drawable.contrast_button);
        buttonCall.setText(getResources().getString(R.string.end_call));
        if (activeCall != null && editTextNumber.getText().length() == 0) {
            String callerNumber = activeCall.getRemoteUri();
            callerNumber = NumberUtils.fromSipUri(callerNumber);
            editTextNumber.setText(callerNumber);
        }
    }

    public void callEnded() {
        wl.release();
        buttonBackspace.setVisibility(View.VISIBLE);
        buttonCall.setBackgroundResource(R.drawable.blue_button);
        buttonCall.setText(getResources().getString(R.string.call));
        editTextNumber.getText().clear();
    }

    public void setActiveCall(BWCall call) {
        activeCall = call;
    }

    public void setNumberText(String text) {
        editTextNumber.setText(text);
    }

}
