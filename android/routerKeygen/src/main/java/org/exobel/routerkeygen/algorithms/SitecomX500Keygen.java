package org.exobel.routerkeygen.algorithms;

import org.exobel.routerkeygen.R;

import java.util.List;
import java.util.Locale;

public class SitecomX500Keygen extends Keygen {

    private final static String CHARSET = "123456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ"; // without
    // i,l,o,0

    public SitecomX500Keygen(String ssid, String mac) {
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
        generateKey(mac.toLowerCase(Locale.getDefault()));//wlm2500
        generateKey(mac.toUpperCase(Locale.getDefault()));//wlm3500
        generateKey(incrementMac(mac, 1).toUpperCase(Locale.getDefault()));// wlm5500 5ghz
        generateKey(incrementMac(mac, 2).toUpperCase(Locale.getDefault()));// wlm5500 2.4ghz
        return getResults();
    }

}
