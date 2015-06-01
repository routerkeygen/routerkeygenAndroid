package org.exobel.routerkeygen.algorithms;

import android.os.Parcel;
import android.os.Parcelable;

import org.exobel.routerkeygen.R;

import java.util.List;
import java.util.Locale;

public class OteBAUDKeygen extends Keygen {

    public static final Parcelable.Creator<OteBAUDKeygen> CREATOR = new Parcelable.Creator<OteBAUDKeygen>() {
        public OteBAUDKeygen createFromParcel(Parcel in) {
            return new OteBAUDKeygen(in);
        }

        public OteBAUDKeygen[] newArray(int size) {
            return new OteBAUDKeygen[size];
        }
    };

    public OteBAUDKeygen(String ssid, String mac) {
        super(ssid, mac);
    }

    private OteBAUDKeygen(Parcel in) {
        super(in);
    }

    @Override
    public List<String> getKeys() {
        if (getMacAddress().length() != 12) {
            setErrorCode(R.string.msg_errpirelli);
            return null;
        }
        addPassword("0" + getMacAddress().toLowerCase(Locale.getDefault()));
        return getResults();
    }


}
