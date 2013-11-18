/*
 * Copyright 2012 Rui Araújo, Luís Fonseca
 *
 * This file is part of Router Keygen.
 *
 * Router Keygen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Router Keygen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Router Keygen.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exobel.routerkeygen;

import java.util.List;
import org.exobel.routerkeygen.AutoConnectManager.onConnectionListener;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import com.farproc.wifi.connecter.Wifi;

public class AutoConnectService extends Service implements onConnectionListener {

	public final static String SCAN_RESULT = "org.exobel.routerkeygen.SCAN_RESULT";
	public final static String KEY_LIST = "org.exobel.routerkeygen.KEY_LIST";

	private final static int DISCONNECT_WAITING_TIME = 10000;

	private final static int FAILING_MINIMUM_TIME = 1500;
	private final int UNIQUE_ID = R.string.app_name
			+ AutoConnectService.class.getName().hashCode();

	private NotificationManager mNotificationManager;

	final private Binder mBinder = new LocalBinder();
	private ScanResult network;
	private List<String> keys;
	private int attempts = 0;
	private AutoConnectManager mReceiver;
	private WifiManager wifi;
	private int mNumOpenNetworksKept;
	private int currentNetworkId = -1;
	private boolean cancelNotification = true;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		AutoConnectService getService() {
			return AutoConnectService.this;
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@SuppressWarnings("deprecation")
	public void onCreate() {
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		mReceiver = new AutoConnectManager(wifi, this);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
			mNumOpenNetworksKept = Settings.Secure.getInt(getContentResolver(),
					Settings.Secure.WIFI_NUM_OPEN_NETWORKS_KEPT, 10);
		else
			mNumOpenNetworksKept = Settings.Global.getInt(getContentResolver(),
					Settings.Global.WIFI_NUM_OPEN_NETWORKS_KEPT, 10);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			stopSelf();
			return START_NOT_STICKY;
		}
		attempts = 0;
		currentNetworkId = -1;
		network = intent.getParcelableExtra(SCAN_RESULT);
		keys = intent.getStringArrayListExtra(KEY_LIST);
		final ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		final NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWifi.isConnected()) {
			if (wifi.disconnect()) {
				// besides disconnecting, we clean any previous configuration
				Wifi.cleanPreviousConfiguration(wifi, network,
						network.capabilities);
				mNotificationManager
						.notify(UNIQUE_ID,
								createProgressBar(
										getString(R.string.app_name),
										getString(R.string.not_auto_connect_waiting),
										0));
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						tryingConnection();
					}
				}, DISCONNECT_WAITING_TIME);
				cancelNotification = true;
			} else {
				mNotificationManager.notify(
						UNIQUE_ID,
						getSimple(getString(R.string.msg_error),
								getString(R.string.msg_error_key_testing))
								.build());
				cancelNotification = false;
				stopSelf();
				return START_NOT_STICKY;
			}
		} else {
			Wifi.cleanPreviousConfiguration(wifi, network, network.capabilities);
			tryingConnection();
		}
		return START_STICKY;
	}

	private void tryingConnection() {
		currentNetworkId = Wifi.connectToNewNetwork(this, wifi, network,
				keys.get(attempts++), mNumOpenNetworksKept);
		Log.d(AutoConnectManager.class.getSimpleName(),
				"Trying " + keys.get(attempts - 1));

		if (currentNetworkId != -1) {
			lastTimeDisconnected = System.currentTimeMillis();
			registerReceiver(mReceiver, new IntentFilter(
					WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
			mNotificationManager.notify(
					UNIQUE_ID,
					createProgressBar(
							getString(R.string.app_name),
							getString(R.string.not_auto_connect_key_testing,
									keys.get(attempts - 1)), attempts));
			cancelNotification = true;
		} else {
			mNotificationManager.notify(
					UNIQUE_ID,
					getSimple(getString(R.string.msg_error),
							getString(R.string.msg_error_key_testing)).build());
			cancelNotification = false;
			stopSelf();
		}
	}

	public void onDestroy() {
		try {
			if (cancelNotification)
				mNotificationManager.cancel(UNIQUE_ID);
			reenableAllHotspots();
			unregisterReceiver(mReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private long lastTimeDisconnected = -1;

	public void onFailedConnection() {
		/* Some phone are very strange and report multiples failures */
		if ((System.currentTimeMillis() - lastTimeDisconnected) < FAILING_MINIMUM_TIME) {
			Log.d(AutoConnectManager.class.getSimpleName(), "Ignoring signal");
			return;
		}
		lastTimeDisconnected = System.currentTimeMillis();
		wifi.removeNetwork(currentNetworkId);
		if (attempts >= keys.size()) {
			reenableAllHotspots();
			mNotificationManager.notify(
					UNIQUE_ID,
					getSimple(getString(R.string.msg_error),
							getString(R.string.msg_no_correct_keys)).build());
			cancelNotification = false;
			stopSelf();
			return;
		}
		tryingConnection();
	}

	public void onSuccessfulConection() {
		reenableAllHotspots();
		mNotificationManager.notify(
				UNIQUE_ID,
				getSimple(
						getString(R.string.app_name),
						getString(R.string.not_correct_key_testing,
								keys.get(attempts - 1))).build());
		cancelNotification = false;
		stopSelf();

	}

	private void reenableAllHotspots() {
		final List<WifiConfiguration> configurations = wifi
				.getConfiguredNetworks();
		if (configurations != null) {
			for (final WifiConfiguration config : configurations) {
				wifi.enableNetwork(config.networkId, false);
			}
		}
	}

	private NotificationCompat.Builder getSimple(CharSequence title,
			CharSequence context) {
		return new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_notification).setTicker(title)
				.setContentTitle(title).setContentText(context)
				.setContentIntent(getPendingIntent());
	}

	private Notification createProgressBar(CharSequence title,
			CharSequence content, int progress) {
		final NotificationCompat.Builder builder = getSimple(title, content);
		final PendingIntent i = PendingIntent.getActivity(
				getApplicationContext(),
				0,
				new Intent(this, CancelOperationActivity.class)
						.putExtra(CancelOperationActivity.SERVICE_TO_TERMINATE,
								AutoConnectService.class.getName())
						.putExtra(CancelOperationActivity.MESSAGE,
								getString(R.string.cancel_auto_test))
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
				PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(i);
		builder.setOngoing(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			builder.setProgress(keys.size(), progress, false);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				builder.addAction(
						android.R.drawable.ic_menu_close_clear_cancel,
						getString(android.R.string.cancel), i);
			}
		} else {
			final RemoteViews contentView = new RemoteViews(getPackageName(),
					R.layout.notification);
			contentView.setTextViewText(R.id.text1, content);
			contentView.setProgressBar(R.id.progress, keys.size(), progress,
					false);
			final Notification not = builder.build();
			not.contentView = contentView;
			return not;
		}
		return builder.build();
	}

	private PendingIntent getPendingIntent() {
		return PendingIntent.getActivity(getApplicationContext(), 0,
				new Intent(), // add this
								// pass null
								// to intent
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

}
