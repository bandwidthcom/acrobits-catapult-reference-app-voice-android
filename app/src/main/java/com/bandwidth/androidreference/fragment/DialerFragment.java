package com.bandwidth.androidreference.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.activity.CallActivity;
import com.bandwidth.androidreference.intent.BWSipIntent;
import com.bandwidth.androidreference.utils.NumberUtils;

import com.bandwidth.bwsip.BWTone;


public class DialerFragment extends Fragment {

    private EditText editTextNumber;
    private Button buttonCall;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_dialer, container, false);

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
        RelativeLayout buttonStar = (RelativeLayout) rootView.findViewById(R.id.buttonStar);
        RelativeLayout buttonPound = (RelativeLayout) rootView.findViewById(R.id.buttonPound);
        ImageButton buttonBackspace = (ImageButton) rootView.findViewById(R.id.buttonBackspace);
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

        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(getActivity(), CallActivity.class);
                callIntent.setAction(BWSipIntent.PHONE_CALL);
                callIntent.putExtra(BWSipIntent.PHONE_CALL, editTextNumber.getText().toString());
                getActivity().startActivity(callIntent);

                editTextNumber.getText().clear();
                buttonCall.setEnabled(false);
            }
        });

        return rootView;
    }

    View.OnClickListener dialerButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editTextNumber.setText(NumberUtils.getPrettyPhoneNumber(editTextNumber.getText() + v.getTag().toString()));
            BWTone.playDigit(v.getTag().toString(), 0.5f);
            buttonCall.setEnabled(true);
        }
    };

    View.OnClickListener buttonBackspaceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (editTextNumber.getText().toString().length() > 0) {
                String number = NumberUtils.removeExtraCharacters(editTextNumber.getText().toString());
                editTextNumber.setText(NumberUtils.getPrettyPhoneNumber(number.substring(0, number.length() - 1)));
            }
            buttonCall.setEnabled(editTextNumber.getText().length() > 0);
        }
    };

}
