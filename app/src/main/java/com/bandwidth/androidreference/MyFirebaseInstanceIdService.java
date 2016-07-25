package com.bandwidth.androidreference;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import cz.acrobits.ali.AndroidUtil;
import cz.acrobits.libsoftphone.Instance;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    public void sendRegistrationToServer(final String token) {
        // execute code on main thread and block current thread until it finishes
        AndroidUtil.rendezvous(new Runnable() {
            @Override
            public void run() {
                Instance.Notifications.Push.setRegistrationId(token);
            }
        });
    }
}
