package com.bandwidth.androidreference;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import cz.acrobits.ali.AndroidUtil;
import cz.acrobits.ali.Xml;
import cz.acrobits.libsoftphone.Instance;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i(TAG, "Received new message: " + remoteMessage.getData());

        // execute code on main thread and block current thread until it finishes
        AndroidUtil.rendezvous(new Runnable() {
            @Override
            public void run() {
                final Xml xml = new Xml("pushMessage");
                for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                    xml.setChildValue(entry.getKey(), entry.getValue());
                }
                Instance.Notifications.Push.handle(xml);
            }
        });
    }
}
