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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TeleTuConfigParser {

	public static Map<String, ArrayList<TeleTuMagicInfo>> parse(InputStream in) {
		Map<String, ArrayList<TeleTuMagicInfo>> supportedTeleTu = new HashMap<String, ArrayList<TeleTuMagicInfo>>();
		final BufferedReader bufferedInput = new BufferedReader(
				new InputStreamReader(in));
		try {
			String line;
			while ((line = bufferedInput.readLine()) != null) {
				final String[] infos = line.split(" ");
				final String name = infos[0];
				ArrayList<TeleTuMagicInfo> supported = supportedTeleTu
						.get(name);
				if (supported == null) {
					supported = new ArrayList<TeleTuMagicInfo>(5);
					supportedTeleTu.put(name, supported);
				}
				int[] range = new int[2];
				range[0] = Integer.parseInt(infos[1], 16); // from
				range[1] = Integer.parseInt(infos[2], 16); // to
				final String serial = infos[3];
				final int base = Integer.parseInt(infos[4], 16);
				final int divider = Integer.parseInt(infos[5]);
				supported
						.add(new TeleTuMagicInfo(range, serial, base, divider));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return supportedTeleTu;
	}

}
