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

import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiListAdapter extends BaseAdapter {
	private ScanResult[] listNetworks;

	final private LayoutInflater inflater;
	private final Drawable[] wifiSignal;
	private final Drawable[] wifiSignalLocked;
	private final Typeface typeface;

	public WifiListAdapter(Context context) {
		this.listNetworks = new ScanResult[0];
		typeface = Typeface.createFromAsset(context.getAssets(),
				"fonts/Roboto-Light.ttf");
		final Resources resources = context.getResources();
		inflater = LayoutInflater.from(context);
		wifiSignal = new Drawable[4];
		wifiSignalLocked = new Drawable[4];
		for (int i = 0; i < 4; ++i) {
			switch (i) {
			case 0:
				wifiSignal[i] = resources
						.getDrawable(R.drawable.ic_wifi_signal_1);
				wifiSignalLocked[i] = resources
						.getDrawable(R.drawable.ic_wifi_lock_signal_1);
				break;
			case 1:
				wifiSignal[i] = resources
						.getDrawable(R.drawable.ic_wifi_signal_2);
				wifiSignalLocked[i] = resources
						.getDrawable(R.drawable.ic_wifi_lock_signal_2);
				break;
			case 2:
				wifiSignal[i] = resources
						.getDrawable(R.drawable.ic_wifi_signal_3);
				wifiSignalLocked[i] = resources
						.getDrawable(R.drawable.ic_wifi_lock_signal_3);
				break;
			case 3:
				wifiSignal[i] = resources
						.getDrawable(R.drawable.ic_wifi_signal_4);
				wifiSignalLocked[i] = resources
						.getDrawable(R.drawable.ic_wifi_lock_signal_4);
				break;
			}
		}
	}

	public int getCount() {
		return listNetworks.length;
	}

	public ScanResult getItem(int position) {
		return listNetworks[position];
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ScanResult wifi = getItem(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list_wifi, parent,
					false);
			convertView.setTag(new ViewHolder((TextView) convertView
					.findViewById(R.id.wifiName), (TextView) convertView
					.findViewById(R.id.wifiMAC), (ImageView) convertView
					.findViewById(R.id.strenght)));

		}

		final ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.ssid.setText(wifi.SSID);
		holder.ssid.setTypeface(typeface);
		holder.ssid.setSelected(true);
		holder.ssid.setEllipsize(TruncateAt.MARQUEE);
		holder.mac.setText(wifi.BSSID.toUpperCase(Locale.getDefault()));
		holder.mac.setTypeface(typeface);
		holder.mac.setSelected(true);
		holder.mac.setEllipsize(TruncateAt.MARQUEE);
		final int strenght = WifiManager.calculateSignalLevel(wifi.level, 4);
		if (isLocked(wifi)) {
			holder.networkStrenght.setImageDrawable(wifiSignalLocked[strenght]);
		} else {
			holder.networkStrenght.setImageDrawable(wifiSignal[strenght]);
		}

		return convertView;
	}

	private static class ViewHolder {
		final private TextView ssid;
		final private TextView mac;
		final private ImageView networkStrenght;

		public ViewHolder(TextView ssid, TextView mac, ImageView networkStrenght) {
			this.ssid = ssid;
			this.mac = mac;
			this.networkStrenght = networkStrenght;
		}
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	public void updateNetworks(ScanResult[] list) {
		if (list != null) {
			listNetworks = list;
			notifyDataSetChanged();
		}
	}

	// Constants used for different security types
	public static final String PSK = "PSK";
	public static final String WEP = "WEP";
	public static final String EAP = "EAP";
	public static final String OPEN = "Open";

	public static boolean isLocked(ScanResult scan) {
		return !OPEN.equals(getScanResultSecurity(scan));
	}

	/**
	 * @return The security of a given {@link ScanResult}.
	 */
	public static String getScanResultSecurity(ScanResult scanResult) {
		final String cap = scanResult.capabilities;
		final String[] securityModes = { WEP, PSK, EAP };
		for (int i = securityModes.length - 1; i >= 0; i--) {
			if (cap.contains(securityModes[i])) {
				return securityModes[i];
			}
		}

		return OPEN;
	}
}
