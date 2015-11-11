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

import android.os.Parcel;

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.config.CytaMagicInfo;
import org.exobel.routerkeygen.config.NetfasterMagicInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*
 * This is not actual an algorithm as
 * it is just the base string followed by a character in the range a-e and then
 * the last 4 characters of the SSID.
 * Source: http://jb.ptsec.info/cabovisao/
 */
public class NetFasterKeygen extends Keygen {
    public static final Creator<NetFasterKeygen> CREATOR = new Creator<NetFasterKeygen>() {
        public NetFasterKeygen createFromParcel(Parcel in) {
            return new NetFasterKeygen(in);
        }

        public NetFasterKeygen[] newArray(int size) {
            return new NetFasterKeygen[size];
        }
    };
    private final ArrayList<NetfasterMagicInfo> supportedNetfasters;

    public NetFasterKeygen(String ssid, String mac,
                           ArrayList<NetfasterMagicInfo> supportedNetfasters) {
        super(ssid, mac);
        this.supportedNetfasters = supportedNetfasters;
    }

    private NetFasterKeygen(Parcel in) {
        super(in);
        supportedNetfasters = in.readArrayList(NetfasterMagicInfo.class.getClassLoader());
    }

    @Override
    public int getSupportState() {
        if (getSsidName().contains("NetFasteR") || getSsidName().contains("hol"))
            return SUPPORTED;
        return UNLIKELY_SUPPORTED;
    }


    @Override
    public List<String> getKeys() {
        final String mac = getMacAddress();
        if (mac.length() != 12) {
            setErrorCode(R.string.msg_nomac);
            return null;
        }
        final long macDec = Long.parseLong(mac.substring(6), 16);
        for (NetfasterMagicInfo info : supportedNetfasters) {
            for (int div : info.getDivisor()) {
                long basi = Long.parseLong(info.getMac(), 16) - info.getBase() * div;
                long diff = macDec - basi;
                if ((diff >= 0) && (diff <= (9999 * div)) && (diff % div == 0)) {
                    long key = diff / div;
                    String result = Long.toString(key);
                    while (result.length() < 4) {
                        result = "0" + result;
                    }
                    final String password = mac.toUpperCase(Locale.getDefault()) + "-" + result;
                    if (!getResults().contains(password)) {
                        addPassword(password);
                    }
                }
            }
        }
        return getResults();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(supportedNetfasters);
    }
}
