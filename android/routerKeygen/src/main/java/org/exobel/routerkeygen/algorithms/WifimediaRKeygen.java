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

/**
 * Simple algorithm for "wifimedia_R-XXXX" seen here:
 * "http://foro.seguridadwireless.net/aplicaciones/(r-wlanxdecrypter-0-9)-generador-de-diccionarios-para-claves-por-defecto-de-r/msg264486/#msg264486"
 * 
 * @author Rui Araujo
 * 
 */
public class WifimediaRKeygen extends Keygen {

	public WifimediaRKeygen(String ssid, String mac) {
		super(ssid, mac);
	}

	@Override
	public List<String> getKeys() {
		final String mac = getMacAddress();
		if (mac.length() != 12) {
			setErrorCode(R.string.msg_errpirelli);
			return null;
		}
		final String possibleKey = mac.substring(0, 11).toLowerCase(
				Locale.getDefault())
				+ "0";
		addPassword(possibleKey);
		addPassword(possibleKey.toUpperCase(Locale.getDefault()));
		return getResults();
	}

	private WifimediaRKeygen(Parcel in) {
		super(in);
	}

	public static final Parcelable.Creator<WifimediaRKeygen> CREATOR = new Parcelable.Creator<WifimediaRKeygen>() {
		public WifimediaRKeygen createFromParcel(Parcel in) {
			return new WifimediaRKeygen(in);
		}

		public WifimediaRKeygen[] newArray(int size) {
			return new WifimediaRKeygen[size];
		}
	};

}
