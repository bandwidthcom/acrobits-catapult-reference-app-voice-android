package com.bandwidth.androidreference;

import android.widget.Toast;

import com.bandwidth.androidreference.fragment.IncomingCallFragment;
import com.bandwidth.androidreference.utils.NumberUtils;
import com.bandwidth.bwsip.BWAccount;
import com.bandwidth.bwsip.BWCall;
import com.bandwidth.bwsip.BWPhone;
import com.bandwidth.bwsip.BWTone;
import com.bandwidth.bwsip.constants.BWCallState;
import com.bandwidth.bwsip.constants.BWSipResponse;
import com.bandwidth.bwsip.constants.BWTransport;
import com.bandwidth.bwsip.delegates.BWAccountDelegate;
import com.bandwidth.bwsip.delegates.BWCallDelegate;

public class CallService implements BWCallDelegate, BWAccountDelegate {

    private BWPhone phone;
    private BWAccount account;
    private BWCall currentCall;
    private MainActivity mainActivity;
    private boolean isRegistered = false;

    private static CallService instance;

    public static CallService getInstance(MainActivity mainActivity) {
        if (instance == null) {
            instance = new CallService(mainActivity);
        }
        return instance;
    }

    private CallService(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        phone = BWPhone.getInstance();
        phone.setTransportType(BWTransport.UDP);
        phone.setLogLevel(9);
        phone.initialize();
        registerUser();
    }

    @Override
    public void onCallStateChanged(BWCall bwCall) {
        if (bwCall.getLastState().equals(BWCallState.DISCONNECTED)) {
            mainActivity.runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   mainActivity.getDialerFragment().callEnded();
               }
            });
        }
    }

    @Override
    public void onIncomingDTMF(BWCall bwCall, String s) {
        BWTone.playDigit(s);
    }

    @Override
    public void onRegStateChanged(final BWAccount bwAccount) {
        if (bwAccount.getLastState().equals(BWSipResponse.OK)) {
            isRegistered = true;
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.getDialerFragment().setCallButtonEnabled(true);
                }
            });
        }
        else {
            isRegistered = false;
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.getDialerFragment().setCallButtonEnabled(false);
                    Toast.makeText(mainActivity, mainActivity.getResources().getString(R.string.toast_registration_error, bwAccount.getLastState().toString()), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onIncomingCall(BWCall bwCall) {
        bwCall.setDelegate(this);
        bwCall.answerCall(BWSipResponse.RINGING);
        currentCall = bwCall;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                IncomingCallFragment incomingCallFragment = new IncomingCallFragment();
                incomingCallFragment.setCall(currentCall);
                mainActivity.goToFragment(incomingCallFragment, true);
            }
        });
    }

    private void registerUser() {
        account = new BWAccount(phone);

        account.setDelegate(this);

        // Using ICE to connect to his account
        account.setIceEnabled(true);

        // Specifying the SIP registrar
        account.setRegistrar(SaveManager.getRealm(mainActivity));

        // Setting the username and password
        account.setCredentials(SaveManager.getCredUsername(mainActivity), SaveManager.getPassword(mainActivity));
        account.connect();
    }

    public BWCall makeCall(String number) {
        String tn = NumberUtils.removeExtraCharacters(number);
        if (tn.charAt(0) != '1') {
            tn = "1" + tn;
        }
        String registrar = SaveManager.getRealm(mainActivity);
        currentCall = new BWCall(account);
        currentCall.setDelegate(this);
        currentCall.setRemoteUri(tn + "@" + registrar);
        currentCall.makeCall();
        return currentCall;
    }

    public void answerIncomingCall() {
        currentCall.answerCall(BWSipResponse.OK);
    }

    public void declineIncomingCall() {
        currentCall.answerCall(BWSipResponse.DECLINE);
        endCall();
    }

    public void endCall() {
        currentCall.hangupCall();
        currentCall.close();
        currentCall = null;
    }

    public BWCall getCurrentCall() {
        return currentCall;
    }

    public boolean isRegistered() {
        return isRegistered;
    }
}
