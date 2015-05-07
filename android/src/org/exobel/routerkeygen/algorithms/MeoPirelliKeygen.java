/*
 * Copyright 2015 Rui Araújo
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.exobel.routerkeygen.R;

import android.os.Parcel;
import android.os.Parcelable;

public class MeoPirelliKeygen extends ArnetPirelliKeygen {

	public MeoPirelliKeygen(String ssid, String mac) {
		super(ssid, mac);
	}

	@Override
	public int getSupportState() {
		if (getSsidName().matches("ADSLPT-AB[0-9]{5}"))
			return SUPPORTED;
		return UNLIKELY_SUPPORTED;
	}

	@Override
	public List<String> getKeys() {
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e1) {
			setErrorCode(R.string.msg_nosha256);
			return null;
		}
		if (getMacAddress().length() != 12) {
			setErrorCode(R.string.msg_nomac);
			return null;
		}
		generateKey(incrementMac(getMacAddress(), -1), 8);
		return getResults();
	}

	private MeoPirelliKeygen(Parcel in) {
		super(in);
	}

	public static final Parcelable.Creator<MeoPirelliKeygen> CREATOR = new Parcelable.Creator<MeoPirelliKeygen>() {
		public MeoPirelliKeygen createFromParcel(Parcel in) {
			return new MeoPirelliKeygen(in);
		}

		public MeoPirelliKeygen[] newArray(int size) {
			return new MeoPirelliKeygen[size];
		}
	};

}
