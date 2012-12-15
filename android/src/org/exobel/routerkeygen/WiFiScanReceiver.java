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

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.exobel.routerkeygen.algorithms.Keygen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class WiFiScanReceiver extends BroadcastReceiver {
	final private OnScanListener[] scanListeners;
	final private WirelessMatcher matcher;
	final private WifiManager wifi;

	public interface OnScanListener {

		public void onScanFinished(Keygen[] networks);
	}

	public WiFiScanReceiver(WirelessMatcher matcher, WifiManager wifi,
			OnScanListener... scanListener) {
		super();
		this.scanListeners = scanListener;
		this.matcher = matcher;
		this.wifi = wifi;
	}

	public void onReceive(Context c, Intent intent) {

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
		final Set<Keygen> set = new TreeSet<Keygen>();
		for (int i = 0; i < results.size() - 1; ++i)
			for (int j = i + 1; j < results.size(); ++j)
				if (results.get(i).SSID.equals(results.get(j).SSID))
					results.remove(j--);
		try {
			for (ScanResult result : results)
				set.add(matcher.getKeygen(result));

		} catch (LinkageError e) {
			Toast.makeText(c, R.string.err_misbuilt_apk, Toast.LENGTH_SHORT)
					.show();
		}
		final Keygen[] networks = new Keygen[set.size()];
		final Iterator<Keygen> it = set.iterator();
		int i = 0;
		while (it.hasNext())
			networks[i++] = it.next();
		for (OnScanListener scanListener : scanListeners)
			scanListener.onScanFinished(networks);
		try {
			c.unregisterReceiver(this);
		} catch (Exception e) {
		}

	}

}
