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
import java.util.Locale;

import org.exobel.routerkeygen.R;

import android.os.Parcel;
import android.os.Parcelable;

public class InterCableKeygen extends Keygen {

	public InterCableKeygen(String ssid, String mac) {
		super(ssid, mac);
	}

	@Override
	public List<String> getKeys() {
		if (getMacAddress().length() != 12) {
			setErrorCode(R.string.msg_nomac);
			return null;
		}
		String wep = "m" + getMacAddress().substring(0, 10).toLowerCase(Locale.getDefault());
		String hex = getMacAddress().substring(10, 12);
		int intValue = Integer.parseInt(hex, 16);
		intValue += 1; // we add 1 and then convert again to hex
		hex = Integer.toHexString(intValue).toLowerCase(Locale.getDefault());
		addPassword(wep+hex);
		//There's a version where 2 is added so we repeat again
		intValue += 1; // we add 1 and then convert again to hex
		hex = Integer.toHexString(intValue).toLowerCase(Locale.getDefault());
		addPassword(wep+hex);
		return getResults();
	}

	private InterCableKeygen(Parcel in) {
		super(in);
	}

	public static final Parcelable.Creator<InterCableKeygen> CREATOR = new Parcelable.Creator<InterCableKeygen>() {
		public InterCableKeygen createFromParcel(Parcel in) {
			return new InterCableKeygen(in);
		}

		public InterCableKeygen[] newArray(int size) {
			return new InterCableKeygen[size];
		}
	};

}
