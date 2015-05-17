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

import org.exobel.routerkeygen.Base64;
import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.utils.StringUtils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The algortihm is described on the link below
 * Link:http://www.wardriving-forum.de/wiki/Standardpassw%C3%B6rter#ALICE
 * 
 * @author Rui Araújo
 * 
 */
public class AliceGermanyKeygen extends Keygen {

	public AliceGermanyKeygen(String ssid, String mac) {
		super(ssid, mac);
	}

	@Override
	public int getSupportState() {
		if (getSsidName().matches("ALICE-WLAN[0-9a-fA-F]{2}"))
			return SUPPORTED;
		return UNLIKELY_SUPPORTED;
	}

	@Override
	public List<String> getKeys() {
		MessageDigest md;
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
			int macEthInt = Integer.parseInt(mac.substring(6), 16) - 1;
			if (macEthInt < 0)
				macEthInt = 0xFFFFFF;
			String macEth = Integer.toHexString(macEthInt);
			while (macEth.length() < 6)
				macEth = "0" + macEth;
			macEth = mac.substring(0, 6) + macEth;
			md.reset();
			md.update(macEth.toLowerCase(Locale.getDefault()).getBytes("ASCII"));
			final byte[] hash = StringUtils.getHexString(md.digest())
					.substring(0, 12).getBytes("ASCII");
			addPassword(Base64.encodeToString(hash, Base64.DEFAULT).trim());
			return getResults();
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	private AliceGermanyKeygen(Parcel in) {
		super(in);
	}

	public static final Parcelable.Creator<AliceGermanyKeygen> CREATOR = new Parcelable.Creator<AliceGermanyKeygen>() {
		public AliceGermanyKeygen createFromParcel(Parcel in) {
			return new AliceGermanyKeygen(in);
		}

		public AliceGermanyKeygen[] newArray(int size) {
			return new AliceGermanyKeygen[size];
		}
	};

}
