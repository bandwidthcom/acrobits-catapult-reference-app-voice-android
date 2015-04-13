package com.bandwidth.androidreference;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.bandwidth.androidreference.activity.IncomingCallActivity;
import com.bandwidth.androidreference.intent.BWSipIntent;
import com.bandwidth.androidreference.utils.NumberUtils;
import com.bandwidth.androidreference.utils.SaveManager;
import com.bandwidth.bwsip.BWAccount;
import com.bandwidth.bwsip.BWCall;
import com.bandwidth.bwsip.BWPhone;
import com.bandwidth.bwsip.constants.BWOutputRoute;
import com.bandwidth.bwsip.constants.BWSipResponse;
import com.bandwidth.bwsip.constants.BWTransport;
import com.bandwidth.bwsip.delegates.BWAccountDelegate;
import com.bandwidth.bwsip.delegates.BWCallDelegate;

import java.util.Date;

public class CallService extends Service implements BWCallDelegate, BWAccountDelegate {

    private static BWPhone phone;
    private static BWAccount account;
    private static BWCall currentCall;
    private static IntentReceiver intentReceiver;
    private static long callStartTime;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public CallService getService() {
            return CallService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // return true so our service is not destroyed on unbind.
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (intentReceiver == null) {
            intentReceiver = new IntentReceiver();
            // Set up broadcast receiver
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BWSipIntent.ANSWER_CALL);
            intentFilter.addAction(BWSipIntent.DECLINE_CALL);
            intentFilter.addAction(BWSipIntent.END_CALL);
            intentFilter.addAction(BWSipIntent.MAKE_CALL);
            intentFilter.addAction(BWSipIntent.MUTE);
            intentFilter.addAction(BWSipIntent.UNMUTE);
            intentFilter.addAction(BWSipIntent.SPEAKER);
            intentFilter.addAction(BWSipIntent.EARPIECE);
            intentFilter.addAction(BWSipIntent.RENEW_REGISTRATION);
            intentFilter.addAction(BWSipIntent.DEREGISTER);
            LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver, intentFilter);
        }

        if (phone == null) {
            phone = BWPhone.getInstance();
            phone.setTransportType(BWTransport.UDP);
            phone.setLogLevel(9);
            phone.initialize();
        }
        if (account == null) {
            registerUser();
        }
    }

    @Override
    public void onCallStateChanged(BWCall bwCall) {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent();
        intent.setAction(BWSipIntent.CALL_STATE);
        intent.putExtra(BWSipIntent.CALL_STATE, bwCall.getLastState());
        broadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onIncomingDTMF(BWCall bwCall, String s) {

    }

    @Override
    public void onRegStateChanged(final BWAccount bwAccount) {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent();
        intent.setAction(BWSipIntent.REGISTRATION);
        intent.putExtra(BWSipIntent.REGISTRATION, bwAccount.getLastState());
        broadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onIncomingCall(BWCall bwCall) {
        if (currentCall == null) {
            currentCall = bwCall;
            bwCall.setDelegate(this);
            bwCall.answerCall(BWSipResponse.RINGING);
            callStartTime = new Date().getTime();

            Intent intent = new Intent(getBaseContext(), IncomingCallActivity.class);
            intent.setAction(BWSipIntent.INCOMING_CALL);
            intent.putExtra(BWSipIntent.INCOMING_CALL, NumberUtils.fromSipUri(bwCall.getRemoteUri()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            getApplication().startActivity(intent);
        }
        else {
            bwCall.answerCall(BWSipResponse.BUSY_HERE);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int fvlags, int startId) {
        return START_STICKY;
    }

    private void registerUser() {
        if (SaveManager.getUsername(getBaseContext()) != null) {
            account = new BWAccount(phone);

            account.setDelegate(this);

            account.setIceEnabled(true);

            account.setRegistrar(SaveManager.getRealm(this));

            account.setCredentials(SaveManager.getCredUsername(this), SaveManager.getPassword(this));
            account.connect();
        }
    }

    private BWCall makeCall(String number) {
        String tn = NumberUtils.removeExtraCharacters(number);
        if (tn.charAt(0) != '1') {
            tn = "1" + tn;
        }
        String registrar = SaveManager.getRealm(this);
        currentCall = new BWCall(account);
        currentCall.setDelegate(this);
        currentCall.setRemoteUri(tn + "@" + registrar);
        currentCall.makeCall();
        callStartTime = new Date().getTime();
        return currentCall;
    }

    private void answerIncomingCall() {
        phone.setAudioOutputRoute(getBaseContext(), BWOutputRoute.EARPIECE);
        currentCall.answerCall(BWSipResponse.OK);
    }

    private void declineIncomingCall() {
        if (currentCall != null) {
            currentCall.answerCall(BWSipResponse.DECLINE);
            endCall();
        }
    }

    private void endCall() {
        if (currentCall != null) {
            currentCall.hangupCall();
            currentCall.close();
            currentCall = null;
        }
    }

    public static long getCallStartTime() {
        return callStartTime;
    }

    public static void dialDTMF(String digits) {
        if (currentCall != null) {
            currentCall.dialDTMF(digits);
        }
    }

    private void renewRegistration() {
        if (account != null) {
            account.updateRegistration(true);
        }
        else {
            registerUser();
        }
    }

    private void deregister() {
        if (account != null) {
            account.close();
            account = null;
        }
    }

    private class IntentReceiver extends BroadcastReceiver
    {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BWSipIntent.ANSWER_CALL)) {
                answerIncomingCall();
            }
            else if (intent.getAction().equals(BWSipIntent.DECLINE_CALL)) {
                declineIncomingCall();
            }
            else if (intent.getAction().equals(BWSipIntent.END_CALL)) {
                endCall();
            }
            else if (intent.getAction().equals(BWSipIntent.MAKE_CALL)) {
                makeCall(intent.getStringExtra(BWSipIntent.MAKE_CALL));
            }
            else if (intent.getAction().equals(BWSipIntent.MUTE)) {
                currentCall.setMute(true);
            }
            else if (intent.getAction().equals(BWSipIntent.UNMUTE)) {
                currentCall.setMute(false);
            }
            else if (intent.getAction().equals(BWSipIntent.SPEAKER)) {
                phone.setAudioOutputRoute(getBaseContext(), BWOutputRoute.LOUDSPEAKER);
            }
            else if (intent.getAction().equals(BWSipIntent.EARPIECE)) {
                phone.setAudioOutputRoute(getBaseContext(), BWOutputRoute.EARPIECE);
            }
            else if (intent.getAction().equals(BWSipIntent.RENEW_REGISTRATION)) {
                renewRegistration();
            }
            else if (intent.getAction().equals(BWSipIntent.DEREGISTER)) {
                deregister();
            }
        }
    }

}
