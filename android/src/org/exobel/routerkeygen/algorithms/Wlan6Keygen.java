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

public class Wlan6Keygen extends Keygen {
	
	final private String ssidIdentifier;
	public Wlan6Keygen(String ssid, String mac ) {
		super(ssid, mac);
		ssidIdentifier = ssid.substring(ssid.length()-6);
	}

	@Override
	public List<String> getKeys() {
		String macStr = getMacAddress();
		if ( macStr.length() != 12 ) 
		{
			setErrorCode(R.string.msg_errpirelli);
			return null;
		}
		char [] ssidSubPart = {'1', '2','3', '4', '5','6' };/*These values are not revelant.*/
		char [] bssidLastByte = { '6', '6' };
		ssidSubPart[0] = ssidIdentifier.charAt(0);
		ssidSubPart[1] = ssidIdentifier.charAt(1);
		ssidSubPart[2] = ssidIdentifier.charAt(2);
		ssidSubPart[3] = ssidIdentifier.charAt(3);
		ssidSubPart[4] = ssidIdentifier.charAt(4);
		ssidSubPart[5] = ssidIdentifier.charAt(5);
		bssidLastByte[0] = macStr.charAt(10);
		bssidLastByte[1] = macStr.charAt(11);
		for ( int  k = 0; k < 6 ; ++k ) 
		    if( ssidSubPart[k] >= 'A')
		        ssidSubPart[k] = (char)(ssidSubPart[k] - 55);

	    if(bssidLastByte[0] >= 'A' )
	        bssidLastByte[0] = (char)(bssidLastByte[0] - 55);
	    if(bssidLastByte[1] >= 'A' )
	        bssidLastByte[1] = (char)(bssidLastByte[1] - 55);
	    
		for ( int i = 0; i < 10 ; ++i )
		{
			/*Do not change the order of this instructions*/
			int aux = i + ( ssidSubPart[3] & 0xf ) +  ( bssidLastByte[0] & 0xf ) + ( bssidLastByte[1] & 0xf );
			int aux1 = ( ssidSubPart[1] & 0xf ) + ( ssidSubPart[2] & 0xf ) + ( ssidSubPart[4] & 0xf ) + ( ssidSubPart[5] & 0xf );
			int second = aux ^ ( ssidSubPart[5] & 0xf );
			int sixth = aux ^ ( ssidSubPart[4] & 0xf );
			int tenth = aux ^ ( ssidSubPart[3] & 0xf );
			int third = aux1 ^ ( ssidSubPart[2] & 0xf );
			int seventh = aux1 ^  ( bssidLastByte[0] & 0xf );
			int eleventh = aux1 ^ ( bssidLastByte[1] & 0xf );
			int fourth =  ( bssidLastByte[0] & 0xf ) ^ ( ssidSubPart[5] & 0xf );
			int eighth = ( bssidLastByte[1] & 0xf ) ^ ( ssidSubPart[4] & 0xf );
			int twelfth = aux ^ aux1;
			int fifth = second ^ eighth;
			int ninth = seventh ^ eleventh;
			int thirteenth = third ^ tenth;
			int first = twelfth ^ sixth;
			String key = Integer.toHexString(first & 0xf) + Integer.toHexString(second & 0xf)+
						Integer.toHexString(third & 0xf) + Integer.toHexString(fourth & 0xf) +
						Integer.toHexString(fifth & 0xf) + Integer.toHexString(sixth & 0xf) +
						Integer.toHexString(seventh & 0xf) + Integer.toHexString(eighth & 0xf) +
						Integer.toHexString(ninth & 0xf) + Integer.toHexString(tenth & 0xf) + 
						Integer.toHexString(eleventh & 0xf) + Integer.toHexString(twelfth & 0xf) +
						Integer.toHexString(thirteenth & 0xf);
			
			addPassword(key.toUpperCase(Locale.getDefault()));
		}
		if ( ( ( ssidSubPart[0] != macStr.charAt(7) ) || ( ssidSubPart[1] != macStr.charAt(8) ) ||( ssidSubPart[2] != macStr.charAt(9) ) )
				&& !getSsidName().startsWith("WiFi"))
		{
			setErrorCode(R.string.msg_err_essid_no_match);
		}
		return getResults();
	}
	

	private Wlan6Keygen(Parcel in) {
		super(in);
		ssidIdentifier = in.readString();
	}

	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(ssidIdentifier);
	}
	
    public static final Parcelable.Creator<Wlan6Keygen> CREATOR = new Parcelable.Creator<Wlan6Keygen>() {
        public Wlan6Keygen createFromParcel(Parcel in) {
            return new Wlan6Keygen(in);
        }

        public Wlan6Keygen[] newArray(int size) {
            return new Wlan6Keygen[size];
        }
    };

}
