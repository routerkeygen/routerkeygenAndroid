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

/*
 * This is not actual an algorithm
 * as the key is calculated from the MAC 
 * address adding a '2' as the first character
 */
public class InfostradaKeygen extends Keygen {

	public InfostradaKeygen(String ssid, String mac, int level, String enc ) {
		super(ssid, mac, level, enc);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public List<String> getKeys() {
		if ( getMacAddress().length() != 12 ) 
		{
			setErrorCode(R.string.msg_errpirelli);
			return null;
		}
		addPassword("2"+getMacAddress().toUpperCase());
		return getResults();
	}

}
