package com.bandwidth.androidreference.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bandwidth.androidreference.ClientApi;
import com.bandwidth.androidreference.MainActivity;
import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.SaveManager;
import com.bandwidth.androidreference.data.User;


public class AccountInfoFragment extends Fragment {

    private MainActivity mainActivity;
    private TextView textViewUsername;
    private TextView textViewNumber;
    private TextView textViewRegistrar;
    private TextView textViewAppServer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) this.getActivity();

        View rootView = inflater.inflate(R.layout.fragment_account_info, container, false);

        textViewUsername = (TextView) rootView.findViewById(R.id.textViewUsername);
        textViewNumber = (TextView) rootView.findViewById(R.id.textViewNumber);
        textViewRegistrar = (TextView) rootView.findViewById(R.id.textViewRegistrar);
        textViewAppServer = (TextView) rootView.findViewById(R.id.textViewAppServer);

        User user = SaveManager.getUser(mainActivity);

        textViewUsername.setText(user.getUsername());
        textViewNumber.setText(user.getNumber());
        textViewRegistrar.setText(user.getEndpoint().getCredentials().getRealm());
        textViewAppServer.setText(ClientApi.APPLICATION_SERVER_URL);

        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return rootView;
    }

}
