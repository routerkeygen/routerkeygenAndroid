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

import android.os.Parcel;
import android.os.Parcelable;

/*
 * This is not actual an algorithm as
 * it is just needed to use the mac address.
 * This only works with some OTEXXXXXX
 */
public class OteKeygen extends Keygen {

	public OteKeygen(String ssid, String mac) {
		super(ssid, mac);
	}

	@Override
	public List<String> getKeys() {
		if (getMacAddress().length() == 12) {
			addPassword(getMacAddress().toLowerCase(Locale.getDefault()));
		} else {
			final String ssidIdentifier = getSsidName().substring(getSsidName().length()-4);
			addPassword("c87b5b" + ssidIdentifier);
			addPassword("fcc897" + ssidIdentifier);
			addPassword("681ab2" + ssidIdentifier);
			addPassword("b075d5" + ssidIdentifier);
			addPassword("384608" + ssidIdentifier);
		}
		return getResults();
	}

	private OteKeygen(Parcel in) {
		super(in);

	}

	public static final Parcelable.Creator<OteKeygen> CREATOR = new Parcelable.Creator<OteKeygen>() {
		public OteKeygen createFromParcel(Parcel in) {
			return new OteKeygen(in);
		}

		public OteKeygen[] newArray(int size) {
			return new OteKeygen[size];
		}
	};

}
