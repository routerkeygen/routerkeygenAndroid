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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class AutoConnectManager extends BroadcastReceiver {
	private static final String TAG = "AutoConnectManager";
	private final onConnectionListener listener;

	public AutoConnectManager(onConnectionListener listener) {
		this.listener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();

		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)){
			final NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			final String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);
			final WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
			Log.d(TAG, String.format("NetworkChanged; NetworkInfo: %s, bssid: %s, wifiInfo: %s, supplicantState: %s",
					networkInfo, bssid, wifiInfo, wifiInfo==null?"NULL":wifiInfo.getSupplicantState()));

			listener.onNetworkChanged(networkInfo, bssid, wifiInfo);
			return;
		}

		if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
			final SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
			int supplicantError = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
			if (state == null){
				Log.w(TAG, "null state, error: " + supplicantError);
				return;
			}

			if (BuildConfig.DEBUG)
				Log.d(this.getClass().getSimpleName(), state.name());
			if (state.equals(SupplicantState.COMPLETED)) {
				listener.onSuccessfulConection(supplicantError);
				return;
			}
			if (state.equals(SupplicantState.FOUR_WAY_HANDSHAKE)) {
				listener.onFourWayHandshake(supplicantError);
				return;
			}
			if (state.equals(SupplicantState.DISCONNECTED)) {
				listener.onFailedConnection(supplicantError);
				return;
			}
		}
	}

	public interface onConnectionListener {
		void onFailedConnection(int supplicantError);

		void onSuccessfulConection(int supplicantError);

		void onFourWayHandshake(int supplicantError);

		void onNetworkChanged(NetworkInfo networkInfo, String bssid, WifiInfo wifiInfo);
	}

}
