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

import android.os.Parcel;
import android.os.Parcelable;

/*
 * This is not actual an algorithm as
 * it is just a default WPA password.
 */
public class AndaredKeygen extends Keygen {

	public AndaredKeygen(String ssid, String mac ) {
		super(ssid, mac);
	}
	
	@Override
	public List<String> getKeys() {
		addPassword("6b629f4c299371737494c61b5a101693a2d4e9e1f3e1320f3ebf9ae379cecf32");
		return getResults();
	}

	private AndaredKeygen(Parcel in) {
		super(in);
	}
	
    public static final Parcelable.Creator<AndaredKeygen> CREATOR = new Parcelable.Creator<AndaredKeygen>() {
        public AndaredKeygen createFromParcel(Parcel in) {
            return new AndaredKeygen(in);
        }

        public AndaredKeygen[] newArray(int size) {
            return new AndaredKeygen[size];
        }
    };
	

}
