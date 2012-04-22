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

/*
 * This is not actual an algorithm
 * as the key is calculated from the MAC 
 * address adding a '2' as the first character
 */
public class InfostradaKeygen extends KeygenThread {

	public InfostradaKeygen(Handler h, Resources res) {
		super(h, res);
	}
	
	public void run(){
		if ( getRouter() == null)
			return;
		if ( getRouter().getMac().length() != 12 ) 
		{
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_errpirelli)));
			return;
		}
		pwList.add("2"+getRouter().getMac().toUpperCase());
		handler.sendEmptyMessage(RESULTS_READY);
		return;
	}
	
}
