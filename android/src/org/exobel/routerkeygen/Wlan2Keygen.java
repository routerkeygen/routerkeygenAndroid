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

/**
 * <b>This only calculates the keys for some WLAN_xx</b>
 * <br> 
 * 
 * Many WLAN_XX don't use this algorithm.
 * Code by Kampanita
 */

public class Wlan2Keygen extends KeygenThread {

	public Wlan2Keygen(Handler h, Resources res) {
		super(h, res);
		// TODO Auto-generated constructor stub
	}

	public void run() {
		char[] key = new char[26];

		if (router == null)
			return;
		String mac = router.getMac();

		if (mac.length() != 12) {
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_errpirelli)));
			return;
		}

		
		key[0] = mac.charAt(10);
		key[1] = mac.charAt(11);
		key[2] = mac.charAt(0);
		key[3] = mac.charAt(1);
		key[4] = mac.charAt(8);
		key[5] = mac.charAt(9);
		key[6] = mac.charAt(2);
		key[7] = mac.charAt(3);
		key[8] = mac.charAt(4);
		key[9] = mac.charAt(5);
		key[10] = mac.charAt(6);
		key[11] = mac.charAt(7);
		key[12] = mac.charAt(10);
		key[13] = mac.charAt(11);
		key[14] = mac.charAt(8);
		key[15] = mac.charAt(9);
		key[16] = mac.charAt(2);
		key[17] = mac.charAt(3);
		key[18] = mac.charAt(4);
		key[19] = mac.charAt(5);
		key[20] = mac.charAt(6);
		key[21] = mac.charAt(7);
		key[22] = mac.charAt(0);
		key[23] = mac.charAt(1);
		key[24] = mac.charAt(4);
		key[25] = mac.charAt(5);

		int max = 9;
		String begin = router.ssidSubpart.substring(0,1);
		int primer_n = Integer.parseInt(begin, 16);  
		if (primer_n > max) {
		   String cadena = String.valueOf(key, 0, 2);  	
		   int value = Integer.parseInt(cadena,16);
		   value=value-1;
		   String cadena2 = Integer.toHexString(value);
		   if ( cadena2.length() < 2 )
			   cadena2 = "0" + cadena2;
		   key[0]=cadena2.charAt(0);
		   key[1]=cadena2.charAt(1);
		}
	
		pwList.add(String.valueOf(key, 0, 26));
		handler.sendEmptyMessage(RESULTS_READY);
		return;
	}
}
