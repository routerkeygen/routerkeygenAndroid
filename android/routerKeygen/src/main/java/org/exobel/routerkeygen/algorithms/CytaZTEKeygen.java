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
import org.exobel.routerkeygen.config.AliceMagicInfo;
import org.exobel.routerkeygen.config.CytaMagicInfo;

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
public class CytaZTEKeygen extends Keygen {
    public static final Creator<CytaZTEKeygen> CREATOR = new Creator<CytaZTEKeygen>() {
        public CytaZTEKeygen createFromParcel(Parcel in) {
            return new CytaZTEKeygen(in);
        }

        public CytaZTEKeygen[] newArray(int size) {
            return new CytaZTEKeygen[size];
        }
    };
    private final Map<String, ArrayList<CytaMagicInfo>> supportedCytaZTEs;

    public CytaZTEKeygen(String ssid, String mac,
                         Map<String, ArrayList<CytaMagicInfo>> supportedCytaZTEs) {
        super(ssid, mac);
        this.supportedCytaZTEs = supportedCytaZTEs;
    }

    private CytaZTEKeygen(Parcel in) {
        super(in);
        supportedCytaZTEs = new HashMap<>();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            ArrayList<CytaMagicInfo> value = in.readArrayList(CytaMagicInfo.class.getClassLoader());
            supportedCytaZTEs.put(key, value);
        }
    }

    @Override
    public int getSupportState() {
        if (getSsidName().startsWith("CYTA"))
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
        for (Map.Entry<String, ArrayList<CytaMagicInfo>> entry : supportedCytaZTEs.entrySet()) {
            for (CytaMagicInfo info : entry.getValue()) {
                long basi = Long.parseLong(entry.getKey(), 16) - info.getBase();
                long diff = macDec - basi;
                if ((diff >= 0) && (diff <= (9999 * info.getDivisor())) && (diff % info.getDivisor() == 0)) {
                    long key = diff / info.getDivisor();
                    String result = Long.toString(key);
                    while (result.length() < 5) {
                        result = "0" + result;
                    }
                    addPassword(info.getKey() + result);
                }
            }
        }
        return getResults();
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(supportedCytaZTEs.size());
        for (Map.Entry<String, ArrayList<CytaMagicInfo>> entry : supportedCytaZTEs.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeList(entry.getValue());
        }
    }
}
