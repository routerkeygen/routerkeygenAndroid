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

public class NetfasterConfigParser {

    public static ArrayList<NetfasterMagicInfo> parse(InputStream in) {
        ArrayList<NetfasterMagicInfo> supportedNetfasters = new ArrayList<>();
        final BufferedReader bufferedInput = new BufferedReader(
                new InputStreamReader(in));
        try {
            String line;
            while ((line = bufferedInput.readLine()) != null) {
                final String[] infos = line.split(" ");
                final String mac = infos[0];
                final int base = Integer.parseInt(infos[1]);
                final int [] divider = (infos[2].equalsIgnoreCase("0")) ? new int[]{4, 5, 9} : new int[]{Integer.parseInt(infos[2])};
                final String key = infos[3];
                supportedNetfasters.add(new NetfasterMagicInfo(divider, base, key, mac));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return supportedNetfasters;
    }

}
