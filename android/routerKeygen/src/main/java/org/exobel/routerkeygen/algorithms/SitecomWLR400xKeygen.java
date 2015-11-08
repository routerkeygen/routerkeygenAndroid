package org.exobel.routerkeygen.algorithms;

import org.exobel.routerkeygen.R;

import java.util.List;
import java.util.Locale;

public class SitecomWLR400xKeygen extends Keygen {

    private final static String CHARSETS_4000[] = {"23456789ABCDEFGHJKLMNPQRSTUVWXYZ38BZ", "WXCDYNJU8VZABKL46PQ7RS9T2E5H3MFGPWR2"};
    private final static String CHARSETS_4004[] = {"JKLMNPQRST23456789ABCDEFGHUVWXYZ38BK", "E5MFJUWXCDKL46PQHAB3YNJ8VZ7RS9TR2GPW"};
    private final static  long MAGIC1 =  0x98124557L;
    private final static  long MAGIC2 =  0x0004321aL;
    private final static  long MAGIC3 =  0x80000000L;

    public SitecomWLR400xKeygen(String ssid, String mac) {
        super(ssid, mac);
    }

    @Override
    public int getSupportState() {
        if (getSsidName().toLowerCase(Locale.getDefault())
                .equalsIgnoreCase("sitecom" + getMacAddress().substring(6)))
            return SUPPORTED;
        return UNLIKELY_SUPPORTED;
    }

    private void generateKey(String mac, String[] charsets) {
        long val = Long.parseLong(mac.substring(4), 16);
        int [] offsets = new int[12];
        for (int i = 0; i < 12; ++i) {
            if ((val & 0x1) == 0) {
                val = val ^ MAGIC2;
                val = val >> 1;
            }
            else{
                val = val ^ MAGIC1;
                val = val >> 1;
                val = val | MAGIC3;
            }
            long offset = val % charsets[0].length();
            offsets[i] = (int)offset; //safe because length is tiny
        }
        StringBuilder wpakey = new StringBuilder();
        wpakey.append(charsets[0].charAt(offsets[0]));
        for (int i = 0; i < 11; ++i) {
            if (offsets[i] != offsets[i+1]) {
                wpakey.append(charsets[0].charAt(offsets[i + 1]));
            }
            else {
                int newOffset = (offsets[i] + i) % charsets[0].length();
                wpakey.append(charsets[1].charAt(newOffset));
            }
        }
        addPassword(wpakey.toString());
    }


    @Override
    public List<String> getKeys() {
        String mac = getMacAddress();
        if (mac.length() != 12) {
            setErrorCode(R.string.msg_errpirelli);
            return null;
        }
        generateKey(mac, CHARSETS_4000);
        generateKey(mac, CHARSETS_4004);
        return getResults();
    }

}
