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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.utils.StringUtils;

import android.os.Parcel;
import android.os.Parcelable;


public class PirelliKeygen extends Keygen{
	
	private MessageDigest md;
	final private String ssidIdentifier;

	public PirelliKeygen(String ssid, String mac ) {
		super(ssid, mac);
		ssidIdentifier = ssid.substring(ssid.length()-12);
	}

	final static byte[] saltMD5 = {
			0x22, 0x33, 0x11, 0x34, 0x02,
		    (byte) 0x81, (byte) 0xFA, 0x22, 0x11, 0x41,
			0x68, 0x11,	0x12, 0x01, 0x05,
			0x22, 0x71, 0x42, 0x10, 0x66 };
	

	@Override
	public List<String> getKeys() {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			setErrorCode(R.string.msg_nomd5);
			return null;
		}
		if ( ssidIdentifier.length() != 12 ) 
		{
			setErrorCode(R.string.msg_errpirelli);
			return null;
		}
		
		byte [] routerESSID = new byte[6];
		for (int i = 0; i < 12; i += 2)
			routerESSID[i / 2] = (byte) ((Character.digit(ssidIdentifier.charAt(i), 16) << 4)
					+ Character.digit(ssidIdentifier.charAt(i + 1), 16));

		md.reset();
		md.update(routerESSID);
		md.update(saltMD5);
		byte [] hash = md.digest();
		short [] key = new short[5];
		/*Grouping in five groups of five bits*/
		key[0] = (short)( (hash[0] & 0xF8) >> 3 );
		key[1] = (short)(( (hash[0] & 0x07) << 2) | ( (hash[1] & 0xC0) >>6 ));
		key[2] = (short)((hash[1] & 0x3E) >> 1 );
		key[3] = (short)(( (hash[1] & 0x01) << 4) |  ((hash[2] & 0xF0) >> 4));
		key[4] = (short)(( (hash[2] & 0x0F) << 1) |  ((hash[3] & 0x80) >> 7) );
		for ( int i = 0 ; i < 5 ; ++i )
			if ( key[i] >= 0x0A )
				key[i] += 0x57;
		try {
			addPassword(StringUtils.getHexString(key));
		} catch (UnsupportedEncodingException e) {}
		return  getResults();
	}

	private PirelliKeygen(Parcel in) {
		super(in);
		ssidIdentifier = in.readString();
	}

	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(ssidIdentifier);
	}
	
    public static final Parcelable.Creator<PirelliKeygen> CREATOR = new Parcelable.Creator<PirelliKeygen>() {
        public PirelliKeygen createFromParcel(Parcel in) {
            return new PirelliKeygen(in);
        }

        public PirelliKeygen[] newArray(int size) {
            return new PirelliKeygen[size];
        }
    };


}
