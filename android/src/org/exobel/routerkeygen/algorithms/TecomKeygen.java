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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.utils.StringUtils;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * This is the algorithm to generate the WPA passphrase 
 * for the Hitachi (TECOM) AH-4021 and Hitachi (TECOM) AH-4222.
 * The key is the 26 first characters from the SSID SHA1 hash.
 *  Link : http://rafale.org/~mattoufoutu/ebooks/Rafale-Mag/Rafale12/Rafale12.08.HTML
 */
public class TecomKeygen extends Keygen {

	private MessageDigest md;
	
	public TecomKeygen(String ssid, String mac ) {
		super(ssid, mac);
	}

	@Override
	public List<String> getKeys() {
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e1) {
			setErrorCode(R.string.msg_nosha1);
			return null;
		}
		md.reset();
		md.update(getSsidName().getBytes());
		byte [] hash = md.digest();
		try {
			addPassword(StringUtils.getHexString(hash).substring(0,26));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getResults();
	}

	private TecomKeygen(Parcel in) {
		super(in);
	}
	
    public static final Parcelable.Creator<TecomKeygen> CREATOR = new Parcelable.Creator<TecomKeygen>() {
        public TecomKeygen createFromParcel(Parcel in) {
            return new TecomKeygen(in);
        }

        public TecomKeygen[] newArray(int size) {
            return new TecomKeygen[size];
        }
    };
}
