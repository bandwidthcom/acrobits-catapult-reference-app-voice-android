package com.bandwidth.androidreference;

import com.bandwidth.androidreference.fragment.IncomingCallFragment;
import com.bandwidth.bwsip.BWAccount;
import com.bandwidth.bwsip.BWCall;
import com.bandwidth.bwsip.BWPhone;
import com.bandwidth.bwsip.BWTone;
import com.bandwidth.bwsip.constants.BWSipResponse;
import com.bandwidth.bwsip.constants.BWTransport;
import com.bandwidth.bwsip.delegates.BWAccountDelegate;
import com.bandwidth.bwsip.delegates.BWCallDelegate;

public class CallService implements BWCallDelegate, BWAccountDelegate {

    private BWPhone phone;
    private BWAccount account;
    private static CallService instance;
    private static MainActivity mainActivity;
    private static BWCall currentCall;

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

    }

    @Override
    public void onIncomingDTMF(BWCall bwCall, String s) {
        BWTone.playDigit(s);
    }

    @Override
    public void onRegStateChanged(BWAccount bwAccount) {

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
        String registrar = SaveManager.getRealm(mainActivity);
        currentCall = new BWCall(account);
        currentCall.setDelegate(this);
        currentCall.setRemoteUri(number + "@" + registrar);
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
}
