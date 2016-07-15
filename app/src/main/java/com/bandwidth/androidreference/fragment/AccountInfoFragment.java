package com.bandwidth.androidreference.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bandwidth.androidreference.CallService;
import com.bandwidth.androidreference.activity.MainActivity;
import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.intent.BWIntent;
import com.bandwidth.androidreference.utils.SaveManager;
import com.bandwidth.androidreference.data.User;

import cz.acrobits.libsoftphone.data.RegistrationState;


public class AccountInfoFragment extends Fragment {

    private TextView textViewRegistrationState;
    private LocalBroadcastManager broadcastManager;
    private IntentReceiver intentReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) this.getActivity();

        broadcastManager = LocalBroadcastManager.getInstance(this.getActivity());
        intentReceiver = new IntentReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BWIntent.REGISTRATION);
        broadcastManager.registerReceiver(intentReceiver, intentFilter);

        View rootView = inflater.inflate(R.layout.fragment_account_info, container, false);

        TextView textViewUsername = (TextView) rootView.findViewById(R.id.textViewUsername);
        TextView textViewNumber = (TextView) rootView.findViewById(R.id.textViewNumber);
        TextView textViewEndpointName = (TextView) rootView.findViewById(R.id.textViewEndpointName);
        TextView textViewRegistrar = (TextView) rootView.findViewById(R.id.textViewRegistrar);
        TextView textViewSipUri = (TextView) rootView.findViewById(R.id.textViewSipUri);
        textViewRegistrationState = (TextView) rootView.findViewById(R.id.textViewRegistrationState);

        RegistrationState registrationState = CallService.getRegistrationState();
        textViewRegistrationState.setText(registrationState.getLabel());

        User user = SaveManager.getUser(mainActivity);

        textViewUsername.setText(user.getUserName());
        textViewNumber.setText(user.getPhoneNumber());
        textViewEndpointName.setText(user.getEndpoint().getCredentials().getUsername());
        textViewRegistrar.setText(user.getEndpoint().getCredentials().getRealm());
        textViewSipUri.setText(user.getEndpoint().getSipUri());

        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        broadcastManager.unregisterReceiver(intentReceiver);
    }

    private class IntentReceiver extends BroadcastReceiver
    {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BWIntent.REGISTRATION)) {
                RegistrationState registrationState = (RegistrationState) intent.getSerializableExtra(BWIntent.REGISTRATION);
                textViewRegistrationState.setText(registrationState.getLabel());
            }
        }
    }

}
