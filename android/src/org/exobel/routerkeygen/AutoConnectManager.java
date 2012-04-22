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

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AutoConnectManager extends BroadcastReceiver {
	WifiManager wifi;
	List<String> keys;
	Activity parent;
	WifiNetwork router;	
	WifiConfiguration config = null;
	String tryingKey = "";
	Handler mess;
	int attempts = 0;
	/* Since this receiver is waiting for
	 * a broadcast that can happen anytime
	 * I want it to wait until I need him.*/
	boolean activated = false;
	public AutoConnectManager(WifiManager wifi , List<String> keys , 
						WifiNetwork router , Activity par , Handler h){
		this.wifi = wifi;
		this.keys = keys;
		this.router = router;
		this.parent = par;
		this.mess = h;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		if ( !activated )
			return;
		NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
		if ( info != null )
		{
			DetailedState state = info.getDetailedState();
			@SuppressWarnings("unused")
			String ola = tryingKey;
			if ( state.equals(DetailedState.CONNECTED) )
			{
				try{
					parent.unregisterReceiver(this);   
				}catch(Exception e ){}
				activated = false;
				mess.sendMessage(Message.obtain(mess, 3, 
						new String(tryingKey + " is the correct key!")));
				//TODO: victory
				return;
			}
			if ( state.equals(DetailedState.CONNECTING)  || 
				 state.equals(DetailedState.OBTAINING_IPADDR) ||
				 state.equals(DetailedState.AUTHENTICATING) )
			{
				return; /* Waiting for a definitive outcome*/
			}
			if ( state.equals(DetailedState.DISCONNECTED) && keys.isEmpty() )
			{
				try{
					parent.unregisterReceiver(this);   
				}catch(Exception e ){
					e.toString();
				}
				activated = false;
				mess.sendMessage(Message.obtain(mess, 3, new String("Failed!")));
				return; /* Waiting for a definitive outcome*/
			}
			if ( state.equals(DetailedState.FAILED) )
			{
				wifi.reassociate();
				testKey();
				return;
			}
		}
		
		if ( attempts > 2 )
			testKey();
		else
		{
			attempts++;
			attemptConnect();
		}

	}
	
	public void activate(){
		if ( !activated )
			firstRun();
		activated = true;
	}
	
	public void firstRun(){
		List<WifiConfiguration> saved = wifi.getConfiguredNetworks();
		for ( WifiConfiguration w : saved )
		{
			if ( w.SSID.equals('"' + router.ssid +'"') )
			{
				wifi.disableNetwork(w.networkId);
				wifi.removeNetwork(w.networkId);
			}
		}
		testKey();
	}
	
	public void attemptConnect(){
		if ( this.config != null )
		{
			boolean b = wifi.disableNetwork(this.config.networkId);
			Log.d("WifiPreference", "disableNetwork " +this.config.networkId + "  returned " + b );
			b = wifi.removeNetwork(this.config.networkId);
			Log.d("WifiPreference", "rmeoveNetwork " +this.config.networkId + "  returned " + b );

		}
		WifiConfiguration config = new WifiConfiguration();
		config.priority = 90 -keys.size();  ;
		config.status = WifiConfiguration.Status.ENABLED;
		config.SSID = '"' + router.ssid	 + '"';
		config.hiddenSSID = true;
		if ( router.encryption.contains("WEP") )
		{
			config.allowedKeyManagement.clear();
			config.allowedAuthAlgorithms.clear();
			config.allowedProtocols.clear();
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.wepTxKeyIndex = 0;
			if (tryingKey.length() != 0) {
				int length = tryingKey.length();
				// WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
				if ((length == 10 || length == 26 || length == 58) &&
						tryingKey.matches("[0-9A-Fa-f]*")) {
					config.wepKeys[0] = tryingKey;
					config.preSharedKey = null;
				}
				else {
					config.wepKeys[0] = '"' + tryingKey + '"';
					config.preSharedKey = null;
				}
			}
		}
		else
			if ( router.encryption.contains("PSK") )
			{
				config.allowedKeyManagement.clear();
				config.allowedAuthAlgorithms.clear();
				config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
				config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
				config.preSharedKey  = '"' + tryingKey +'"';
			}

		int res = 0;
		res = wifi.addNetwork(config);
		config.networkId = res;
		this.config = config;
		Log.d("WifiPreference", "add Network returned " + res );
		boolean b = wifi.enableNetwork(res, true);        
		Log.d("WifiPreference", "enableNetwork returned " + b );
		
	}
	
	public void testKey(){
		attempts = 0;
		if ( !keys.isEmpty() )
		{
			tryingKey = keys.get(0);
			keys.remove(0);	
			mess.sendEmptyMessage(2);
		}
		attemptConnect();
	}

}
