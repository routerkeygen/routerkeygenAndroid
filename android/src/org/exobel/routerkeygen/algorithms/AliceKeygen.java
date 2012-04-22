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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.exobel.routerkeygen.R;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;

public class AliceKeygen extends KeygenThread {


	public AliceKeygen(Handler h, Resources res) {
        super(h, res);
    }


    final private String preInitCharset =
			 "0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvWxyz0123";
	 
	 private byte specialSeq[/*32*/]= {
		0x64, (byte) 0xC6, (byte) 0xDD, (byte) 0xE3, 
		(byte) 0xE5, 0x79, (byte) 0xB6, (byte) 0xD9, 
		(byte) 0x86, (byte) 0x96, (byte) 0x8D, 0x34, 
		0x45, (byte) 0xD2, 0x3B, 0x15, 
		(byte) 0xCA, (byte) 0xAF, 0x12, (byte) 0x84, 
		0x02, (byte) 0xAC, 0x56, 0x00, 
		0x05, (byte) 0xCE, 0x20, 0x75, 
		(byte) 0x91, 0x3F, (byte) 0xDC, (byte) 0xE8};
	
	
	public void run() {

		if ( getRouter() == null)
			return;
		if ( getRouter().getSupportedAlice() == null )
		{
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_erralicenotsupported)));
			return;
		}
		if ( getRouter().getSupportedAlice().isEmpty() )
		{
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_erralicenotsupported)));
			return;
		}
		
		try {
			md = MessageDigest.getInstance("SHA256");
		} catch (NoSuchAlgorithmException e1) {
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_nosha256)));
			return;
		}
		for ( int j = 0 ; j <getRouter().getSupportedAlice().size() ; ++j )
		{/*For pre AGPF 4.5.0sx*/
			String serialStr = getRouter().getSupportedAlice().get(j).getSerial() + "X";
			int Q = getRouter().getSupportedAlice().get(j).getMagic()[0];
			int k = getRouter().getSupportedAlice().get(j).getMagic()[1] ;
			int serial = ( Integer.valueOf(getRouter().getSSIDsubpart()) - Q ) / k;
			String tmp = Integer.toString(serial);
			for (int i = 0; i < 7 - tmp.length(); i++){
				serialStr += "0";
			}
			serialStr += tmp;
			
			byte [] mac = new byte[6];
			String key = "";
			byte [] hash;		
			
			if (  getRouter().getMac().length() == 12 ) {
					
				
				for (int i = 0; i < 12; i += 2)
					mac[i / 2] = (byte) ((Character.digit(getRouter().getMac().charAt(i), 16) << 4)
							+ Character.digit(getRouter().getMac().charAt(i + 1), 16));
	
				md.reset();
				md.update(specialSeq);
				try {
					md.update(serialStr.getBytes("ASCII"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				md.update(mac);
				hash = md.digest();
				for ( int i = 0 ; i < 24 ; ++i )
				{
					key += preInitCharset.charAt(hash[i] & 0xFF);
				}
				if ( !pwList.contains(key)  ) 
					pwList.add(key);
			}
			
			/*For post AGPF 4.5.0sx*/
			String macEth = getRouter().getMac().substring(0,6);
			int extraNumber = 0;
			while ( extraNumber <= 9 )
			{
				String calc = Integer.toHexString(Integer.valueOf(
						extraNumber + getRouter().getSSIDsubpart()) ).toUpperCase();
				if ( macEth.charAt(5) == calc.charAt(0))
				{
					macEth += calc.substring(1);
					break;
				}
				extraNumber++;
			}
			if ( macEth.equals(getRouter().getMac().substring(0,6)) )
			{
				handler.sendEmptyMessage(RESULTS_READY);
				return;
			}
			
			
			
			for (int i = 0; i < 12; i += 2)
				mac[i / 2] = (byte) ((Character.digit(macEth.charAt(i), 16) << 4)
						+ Character.digit(macEth.charAt(i + 1), 16));
			md.reset();
			md.update(specialSeq);
			try {
				md.update(serialStr.getBytes("ASCII"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			md.update(mac);
			key = "";
			hash = md.digest();
			for ( int i = 0 ; i < 24 ; ++i )
				key += preInitCharset.charAt(hash[i] & 0xFF);
			if ( !pwList.contains(key)  ) 
				pwList.add(key);
		}
		handler.sendEmptyMessage(RESULTS_READY);
		return;
	}
}
