package com.bandwidth.androidreference;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import cz.acrobits.libsoftphone.Instance;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();

    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    public void sendRegistrationToServer(final String token) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Instance.Notifications.Push.setRegistrationId(token);
            }
        });
    }
}
