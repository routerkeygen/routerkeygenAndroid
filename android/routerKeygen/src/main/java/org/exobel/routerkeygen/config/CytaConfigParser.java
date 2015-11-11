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

public class CytaConfigParser {

    public static Map<String, ArrayList<CytaMagicInfo>> parse(InputStream in) {
        Map<String, ArrayList<CytaMagicInfo>> supportedAlices = new HashMap<>();
        final BufferedReader bufferedInput = new BufferedReader(
                new InputStreamReader(in));
        try {
            String line;
            while ((line = bufferedInput.readLine()) != null) {
                final String[] infos = line.split(" ");
                final String mac = infos[0];
                ArrayList<CytaMagicInfo> supported = supportedAlices.get(mac);
                if (supported == null) {
                    supported = new ArrayList<>();
                    supportedAlices.put(mac, supported);
                }
                final String key = infos[1];
                final long base = Long.parseLong(infos[2], 16);
                final long divider = Long.parseLong(infos[3], 16);
                supported.add(new CytaMagicInfo(divider, base, key, mac));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return supportedAlices;
    }

}
