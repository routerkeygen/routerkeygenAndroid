package org.exobel.routerkeygen.algorithms;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.exobel.routerkeygen.R;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

/*
 * Copyright 2016 Dusan Klinec, Miroslav Svitok
 *
 * This file is part of Router Keygen.
 *
 * Router Keygen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Router Keygen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Router Keygen.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Using UPC default key generator implemented by blasty.
 * Source: https://haxx.in/upc_keys.c
 */
public class UpcKeygen extends Keygen {
    private static final String TAG="UpcKeygen";
    private final List<String> computedKeys = new LinkedList<>();

    static {
        System.loadLibrary("upc");
    }

    public static final Parcelable.Creator<UpcKeygen> CREATOR = new Parcelable.Creator<UpcKeygen>() {
        public UpcKeygen createFromParcel(Parcel in) {
            return new UpcKeygen(in);
        }
        public UpcKeygen[] newArray(int size) {
            return new UpcKeygen[size];
        }
    };

    public UpcKeygen(String ssid, String mac, int frequency) {
        super(ssid, mac, frequency);
    }

    private UpcKeygen(Parcel in) {
        super(in);
    }

    public static int getStaticSupportState(String ssid, String mac, int frequency){
        ssid = ssid.trim();
        if (ssid.matches("UPC[0-9]{7}")) {
            return SUPPORTED;
        } else if (ssid.matches("UPC[0-9]{5,6}")) {
            return UNLIKELY_SUPPORTED;
        } else if (ssid.matches("UPC[0-9]{8}")) {
            return UNLIKELY_SUPPORTED;
        } else if (mac != null && (mac.startsWith("64:7C:34") || mac.toUpperCase().startsWith("647C34"))) {
            return UNLIKELY_SUPPORTED;
        }

        return UNSUPPORTED;
    }

    @Override
    public int getSupportState() {
        return getStaticSupportState(getSsidName(), getMacAddress(), getFrequency());
    }

    @Override
    public synchronized void setStopRequested(boolean stopRequested) {
        super.setStopRequested(stopRequested);
    }

    /**
     * Called by native code when a key is computed.
     */
    public void onKeyComputed(String key){
        computedKeys.add(key);
        if (monitor != null){
            monitor.onKeyComputed();
        }
    }

    /**
     * Called by native code when a progress in computation is made.
     * @param progress 0..1 value. 0=0%, 1=100%
     */
    public void onProgressed(double progress){
        if (monitor != null){
            monitor.onKeygenProgressed(progress);
        }
    }

    @Override
    public boolean keygenSupportsProgress() {
        return true;
    }

    @Override
    public List<String> getKeys() {
        String[] results = null;
        try {
            final String targetSsid = getSsidName();
            final String targetMac = getMacAddress();
            final boolean is5G = getFrequency() > 5000;
            Log.d(TAG, String.format("Starting a new task for ssid: %s, frequency: %d", targetSsid, getFrequency()));

            // Ubee extension first, better matching.
            final BigInteger macInt = new BigInteger(targetMac, 16);
            final BigInteger macStart = macInt.subtract(BigInteger.valueOf(10));
            for(int i=0; i<20; i++){
                final BigInteger curMac = macStart.add(BigInteger.valueOf(i));
                final String curSsid = upcUbeeSsid(curMac.toByteArray());
                if (targetSsid.equalsIgnoreCase(curSsid)){
                    final String curPass = upcUbeePass(curMac.toByteArray());
                    computedKeys.add(curPass);

                    Log.v(TAG, String.format("Ubee match found, mac: %s, ssid: %s, pass: %s", curMac.toString(16), curSsid, curPass));
                }
            }

            // Ubee extension - purely on mac address, received (-4, -3, -2, -1, -0, +1, +2) for 2.4 GHz
            final String upperMac = targetMac.toUpperCase();
            if (upperMac.startsWith("647C34")) {
                final BigInteger macStart2 = macInt.subtract(BigInteger.valueOf(4));
                for (int i = 0; i < 7; i++) {
                    final BigInteger curMac = macStart2.add(BigInteger.valueOf(i));
                    final String curPass = upcUbeePass(curMac.toByteArray());
                    if (!computedKeys.contains(curPass)) {
                        computedKeys.add(curPass);

                        Log.v(TAG, String.format("Ubee attempt added, mac: %s, pass: %s", curMac.toString(16), curPass));
                    }
                }
            }

            // upc_keys.c attack.
            if (targetSsid.startsWith("UPC")) {
                upcNative((targetSsid+"\0").getBytes("US-ASCII"), is5G);
            }

            results = computedKeys.toArray(new String[computedKeys.size()]);

        } catch (Exception e) {
            Log.e(TAG, "Exception in native computation", e);
            setErrorCode(R.string.msg_err_native);
        }

        if (isStopRequested() || results == null)
            return null;
        for (String result : results)
            addPassword(result);
        if (getResults().size() == 0)
            setErrorCode(R.string.msg_errnomatches);
        return getResults();
    }

    /**
     * Native key generator implementation.
     * @param essid
     * @return
     */
    private native void upcNative(byte[] essid, boolean is5g);

    /**
     * Returns SSID
     * @param macc
     * @return
     */
    private native String upcUbeeSsid(byte[] macc);

    /**
     * Returns passwd for given mac.
     * @param mac
     * @return
     */
    private native String upcUbeePass(byte[] mac);
}
