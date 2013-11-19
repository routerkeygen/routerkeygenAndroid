package org.exobel.routerkeygen;

import org.exobel.routerkeygen.ui.NetworksListActivity;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public final class NotificationUtils {

	public static NotificationCompat.Builder getSimple(Context context,
			CharSequence title, CharSequence text) {
		return new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_notification).setTicker(title)
				.setContentTitle(title).setContentText(text)
				.setOnlyAlertOnce(true).setAutoCancel(true)
				.setContentIntent(getDefaultPendingIntent(context));
	}

	@TargetApi(16)
	public static Notification updateProgressBar(Notification update, int max,
			int progress, boolean indeterminate) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			update.contentView.setProgressBar(android.R.id.progress, max,
					progress, indeterminate);
		else
			update.contentView.setProgressBar(R.id.progress, max, progress,
					indeterminate);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			update.bigContentView.setProgressBar(android.R.id.progress, max,
					progress, indeterminate);
		return update;
	}

	public static Notification createProgressBar(Context context,
			CharSequence title, CharSequence content, int max, int progress,
			boolean indeterminate, PendingIntent i) {
		final NotificationCompat.Builder builder = getSimple(context, title,
				content);
		builder.setContentIntent(i);
		builder.setOngoing(true);
		builder.setAutoCancel(false);
		final Notification update;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			builder.setProgress(max, progress, indeterminate);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
					&& !indeterminate) {
				builder.addAction(
						android.R.drawable.ic_menu_close_clear_cancel,
						context.getString(android.R.string.cancel), i);
			}
			update = builder.build();
		} else {
			RemoteViews contentView = new RemoteViews(context.getPackageName(),
					R.layout.notification);
			contentView.setTextViewText(R.id.text1, content);
			contentView.setProgressBar(R.id.progress, max, progress,
					indeterminate);
			update = builder.build();
			update.contentView = contentView;
		}
		return update;
	}

	public static PendingIntent getDefaultPendingIntent(Context context) {
		return PendingIntent.getActivity(context, 0, new Intent(context,
				NetworksListActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), // add this
				// pass null
				// to intent
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private NotificationUtils() {
	}
}
