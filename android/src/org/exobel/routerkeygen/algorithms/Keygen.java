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

import android.os.Parcel;
import android.os.Parcelable;

public abstract class Keygen implements Comparable<Keygen>, Parcelable {

	final public static String OPEN_ENCRIPTION = "Open";
	
	private String ssidName;
	private String macAddress;
	private int level;
	private String encryption;
	private boolean stopRequested = false;
	private int errorCode;
	private List<String> pwList;

	public Keygen(final String ssid, final String mac, int level, String enc) {
		this.ssidName = ssid;
		this.macAddress = mac;
		this.level = level;
		this.encryption = enc;
		this.pwList = new ArrayList<String>();
	}

	public List<String> getResults() {
		return pwList;
	}

	public synchronized boolean isStopRequested() {
		return stopRequested;
	}

	public synchronized void setStopRequested(boolean stopRequested) {
		this.stopRequested = stopRequested;
	}

	protected void addPassword(final String key) {
		if (!pwList.contains(key))
			pwList.add(key);
	}

	public String getMacAddress() {
		return macAddress.replace(":", "");
	}

	public String getDisplayMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getSsidName() {
		return ssidName;
	}

	public void setSsidName(String ssidName) {
		this.ssidName = ssidName;
	}

	abstract public List<String> getKeys();

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public boolean isSupported() {
		return true;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getEncryption() {
		return encryption;
	}

	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}

	public int compareTo(Keygen another) {
		if (isSupported() && another.isSupported()) {
			if (another.level == this.level)
				return ssidName.compareTo(another.ssidName);
			else
				return another.level - level;
		} else if (isSupported())
			return -1;
		else
			return 1;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(ssidName);
		dest.writeInt(macAddress != null ? 1 : 0);
		if (macAddress != null)
			dest.writeString(macAddress);
		dest.writeInt(encryption != null ? 1 : 0);
		if (encryption != null)
			dest.writeString(encryption);
		dest.writeInt(level);
		dest.writeInt(errorCode);
		dest.writeInt(stopRequested ? 1 : 0);
		dest.writeStringList(pwList);
	}

	protected Keygen(Parcel in) {
		ssidName = in.readString();
		if (in.readInt() == 1)
			macAddress = in.readString();
		if (in.readInt() == 1)
			encryption = in.readString();
		level = in.readInt();
		errorCode = in.readInt();
		stopRequested = in.readInt() == 1;
		pwList = in.createStringArrayList();

	}
	
	public boolean isLocked(){
		return !OPEN_ENCRIPTION.equals(encryption);
	}

}
