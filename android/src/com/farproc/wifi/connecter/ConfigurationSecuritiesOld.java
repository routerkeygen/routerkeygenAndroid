package com.farproc.wifi.connecter;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.text.TextUtils;
import android.util.Log;

public class ConfigurationSecuritiesOld extends ConfigurationSecurities {
	
	// Constants used for different security types
	public static final String WPA2 = "WPA2";
	public static final String WPA = "WPA";
	public static final String WEP = "WEP";
	public static final String OPEN = "Open";
    // For EAP Enterprise fields
    public static final String WPA_EAP = "WPA-EAP";
    public static final String IEEE8021X = "IEEE8021X";

    public static final String[] EAP_METHOD = { "PEAP", "TLS", "TTLS" };
    
    public static final int WEP_PASSWORD_AUTO = 0;
    public static final int WEP_PASSWORD_ASCII = 1;
    public static final int WEP_PASSWORD_HEX = 2;
    
	static final String[] SECURITY_MODES = { WEP, WPA, WPA2, WPA_EAP, IEEE8021X };
    
    private static final String TAG = "ConfigurationSecuritiesOld";

	@Override
	public String getWifiConfigurationSecurity(WifiConfiguration wifiConfig) {

        if (wifiConfig.allowedKeyManagement.get(KeyMgmt.NONE)) {
            // If we never set group ciphers, wpa_supplicant puts all of them.
            // For open, we don't set group ciphers.
            // For WEP, we specifically only set WEP40 and WEP104, so CCMP
            // and TKIP should not be there.
            if (!wifiConfig.allowedGroupCiphers.get(GroupCipher.CCMP)
                    && 
                    (wifiConfig.allowedGroupCiphers.get(GroupCipher.WEP40)
                            || wifiConfig.allowedGroupCiphers.get(GroupCipher.WEP104))) {
                return WEP;
            } else {
                return OPEN;
            }
        } else if (wifiConfig.allowedProtocols.get(Protocol.RSN)) {
            return WPA2;
        } else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_EAP)) {
            return WPA_EAP;
        } else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return IEEE8021X;
        } else if (wifiConfig.allowedProtocols.get(Protocol.WPA)) {
            return WPA;
        } else {
            Log.w(TAG, "Unknown security type from WifiConfiguration, falling back on open.");
            return OPEN;
        }
    }

	@Override
    public String getScanResultSecurity(ScanResult scanResult) {
        final String cap = scanResult.capabilities;
        for (int i = SECURITY_MODES.length - 1; i >= 0; i--) {
            if (cap.contains(SECURITY_MODES[i])) {
                return SECURITY_MODES[i];
            }
        }
        
        return OPEN;
    }
	@Override
	public String getDisplaySecirityString(final ScanResult scanResult) {
		return getScanResultSecurity(scanResult);
	}
	
	private static boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();
        
        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }
        
        return isHex(wepKey);
    }
    
    private static boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f')) {
                return false;
            }
        }
        
        return true;
    }

	@Override
	public void setupSecurity(WifiConfiguration config, String security, final String password) {
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        
        if (TextUtils.isEmpty(security)) {
            security = OPEN;
            Log.w(TAG, "Empty security, assuming open");
        }
        
        if (security.equals(WEP)) {
        	 int wepPasswordType = WEP_PASSWORD_AUTO;
            // If password is empty, it should be left untouched
            if (!TextUtils.isEmpty(password)) {
				if (wepPasswordType == WEP_PASSWORD_AUTO) {
                    if (isHexWepKey(password)) {
                        config.wepKeys[0] = password;
                    } else {
                        config.wepKeys[0] = Wifi.convertToQuotedString(password);
                    }
                } else {
                    config.wepKeys[0] = wepPasswordType == WEP_PASSWORD_ASCII
                            ? Wifi.convertToQuotedString(password)
                            : password;
                }
            }
            
            config.wepTxKeyIndex = 0;
            
            config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);

            config.allowedKeyManagement.set(KeyMgmt.NONE);
            
            config.allowedGroupCiphers.set(GroupCipher.WEP40);
            config.allowedGroupCiphers.set(GroupCipher.WEP104);
            
        } else if (security.equals(WPA) || security.equals(WPA2)){
            config.allowedGroupCiphers.set(GroupCipher.TKIP);
            config.allowedGroupCiphers.set(GroupCipher.CCMP);
            
            config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
            
            config.allowedPairwiseCiphers.set(PairwiseCipher.CCMP);
            config.allowedPairwiseCiphers.set(PairwiseCipher.TKIP);

            config.allowedProtocols.set(security.equals(WPA2) ? Protocol.RSN : Protocol.WPA);
            
            // If password is empty, it should be left untouched
            if (!TextUtils.isEmpty(password)) {
                if (password.length() == 64 && isHex(password)) {
                    // Goes unquoted as hex
                    config.preSharedKey = password;
                } else {
                    // Goes quoted as ASCII
                    config.preSharedKey = Wifi.convertToQuotedString(password);
                }
            }
            
        } else if (security.equals(OPEN)) {
            config.allowedKeyManagement.set(KeyMgmt.NONE);
        } else if (security.equals(WPA_EAP) || security.equals(IEEE8021X)) {
            config.allowedGroupCiphers.set(GroupCipher.TKIP);
            config.allowedGroupCiphers.set(GroupCipher.CCMP);
            if (security.equals(WPA_EAP)) {
                config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
            } else {
                config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
            }
            if (!TextUtils.isEmpty(password)) {
                config.preSharedKey = Wifi.convertToQuotedString(password);
            }
        }
    }

	@Override
	public boolean isOpenNetwork(String security) {
		return OPEN.equals(security);
	}

}
