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
package org.exobel.routerkeygen.config;

import android.os.Parcel;
import android.os.Parcelable;

public class TeleTuMagicInfo implements Parcelable {
	private final int[] range;
	private final int base;
	private final String serial;
	private final int divider;

	public TeleTuMagicInfo(int[] range, String serial, int base, int divider) {
		this.serial = serial;
		this.range = range;
		this.base = base;
		this.divider = divider;
	}

	public int[] getRange() {
		return range;
	}

	public int getBase() {
		return base;
	}

	public int getDivider() {
		return divider;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(base);
		dest.writeInt(divider);
		dest.writeIntArray(range);
		dest.writeString(serial);
	}

	private TeleTuMagicInfo(Parcel in) {
		this.base = in.readInt();
		this.divider = in.readInt();
		this.range = in.createIntArray();
		this.serial = in.readString();
	}

	public static final Parcelable.Creator<TeleTuMagicInfo> CREATOR = new Parcelable.Creator<TeleTuMagicInfo>() {
		public TeleTuMagicInfo createFromParcel(Parcel in) {
			return new TeleTuMagicInfo(in);
		}

		public TeleTuMagicInfo[] newArray(int size) {
			return new TeleTuMagicInfo[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public String getSerial() {
		return serial;
	}

}
