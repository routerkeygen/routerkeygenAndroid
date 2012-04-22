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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.widget.Toast;

class WiFiScanReceiver extends BroadcastReceiver {
	  RouterKeygen solver;

	  public WiFiScanReceiver( RouterKeygen wifiDemo) {
	    super();
	    this.solver = wifiDemo;
	  }

	  public void onReceive(Context c, Intent intent) {
		  
		if ( solver == null )
			return;
		if ( solver.wifi == null )
			return;
	    List<ScanResult> results = solver.wifi.getScanResults();
	    ArrayList<WifiNetwork> list = new ArrayList<WifiNetwork>();
	    Set<WifiNetwork> set = new TreeSet<WifiNetwork>();
	    if ( results == null )/*He have had reports of this returning null instead of empty*/
	    	return;
	    for (int i = 0; i < results.size() - 1; ++i)
	    	for (int j = i+1; j < results.size(); ++j)
		    	if(results.get(i).SSID.equals(results.get(j).SSID))
		    		results.remove(j--);
	    
	    for (ScanResult result : results) {
	    	  set.add(new WifiNetwork(result.SSID, result.BSSID, result.level , result.capabilities , solver));
	    }
	    Iterator<WifiNetwork> it = set.iterator();
	    while( it.hasNext())
	    	list.add(it.next());
	    solver.vulnerable = list;
	    if (  list.isEmpty() )
	    {
			Toast.makeText( solver , solver.getResources().getString(R.string.msg_nowifidetected) ,
					Toast.LENGTH_SHORT).show();
	    }
	    solver.scanResuls.setAdapter(new WifiListAdapter(list , solver)); 
	    try{
		solver.unregisterReceiver(this);   
	    }catch(Exception e ){}
	    
	 }

}
