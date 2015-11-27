package org.exobel.routerkeygen.algorithms;

import android.os.Parcel;
import android.os.Parcelable;

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

public class Sitecom2100Keygen extends Keygen {

    private final static String CHARSET = "ABCDEFGHJKLMNPQRSTUVWXYZ"; //Missing I,O
    public static final Parcelable.Creator<Sitecom2100Keygen> CREATOR = new Creator<Sitecom2100Keygen>()
    {
        @Override
        public Sitecom2100Keygen createFromParcel(Parcel in)
        {
            return new Sitecom2100Keygen(in);
        }

        @Override
        public Sitecom2100Keygen[] newArray(int size)
        {
            return new Sitecom2100Keygen[size];
        }
    };

    public Sitecom2100Keygen(String ssid, String mac) {
        super(ssid, mac);
    }

    public Sitecom2100Keygen(Parcel in) {super(in);}

    @Override
    public int getSupportState() {
        if (getSsidName().toLowerCase(Locale.getDefault())
                .startsWith("sitecom"))
            return SUPPORTED;
        return UNLIKELY_SUPPORTED;
    }

    private String generateKey(String slicedHash) {
        final StringBuilder key = new StringBuilder();
        final BigInteger divider = new BigInteger("24");
        BigInteger magicNrBig = new BigInteger(slicedHash, 16);

        for (int i = 0; i < 12; i++)
        {
            key.append(CHARSET.charAt(magicNrBig.mod(divider).intValue()));
            magicNrBig = magicNrBig.divide(divider);
        }

        return key.toString();
    }

    @Override
    public List<String> getKeys() {
        String mac = getMacAddress();
        if (mac.length() != 12) {
            setErrorCode(R.string.msg_errpirelli);
            return null;
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e1) {
            setErrorCode(R.string.msg_nomd5);
            return null;
        }
        try {
            md.reset();
            md.update(mac.toLowerCase(Locale.getDefault()).getBytes("ASCII"));
            byte[] hash = md.digest();
            String hashStr = StringUtils.getHexString(hash);
            hashStr = hashStr.substring(hashStr.length() - 16);
            addPassword(generateKey(hashStr));
            return getResults();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
