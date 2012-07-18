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
package org.exobel.routerkeygen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class WifiStateReceiver extends BroadcastReceiver {

	final private WifiManager wifi;
	public WifiStateReceiver(WifiManager wifi ){
		this.wifi = wifi;
	}
	@Override
	public void onReceive(Context context, Intent arg1) {
		if ( wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED  )
		{
			wifi.startScan();
			try{
    		context.unregisterReceiver(this);
			}catch(Exception e ){}
    		Toast.makeText( context ,
					context.getResources().getString(R.string.msg_scanstarted),
					Toast.LENGTH_SHORT).show();
    		return;
		}
		if ( wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLING )
		{
			Toast.makeText( context , 
					  context.getResources().getString(R.string.msg_nowifi),
					  Toast.LENGTH_SHORT).show();
			return;
		}
	}

}
