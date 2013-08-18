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

/**
 * The algortihm is described on the pdf below
 * Link:http://www.remote-exploit.org/content/Pirelli_Discus_DRG_A225_WiFi_router.pdf
 * @author Rui Araújo
 *
 */
public class DiscusKeygen extends Keygen {


	public DiscusKeygen(String ssid, String mac) {
		super(ssid, mac);
	}

	static final int essidConst = 0xD0EC31;

	@Override
	public List<String> getKeys() {
		int routerEssid = Integer.parseInt( getSsidName().substring(getSsidName().length()-6) , 16);
		int result  = ( routerEssid - essidConst )>>2;
		addPassword("YW0" + Integer.toString(result));
		return getResults();
	}

	private DiscusKeygen(Parcel in) {
		super(in);
	}
	
    public static final Parcelable.Creator<DiscusKeygen> CREATOR = new Parcelable.Creator<DiscusKeygen>() {
        public DiscusKeygen createFromParcel(Parcel in) {
            return new DiscusKeygen(in);
        }

        public DiscusKeygen[] newArray(int size) {
            return new DiscusKeygen[size];
        }
    };
}
