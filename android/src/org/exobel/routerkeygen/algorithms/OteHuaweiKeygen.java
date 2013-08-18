package org.exobel.routerkeygen.algorithms;

import java.util.List;

import org.exobel.routerkeygen.R;

import android.os.Parcel;
import android.os.Parcelable;

public class OteHuaweiKeygen extends Keygen {
	public final static int MAGIC_NUMBER = 65535;
	private final String magicValues;

	public OteHuaweiKeygen(String ssid, String mac,
			String magicValues) {
		super(ssid, mac);
		this.magicValues = magicValues;
	}

	@Override
	public List<String> getKeys() {
		final String mac = getMacAddress();
		if (mac.length() != 12) {
			setErrorCode(R.string.msg_errpirelli);
			return null;
		}
		final String[] magic = magicValues.split(" ");
		final String series = mac.substring(0, 2) + mac.substring(6, 8);
		final int point;
		if (series.equals("E8FD"))
			point = 0;
		else if (series.equals("E8F5"))
			point = 1;
		else if (series.equals("E8F6"))
			point = 2;
		else
			return getResults();
		if (point >= magic.length)
			return getResults();
		final String pass = "000000" + magic[point];
		addPassword(pass.substring(pass.length() - 8));
		return getResults();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(magicValues);
	}

	private OteHuaweiKeygen(Parcel in) {
		super(in);
		magicValues = in.readString();
	}

	public static final Parcelable.Creator<OteHuaweiKeygen> CREATOR = new Parcelable.Creator<OteHuaweiKeygen>() {
		public OteHuaweiKeygen createFromParcel(Parcel in) {
			return new OteHuaweiKeygen(in);
		}

		public OteHuaweiKeygen[] newArray(int size) {
			return new OteHuaweiKeygen[size];
		}
	};

}
