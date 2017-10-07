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
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.farproc.wifi.connecter.Wifi;

import org.exobel.routerkeygen.AutoConnectManager.onConnectionListener;
import org.exobel.routerkeygen.utils.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoConnectService extends Service implements onConnectionListener {
    public final static String SCAN_RESULT = "org.exobel.routerkeygen.SCAN_RESULT";
    public final static String KEY_LIST = "org.exobel.routerkeygen.KEY_LIST";

    private final static int DISCONNECT_WAITING_TIME = 10000;
    private final static String TAG = AutoConnectManager.class.getSimpleName();

    private final static int FAILING_MINIMUM_TIME = 1500;
    private final int UNIQUE_ID = R.string.app_name
            + AutoConnectService.class.getName().hashCode();
    final private Binder mBinder = new LocalBinder();
    private NotificationManager mNotificationManager;
    private Handler handler;
    private ScanResult network;
    private List<String> keys;
    private final AtomicBoolean registered = new AtomicBoolean(false);
    private final AtomicBoolean scanningStarted = new AtomicBoolean(false);
    private final AtomicBoolean waitingToDisconnect = new AtomicBoolean(false);
    private final AtomicInteger handshakeAttempt = new AtomicInteger(-1);
    private final AtomicInteger sameHandshakeAttempts = new AtomicInteger(-1);
    private int attempts = 0;
    private AutoConnectManager mReceiver;
    private WifiManager wifi;
    private int mNumOpenNetworksKept;
    private int currentNetworkId = -1;
    private boolean cancelNotification = true;
    private long lastTimeDisconnected = -1;
    private final Runnable tryAfterDisconnecting = new Runnable() {
        public void run() {
            if (!scanningStarted.get()) {
                Log.d(TAG, "Scanning still not started, fallback");
                tryingConnection();
            }
        }
    };

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

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressWarnings("deprecation")
    public void onCreate() {
        handler = new Handler();
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mReceiver = new AutoConnectManager(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            mNumOpenNetworksKept = Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.WIFI_NUM_OPEN_NETWORKS_KEPT, 10);
        else
            mNumOpenNetworksKept = Settings.Global.getInt(getContentResolver(),
                    Settings.Global.WIFI_NUM_OPEN_NETWORKS_KEPT, 10);

    }

    @Override
    @SuppressWarnings("deprecation")
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
        final NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!registered.get()){
            registerReceiver(mReceiver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
            registerReceiver(mReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
            registered.set(true);
        }

        if (mWifi.isConnected()) {//TODO: before each tryConnect???
            waitingToDisconnect.set(true);
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
                handler.postDelayed(tryAfterDisconnecting, DISCONNECT_WAITING_TIME);
                cancelNotification = true;
            } else {
                waitingToDisconnect.set(false);
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

    private int disconnectCurrent(){
        final ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        final NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!registered.get()){
            registerReceiver(mReceiver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
            registerReceiver(mReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
            registered.set(true);
        }

        if (mWifi.isConnected()) {
            waitingToDisconnect.set(true);
            if (wifi.disconnect()) {
                // besides disconnecting, we clean any previous configuration
                Wifi.cleanPreviousConfiguration(wifi, network, network.capabilities);
                cancelNotification = true;
                handler.postDelayed(tryAfterDisconnecting, DISCONNECT_WAITING_TIME);

                return 1;

            } else {
                waitingToDisconnect.set(false);
                mNotificationManager.notify(
                        UNIQUE_ID,
                        NotificationUtils.getSimple(this,
                                getString(R.string.msg_error),
                                getString(R.string.msg_error_key_testing))
                                .build());
                cancelNotification = false;
                stopSelf();
                return -1;
            }
        } else {
            Log.d(TAG, "Not connected");
            tryingConnection();
            return 0;
        }
    }

    private void tryingConnection() {
        currentNetworkId = -1;
        try {
            scanningStarted.set(true);

            // If attempt counter is too high, we are done here.
            if (keys.size() <= attempts){
                Log.e(TAG, "Attempt counter too big, stopping");
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

            currentNetworkId = Wifi.connectToNewNetwork(this, wifi, network, keys.get(attempts), mNumOpenNetworksKept);
            Log.d(AutoConnectManager.class.getSimpleName(), "Trying " + keys.get(attempts));
            if (currentNetworkId != -1) {
                lastTimeDisconnected = System.currentTimeMillis();
                mNotificationManager
                        .notify(UNIQUE_ID,
                                NotificationUtils
                                        .createProgressBar(
                                                this,
                                                getString(R.string.app_name),
                                                getString(
                                                        R.string.not_auto_connect_key_testing,
                                                        keys.get(attempts)),
                                                keys.size(),
                                                attempts,
                                                false,
                                                getDefaultPendingIntent(getApplicationContext())));
                cancelNotification = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(AutoConnectService.class.getSimpleName(), "Error during connection", e);
        }
        if (currentNetworkId == -1) {
            mNotificationManager.notify(
                    UNIQUE_ID,
                    NotificationUtils.getSimple(this,
                            getString(R.string.msg_error),
                            getString(R.string.msg_error_key_testing)).build());
            cancelNotification = false;
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(tryAfterDisconnecting);
        if (cancelNotification)
            mNotificationManager.cancel(UNIQUE_ID);
        reenableAllHotspots();
        try {
            if (registered.get()) {
                Log.e(AutoConnectService.class.getSimpleName(), "Unregistering listener");
                unregisterReceiver(mReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailedConnection(int supplicantError) {
        if (waitingToDisconnect.get()){
            Log.d(TAG, "onFailed, as expected, start scan");
            waitingToDisconnect.set(false);
            tryingConnection();
            return;
        }

        if (!scanningStarted.get()){
            Log.d(TAG, "onFailed, not yet started");
            return;
        }

        Log.d(TAG, String.format("onFailed, error: %s, handshaked attempt: %s, current attempt: %s",
                supplicantError, handshakeAttempt.get(), attempts));

        /* Some phone are very strange and report multiples failures */
        if ((System.currentTimeMillis() - lastTimeDisconnected) < FAILING_MINIMUM_TIME) {
            Log.d(TAG, "Ignoring signal");
            handler.postDelayed(tryAfterDisconnecting, DISCONNECT_WAITING_TIME);
            return;
        }

        lastTimeDisconnected = System.currentTimeMillis();
        wifi.removeNetwork(currentNetworkId);

        // The password has to go through handshake phase.
        if (attempts != handshakeAttempt.get()){
            Log.w(TAG, "Handshaked password does not match the attempt");
            final int failedAttempts = sameHandshakeAttempts.incrementAndGet();

            if (failedAttempts >= 8) {
                Log.w(TAG, "Too many missed handshakes, trying without it.");
                sameHandshakeAttempts.set(0);
                attempts+=1;

            } else if (failedAttempts >= 4) {
                Log.w(TAG, "Too many missed handshakes, Reinit");
                disconnectCurrent();
                return;
            }

            tryingConnection();
            return;
        }

        // Failed to connect, increase attempt ctr to move to next password
        attempts+=1;
        tryingConnection();
    }

    @Override
    public void onFourWayHandshake(int supplicantError){
        handshakeAttempt.set(attempts);
        Log.d(AutoConnectManager.class.getSimpleName(),
                String.format("4Way handshake - Trying %s, error: %s", keys.get(attempts), supplicantError));
    }

    @Override
    public void onNetworkChanged(NetworkInfo networkInfo, String bssid, WifiInfo wifiInfo) {
        // If scanning not yet started, not interested in network change.
        if (!scanningStarted.get()){
            return;
        }
    }

    @Override
    public void onSuccessfulConection(int supplicantError) {
        if (!scanningStarted.get()) {
            Log.d(TAG, "onSuccess, but scanning not started yet.");
            handler.postDelayed(tryAfterDisconnecting, DISCONNECT_WAITING_TIME);
            return;
        }

        if (waitingToDisconnect.get()){
            Log.d(TAG, "onSuccess, but waiting to disconnect...");
            disconnectCurrent();
            return;
        }

        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService (Context.WIFI_SERVICE);
        final WifiInfo wifiInfo = wifiManager == null ? null : wifiManager.getConnectionInfo();
        final String connectedSsid = wifiInfo == null ? null : wifiInfo.getSSID();
        Log.d(TAG, String.format("onSuccess, connected to: %s, state: %s, error: %s",
                connectedSsid, wifiInfo==null?"NULL":wifiInfo.getSupplicantState(), supplicantError));

        if (wifiInfo != null
                && wifiInfo.getSupplicantState() != SupplicantState.COMPLETED){
            Log.d(TAG, "Not really connected.");
            tryingConnection();
            return;
        }

        // Check if connected SSID matches.
        if (wifiInfo != null
                && wifiInfo.getSupplicantState() == SupplicantState.COMPLETED
                && !StringUtils.isEmpty(connectedSsid)
                && !StringUtils.isEmpty(network.SSID)
                && !("0x").equals(connectedSsid)
                && !network.SSID.equals(connectedSsid)
                && !("\""+network.SSID+"\"").equals(connectedSsid))
        {
            Log.d(TAG, String.format("Connected SSID does not match target, connected: %s target: %s", connectedSsid, network.SSID));
            disconnectCurrent();
            return;
        }

        reenableAllHotspots();
        mNotificationManager.notify(
                UNIQUE_ID,
                NotificationUtils.getSimple(
                        this,
                        getString(R.string.app_name),
                        getString(R.string.not_correct_key_testing,
                                keys.get(attempts))).build());
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

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    private class LocalBinder extends Binder {
        AutoConnectService getService() {
            return AutoConnectService.this;
        }
    }
}
