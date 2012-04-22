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

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.StringUtils;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;

public class EircomKeygen extends KeygenThread  {

	public EircomKeygen(Handler h, Resources res) {
		super(h, res);
	}

	public void run(){
		String mac=  getRouter().getMacEnd();
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e1) {
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_nosha1)));
			return;
		}
		byte [] routerMAC = new byte[4];
		routerMAC[0] = 1;
		for (int i = 0; i < 6; i += 2)
			routerMAC[i / 2 + 1] = (byte) ((Character.digit(mac.charAt(i), 16) << 4)
					+ Character.digit(mac.charAt(i + 1), 16));
		int macDec = ( (0xFF & routerMAC[0]) << 24 ) | ( (0xFF & routerMAC[1])  << 16 ) |
					 ( (0xFF & routerMAC[2])  << 8 ) | (0xFF & routerMAC[3]);
		mac = StringUtils.dectoString(macDec) + "Although your world wonders me, ";
		md.reset();
		md.update(mac.getBytes());
		byte [] hash = md.digest();
		try {
			pwList.add(StringUtils.getHexString(hash).substring(0,26));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		handler.sendEmptyMessage(RESULTS_READY);
		return;
	}
	
}
