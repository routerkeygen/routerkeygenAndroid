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

import org.doublecheck.wifiscanner.WifiScanReceiver.OnScanListener;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class NetworksListActivity extends SherlockFragmentActivity implements
		OnScanListener {
	private NetworksListFragment networkListFragment;
	private WifiManager wifi;
	private BroadcastReceiver scanFinished;
	private BroadcastReceiver stateChanged;
	private static final String donateScreenShownPref = "donateScreenShown";
	private boolean welcomeScreenShown;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_networks_list);

		networkListFragment = ((NetworksListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.frag_networks_list));
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		wifiState = wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED
				|| wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLING;
		scanFinished = new WifiScanReceiver(wifi, networkListFragment, this);
		stateChanged = new WifiStateReceiver(wifi, networkListFragment);

		final SharedPreferences mPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		welcomeScreenShown = mPrefs.getBoolean(donateScreenShownPref, false);

		if (!welcomeScreenShown) {

			final String whatsNewTitle = getString(R.string.app_name);
			final String whatsNewText = getString(R.string.msg_welcome_text_donate);
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(whatsNewTitle)
					.setMessage(whatsNewText)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show();
			final SharedPreferences.Editor editor = mPrefs.edit();
			editor.putBoolean(donateScreenShownPref, true);
			editor.commit();
		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		mOptionsMenu = menu;
		getSupportMenuInflater().inflate(R.menu.networks_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.wifi_scan:
			scan();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (wifiOn) {
			try {
				if (!wifi.setWifiEnabled(true))
					networkListFragment.setMessage(R.string.msg_wifibroken);
				else
					wifiState = true;
			} catch (SecurityException e) {
				// Workaround for
				// http://code.google.com/p/android/issues/detail?id=22036
				networkListFragment.setMessage(R.string.msg_wifibroken);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		scan();
	}

	@Override
	public void onStop() {
		super.onStop();
		try {
			unregisterReceiver(scanFinished);
			unregisterReceiver(stateChanged);
		} catch (Exception e) {
		}
	}

	private Menu mOptionsMenu;
	private View mRefreshIndeterminateProgressView = null;

	public void setRefreshActionItemState(boolean refreshing) {
		// On Honeycomb, we can set the state of the refresh button by giving it
		// a custom
		// action view.
		if (mOptionsMenu == null) {
			return;
		}

		final MenuItem refreshItem = mOptionsMenu.findItem(R.id.wifi_scan);
		if (refreshItem != null) {
			if (refreshing) {
				if (mRefreshIndeterminateProgressView == null) {
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					mRefreshIndeterminateProgressView = inflater.inflate(
							R.layout.actionbar_indeterminate_progress, null);
				}

				refreshItem.setActionView(mRefreshIndeterminateProgressView);
			} else {
				refreshItem.setActionView(null);
			}
		}
	}

	public void scan() {
		if (!wifiState && !wifiOn) {
			networkListFragment.setMessage(R.string.msg_nowifi);
			return;
		}
		registerReceiver(scanFinished, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
			registerReceiver(stateChanged, new IntentFilter(
					WifiManager.WIFI_STATE_CHANGED_ACTION));
			Toast.makeText(this, R.string.msg_wifienabling, Toast.LENGTH_SHORT)
					.show();
		} else {
			if (wifi.startScan()) {
				setRefreshActionItemState(true);
			} else
				networkListFragment.setMessage(R.string.msg_scanfailed);
		}
	}

	private boolean wifiState;
	private boolean wifiOn;

	public void onScanFinished(ScanResult[] networks) {
		setRefreshActionItemState(false);
	}

}
