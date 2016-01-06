package org.exobel.routerkeygen.algorithms;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.exobel.routerkeygen.R;
import java.util.ArrayList;
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
    static {
        System.loadLibrary("upc");
    }

    private final List<UpcNativeTask> tasks = new ArrayList<>();

    public static final Parcelable.Creator<UpcKeygen> CREATOR = new Parcelable.Creator<UpcKeygen>() {
        public UpcKeygen createFromParcel(Parcel in) {
            return new UpcKeygen(in);
        }
        public UpcKeygen[] newArray(int size) {
            return new UpcKeygen[size];
        }
    };

    public UpcKeygen(String ssid, String mac) {
        super(ssid, mac);
    }

    private UpcKeygen(Parcel in) {
        super(in);
    }

    @Override
    public int getSupportState() {
        if (getSsidName().matches("UPC[0-9]{5,7}")) {
            return SUPPORTED;
        }
        return UNLIKELY_SUPPORTED;
    }

    @Override
    public synchronized void setStopRequested(boolean stopRequested) {
        super.setStopRequested(stopRequested);
        for (UpcNativeTask t : tasks) {
            t.stopRequested = true;
        }
    }

    @Override
    public List<String> getKeys() {
        // No paralelization yet.
        try {
            Log.d(TAG, "Starting a new task for ssid: " + getSsidName());
            final UpcNativeTask task = new UpcNativeTask(this, getSsidName().getBytes("US-ASCII"));
            tasks.add(task);

            task.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (UpcNativeTask t : tasks) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                continue;
            }
            final String[] results = t.results;
            if (t.error || results == null)
                setErrorCode(R.string.msg_err_native);
            if (isStopRequested())
                return null;
            for (String result : results) addPassword(result);
        }
        if (getResults().size() == 0)
            setErrorCode(R.string.msg_errnomatches);
        return getResults();
    }

    /**
     * Native key generator implementation.
     * @param essid
     * @return
     */
    private native String[] upcNative(byte[] essid);

    /**
     * Computation thread.
     */
    public static class UpcNativeTask extends Thread {


        private final UpcKeygen keygen;
        private final byte[] routerESSID;
        private boolean error = false;
        private String[] results;
        @SuppressWarnings("unused")
        //This is read in the native code
        private boolean stopRequested = false;

        public UpcNativeTask(UpcKeygen keygen, byte[] routerESSID) {
            this.keygen = keygen;
            this.routerESSID = routerESSID;
        }

        @Override
        public void run() {
            try {
                results = keygen.upcNative(routerESSID);
            } catch (Exception e) {
                error = true;
            }
        }
    }
}
