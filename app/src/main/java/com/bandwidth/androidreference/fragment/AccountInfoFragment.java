package com.bandwidth.androidreference.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bandwidth.androidreference.activity.MainActivity;
import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.utils.SaveManager;
import com.bandwidth.androidreference.data.User;


public class AccountInfoFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) this.getActivity();

        View rootView = inflater.inflate(R.layout.fragment_account_info, container, false);

        TextView textViewUsername = (TextView) rootView.findViewById(R.id.textViewUsername);
        TextView textViewNumber = (TextView) rootView.findViewById(R.id.textViewNumber);
        TextView textViewEndpointName = (TextView) rootView.findViewById(R.id.textViewEndpointName);
        TextView textViewRegistrar = (TextView) rootView.findViewById(R.id.textViewRegistrar);
        TextView textViewSipUri = (TextView) rootView.findViewById(R.id.textViewSipUri);

        User user = SaveManager.getUser(mainActivity);

        textViewUsername.setText(user.getUserName());
        textViewNumber.setText(user.getPhoneNumber());
        textViewEndpointName.setText(user.getEndpoint().getCredentials().getUsername());
        textViewRegistrar.setText(user.getEndpoint().getCredentials().getRealm());
        textViewSipUri.setText(user.getEndpoint().getSipUri());

        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return rootView;
    }

}
