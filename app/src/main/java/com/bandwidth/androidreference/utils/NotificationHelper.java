package com.bandwidth.androidreference.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.bandwidth.androidreference.R;
import com.bandwidth.androidreference.activity.CallActivity;
import com.bandwidth.androidreference.intent.BWSipIntent;

public class NotificationHelper {

    private final static int INCOMING_CALL_NOTIFICATION_ID = 1;
    private final static int ONGOING_CALL_NOTIFICATION_ID = 2;

    public static void placeIncomingCallNotification(Context context, String contact) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Intent answerIntent = new Intent(context, CallActivity.class);
        answerIntent.setAction(BWSipIntent.ANSWER_CALL);
        answerIntent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent answerPendingIntent = PendingIntent.getActivity(context, -1, answerIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent declineIntent = new Intent(context, CallActivity.class);
        declineIntent.setAction(BWSipIntent.DECLINE_CALL);
        declineIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        declineIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        declineIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        PendingIntent declinePendingIntent = PendingIntent.getActivity(context, -1, declineIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        builder.setContentTitle(contact)
                .setContentText(context.getResources().getString(R.string.incoming_call))
                .setSmallIcon(R.drawable.ic_stat_phone_call)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .addAction(R.drawable.ic_stat_decline, context.getResources().getString(R.string.decline), declinePendingIntent)
                .addAction(R.drawable.ic_stat_answer, context.getResources().getString(R.string.answer), answerPendingIntent)
                .setAutoCancel(true)
                .setSound(uri)
                .setVibrate(new long[]{0, 1000, 1000, 1000, 1000});

        notificationManager.notify(1, builder.build());
    }

    public static void clearIncomingCallNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(INCOMING_CALL_NOTIFICATION_ID);
    }

    public static void clearOngoingCallNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(ONGOING_CALL_NOTIFICATION_ID);
    }
}
