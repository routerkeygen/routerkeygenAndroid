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

import java.util.List;
import java.util.Locale;

/*
 * This is not actual an algorithm as
 * it is just the mac address.
 */
public class HG824xKeygen extends Keygen {

    public static final Creator<HG824xKeygen> CREATOR = new Creator<HG824xKeygen>() {
        public HG824xKeygen createFromParcel(Parcel in) {
            return new HG824xKeygen(in);
        }

        public HG824xKeygen[] newArray(int size) {
            return new HG824xKeygen[size];
        }
    };

    public HG824xKeygen(String ssid, String mac) {
        super(ssid, mac);
    }

    private HG824xKeygen(Parcel in) {
        super(in);
    }

    @Override
    public List<String> getKeys() {
        final String mac = getMacAddress();
        if (mac.length() != 12) {
            setErrorCode(R.string.msg_errpirelli);
            return null;
        }
        final StringBuilder wpaPassword = new StringBuilder();
        wpaPassword.append(mac.substring(6,8));
        final int lastPair = Integer.parseInt(mac.substring(10), 16);
        if ( lastPair <= 8 ) {
            int fifthPair = (Integer.parseInt(mac.substring(8, 10), 16) - 1) & 0xFF;
            wpaPassword.append(Integer.toString(fifthPair, 16));
        } else {
            wpaPassword.append(mac.substring(8,10));
        }
        final int lastChar = Integer.parseInt(mac.substring(11), 16);
        if ( lastChar <= 8 ) {
            final int nextPart = (Integer.parseInt(mac.substring(10,11), 16)-1) & 0xF;
            wpaPassword.append(Integer.toString(nextPart, 16));
        } else {
            wpaPassword.append(mac.substring(10,11));
        }
        switch (lastChar) {
            case 8:
                wpaPassword.append("F");
                break;
            case 9:
                wpaPassword.append("0");
                break;
            case 0xA:
                wpaPassword.append("1");
                break;
            case 0xB:
                wpaPassword.append("2");
                break;
            case 0xC:
                wpaPassword.append("3");
                break;
            case 0xD:
                wpaPassword.append("4");
                break;
            case 0xE:
                wpaPassword.append("5");
                break;
            case 0xF:
                wpaPassword.append("6");
                break;
            case 0:
                wpaPassword.append("7");
                break;
            case 1:
                wpaPassword.append("8");
                break;
            case 2:
                wpaPassword.append("9");
                break;
            case 3:
                wpaPassword.append("A");
                break;
            case 4:
                wpaPassword.append("B");
                break;
            case 5:
                wpaPassword.append("C");
                break;
            case 6:
                wpaPassword.append("D");
                break;
            case 7:
                wpaPassword.append("E");
                break;
            default:
                setErrorCode(R.string.msg_errpirelli);
                return null;
        }
        switch (mac.substring(0,2)) {
            case "28":
                wpaPassword.append("03");
                break;
            case "08":
                wpaPassword.append("05");
                break;
            case "80":
                wpaPassword.append("06");
                break;
            case "E0":
                wpaPassword.append("0C");
                break;
            case "00":
                wpaPassword.append("0D");
                break;
            case "10":
                wpaPassword.append("0E");
                break;
            case "CC":
                wpaPassword.append("12");
                break;
            case "D4":
                wpaPassword.append("35");
                break;
            case "AC":
                wpaPassword.append("1A");
                break;
            case "20":
                wpaPassword.append("1F");
                break;
            case "70":
                wpaPassword.append("20");
                break;
            case "F8":
                wpaPassword.append("21");
                break;
            case "48":
                wpaPassword.append("24");
                break;
            default:
                setErrorCode(R.string.msg_errpirelli);
                return null;
        }
        addPassword(wpaPassword.toString().toUpperCase(Locale.getDefault()));
        return getResults();
    }


}
