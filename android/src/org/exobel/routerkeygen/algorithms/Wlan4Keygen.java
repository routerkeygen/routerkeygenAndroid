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
import org.exobel.routerkeygen.StringUtils;

public class Wlan4Keygen extends Keygen {

	final private String ssidIdentifier;
	private MessageDigest md;
	public Wlan4Keygen(String ssid, String mac, int level, String enc ) {
		super(ssid, mac, level, enc);
		ssidIdentifier = ssid.substring(ssid.length()-4);
	}
	static final String magic = "bcgbghgg";
	
	@Override
	public List<String> getKeys() {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			setErrorCode(R.string.msg_nomd5);
			return null;
		}
		if ( getMacAddress().length() != 12 ) 
		{
			setErrorCode(R.string.msg_errpirelli);
			return null;
		}
		String macMod = getMacAddress().substring(0,8) + ssidIdentifier;
		md.reset();
		try {
			if ( !getMacAddress().toUpperCase().startsWith("001FA4") )
				md.update(magic.getBytes("ASCII"));
			if ( !getMacAddress().toUpperCase().startsWith("001FA4") )
				md.update(macMod.toUpperCase().getBytes("ASCII"));
			else
				md.update(macMod.toLowerCase().getBytes("ASCII"));
			if ( !getMacAddress().toUpperCase().startsWith("001FA4") )
				md.update( getMacAddress().toUpperCase().getBytes("ASCII"));
			byte [] hash = md.digest();
			if  ( !getMacAddress().toUpperCase().startsWith("001FA4") )
				addPassword(StringUtils.getHexString(hash).substring(0,20));
			else
				addPassword(StringUtils.getHexString(hash).substring(0,20).toUpperCase());
			return getResults();
		} catch (UnsupportedEncodingException e) {}
		return null;
	}

}
