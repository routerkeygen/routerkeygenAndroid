/*
 * Copyright 2012 Rui Araújo, Luís Fonseca
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
 */
package org.exobel.routerkeygen.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.exobel.routerkeygen.R;

import android.os.Parcel;
import android.os.Parcelable;

public class NativeThomson extends Keygen {
	final private String ssidIdentifier;
	final private List<ThomsonTask> tasks;

	private final static int MAGIC_NUMBER = 46657;

	public NativeThomson(Keygen keygen) {
		super(keygen.getSsidName(), keygen.getMacAddress());
		ssidIdentifier = keygen.getSsidName().substring(
				keygen.getSsidName().length() - 6);
		tasks = new ArrayList<ThomsonTask>();
	}

	@Override
	public synchronized void setStopRequested(boolean stopRequested) {
		super.setStopRequested(stopRequested);
		for (ThomsonTask t : tasks)
			t.stopRequested = true;
	}

	static {
		System.loadLibrary("thomson");
	}

	/**
	 * Native processing without a dictionary.
	 */
	public native String[] thomson(byte[] essid, int start, int end);

	@Override
	public List<String> getKeys() {
		if (ssidIdentifier.length() != 6) {
			setErrorCode(R.string.msg_shortessid6);
			return null;
		}
		byte[] routerESSID = new byte[3];

		for (int i = 0; i < 6; i += 2)
			routerESSID[i / 2] = (byte) ((Character.digit(
					ssidIdentifier.charAt(i), 16) << 4) + Character.digit(
					ssidIdentifier.charAt(i + 1), 16));
		int cores = Runtime.getRuntime().availableProcessors();
		if (cores <= 0)
			cores = 1;// WTF?? HOW? :P
		int work = MAGIC_NUMBER / cores;
		int beggining = 0;
		for (int i = 1; i < cores; ++i) {
			tasks.add(new ThomsonTask(this, routerESSID, beggining, beggining
					+ work));
			tasks.get(tasks.size() - 1).start();
			beggining += work;
		}
		tasks.add(new ThomsonTask(this, routerESSID, beggining, MAGIC_NUMBER));
		tasks.get(tasks.size() - 1).start();
		for (ThomsonTask t : tasks) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;	
			}
			final String[] results = t.results;
			if ( t.error )
				setErrorCode(R.string.msg_err_native);
			if (isStopRequested())
				return null;
			for (int i = 0; i < results.length; ++i)
				addPassword(results[i]);
		}
		if (getResults().size() == 0)
			setErrorCode(R.string.msg_errnomatches);
		return getResults();
	}

	private NativeThomson(Parcel in) {
		super(in);
		ssidIdentifier = in.readString();
		tasks = new ArrayList<ThomsonTask>();
	}

	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(ssidIdentifier);
	}

	public static final Parcelable.Creator<NativeThomson> CREATOR = new Parcelable.Creator<NativeThomson>() {
		public NativeThomson createFromParcel(Parcel in) {
			return new NativeThomson(in);
		}

		public NativeThomson[] newArray(int size) {
			return new NativeThomson[size];
		}
	};

	public static class ThomsonTask extends Thread {
		private final NativeThomson keygen;
		private final byte[] routerESSID;
		private final int begin;
		private final int end;
		private boolean error = false;
		private String[] results;
		@SuppressWarnings("unused")
		//This is read in the native code
		private boolean stopRequested = false;

		static {
			System.loadLibrary("thomson");
		}

		public ThomsonTask(NativeThomson keygen, byte[] routerESSID, int begin,
				int end) {
			this.keygen = keygen;
			this.routerESSID = routerESSID;
			this.begin = begin;
			this.end = end;
		}

		@Override
		public void run() {
			try {
				results = keygen.thomson(routerESSID, begin, end);
			} catch (Exception e) {
				error = true;
			}
		}
	}
}
