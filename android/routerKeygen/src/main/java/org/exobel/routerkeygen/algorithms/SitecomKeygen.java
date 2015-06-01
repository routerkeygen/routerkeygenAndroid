package org.exobel.routerkeygen.algorithms;

import org.exobel.routerkeygen.R;

import java.util.List;
import java.util.Locale;

public class SitecomKeygen extends Keygen {

    private final static String CHARSET = "123456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ"; // without
    // i,l,o,0

    public SitecomKeygen(String ssid, String mac) {
        super(ssid, mac);
    }

    @Override
    public int getSupportState() {
        if (getSsidName().toLowerCase(Locale.getDefault())
                .startsWith("sitecom"))
            return SUPPORTED;
        return UNLIKELY_SUPPORTED;
    }

    private void generateKey(String mac) {
        StringBuilder key = new StringBuilder();
        int numericMac = Integer.parseInt("0"
                + mac.substring(6).split("[A-Fa-f]")[0]);
        key.append(CHARSET.charAt(((numericMac + mac.charAt(11) + mac.charAt(5)) * (mac
                .charAt(9) + mac.charAt(3) + mac.charAt(11)))
                % CHARSET.length()));
        key.append(CHARSET.charAt(((numericMac + mac.charAt(11) + mac.charAt(6)) * (mac
                .charAt(8) + mac.charAt(10) + mac.charAt(11)))
                % CHARSET.length()));
        key.append(CHARSET.charAt(((numericMac + mac.charAt(3) + mac.charAt(5)) * (mac
                .charAt(7) + mac.charAt(9) + mac.charAt(11)))
                % CHARSET.length()));
        key.append(CHARSET.charAt(((numericMac + mac.charAt(7) + mac.charAt(6)) * (mac
                .charAt(5) + mac.charAt(4) + mac.charAt(11)))
                % CHARSET.length()));
        key.append(CHARSET.charAt(((numericMac + mac.charAt(7) + mac.charAt(6)) * (mac
                .charAt(8) + mac.charAt(9) + mac.charAt(11)))
                % CHARSET.length()));
        key.append(CHARSET.charAt(((numericMac + mac.charAt(11) + mac.charAt(5)) * (mac
                .charAt(3) + mac.charAt(4) + mac.charAt(11)))
                % CHARSET.length()));
        key.append(CHARSET.charAt(((numericMac + mac.charAt(11) + mac.charAt(4)) * (mac
                .charAt(6) + mac.charAt(8) + mac.charAt(11)))
                % CHARSET.length()));
        key.append(CHARSET.charAt(((numericMac + mac.charAt(10) + mac
                .charAt(11)) * (mac.charAt(7) + mac.charAt(8) + mac.charAt(11)))
                % CHARSET.length()));
        addPassword(key.toString());
    }

    @Override
    public List<String> getKeys() {
        String mac = getMacAddress();
        if (mac.length() != 12) {
            setErrorCode(R.string.msg_errpirelli);
            return null;
        }
        generateKey(mac);
        final String shortMac = mac.substring(0, 11);
        int lastChar = Integer.parseInt(mac.substring(11), 16);
        lastChar = (lastChar + 1) % 0x10;
        generateKey(shortMac
                + Integer.toHexString(lastChar)
                .toUpperCase(Locale.getDefault()));
        lastChar = (lastChar + 1) % 0x10;
        generateKey(shortMac
                + Integer.toHexString(lastChar)
                .toUpperCase(Locale.getDefault()));
        return getResults();
    }

}
