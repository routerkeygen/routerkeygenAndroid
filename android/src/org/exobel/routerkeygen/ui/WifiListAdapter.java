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
import org.exobel.routerkeygen.algorithms.Keygen;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiListAdapter extends BaseAdapter {
	private Keygen[] listNetworks;

	final private LayoutInflater inflater;
	private final Drawable[] supported;
	private final Drawable[] wifiSignal;
	private final Drawable[] wifiSignalLocked;

	public WifiListAdapter(Keygen[] list, Context context) {
		if (list != null)
			this.listNetworks = list;
		else
			this.listNetworks = new Keygen[0];
		final Resources resources = context.getResources();
		inflater = LayoutInflater.from(context);
		supported = new Drawable[3];
		supported[0] = resources.getDrawable(R.drawable.ic_possible);
		supported[1] = resources.getDrawable(R.drawable.ic_maybe);
		supported[2] = resources.getDrawable(R.drawable.ic_impossible);
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

	public Object getItem(int position) {
		return listNetworks[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final Keygen wifi = listNetworks[position];
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list_wifi, parent,
					false);
			convertView.setTag(new ViewHolder((TextView) convertView
					.findViewById(R.id.wifiName), (TextView) convertView
					.findViewById(R.id.wifiMAC), (ImageView) convertView
					.findViewById(R.id.icon), (ImageView) convertView
					.findViewById(R.id.strenght)));
		}

		final ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.ssid.setText(wifi.getSsidName());
		holder.ssid.setSelected(true);
		holder.ssid.setEllipsize(TruncateAt.MARQUEE);
		holder.mac.setText(wifi.getDisplayMacAddress());
		holder.mac.setSelected(true);
		holder.mac.setEllipsize(TruncateAt.MARQUEE);
		switch(wifi.getSupportState()){
		case Keygen.SUPPORTED:
			holder.supported.setImageDrawable(supported[0]);
			break;
		case Keygen.MAYBE_SUP:
			holder.supported.setImageDrawable(supported[1]);
			break;
		case Keygen.UNSUPPORTED:
			holder.supported.setImageDrawable(supported[2]);
			break;
		}
		final int strenght = wifi.getLevel();
		if (wifi.isLocked()) {
			holder.networkStrenght.setImageDrawable(wifiSignalLocked[strenght]);
		} else {
			holder.networkStrenght.setImageDrawable(wifiSignal[strenght]);
		}
		return convertView;
	}

	private static class ViewHolder {
		final private TextView ssid;
		final private TextView mac;
		final private ImageView supported;
		final private ImageView networkStrenght;

		public ViewHolder(TextView ssid, TextView mac, ImageView supported,
				ImageView networkStrenght) {
			this.ssid = ssid;
			this.mac = mac;
			this.supported = supported;
			this.networkStrenght = networkStrenght;
		}

	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

}
