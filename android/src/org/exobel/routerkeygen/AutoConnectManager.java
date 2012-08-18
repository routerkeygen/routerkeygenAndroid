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
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiManager;
import android.util.Log;

public class AutoConnectManager extends BroadcastReceiver {
	final WifiManager wifi;
	final onConnectionListener listener;

	public AutoConnectManager(WifiManager wifi, onConnectionListener listener) {
		this.listener = listener;
		this.wifi = wifi;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		NetworkInfo info = intent
				.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		if (info != null) {
			DetailedState state = info.getDetailedState();
			Log.d(this.getClass().getSimpleName(), state.name());
			if (state.equals(DetailedState.CONNECTED)) {
				listener.onSuccessfulConection();
				return;
			}
			if (state.equals(DetailedState.CONNECTING)
					|| state.equals(DetailedState.AUTHENTICATING)
					|| state.equals(DetailedState.OBTAINING_IPADDR)) {
				return; /* Waiting for a definitive outcome */
			}
			if (state.equals(DetailedState.DISCONNECTED)
					|| state.equals(DetailedState.FAILED)) {
				listener.onFailedConnection();
				return; /* Failed */
			}
		}

	}

	public interface onConnectionListener {
		public void onFailedConnection();

		public void onSuccessfulConection();
	}

}
