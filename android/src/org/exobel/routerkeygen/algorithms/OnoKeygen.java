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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.utils.StringUtils;

import android.os.Parcel;
import android.os.Parcelable;
/*
 * The algorithm for the type of network
 * whose SSID must be in the form of [pP]1XXXXXX0000X
 * where X means a digit.
 * Algorithm:
 * Adding +1 to the last digit and use the resulting 
 * string as the passphrase for WEP key generation.
 * Use the first of the 64 bit keys and the 128 bit one
 * as possible keys.
 * Credit:
 *  pulido from http://foro.elhacker.net
 *  http://foro.elhacker.net/hacking_wireless/desencriptando_wep_por_defecto_de_las_redes_ono_wifi_instantaneamente-t160928.0.html
 * */
public class OnoKeygen extends Keygen {

	private MessageDigest md;

	public OnoKeygen(String ssid, String mac ) {
		super(ssid, mac);
	}

	@Override
	public List<String> getKeys() {
		if ( getSsidName().length() != 13 ) 
		{
			setErrorCode(R.string.msg_shortessid13);
			return null;
		}
		String val = getSsidName().substring(0,11)+ 
					Integer.toString(Integer.parseInt(getSsidName().substring(11))+1);
		if ( val.length() < 13 )
			val = getSsidName().substring(0,11)+ "0" + getSsidName().substring(11);
		int [] pseed = new int[4];
		pseed[0] = 0;
		pseed[1] = 0;
		pseed[2] = 0;
		pseed[3] = 0;
		int randNumber = 0;
		String key = "";
		for (int i = 0; i < val.length(); i++)
		{
			pseed[i%4] ^= (int) val.charAt(i);
		}
		randNumber = pseed[0] | (pseed[1] << 8) | (pseed[2] << 16) | (pseed[3] << 24);
		short tmp = 0;
		for (int j = 0; j < 5; j++)
		{
			randNumber = (randNumber * 0x343fd + 0x269ec3) & 0xffffffff;
			tmp = (short) ((randNumber >> 16) & 0xff);
			key += StringUtils.getHexString(tmp).toUpperCase(Locale.getDefault());
		}
		addPassword(key);
		key = "";
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			setErrorCode(R.string.msg_nomd5);
			return null; 
		}
		md.reset();
		md.update(padto64(val).getBytes());
		byte [] hash = md.digest();
		for ( int i = 0 ; i < 13 ; ++i )
			key += StringUtils.getHexString((short)hash[i]);
		addPassword(key.toUpperCase(Locale.getDefault()));
		return getResults();
	}
	
	
	private String padto64( String val ){
		if ( val.equals("") )
			return ""; 
		String ret = "";
		for ( int i = 0; i < ( 1 + (64 / (val.length())) ) ; ++i)
			ret += val;
		return ret.substring(0,64);
	}
	

	private OnoKeygen(Parcel in) {
		super(in);
	}
	
    public static final Parcelable.Creator<OnoKeygen> CREATOR = new Parcelable.Creator<OnoKeygen>() {
        public OnoKeygen createFromParcel(Parcel in) {
            return new OnoKeygen(in);
        }

        public OnoKeygen[] newArray(int size) {
            return new OnoKeygen[size];
        }
    };

}
