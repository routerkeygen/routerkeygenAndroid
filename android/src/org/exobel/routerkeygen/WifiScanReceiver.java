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
import java.util.zip.ZipInputStream;

import org.exobel.routerkeygen.algorithms.Keygen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.widget.Toast;

public class WifiScanReceiver extends BroadcastReceiver {
	final private OnScanListener[] scanListeners;
	final private WifiManager wifi;
	private KeygenMatcherTask task;

	public interface OnScanListener {

		public void onScanFinished(Keygen[] networks);
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
		if (task == null || task.getStatus() == Status.FINISHED) {
			task = new KeygenMatcherTask(results, context);
			task.execute();
		}
	}

	private class KeygenMatcherTask extends AsyncTask<Void, Void, Keygen[]> {
		private final List<ScanResult> results;
		private final Context context;
		private boolean misbuiltAPK = false;

		public KeygenMatcherTask(List<ScanResult> results, Context context) {
			this.results = results;
			this.context = context;
		}

		@Override
		protected void onPostExecute(Keygen[] networks) {
			if (misbuiltAPK)
				Toast.makeText(context, R.string.err_misbuilt_apk,
						Toast.LENGTH_SHORT).show();
			for (OnScanListener scanListener : scanListeners)
				scanListener.onScanFinished(networks);
		}

		@Override
		protected Keygen[] doInBackground(Void... params) {

			final Set<Keygen> set = new TreeSet<Keygen>();
			for (int i = 0; i < results.size() - 1; ++i)
				for (int j = i + 1; j < results.size(); ++j)
					if (results.get(i).SSID.equals(results.get(j).SSID))
						results.remove(j--);
			for (ScanResult result : results) {
				try {
					set.add(WirelessMatcher.getKeygen(result,
							new ZipInputStream(context.getResources()
									.openRawResource(R.raw.magic_info))));
				} catch (LinkageError e) {
					misbuiltAPK = true;
				}
			}

			final Keygen[] networks = new Keygen[set.size()];
			final Iterator<Keygen> it = set.iterator();
			int i = 0;
			while (it.hasNext())
				networks[i++] = it.next();
			return networks;
		}

	}
}
