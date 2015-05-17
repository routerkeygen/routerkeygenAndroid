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
package org.doublecheck.wifiscanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class WifiStateReceiver extends BroadcastReceiver {

	private final WifiManager wifi;
	private final MessagePublisher messagePublisher;

	public WifiStateReceiver(WifiManager wifi, MessagePublisher messagePublisher) {
		this.wifi = wifi;
		this.messagePublisher = messagePublisher;
	}

	@Override
	public void onReceive(Context context, Intent arg1) {
		if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			wifi.startScan();
			try {
				context.unregisterReceiver(this);
			} catch (Exception e) {
			}

			messagePublisher.setMessage(R.string.msg_scanstarted);
			return;
		}
		if (wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
			messagePublisher.setMessage(R.string.msg_nowifi);
			return;
		}
	}
}
