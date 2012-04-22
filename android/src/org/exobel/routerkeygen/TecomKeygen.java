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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;

/*
 * This is the algorithm to generate the WPA passphrase 
 * for the Hitachi (TECOM) AH-4021 and Hitachi (TECOM) AH-4222.
 * The key is the 26 first characters from the SSID SHA1 hash.
 *  Link : http://rafale.org/~mattoufoutu/ebooks/Rafale-Mag/Rafale12/Rafale12.08.HTML
 */
public class TecomKeygen extends KeygenThread {

	public TecomKeygen(Handler h, Resources res) {
		super(h, res);
	}
	
	
	public void run(){
		if ( router == null)
			return;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e1) {
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_nosha1)));
			return;
		}
		md.reset();
		md.update(router.ssid.getBytes());
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
