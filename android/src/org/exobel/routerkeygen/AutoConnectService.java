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
import android.util.Log;

import com.farproc.wifi.connecter.Wifi;

public class AutoConnectService extends Service implements onConnectionListener {

	public final static String SCAN_RESULT = "org.exobel.routerkeygen.SCAN_RESULT";
	public final static String KEY_LIST = "org.exobel.routerkeygen.KEY_LIST";

	private final static int DISCONNECT_WAITING_TIME = 10000;

	private final static int FAILING_MINIMUM_TIME = 1500;
	private final int UNIQUE_ID = R.string.app_name
			+ AutoConnectService.class.getName().hashCode();

	private NotificationManager mNotificationManager;
	private Handler handler;

	final private Binder mBinder = new LocalBinder();
	private ScanResult network;
	private List<String> keys;
	private int attempts = 0;
	private AutoConnectManager mReceiver;
	private WifiManager wifi;
	private int mNumOpenNetworksKept;
	private int currentNetworkId = -1;
	private boolean cancelNotification = true;

	private Runnable tryAfterDisconnecting = new Runnable() {
		public void run() {
			tryingConnection();
		}
	};

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
		handler = new Handler();
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
								NotificationUtils
										.createProgressBar(
												this,
												getString(R.string.app_name),
												getString(R.string.not_auto_connect_waiting),
												keys.size(),
												0,
												false,
												getDefaultPendingIntent(getApplicationContext())));
				handler.postDelayed(tryAfterDisconnecting,
						DISCONNECT_WAITING_TIME);
				cancelNotification = true;
			} else {
				mNotificationManager.notify(
						UNIQUE_ID,
						NotificationUtils.getSimple(this,
								getString(R.string.msg_error),
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
			if (attempts == 1)// first try, we register the listener
				registerReceiver(mReceiver, new IntentFilter(
						WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
			mNotificationManager.notify(UNIQUE_ID, NotificationUtils
					.createProgressBar(
							this,
							getString(R.string.app_name),
							getString(R.string.not_auto_connect_key_testing,
									keys.get(attempts - 1)), keys.size(),
							attempts, false,
							getDefaultPendingIntent(getApplicationContext())));
			cancelNotification = true;
		} else {
			mNotificationManager.notify(
					UNIQUE_ID,
					NotificationUtils.getSimple(this,
							getString(R.string.msg_error),
							getString(R.string.msg_error_key_testing)).build());
			cancelNotification = false;
			stopSelf();
		}
	}

	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(tryAfterDisconnecting);
		if (cancelNotification)
			mNotificationManager.cancel(UNIQUE_ID);
		reenableAllHotspots();
		try {
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
					NotificationUtils.getSimple(this,
							getString(R.string.msg_error),
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
				NotificationUtils.getSimple(
						this,
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

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static PendingIntent getDefaultPendingIntent(Context context) {
		final Intent i = new Intent(context, CancelOperationActivity.class)
				.putExtra(CancelOperationActivity.SERVICE_TO_TERMINATE,
						AutoConnectService.class.getName())
				.putExtra(CancelOperationActivity.MESSAGE,
						context.getString(R.string.cancel_auto_test))
				.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		return PendingIntent.getActivity(context, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}
}
