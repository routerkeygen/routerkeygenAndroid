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

package org.exobel.routerkeygen.ui;

import java.util.List;

import org.exobel.routerkeygen.Preferences;
import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.WiFiScanReceiver;
import org.exobel.routerkeygen.WiFiScanReceiver.OnScanListener;
import org.exobel.routerkeygen.WifiStateReceiver;
import org.exobel.routerkeygen.WirelessMatcher;
import org.exobel.routerkeygen.algorithms.Keygen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
		NetworksListFragment.OnItemSelectionListener, OnScanListener {

	private boolean mTwoPane;

	private WirelessMatcher networkMatcher;
	private WifiManager wifi;
	private BroadcastReceiver scanFinished;
	private BroadcastReceiver stateChanged;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_networks_list);

		final NetworksListFragment fragment = ((NetworksListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.item_list));
		if (findViewById(R.id.item_detail_container) != null) {
			mTwoPane = true;
			fragment.setActivateOnItemClick(true);
		}
		networkMatcher = new WirelessMatcher(getResources().openRawResource(
				R.raw.alice));
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		wifi_state = wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED
				|| wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLING;
		scanFinished = new WiFiScanReceiver(networkMatcher, wifi, fragment, this);
		stateChanged = new WifiStateReceiver(wifi);
	}

	public void onItemSelected(Keygen keygen) {
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putParcelable(NetworkFragment.NETWORK_ID, keygen);
			NetworkFragment fragment = new NetworkFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.item_detail_container, fragment).commit();

		} else {
			if ( !keygen.isSupported() ){
				Toast.makeText(this, R.string.msg_unspported, Toast.LENGTH_SHORT).show();
				return;
			}
			Intent detailIntent = new Intent(this, NetworkActivity.class);
			detailIntent.putExtra(NetworkFragment.NETWORK_ID, keygen);
			startActivity(detailIntent);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		mOptionsMenu = menu;
		getSupportMenuInflater().inflate(R.menu.networks_list, menu);
		getSupportMenuInflater().inflate(R.menu.preferences, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.manual_input:
			ManualDialogFragment.newInstance(manualMac, networkMatcher ).show(getSupportFragmentManager(), "ManualInput");
			return true;
		case R.id.wifi_scan:
			scan();
			return true;
		case R.id.pref:
			startActivity(new Intent(this, Preferences.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onStart() {
		super.onStart();
		getPrefs();
		if (wifiOn) {
			if (!wifi.setWifiEnabled(true))
				Toast.makeText(this, R.string.msg_wifibroken,
						Toast.LENGTH_SHORT).show();
			else
				wifi_state = true;
		}
		scan();
	}
	


    private Menu mOptionsMenu;
    private View mRefreshIndeterminateProgressView = null;
    public void setRefreshActionItemState(boolean refreshing) {
        // On Honeycomb, we can set the state of the refresh button by giving it a custom
        // action view.
        if (mOptionsMenu == null) {
            return;
        }

        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.wifi_scan);
        if (refreshItem != null) {
            if (refreshing) {
                if (mRefreshIndeterminateProgressView == null) {
                    LayoutInflater inflater = (LayoutInflater)
                            getSystemService(
                                    Context.LAYOUT_INFLATER_SERVICE);
                    mRefreshIndeterminateProgressView = inflater.inflate(
                            R.layout.actionbar_indeterminate_progress, null);
                }

                refreshItem.setActionView(mRefreshIndeterminateProgressView);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }

	
	public void onResume(){
		super.onResume();
		getPrefs();
	}

	public void scan() {
		if (!wifi_state && !wifiOn) {
			Toast.makeText(this, R.string.msg_nowifi, Toast.LENGTH_SHORT)
					.show();
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
			if (wifi.startScan()){
				setRefreshActionItemState(true);
			}
			else
				Toast.makeText(this, R.string.msg_scanfailed,
						Toast.LENGTH_SHORT).show();
		}
	}

	private boolean wifi_state;
	private boolean wifiOn;
	private boolean manualMac;

	private void getPrefs() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		wifiOn = prefs.getBoolean(Preferences.wifiOnPref, true);
		manualMac = prefs.getBoolean(Preferences.manualMacPref, false);
	}

	public void onScanFinished(List<Keygen> networks) {
		setRefreshActionItemState(false);
	}

}
