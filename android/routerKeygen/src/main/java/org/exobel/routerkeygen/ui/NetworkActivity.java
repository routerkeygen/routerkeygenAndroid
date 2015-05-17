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

import org.exobel.routerkeygen.AdsUtils;
import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.RefreshHandler;
import org.exobel.routerkeygen.algorithms.WiFiNetwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.millennialmedia.android.MMAdView;

public class NetworkActivity extends SherlockFragmentActivity {

	private RefreshHandler adRefreshHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_fragment);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		MMAdView ad = AdsUtils.loadAdIfNeeded(this);
		if (ad != null) {
			adRefreshHandler = new RefreshHandler(ad);
		}
		if (savedInstanceState == null) {
			final Bundle arguments = new Bundle();
			final WiFiNetwork wiFiNetwork = getIntent().getParcelableExtra(NetworkFragment.NETWORK_ID);
			arguments.putParcelable(NetworkFragment.NETWORK_ID, wiFiNetwork);
			setTitle(wiFiNetwork.getSsidName());
			NetworkFragment fragment = new NetworkFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.keygen_fragment, fragment).commit();
		}
	}


	@Override
	public void onStart() {
		super.onStart();
		//Get an Analytics tracker to report app starts and uncaught exceptions etc.
		GoogleAnalytics.getInstance(this).reportActivityStart(this);

	}

	@Override
	public void onStop() {
		super.onStop();
		//Stop the analytics tracking
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adRefreshHandler != null)
			adRefreshHandler.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (adRefreshHandler != null)
			adRefreshHandler.onPause();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this,
					NetworksListActivity.class));
			return true;
		case R.id.pref:
			startActivity(new Intent(this, Preferences.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.preferences, menu);
		return true;
	}
}
