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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class Keygen implements Parcelable {

	public static final int SUPPORTED = 2;
	public static final int UNLIKELY_SUPPORTED = 1;
	public static final int UNSUPPORTED = 0;
	final private String ssidName;
	final private String macAddress;
	private List<String> pwList;

	private boolean stopRequested = false;
	private int errorCode;

	public Keygen(final String ssid, final String mac) {
		this.ssidName = ssid;
		this.macAddress = mac.replace(":", "").toUpperCase(Locale.getDefault());
		this.pwList = new ArrayList<String>();
	}

	public synchronized boolean isStopRequested() {
		return stopRequested;
	}

	public synchronized void setStopRequested(boolean stopRequested) {
		this.stopRequested = stopRequested;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public String getSsidName() {
		return ssidName;
	}

	protected void addPassword(final String key) {
		if (!pwList.contains(key))
			pwList.add(key);
	}

	protected List<String> getResults() {
		return pwList;
	}

	abstract public List<String> getKeys();

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getSupportState() {
		return SUPPORTED;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(ssidName);
		dest.writeInt(macAddress != null ? 1 : 0);
		if (macAddress != null)
			dest.writeString(macAddress);
		dest.writeInt(errorCode);
		dest.writeInt(stopRequested ? 1 : 0);
		dest.writeStringList(pwList);
	}

	protected Keygen(Parcel in) {
		ssidName = in.readString();
		if (in.readInt() == 1)
			macAddress = in.readString();
		else
			macAddress = "";
		errorCode = in.readInt();
		stopRequested = in.readInt() == 1;
		pwList = in.createStringArrayList();
	}

	public static final Parcelable.Creator<Keygen> CREATOR = new Parcelable.Creator<Keygen>() {

		public Keygen[] newArray(int size) {
			return new Keygen[size];
		}

		@Override
		public Keygen createFromParcel(Parcel source) {
			return null;
		}
	};

}
