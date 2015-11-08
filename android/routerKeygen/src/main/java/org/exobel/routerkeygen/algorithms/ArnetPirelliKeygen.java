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
import android.os.Parcelable;

import org.exobel.routerkeygen.R;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ArnetPirelliKeygen extends Keygen {
    public static final Parcelable.Creator<ArnetPirelliKeygen> CREATOR = new Parcelable.Creator<ArnetPirelliKeygen>() {
        public ArnetPirelliKeygen createFromParcel(Parcel in) {
            return new ArnetPirelliKeygen(in);
        }

        public ArnetPirelliKeygen[] newArray(int size) {
            return new ArnetPirelliKeygen[size];
        }
    };
    private final static String LOOKUP = "0123456789abcdefghijklmnopqrstuvwxyz";
    MessageDigest md;

    public ArnetPirelliKeygen(String ssid, String mac) {
        super(ssid, mac);
    }

    ArnetPirelliKeygen(Parcel in) {
        super(in);
    }

    @Override
    public int getSupportState() {
        if (getSsidName().startsWith("WiFi-Arnet-"))
            return SUPPORTED;
        return UNLIKELY_SUPPORTED;
    }

    void generateKey(String mac, int length) {
        byte[] macBytes = new byte[6];
        for (int i = 0; i < 12; i += 2) {
            macBytes[i / 2] = (byte) ((Character.digit(mac.charAt(i), 16) << 4) + Character
                    .digit(mac.charAt(i + 1), 16));
        }
        md.reset();
        md.update(AliceItalyKeygen.ALICE_SEED);
        try {
            md.update("1236790".getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }
        md.update(macBytes);
        final byte[] hash = md.digest();
        final StringBuilder key = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            key.append(LOOKUP.charAt((hash[i] & 0xFF) % LOOKUP.length()));
        }
        addPassword(key.toString());
    }

    @Override
    public List<String> getKeys() {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            setErrorCode(R.string.msg_nosha256);
            return null;
        }
        if (getMacAddress().length() != 12) {
            setErrorCode(R.string.msg_nomac);
            return null;
        }
        generateKey(incrementMac(getMacAddress(), 1), 10);
        return getResults();
    }

}
