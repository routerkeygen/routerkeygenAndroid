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

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class NetworksListFragment extends SherlockFragment implements
		OnScanListener, MessagePublisher {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private static final String NETWORKS_FOUND = "network_found";

	private int mActivatedPosition = ListView.INVALID_POSITION;
	private ListView listview;
	private WifiListAdapter wifiListAdapter;
	private View noNetworksMessage;

	private ScanResult[] networksFound;

	public NetworksListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		RelativeLayout root = (RelativeLayout) inflater.inflate(
				R.layout.fragment_networks_list, container, false);
		listview = (ListView) root.findViewById(R.id.networks_list);
		wifiListAdapter = new WifiListAdapter(getActivity());
		listview.setAdapter(wifiListAdapter);
		noNetworksMessage = root.findViewById(R.id.message_group);
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(NETWORKS_FOUND)) {
				Parcelable[] storedNetworksFound = savedInstanceState
						.getParcelableArray(NETWORKS_FOUND);
				networksFound = new ScanResult[storedNetworksFound.length];
				for (int i = 0; i < storedNetworksFound.length; ++i)
					networksFound[i] = (ScanResult) storedNetworksFound[i];
				onScanFinished(networksFound);
			}
			if (savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
				setActivatedPosition(savedInstanceState
						.getInt(STATE_ACTIVATED_POSITION));
			}
		}
		registerForContextMenu(listview);
		return root;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (networksFound != null)
			outState.putParcelableArray(NETWORKS_FOUND, networksFound);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	public void setActivateOnItemClick(boolean activateOnItemClick) {
		listview.setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
				: ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			listview.setItemChecked(mActivatedPosition, false);
		} else {
			listview.setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	public void setMessage(int message) {
		noNetworksMessage.findViewById(R.id.loading_spinner).setVisibility(
				View.GONE);
		listview.setVisibility(View.GONE);
		TextView messageView = (TextView) noNetworksMessage
				.findViewById(R.id.message);
		messageView.setVisibility(View.VISIBLE);
		messageView.setText(message);
		noNetworksMessage.setVisibility(View.VISIBLE);
	}

	public void onScanFinished(ScanResult[] networks) {
		networksFound = networks;
		if (getActivity() == null)
			return;
		if (networks.length > 0) {
			noNetworksMessage.setVisibility(View.GONE);
			wifiListAdapter.updateNetworks(networks);
			listview.setVisibility(View.VISIBLE);
		} else {
			noNetworksMessage.findViewById(R.id.loading_spinner).setVisibility(
					View.GONE);
			listview.setVisibility(View.GONE);
			listview.setVisibility(View.GONE);
			TextView messageView = (TextView) noNetworksMessage
					.findViewById(R.id.message);
			messageView.setVisibility(View.VISIBLE);
			messageView.setText(R.string.msg_nowifidetected);
			noNetworksMessage.setVisibility(View.VISIBLE);
		}
	}


}
