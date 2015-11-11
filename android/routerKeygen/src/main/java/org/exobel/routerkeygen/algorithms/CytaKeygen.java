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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * This is not actual an algorithm as
 * it is just the base string followed by a character in the range a-e and then
 * the last 4 characters of the SSID.
 * Source: http://jb.ptsec.info/cabovisao/
 */
public class CytaKeygen extends Keygen {
    public static final Creator<CytaKeygen> CREATOR = new Creator<CytaKeygen>() {
        public CytaKeygen createFromParcel(Parcel in) {
            return new CytaKeygen(in);
        }

        public CytaKeygen[] newArray(int size) {
            return new CytaKeygen[size];
        }
    };
    private final ArrayList<CytaMagicInfo> supportedCyta;

    public CytaKeygen(String ssid, String mac,
                      ArrayList<CytaMagicInfo> supportedCyta) {
        super(ssid, mac);
        this.supportedCyta = supportedCyta;
    }

    private CytaKeygen(Parcel in) {
        super(in);
        supportedCyta = in
                .readArrayList(CytaMagicInfo.class.getClassLoader());
    }


    @Override
    public int getSupportState() {
        if (getSsidName().startsWith("CYTA") || getSsidName().matches("Discus--?[0-9a-fA-F]{6}"))
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
        final Long macDec = Long.parseLong(mac.substring(6), 16);
        for (CytaMagicInfo info : supportedCyta) {
            long diff = macDec - info.getBase();
            if ((diff >= 0) && (diff <= (9999999)) && (diff % info.getDivisor() == 0)) {
                final long key = diff / info.getDivisor();
                String result = Long.toString(key);
                while (result.length() < 7) {
                    result = "0" + result;
                }
                addPassword(info.getKey() + result);
            }
        }
        return getResults();
    }


    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(supportedCyta);
    }
}
