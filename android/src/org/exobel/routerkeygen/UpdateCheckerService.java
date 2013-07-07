package org.exobel.routerkeygen;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.exobel.routerkeygen.ui.Preferences;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class UpdateCheckerService extends IntentService {

	private final static String URL_DOWNLOAD = "https://raw.github.com/ruiaraujo/routerkeygen/master/android/routerkeygen_version.json";

	public UpdateCheckerService() {
		super("UpdateCheckerService");
	}

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private final int UNIQUE_ID = R.string.app_name
			+ UpdateCheckerService.class.getName().hashCode();

	@Override
	protected void onHandleIntent(Intent intent) {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		try {
			final JSONObject version = getRemoteObjectAsJson(new URL(
					URL_DOWNLOAD));
			if (version == null)
				return;
			if (!Preferences.VERSION.equals(version.getString("version"))) {
				final NotificationCompat.Builder builder = new NotificationCompat.Builder(
						this)
						.setSmallIcon(R.drawable.icon)
						.setTicker(getString(R.string.update_title))
						.setContentTitle(getString(R.string.update_title))
						.setContentText(
								getString(R.string.update_message, version))
						.setOnlyAlertOnce(true)
						.setContentIntent(
								PendingIntent.getActivity(
										getApplicationContext(), 0,
										new Intent(Intent.ACTION_VIEW)
												.setData(Uri.parse(version
														.getString("url"))),
										PendingIntent.FLAG_ONE_SHOT));
				mNotificationManager.notify(UNIQUE_ID, builder.build());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected static JSONObject getRemoteObjectAsJson(URL url) {
		InputStream inputStream = null;
		try {
			byte[] buffer = new byte[128];
			int read = 0;
			String jsonAsString = new String();

			inputStream = url.openStream();

			do {
				read = inputStream.read(buffer);
				if (read > 0) {
					jsonAsString += new String(buffer, 0, read);
				}
			} while (read > -1);

			return new JSONObject(jsonAsString);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}
}
