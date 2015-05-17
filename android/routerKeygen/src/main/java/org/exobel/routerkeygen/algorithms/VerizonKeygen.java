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

import org.exobel.routerkeygen.R;

import android.os.Parcel;
import android.os.Parcelable;

public class VerizonKeygen extends Keygen {


	public VerizonKeygen(String ssid, String mac ) {
		super(ssid, mac);
	}

	@Override
	public List<String> getKeys() {
		if ( getSsidName().length() != 5 )
		{
			setErrorCode(R.string.msg_shortessid5);
			return null;
		}
		char [] inverse = new char[5];
		inverse[0] = getSsidName().charAt(4);
		inverse[1] = getSsidName().charAt(3);
        inverse[2] = getSsidName().charAt(2);
		inverse[3] = getSsidName().charAt(1);
		inverse[4] = getSsidName().charAt(0);
		
		int result = 0;
		try{
			result = Integer.valueOf(String.copyValueOf(inverse), 36);
		}catch(NumberFormatException e){
			setErrorCode(R.string.msg_err_verizon_ssid);
			return null;
		}
		
		String ssidKey = Integer.toHexString(result).toUpperCase(Locale.getDefault());
		while ( ssidKey.length() < 6 )
			ssidKey = "0" + ssidKey;
	    if ( !getMacAddress().equals(""))
	    {
	    	addPassword(getMacAddress().substring(3,5) + getMacAddress().substring(6,8) + 
	    					ssidKey);
	    }
	    else	
	    {
	    	addPassword("1801" + ssidKey);
	    	addPassword("1F90" + ssidKey);
	    }
		return getResults();
	}


	private VerizonKeygen(Parcel in) {
		super(in);
	}
	
    public static final Parcelable.Creator<VerizonKeygen> CREATOR = new Parcelable.Creator<VerizonKeygen>() {
        public VerizonKeygen createFromParcel(Parcel in) {
            return new VerizonKeygen(in);
        }

        public VerizonKeygen[] newArray(int size) {
            return new VerizonKeygen[size];
        }
    };
}
