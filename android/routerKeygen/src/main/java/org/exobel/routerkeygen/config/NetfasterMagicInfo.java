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

import android.os.Parcel;
import android.os.Parcelable;

public class NetfasterMagicInfo implements Parcelable {
    public static final Creator<NetfasterMagicInfo> CREATOR = new Creator<NetfasterMagicInfo>() {
        public NetfasterMagicInfo createFromParcel(Parcel in) {
            return new NetfasterMagicInfo(in);
        }

        public NetfasterMagicInfo[] newArray(int size) {
            return new NetfasterMagicInfo[size];
        }
    };
    final private int [] divisor;
    final private int base;
    final private String key;
    final private String mac;

    public NetfasterMagicInfo(int [] divisor, int base, String key, String mac) {
        this.divisor = divisor;
        this.base = base;
        this.key = key;
        this.mac = mac;
    }

    private NetfasterMagicInfo(Parcel in) {
        this.divisor = in.createIntArray();
        this.base = in.readInt();
        this.key = in.readString();
        this.mac = in.readString();
    }

    public String getKey() {
        return key;
    }

    public int [] getDivisor() {
        return divisor;
    }

    public int getBase() {
        return base;
    }

    public String getMac() {
        return mac;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(divisor);
        dest.writeInt(base);
        dest.writeString(key);
        dest.writeString(mac);
    }

}
