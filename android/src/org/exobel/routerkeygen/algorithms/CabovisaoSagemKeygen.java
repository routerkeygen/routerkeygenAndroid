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
 * it is just the base string followed by a character in the range a-e and then
 * the last 4 characters of the SSID.
 * Source: http://jb.ptsec.info/cabovisao/
 */
public class CabovisaoSagemKeygen extends Keygen {
	private final static String KEY_BASE = "2ce412e";
	final private String ssidIdentifier;
	public CabovisaoSagemKeygen(String ssid, String mac ) {
		super(ssid, mac);
		this.ssidIdentifier = ssid.substring(ssid.length()-4).toLowerCase(Locale.getDefault());
	}
	
	@Override
	public List<String> getKeys() {
		addPassword(KEY_BASE + 'a' + ssidIdentifier);
		addPassword(KEY_BASE + 'b' + ssidIdentifier);
		addPassword(KEY_BASE + 'c' + ssidIdentifier);
		addPassword(KEY_BASE + 'd' + ssidIdentifier);
		return getResults();
	}

	private CabovisaoSagemKeygen(Parcel in) {
		super(in);
		ssidIdentifier = in.readString();
	}


	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(ssidIdentifier);
	}
    public static final Parcelable.Creator<CabovisaoSagemKeygen> CREATOR = new Parcelable.Creator<CabovisaoSagemKeygen>() {
        public CabovisaoSagemKeygen createFromParcel(Parcel in) {
            return new CabovisaoSagemKeygen(in);
        }

        public CabovisaoSagemKeygen[] newArray(int size) {
            return new CabovisaoSagemKeygen[size];
        }
    };
	

}
