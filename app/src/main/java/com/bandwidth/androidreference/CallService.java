package com.bandwidth.androidreference;

import android.app.AlertDialog;
import android.content.Context;

import com.bandwidth.androidreference.data.User;
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
    private static Context context;
    private static BWCall currentCall;

    public static CallService getInstance(Context context) {
        if (instance == null) {
            instance = new CallService(context);
        }
        return instance;
    }

    private CallService(Context context) {
        this.context = context;
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
        bwCall.answerCall(BWSipResponse.RINGING);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Ring, ring, ring...").create().show();
    }

    private void registerUser() {
        account = new BWAccount(phone);

        account.setDelegate(this);

        // Using ICE to connect to his account
        account.setIceEnabled(true);

        // Specifying the SIP registrar
        account.setRegistrar(SaveManager.getRealm(context));

        // Setting the username and password
        account.setCredentials(SaveManager.getCredUsername(context), SaveManager.getPassword(context));
        account.connect();
    }

    public BWCall makeCall(String number) {
        String registrar = SaveManager.getRealm(context);
        currentCall = new BWCall(account);
        currentCall.setDelegate(this);
        currentCall.setRemoteUri(number + "@" + registrar);
        currentCall.makeCall();
        return currentCall;
    }

    public void endCall() {
        currentCall.hangupCall();
        currentCall.close();
    }
}
