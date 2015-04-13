package com.bandwidth.androidreference.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bandwidth.androidreference.data.ClientApi;
import com.bandwidth.androidreference.activity.MainActivity;
import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.utils.SaveManager;
import com.bandwidth.androidreference.data.User;
import com.bandwidth.androidreference.intent.BWSipIntent;


public class RegisterFragment extends Fragment {

    private EditText editTextUsername;
    private Button buttonRegister;
    private ProgressBar progressBarRegister;
    private LocalBroadcastManager broadcastManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        broadcastManager = LocalBroadcastManager.getInstance(getActivity());

        editTextUsername = (EditText) rootView.findViewById(R.id.editTextUsername);
        editTextUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    registerClickListener.onClick(editTextUsername);
                    handled = true;
                }
                return handled;
            }
        });

        buttonRegister = (Button) rootView.findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(registerClickListener);

        progressBarRegister = (ProgressBar) rootView.findViewById(R.id.progressBarRegister);

        ((MainActivity) getActivity()).setMenuVisible(false);

        return rootView;
    }

    private View.OnClickListener registerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            buttonRegister.setVisibility(View.GONE);
            progressBarRegister.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).hideKeyboard();

            final String username = editTextUsername.getText().toString();
            final String password = "somemadeuppassword";

            AsyncTask<String, Void, User> getUserAsync = new AsyncTask<String, Void, User>() {
                @Override
                protected User doInBackground(String... params) {
                    return ClientApi.getUser(getActivity(), params[0], params[1]);
                }

                @Override
                protected void onPostExecute(User user) {
                    if (user != null) {
                        user.getEndpoint().getCredentials().setPassword(password);
                        SaveManager.saveUser(getActivity(), user);
                        broadcastManager.sendBroadcast(new Intent(BWSipIntent.RENEW_REGISTRATION));
                        ((MainActivity)getActivity()).goToFragment(new DialerFragment());
                        ((MainActivity) getActivity()).setMenuVisible(true);
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_register_message, user.getPhoneNumber()), Toast.LENGTH_LONG).show();
                    }
                    else {
                        progressBarRegister.setVisibility(View.GONE);
                        buttonRegister.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), getResources().getString(R.string.toast_register_error), Toast.LENGTH_LONG).show();
                    }
                }
            };

            getUserAsync.execute(username, password);
        }
    };
}