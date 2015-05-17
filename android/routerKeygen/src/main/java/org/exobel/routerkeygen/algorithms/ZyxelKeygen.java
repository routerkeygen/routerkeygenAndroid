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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.utils.StringUtils;

import android.os.Parcel;
import android.os.Parcelable;

public class ZyxelKeygen extends Keygen {

	final private String ssidIdentifier;
	private MessageDigest md;

	public ZyxelKeygen(String ssid, String mac) {
		super(ssid, mac);
		ssidIdentifier = ssid.substring(ssid.length() - 4);
	}

	@Override
	public List<String> getKeys() {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			setErrorCode(R.string.msg_nomd5);
			return null;
		}
		final String mac = getMacAddress();
		if (mac.length() != 12) {
			setErrorCode(R.string.msg_errpirelli);
			return null;
		}
		try {

			final String macMod = mac.substring(0, 8) + ssidIdentifier;
			md.reset();
			md.update(macMod.toLowerCase(Locale.getDefault()).getBytes("ASCII"));

			byte[] hash = md.digest();
			addPassword(StringUtils.getHexString(hash).substring(0, 20)
					.toUpperCase(Locale.getDefault()));
			return getResults();
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	private ZyxelKeygen(Parcel in) {
		super(in);
		ssidIdentifier = in.readString();
	}

	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(ssidIdentifier);
	}

	public static final Parcelable.Creator<ZyxelKeygen> CREATOR = new Parcelable.Creator<ZyxelKeygen>() {
		public ZyxelKeygen createFromParcel(Parcel in) {
			return new ZyxelKeygen(in);
		}

		public ZyxelKeygen[] newArray(int size) {
			return new ZyxelKeygen[size];
		}
	};

}
