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

public class ComtrendKeygen extends Keygen {

	private MessageDigest md;

	public ComtrendKeygen(String ssid, String mac) {
		super(ssid, mac);
	}

	static final String magic = "bcgbghgg";
	static final String lowermagic = "64680C";
	static final String highermagic = "3872C0";
	static final String mac001a2b = "001A2B";

	@Override
	public List<String> getKeys() {
		final String ssidIdentifier = getSsidName().substring(
				getSsidName().length() - 4);
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
			if (mac.substring(0, 6).equalsIgnoreCase(mac001a2b)) {
				for (int i = 0; i < 512; i++) {
					md.reset();
					md.update(magic.getBytes("ASCII"));
					String xx;
					if (i < 256) {
						md.update(lowermagic.getBytes("ASCII"));
						xx = Integer.toHexString(i).toUpperCase(Locale.US);
					} else {
						md.update(highermagic.getBytes("ASCII"));
						xx = Integer.toHexString(i - 256).toUpperCase(Locale.US);
					}
					while (xx.length() < 2)
						xx = "0" + xx;
					md.update(xx.getBytes("ASCII"));
					md.update(ssidIdentifier.getBytes("ASCII"));
					md.update(mac.getBytes("ASCII"));
					byte[] hash = md.digest();
					addPassword(StringUtils.getHexString(hash).substring(0, 20));
				}
			} else {
				final String macMod = mac.substring(0, 8) + ssidIdentifier;
				md.reset();
				md.update(magic.getBytes("ASCII"));
				md.update(macMod.toUpperCase(Locale.getDefault()).getBytes(
						"ASCII"));
				md.update(mac.toUpperCase(Locale.getDefault())
						.getBytes("ASCII"));
				byte[] hash = md.digest();
				addPassword(StringUtils.getHexString(hash).substring(0, 20));

			}
			return getResults();
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	private ComtrendKeygen(Parcel in) {
		super(in);
	}

	public static final Parcelable.Creator<ComtrendKeygen> CREATOR = new Parcelable.Creator<ComtrendKeygen>() {
		public ComtrendKeygen createFromParcel(Parcel in) {
			return new ComtrendKeygen(in);
		}

		public ComtrendKeygen[] newArray(int size) {
			return new ComtrendKeygen[size];
		}
	};

}
