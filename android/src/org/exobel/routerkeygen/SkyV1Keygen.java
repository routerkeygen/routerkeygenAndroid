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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;


/*
 * This is the algorithm to generate the WPA passphrase 
 * for the SKYv1.
 * Generate the md5 hash form the mac.
 * Use the numbers in the following positions on the hash.
 *  Position 3,7,11,15,19,23,27,31 ,
 *  Use theses numbers, modulus 26, to find the correct letter
 *  and append to the key.
 */
public class SkyV1Keygen extends KeygenThread{

	public SkyV1Keygen(Handler h, Resources res) {
		super(h, res);
	}

	final static String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public void run(){
		if ( router == null)
			return;
		if ( router.getMac().length() != 12 ) 
		{
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_nomac)));
			return;
		}
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_nomd5)));
			return;
		}
		md.reset();
		md.update(router.getMac().getBytes());
		byte [] hash = md.digest();
		String key ="";
		for ( int i = 1 ; i <= 15 ; i += 2 )
		{
			int index = hash[i] & 0xFF;
			index %= 26;
			key += ALPHABET.substring(index,index+1 );
		}

		pwList.add(key);
		handler.sendEmptyMessage(RESULTS_READY);
		return;
	}
}
