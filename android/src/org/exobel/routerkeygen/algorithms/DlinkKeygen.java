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

import org.exobel.routerkeygen.R;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * Link:http://fodi.me/codigo-fonte-wpa-dlink-php-c/
 * @author Rui Araújo
 *
 */
public class DlinkKeygen extends Keygen {

	public DlinkKeygen(String ssid, String mac) {
		super(ssid, mac);
	}

	final static char hash[] =  { 'X', 'r', 'q', 'a', 'H', 'N',
			 			'p', 'd', 'S', 'Y', 'w', 
			 			'8', '6', '2', '1', '5'};

	@Override
	public List<String> getKeys() {
		if ( getMacAddress().equals("") ) 
		{
			setErrorCode(R.string.msg_nomac);
			return null;
		}
		final char[] key = new char[20];
		final String mac = getMacAddress();
		key[0]=mac.charAt(11);
		key[1]=mac.charAt(0);
		 
		key[2]=mac.charAt(10);
		key[3]=mac.charAt(1);
		
		key[4]=mac.charAt(9);
		key[5]=mac.charAt(2);
		 
		key[6]=mac.charAt(8);
		key[7]=mac.charAt(3);
		
		key[8]=mac.charAt(7);
		key[9]=mac.charAt(4);
		
		key[10]=mac.charAt(6);
		key[11]=mac.charAt(5);
		
		key[12]=mac.charAt(1);
		key[13]=mac.charAt(6);
		
		key[14]=mac.charAt(8);
		key[15]=mac.charAt(9);
		
		key[16]=mac.charAt(11);
		key[17]=mac.charAt(2);
		
		key[18]=mac.charAt(4);
		key[19]=mac.charAt(10);
		char [] newkey = new char[20];
		char t;
		int index = 0;
		for (int i=0; i < 20 ; i++)
		{
			t=key[i];
			if ((t >= '0') && (t <= '9'))
				index = t-'0';
			else
			{
				t=Character.toUpperCase(t);
				if ((t >= 'A') && (t <= 'F'))
					index = t-'A'+10;
				else
				{
					setErrorCode(R.string.msg_dlinkerror);
					return null;
				}
			}
			newkey[i]=hash[index];
		}
		addPassword(String.valueOf(newkey, 0, 20));
		return getResults();
	}
	

	private DlinkKeygen(Parcel in) {
		super(in);
	}
	
    public static final Parcelable.Creator<DlinkKeygen> CREATOR = new Parcelable.Creator<DlinkKeygen>() {
        public DlinkKeygen createFromParcel(Parcel in) {
            return new DlinkKeygen(in);
        }

        public DlinkKeygen[] newArray(int size) {
            return new DlinkKeygen[size];
        }
    };

}
