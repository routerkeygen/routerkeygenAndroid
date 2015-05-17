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

import java.util.Iterator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class WifiScanReceiver extends BroadcastReceiver {
	final private OnScanListener[] scanListeners;
	final private WifiManager wifi;

	public interface OnScanListener {

		public void onScanFinished(ScanResult[] networks);
	}

	public WifiScanReceiver(WifiManager wifi, OnScanListener... scanListener) {
		this.scanListeners = scanListener;
		this.wifi = wifi;
	}

	public void onReceive(Context context, Intent intent) {
		if (intent == null
				|| !intent.getAction().equals(
						WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
			return;
		if (scanListeners == null)
			return;
		if (wifi == null)
			return;
		final List<ScanResult> results = wifi.getScanResults();
		/*
		 * He have had reports of this returning null instead of empty
		 */
		if (results == null)
			return;

		try {
			// Single scan
			context.unregisterReceiver(this);
		} catch (Exception e) {
		}

		for (int i = 0; i < results.size() - 1; ++i)
			for (int j = i + 1; j < results.size(); ++j)
				if (results.get(i).SSID.equals(results.get(j).SSID))
					results.remove(j--);
		final ScanResult[] networks = new ScanResult[results.size()];
		final Iterator<ScanResult> it = results.iterator();
		int i = 0;
		while (it.hasNext())
			networks[i++] = it.next();
		for (OnScanListener scanListener : scanListeners)
			scanListener.onScanFinished(networks);
	}

}
