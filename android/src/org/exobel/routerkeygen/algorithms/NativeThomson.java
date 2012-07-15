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

public class NativeThomson extends Keygen{


	final private String ssidIdentifier;
	public NativeThomson(String ssid, String mac, int level, String enc ) {
		super(ssid, mac, level, enc);
		ssidIdentifier = ssid.substring(ssid.length()-6);
	}

	public NativeThomson( Keygen keygen) {
		super(keygen.getSsidName(), keygen.getMacAddress(), keygen.getLevel(), keygen.getEncryption());
		ssidIdentifier = keygen.getSsidName().substring(keygen.getSsidName().length()-6);
	}


	static {
		System.loadLibrary("thomson");
    }
	
    		  
  /** 
   * Native processing without a dictionary.
   */
	public native String[] thomson( byte [] essid );

	@Override
	public List<String> getKeys() {
		if ( ssidIdentifier.length() != 6 ) 
		{
			setErrorCode(R.string.msg_shortessid6);
			return null;
		}
		byte [] routerESSID = new byte[3];

		for (int i = 0; i < 6; i += 2)
			routerESSID[i / 2] = (byte) ((Character.digit(ssidIdentifier.charAt(i), 16) << 4)
					+ Character.digit(ssidIdentifier.charAt(i + 1), 16));
		String [] results;
		try{
			results = this.thomson(routerESSID);
		}catch (Exception e) {
			setErrorCode(R.string.msg_err_native);
			return null;
		}
		if ( isStopRequested() )
			return null;
		for (int i = 0 ; i < results.length ; ++i  )
			addPassword(results[i]);
		
		 if(getResults().size() == 0)
			setErrorCode(R.string.msg_errnomatches);
		return getResults();
	}


}
