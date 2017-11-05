package org.exobel.routerkeygen;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import org.exobel.routerkeygen.ui.NetworksListActivity;

final class NotificationUtils {

    private static boolean channelCreated = false;

    private NotificationUtils() {
    }

    static void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26 || channelCreated) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(NotificationCompat.CATEGORY_SERVICE,
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
        channelCreated = true;
    }

    static NotificationCompat.Builder getSimple(Context context,
                                                CharSequence title, CharSequence text) {
        initChannels(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationCompat.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.ic_notification).setTicker(title)
                .setContentTitle(title).setContentText(text)
                .setOnlyAlertOnce(true).setAutoCancel(true)
                .setContentIntent(getDefaultPendingIntent(context));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        return builder;
    }

    static Notification createProgressBar(Context context,
                                          CharSequence title, CharSequence content, int max, int progress,
                                          boolean indeterminate, PendingIntent i) {
        final NotificationCompat.Builder builder = getSimple(context, title,
                content);
        builder.setContentIntent(i);
        builder.setOngoing(true);
        builder.setAutoCancel(false);
        builder.setProgress(max, progress, indeterminate);
        if (!indeterminate) {
            builder.addAction(
                    android.R.drawable.ic_menu_close_clear_cancel,
                    context.getString(android.R.string.cancel), i);
        }
        return builder.build();
    }

    static PendingIntent getDefaultPendingIntent(Context context) {
        return PendingIntent.getActivity(context, 0, new Intent(context,
                NetworksListActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
