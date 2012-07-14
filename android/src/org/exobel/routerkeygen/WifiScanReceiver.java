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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.exobel.routerkeygen.algorithms.Keygen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.widget.Toast;

class WiFiScanReceiver extends BroadcastReceiver {
	  RouterKeygen solver;
	  final private WirelessMatcher matcher;
	  public WiFiScanReceiver( RouterKeygen wifiDemo, WirelessMatcher matcher ) {
	    super();
	    this.solver = wifiDemo;
	    this.matcher = matcher;
	  }

	  public void onReceive(Context c, Intent intent) {
		  
		if ( solver == null )
			return;
		if ( solver.getWifi() == null )
			return;
	    List<ScanResult> results = solver.getWifi().getScanResults();
	    ArrayList<Keygen> list = new ArrayList<Keygen>();
	    Set<Keygen> set = new TreeSet<Keygen>();
	    if ( results == null )/*He have had reports of this returning null instead of empty*/
	    	return;
	    for (int i = 0; i < results.size() - 1; ++i)
	    	for (int j = i+1; j < results.size(); ++j)
		    	if(results.get(i).SSID.equals(results.get(j).SSID))
		    		results.remove(j--);
		try {
		    for (ScanResult result : results)
		    	  set.add(matcher.getKeygen(result.SSID, result.BSSID, result.level , result.capabilities ));

		}catch(LinkageError e){
			Toast.makeText( c ,R.string.err_misbuilt_apk, 
					Toast.LENGTH_SHORT).show();
		}
	    Iterator<Keygen> it = set.iterator();
	    while( it.hasNext())
	    	list.add(it.next());
	    solver.setVulnerable(list);
	    if (  list.isEmpty() )
			Toast.makeText( c , R.string.msg_nowifidetected ,Toast.LENGTH_SHORT).show();

	    solver.getScanResuls().setAdapter(new WifiListAdapter(list , c)); 
	    try{
		c.unregisterReceiver(this);   
	    }catch(Exception e ){}
	    
	 }

}
