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

import org.exobel.routerkeygen.R;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;

public class NativeThomson extends KeygenThread{

	public NativeThomson(Handler h, Resources res) {
		super(h, res);
	}


	static {
		System.loadLibrary("thomson");
    }
	
    		  
  /** 
   * Native processing without a dictionary.
   */
	public native String[] thomson( byte [] essid );
  
  
	public void run(){
		if ( getRouter() == null)
			return;
		if ( getRouter().getSSIDsubpart().length() != 6 ) 
		{
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_shortessid6)));
			return;
		}
		byte [] routerESSID = new byte[3];

		for (int i = 0; i < 6; i += 2)
			routerESSID[i / 2] = (byte) ((Character.digit(getRouter().getSSIDsubpart().charAt(i), 16) << 4)
					+ Character.digit(getRouter().getSSIDsubpart().charAt(i + 1), 16));
		String [] results;
		try{
			results = this.thomson(routerESSID);
		}catch (Exception e) {
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_err_native)));
			return;
		}
		if ( isStopRequested() )
			return;
		for (int i = 0 ; i < results.length ; ++i  )
			pwList.add(results[i]);
		
		if(pwList.toArray().length == 0)
		{
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_errnomatches)));
			return;
		}
		handler.sendEmptyMessage(RESULTS_READY);
		return;
	}

}
