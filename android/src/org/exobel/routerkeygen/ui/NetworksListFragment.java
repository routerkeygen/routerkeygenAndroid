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

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.WiFiScanReceiver.OnScanListener;
import org.exobel.routerkeygen.algorithms.Keygen;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

@SuppressWarnings("deprecation")
public class NetworksListFragment extends SherlockListFragment implements
		OnScanListener {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private static final String NETWORKS_FOUND = "network_found";

	private OnItemSelectionListener mCallbacks = sDummyCallbacks;
	private int mActivatedPosition = ListView.INVALID_POSITION;

	private Keygen[] networksFound;

	public interface OnItemSelectionListener {

		public void onItemSelected(Keygen id);

		public void onItemSelected(String mac);
	}

	private static OnItemSelectionListener sDummyCallbacks = new OnItemSelectionListener() {
		public void onItemSelected(Keygen id) {
		}

		public void onItemSelected(String mac) {
		}
	};

	public NetworksListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(NETWORKS_FOUND)) {
				Parcelable[] storedNetworksFound = savedInstanceState
						.getParcelableArray(NETWORKS_FOUND);
				networksFound = new Keygen[storedNetworksFound.length];
				for (int i = 0; i < storedNetworksFound.length; ++i)
					networksFound[i] = (Keygen) storedNetworksFound[i];
				setListAdapter(new WifiListAdapter(networksFound, getActivity()));
			}
			if (savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
				setActivatedPosition(savedInstanceState
						.getInt(STATE_ACTIVATED_POSITION));
			}
		}
		registerForContextMenu(getListView());
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof OnItemSelectionListener)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (OnItemSelectionListener) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		mCallbacks.onItemSelected(networksFound[position]);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArray(NETWORKS_FOUND, networksFound);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	public void setActivateOnItemClick(boolean activateOnItemClick) {
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.networks_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.copy_ssid: {
			ClipboardManager clipboard = (ClipboardManager) getActivity()
					.getSystemService(Context.CLIPBOARD_SERVICE);
			final String ssid = networksFound[(int) info.id].getSsidName();
			clipboard.setText(ssid);
			Toast.makeText(getActivity(), getString(R.string.msg_copied, ssid),
					Toast.LENGTH_SHORT).show();
			return true;
		}
		case R.id.copy_mac: {
			ClipboardManager clipboard = (ClipboardManager) getActivity()
					.getSystemService(Context.CLIPBOARD_SERVICE);
			final String mac = networksFound[(int) info.id]
					.getDisplayMacAddress();
			clipboard.setText(mac);
			Toast.makeText(getActivity(), getString(R.string.msg_copied, mac),
					Toast.LENGTH_SHORT).show();
			return true;
		}
		case R.id.use_mac:
			mCallbacks.onItemSelected(networksFound[(int) info.id]
					.getMacAddress());
			return true;
		}
		return super.onContextItemSelected(item);
	}

	public void onScanFinished(Keygen[] networks) {
		networksFound = networks;
		if (getActivity() != null)
			setListAdapter(new WifiListAdapter(networksFound, getActivity()));
	}

}
