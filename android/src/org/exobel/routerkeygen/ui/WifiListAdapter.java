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

import java.util.ArrayList;
import java.util.List;

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.algorithms.Keygen;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiListAdapter extends BaseAdapter {
	private List<Keygen> listNetworks;

	final private Resources resources;
	final private LayoutInflater inflater;

	public WifiListAdapter(List<Keygen> list, Context context) {
		if (list != null)
			this.listNetworks = list;
		else
			this.listNetworks = new ArrayList<Keygen>();
		resources = context.getResources();
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return listNetworks.size();
	}

	public Object getItem(int position) {
		return listNetworks.get(position);
	}

	public long getItemId(int position) {
		return listNetworks.get(position).hashCode();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final Keygen wifi = listNetworks.get(position);
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
		holder.mac.setText(wifi.getDisplayMacAddress());
		if (wifi.isSupported())
			holder.supported.setImageDrawable(resources.getDrawable(R.drawable.ic_possible));
		else
			holder.supported.setImageDrawable(resources
					.getDrawable(R.drawable.ic_impossible));
		final int strenght = wifi.getLevel();
		if (wifi.isLocked()) {
			switch (strenght) {
			case 0:
				holder.networkStrenght.setImageDrawable(resources
						.getDrawable(R.drawable.ic_wifi_lock_signal_1));
				break;
			case 1:
				holder.networkStrenght.setImageDrawable(resources
						.getDrawable(R.drawable.ic_wifi_lock_signal_2));
				break;
			case 2:
				holder.networkStrenght.setImageDrawable(resources
						.getDrawable(R.drawable.ic_wifi_lock_signal_3));
				break;
			case 3:
				holder.networkStrenght.setImageDrawable(resources
						.getDrawable(R.drawable.ic_wifi_lock_signal_4));
				break;
			}
		} else {
			switch (strenght) {
			case 0:
				holder.networkStrenght.setImageDrawable(resources
						.getDrawable(R.drawable.ic_wifi_signal_1));
				break;
			case 1:
				holder.networkStrenght.setImageDrawable(resources
						.getDrawable(R.drawable.ic_wifi_signal_2));
				break;
			case 2:
				holder.networkStrenght.setImageDrawable(resources
						.getDrawable(R.drawable.ic_wifi_signal_3));
				break;
			case 3:
				holder.networkStrenght.setImageDrawable(resources
						.getDrawable(R.drawable.ic_wifi_signal_4));
				break;
			}
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
