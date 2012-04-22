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

import java.security.MessageDigest;
import java.util.ArrayList;

import org.exobel.routerkeygen.WifiNetwork;

import android.content.res.Resources;
import android.os.Handler;


public class KeygenThread extends Thread {
	
	MessageDigest md;
	private WifiNetwork router;
	private boolean stopRequested = false;
	ArrayList<String> pwList;
	public static final int RESULTS_READY = 1000;
	public static final int ERROR_MSG = 1001;
	Handler handler;
	Resources resources;
	

	public KeygenThread( Handler h , Resources res)
	{
		this.handler = h;
		this.resources = res;
		this.pwList = new ArrayList<String>();

	}


	public ArrayList<String> getResults() {
		return pwList;
	}


    public boolean isStopRequested() {
        return stopRequested;
    }


    public void setStopRequested(boolean stopRequested) {
        this.stopRequested = stopRequested;
    }


    public WifiNetwork getRouter() {
        return router;
    }


    public void setRouter(WifiNetwork router) {
        this.router = router;
    }

	
}
