package org.exobel.routerkeygen.algorithms;

import java.util.List;

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
	public boolean isSupported() {
		return false;
	}

	
}
