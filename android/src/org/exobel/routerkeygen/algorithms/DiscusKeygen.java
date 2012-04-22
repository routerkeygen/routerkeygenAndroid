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


import android.content.res.Resources;
import android.os.Handler;

public class DiscusKeygen extends KeygenThread {

	public DiscusKeygen(Handler h, Resources res) {
		super(h, res);
	}

	static final int essidConst = 0xD0EC31;

	public void run(){
		int routerEssid = Integer.parseInt( getRouter().getSSIDsubpart() , 16);
		int result  = ( routerEssid - essidConst )>>2;
		pwList.add("YW0" + Integer.toString(result));
		handler.sendEmptyMessage(RESULTS_READY);
		return;
		
	}

}
