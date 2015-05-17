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
package org.exobel.routerkeygen.algorithms;

import java.util.List;

import org.exobel.routerkeygen.R;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The algortihm is described on the link below
 * Link:http://www.wardriving-forum.
 * de/wiki/Standardpassw%C3%B6rter#Diverse_Speedport-Modelle
 * 
 * @author Rui Araújo
 * 
 */
public class Speedport500Keygen extends Keygen {

	public Speedport500Keygen(String ssid, String mac) {
		super(ssid, mac);
	}

	@Override
	public List<String> getKeys() {
		final String mac = getMacAddress();
		if (mac.length() != 12) {
			setErrorCode(R.string.msg_errpirelli);
			return null;
		}
		final String ssid = getSsidName();
		final String block = ssid.charAt(10) + mac.substring(9);
		for (int x = 0; x < 10; ++x)
			for (int y = 0; y < 10; ++y)
				for (int z = 0; z < 10; ++z)
					addPassword("SP-" + ssid.charAt(9) + z + block + x + y + z);
		return getResults();
	}

	private Speedport500Keygen(Parcel in) {
		super(in);
	}

	public static final Parcelable.Creator<Speedport500Keygen> CREATOR = new Parcelable.Creator<Speedport500Keygen>() {
		public Speedport500Keygen createFromParcel(Parcel in) {
			return new Speedport500Keygen(in);
		}

		public Speedport500Keygen[] newArray(int size) {
			return new Speedport500Keygen[size];
		}
	};
}
