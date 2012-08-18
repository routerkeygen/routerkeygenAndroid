package com.farproc.wifi.connecter;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

public abstract class ConfigurationSecurities {
	/**
     * @return The security of a given {@link WifiConfiguration}.
     */
	public abstract String getWifiConfigurationSecurity(WifiConfiguration wifiConfig);
	/**
     * @return The security of a given {@link ScanResult}.
     */
	public abstract String getScanResultSecurity(ScanResult scanResult);
	/**
     * Fill in the security fields of WifiConfiguration config.
     * @param config The object to fill.
     * @param security If is OPEN, password is ignored.
     * @param password Password of the network if security is not OPEN.
     */
	public abstract void setupSecurity(WifiConfiguration config, String security, final String password);
	public abstract String getDisplaySecirityString(final ScanResult scanResult);
	public abstract boolean isOpenNetwork(final String security);
	
	public static ConfigurationSecurities newInstance() {
		if(Version.SDK < 8) {
			return new ConfigurationSecuritiesOld();
		} else {
			return new ConfigurationSecuritiesV8();
		}
	}
	
}
