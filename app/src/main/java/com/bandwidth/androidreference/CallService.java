package com.bandwidth.androidreference;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bandwidth.androidreference.activity.IncomingCallActivity;
import com.bandwidth.androidreference.intent.BWSipIntent;
import com.bandwidth.androidreference.utils.NotificationHelper;
import com.bandwidth.androidreference.utils.NumberUtils;
import com.bandwidth.androidreference.utils.SaveManager;

import java.util.Date;

import cz.acrobits.ali.AndroidUtil;
import cz.acrobits.ali.Xml;
import cz.acrobits.libsoftphone.Instance;
import cz.acrobits.libsoftphone.data.AudioRoute;
import cz.acrobits.libsoftphone.data.Call;
import cz.acrobits.libsoftphone.data.RegistrationState;
import cz.acrobits.libsoftphone.event.CallEvent;
import cz.acrobits.libsoftphone.event.StreamParty;
import cz.acrobits.libsoftphone.support.Listeners;

public class CallService extends Service implements Listeners.OnIncomingCall,
        Listeners.OnRegistrationStateChanged, Listeners.OnCallStateChanged {

    private static final String TAG = "CallService";
    public static final String TEST_ACCOUNT_ID = "Test Account";

    private static IntentReceiver intentReceiver;
    private static Long callStartTime;
    private static RegistrationState registrationState = RegistrationState.NotRegistered;
    private static boolean isLibraryInitialized;

    private final Listeners listeners = new Listeners();

    private CallEvent currentCall;
    private Xml accountXml;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
            intentFilter.addAction(BWSipIntent.PHONE_CALL);
            intentFilter.addAction(BWSipIntent.MUTE);
            intentFilter.addAction(BWSipIntent.UNMUTE);
            intentFilter.addAction(BWSipIntent.SPEAKER);
            intentFilter.addAction(BWSipIntent.EARPIECE);
            intentFilter.addAction(BWSipIntent.RENEW_REGISTRATION);
            intentFilter.addAction(BWSipIntent.DEREGISTER);
            LocalBroadcastManager.getInstance(this).registerReceiver(intentReceiver, intentFilter);
        }

        Instance.loadLibrary(getApplicationContext());
        Xml prov = new Xml("provisioning");

        Xml saas = new Xml("saas");
        saas.replaceChild("identifier", getString(R.string.acrobits_license_id));
        prov.replaceChild(saas);

        if (!isLibraryInitialized) {
            isLibraryInitialized = Instance.init(getApplicationContext(), prov);
        }

        Instance.setObserver(listeners);
        listeners.register(this);
        Instance.State.update(Instance.State.Background);

        Instance.Audio.setCallAudioRoute(AudioRoute.Headset);

        if (accountXml == null) {
            registerUser();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onIncomingCall(@NonNull CallEvent callEvent) {
        Log.i(TAG, "New incoming call: " + Instance.Calls.getState(callEvent));

        if (currentCall == null) {
            if (Instance.Calls.getState(callEvent) != Call.State.IncomingRinging) {
                return;
            }

            currentCall = callEvent;
            callStartTime = new Date().getTime();

            KeyguardManager keyguardManager = (KeyguardManager) getBaseContext().getSystemService(Context.KEYGUARD_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !keyguardManager.inKeyguardRestrictedInputMode()) {
                NotificationHelper.placeIncomingCallNotification(getBaseContext(), callEvent.getRemoteUser().getTransportUri());
            }
            else {
                Intent intent = new Intent(getBaseContext(), IncomingCallActivity.class);
                intent.setAction(BWSipIntent.INCOMING_CALL);
                intent.putExtra(BWSipIntent.INCOMING_CALL, callEvent.getRemoteUser().getTransportUri());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                getApplication().startActivity(intent);
            }
        }
        else {
            Instance.Calls.rejectIncoming(callEvent);
        }
    }

    @Override
    public void onRegistrationStateChanged(@Nullable String changedAccount, @NonNull RegistrationState newRegistrationState) {
        Log.i(TAG, String.format("Account [%s] new registration state [%s]", changedAccount, newRegistrationState));

        registrationState = newRegistrationState;
        broadcastRegistrationState();
    }

    @Override
    public void onCallStateChanged(@NonNull CallEvent callEvent, @NonNull Call.State state) {
        Log.i(TAG, String.format("Call [%s] state changed to [%s]", callEvent, state));

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent();
        intent.setAction(BWSipIntent.CALL_STATE);
        intent.putExtra(BWSipIntent.CALL_STATE, state);
        broadcastManager.sendBroadcast(intent);

        if (state.isTerminal()) {
            endCall();
        }
    }

    private void broadcastRegistrationState() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent();
        intent.setAction(BWSipIntent.REGISTRATION);
        intent.putExtra(BWSipIntent.REGISTRATION, registrationState);
        broadcastManager.sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int fvlags, int startId) {
        return START_STICKY;
    }

    private void registerUser() {
        if (SaveManager.getUsername(getBaseContext()) != null) {
            accountXml = Instance.Registration.getAccount(TEST_ACCOUNT_ID);
            if (accountXml == null) {
                // Register an account
                accountXml = new Xml("account");
                accountXml.setAttribute("id", TEST_ACCOUNT_ID);
                accountXml.setChildValue("username", SaveManager.getCredUsername(this));
                accountXml.setChildValue("password", SaveManager.getPassword(this));
                accountXml.setChildValue("host", SaveManager.getRealm(this));
                accountXml.setChildValue("natTraversal", "ice");
                accountXml.setChildValue("pushMethod", "tunnel");
                accountXml.setChildValue("sipisHost", "https://ec2-107-21-79-106.compute-1.amazonaws.com/sipis/");
                accountXml.setChildValue("sipisRegServer", "https://ec2-107-21-79-106.compute-1.amazonaws.com/sipis/");
                Instance.Registration.saveAccount(accountXml);
            }
            Instance.State.update(Instance.State.Active);

            registrationState = Instance.Registration.getRegistrationState(TEST_ACCOUNT_ID);
            broadcastRegistrationState();
        }
    }

    private CallEvent makeCall(String number) {
        String tn = NumberUtils.removeExtraCharacters(number);
        if (tn.charAt(0) != '1') {
            tn = "1" + tn;
        }
        tn = "+" + tn;
        currentCall = new CallEvent(new StreamParty(tn).match(TEST_ACCOUNT_ID).toRemoteUser());
        int res = Instance.Events.post(currentCall);
        if (res != Instance.Events.PostResult.SUCCESS)
        {
            AndroidUtil.toast(false, "Call failed: %d", res);
            currentCall = null;
            return null;
        }
        callStartTime = new Date().getTime();
        Instance.Audio.setCallAudioRoute(AudioRoute.Headset);
        return currentCall;
    }

    private void answerIncomingCall() {
        Instance.Audio.setCallAudioRoute(AudioRoute.Headset);

        Instance.Calls.answerIncoming(currentCall, Call.DesiredMedia.voiceOnly());
    }

    private void declineIncomingCall() {
        if (currentCall != null) {
            Instance.Calls.ignoreIncoming(currentCall);
            endCall();
        }
    }

    private void endCall() {
        if (currentCall != null) {
            Instance.Calls.hangup(currentCall);
            currentCall.close();
            currentCall = null;
            Instance.Audio.setCallAudioRoute(AudioRoute.Speaker);
        }
    }

    public static Long getCallStartTime() {
        return callStartTime;
    }

    private void renewRegistration() {
        if (accountXml != null) {
            Instance.Registration.deleteAccount(TEST_ACCOUNT_ID);
            accountXml = null;
        }
        registerUser();
    }

    private void deregister() {
        if (accountXml != null) {
            Instance.Registration.deleteAccount(TEST_ACCOUNT_ID);
            accountXml = null;
            registrationState = RegistrationState.NotRegistered;
            broadcastRegistrationState();
        }
    }

    public static RegistrationState getRegistrationState() {
        return registrationState;
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
            else if (intent.getAction().equals(BWSipIntent.PHONE_CALL)) {
                makeCall(intent.getStringExtra(BWSipIntent.PHONE_CALL));
            }
            else if (intent.getAction().equals(BWSipIntent.MUTE)) {
                Instance.Audio.setMuted(true);
            }
            else if (intent.getAction().equals(BWSipIntent.UNMUTE)) {
                Instance.Audio.setMuted(false);
            }
            else if (intent.getAction().equals(BWSipIntent.SPEAKER)) {
                Instance.Audio.setCallAudioRoute(AudioRoute.Speaker);
            }
            else if (intent.getAction().equals(BWSipIntent.EARPIECE)) {
                Instance.Audio.setCallAudioRoute(AudioRoute.Headset);
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
