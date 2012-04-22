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
package org.exobel.routerkeygen;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;

public class Wlan6Keygen extends KeygenThread {
	
	public Wlan6Keygen(Handler h, Resources res) {
		super(h, res);
	}

	public void run()
	{
		if ( router == null )
			return;
		if ( router.getMac().equals("") ) 
		{	
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_nomac)));
			return;
		}
		String ssidStr = router.getSSIDsubpart();
		String macStr = router.mac;
		char [] ssidSubPart = {'1', '2','3', '4', '5','6' };/*These values are not revelant.*/
		char [] bssidLastByte = { '6', '6' };
		ssidSubPart[0] = ssidStr.charAt(0);
		ssidSubPart[1] = ssidStr.charAt(1);
		ssidSubPart[2] = ssidStr.charAt(2);
		ssidSubPart[3] = ssidStr.charAt(3);
		ssidSubPart[4] = ssidStr.charAt(4);
		ssidSubPart[5] = ssidStr.charAt(5);
		bssidLastByte[0] = macStr.charAt(15);
		bssidLastByte[1] = macStr.charAt(16);
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
			
			pwList.add(key.toUpperCase());
		}
		handler.sendEmptyMessage(RESULTS_READY);
		if ( ( ( ssidSubPart[0] != macStr.charAt(10) ) || ( ssidSubPart[1] != macStr.charAt(12) ) ||( ssidSubPart[2] != macStr.charAt(13) ) )
				&& !router.ssid.startsWith("WiFi"))
		{
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_err_essid_no_match)));
		}
		return;
	}


}
