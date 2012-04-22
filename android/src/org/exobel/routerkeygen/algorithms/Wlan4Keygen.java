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

public class Wlan4Keygen extends KeygenThread {

	public Wlan4Keygen(Handler h, Resources res) {
		super(h, res);
	}
	
	static final String magic = "bcgbghgg";
	public void run(){
		if ( getRouter() == null)
			return;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_nomd5)));
			return;
		}
		if ( getRouter().getMac().length() != 12 ) 
		{
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_errpirelli)));
			return;
		}
		String macMod = getRouter().getMac().substring(0,8) + getRouter().getSSIDsubpart();
		md.reset();
		try {
			if ( !getRouter().getMac().toUpperCase().startsWith("001FA4") )
				md.update(magic.getBytes("ASCII"));
			if ( !getRouter().getMac().toUpperCase().startsWith("001FA4") )
				md.update(macMod.toUpperCase().getBytes("ASCII"));
			else
				md.update(macMod.toLowerCase().getBytes("ASCII"));
			if ( !getRouter().getMac().toUpperCase().startsWith("001FA4") )
				md.update(getRouter().getMac().toUpperCase().getBytes("ASCII"));
			byte [] hash = md.digest();
			if  ( !getRouter().getMac().toUpperCase().startsWith("001FA4") )
				pwList.add(StringUtils.getHexString(hash).substring(0,20));
			else
				pwList.add(StringUtils.getHexString(hash).substring(0,20).toUpperCase());
			handler.sendEmptyMessage(RESULTS_READY);
			return;
		} catch (UnsupportedEncodingException e) {}
		
	}

}
