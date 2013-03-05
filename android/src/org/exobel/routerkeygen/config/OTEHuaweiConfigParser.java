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
package org.exobel.routerkeygen.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OTEHuaweiConfigParser {

	public static String[] parse(InputStream in) {
		final String[] supportedOTE = new String[61440];
		final BufferedReader bufferedInput = new BufferedReader(
				new InputStreamReader(in));
		int j = 0;
		try {
			String line;
			while ((line = bufferedInput.readLine()) != null)
				supportedOTE[j++] = line;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (j != supportedOTE.length)
			throw new RuntimeException();
		return supportedOTE;
	}

}
