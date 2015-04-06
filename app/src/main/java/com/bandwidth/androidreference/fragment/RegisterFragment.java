package com.bandwidth.androidreference.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.bandwidth.androidreference.ClientApi;
import com.bandwidth.androidreference.MainActivity;
import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.SaveManager;
import com.bandwidth.androidreference.data.User;


public class RegisterFragment extends Fragment {

    private MainActivity mainActivity;
    private EditText editTextUsername;
    private Button buttonRegister;
    private ProgressBar progressBarRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) this.getActivity();

        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

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

        return rootView;
    }

    private View.OnClickListener registerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            buttonRegister.setVisibility(View.GONE);
            progressBarRegister.setVisibility(View.VISIBLE);
            mainActivity.hideKeyboard();

            final String username = editTextUsername.getText().toString();
            final String password = "somemadeuppassword";

            AsyncTask<String, Void, User> getUserAsync = new AsyncTask<String, Void, User>() {
                @Override
                protected User doInBackground(String... params) {
                    return ClientApi.getUser(params[0], params[1]);
                }

                @Override
                protected void onPostExecute(User user) {
                    if (user != null) {
                        user.getEndpoint().getCredentials().setPassword(password);
                        SaveManager.saveUser(mainActivity, user);
                        mainActivity.goToFragment(new DialerFragment());
                        Toast.makeText(mainActivity, getResources().getString(R.string.toast_register_message, user.getNumber()), Toast.LENGTH_LONG).show();
                    }
                    else {
                        progressBarRegister.setVisibility(View.GONE);
                        buttonRegister.setVisibility(View.VISIBLE);
                        Toast.makeText(mainActivity, getResources().getString(R.string.toast_register_error), Toast.LENGTH_LONG).show();
                    }
                }
            };

            getUserAsync.execute(username, password);
        }
    };
}