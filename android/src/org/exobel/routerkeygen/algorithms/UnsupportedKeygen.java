package org.exobel.routerkeygen.algorithms;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class UnsupportedKeygen extends Keygen{

	public UnsupportedKeygen(String ssid, String mac, int level, String enc) {
		super(ssid, mac, level, enc);
	}

	@Override
	public List<String> getKeys() {
		setErrorCode(0);
		return null;
	}

	@Override
	public int getSupportState() {
		return UNSUPPORTED;
	}


	private UnsupportedKeygen(Parcel in) {
		super(in);
	}
	
    public static final Parcelable.Creator<UnsupportedKeygen> CREATOR = new Parcelable.Creator<UnsupportedKeygen>() {
        public UnsupportedKeygen createFromParcel(Parcel in) {
            return new UnsupportedKeygen(in);
        }

        public UnsupportedKeygen[] newArray(int size) {
            return new UnsupportedKeygen[size];
        }
    };
}
