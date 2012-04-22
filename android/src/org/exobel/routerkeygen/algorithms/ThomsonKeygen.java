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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipInputStream;

import org.exobel.routerkeygen.Preferences;
import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.StringUtils;

import android.content.res.Resources;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class ThomsonKeygen extends KeygenThread {

	byte[] cp;
	byte[] hash;
	byte[] entry;
	byte[] table;
	int a, b, c;
	int year;
	int week;
	int sequenceNumber;
	byte[] routerESSID;
	boolean thomson3g;
	private boolean errorDict;
	int len = 0;
	String folderSelect;

	public ThomsonKeygen(Handler h, Resources res , String folder , boolean thomson3g ) {
		super(h, res);
		this.folderSelect = folder;
		this.cp = new byte[12];
		this.hash = new byte[19];
		this.table= new byte[1282];
		this.routerESSID = new byte[3];
		this.thomson3g = thomson3g;
		this.setErrorDict(false);
	}

	public void run(){
		if ( getRouter() == null)
			return;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e1) {
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_nosha1)));
			return;
		}
		if ( getRouter().getSSIDsubpart().length() != 6 ) 
		{
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_shortessid6)));
			return;
		}
		
		for (int i = 0; i < 6; i += 2)
			routerESSID[i / 2] = (byte) ((Character.digit(getRouter().getSSIDsubpart().charAt(i), 16) << 4)
					+ Character.digit(getRouter().getSSIDsubpart().charAt(i + 1), 16));

		
		if ( !thomson3g )
		{
			if (!localCalc() )
				return;
		}
		else
		{
			if (!internetCalc())
				return;
		}

		if(pwList.toArray().length == 0)
		{
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_errnomatches)));
			return;
		}
		handler.sendEmptyMessage(RESULTS_READY);
		return;
	}
	private boolean internetCalc(){
		try{
			DataInputStream onlineFile = null;
			int lenght =0 ;
			URL url;
			InputStream file = resources.openRawResource(R.raw.webdic);
			ZipInputStream fis = new ZipInputStream(file);
			fis.getNextEntry();
			int check = 0 , ret = 0 ;
			while ( check != 1024 )/*ZipInputStream doens't seems to block.*/
			{
				ret = fis.read(table , check , 1024 - check);
				if ( ret == -1 )
				{
					handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
							resources.getString(R.string.msg_err_webdic_table)));
					setErrorDict(true);
					return false;
				}
				else
					check += ret;
			}
			int totalOffset = 0;
			int offset = 0;
			int lastLength = 0 ;
			int i = ( 0xFF &routerESSID[0] )*4;
			offset =( (0xFF & table[i]) << 24 ) | ( (0xFF & table[i + 1])  << 16 ) |
					( (0xFF & table[i + 2])  << 8 ) | (0xFF & table[i + 3]);
			if ( i != 1020 ) // routerESSID[0] != 0xFF   ( 255*4 == 1020 )
				lastLength = ( (0xFF & table[i + 4]) << 24 ) | ( (0xFF & table[i + 5])  << 16 ) |
					( (0xFF & table[i + 6])  << 8 ) | (0xFF & table[i + 7]);
			totalOffset += offset;
	        long checkLong = 0 , retLong ;
            while ( checkLong != (i/4)*768 )/*ZipInputStream doens't seems to block.*/
            {
                retLong = fis.skip((i/4)*768 - checkLong);
                if ( retLong == -1 )
                {
                    handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
                            resources.getString(R.string.msg_err_webdic_table)));
                    setErrorDict(true);
                    return false;
                }
                else
                    checkLong += retLong;
            }
			check = 0 ;
			while ( check != 768 )
			{
				ret = fis.read(table , check , 768 - check);
				if ( ret == -1 )
				{
					handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
							resources.getString(R.string.msg_err_webdic_table)));
					setErrorDict(true);
					return false;
				}
				else
					check += ret;
			}
			i = ( 0xFF &routerESSID[1] )*3;
			offset =( (0xFF & table[i])  << 16 ) |
					( (0xFF & table[i + 1 ])  << 8 ) | (0xFF & table[i + 2]);
			/*There's no check here because humans are lazy people and because it doesn't matter*/
			lenght =  ( (0xFF & table[i + 3])  << 16 ) |
					( (0xFF & table[i + 4])  << 8 ) | (0xFF & table[i + 5]);
			totalOffset += offset;
			lenght -= offset;
			if ( ( lastLength != 0 ) && ( (0xFF & routerESSID[1] ) == 0xFF ) )
			{
				/*Only for SSID starting with XXFF. We use the next item on the main table
			 	to know the length of the sector we are looking for. */
				lastLength -= totalOffset;
				lenght = lastLength;
			}
			if ( ( (0xFF & routerESSID[0] ) == 0xFF ) && ( (0xFF & routerESSID[1] ) == 0xFF  ) )
			{
			 /*Only for SSID starting with FFFF as we don't have a marker of the end.*/
					lenght = 2000;
			}
			url = new URL(Preferences.PUB_DOWNLOAD);
			URLConnection con= url.openConnection();
			con.setRequestProperty("Range", "bytes="  + totalOffset + "-");
			onlineFile = new DataInputStream(con.getInputStream());
			len = 0;
			this.entry = new byte[lenght];
			if ( ( len = onlineFile.read(this.entry , 0 , lenght ) ) != -1 ){
				lenght = len;;
			}
			
			onlineFile.close();
			fis.close();
			return thirdDic();
		} catch ( IOException e) {
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_err_webdic_table)));
			setErrorDict(true);
			return false;
		}
	}

	private boolean localCalc(){

		if ( !Environment.getExternalStorageState().equals("mounted")  && 
		     !Environment.getExternalStorageState().equals("mounted_ro")	)
		{
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_nosdcard)));
			setErrorDict(true);
			return false;
		}
		RandomAccessFile fis;
		try {
			File dictionay = getDictionaryFile();
			fis = new RandomAccessFile(dictionay, "r");
		} catch (FileNotFoundException e2) {
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_dictnotfound)));
			setErrorDict(true);
			return false;
		}
		int version = 0;
		try {
			if ( fis.read(table) == -1 )
			{
				handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
						resources.getString(R.string.msg_errordict)));
				setErrorDict(true);
				return false;
			}
			version = table[0] << 8 | table[1];
			int totalOffset = 0;
			int offset = 0;
			int lastLength = 0 , length = 0;
			if ( table[( 0xFF &routerESSID[0] )*5 + 2 ] == routerESSID[0] )
			{
				int i = ( 0xFF &routerESSID[0] )*5 + 2;
				offset =( (0xFF & table[i + 1]) << 24 ) | ( (0xFF & table[i + 2])  << 16 ) |
						( (0xFF & table[i + 3])  << 8 ) | (0xFF & table[i + 4]);
				if ( (0xFF & table[i]) != 0xFF )
					lastLength = ( (0xFF & table[i + 6]) << 24 ) | ( (0xFF & table[i + 7])  << 16 ) |
						( (0xFF & table[i + 8])  << 8 ) | (0xFF & table[i + 9]);
			}
			totalOffset += offset;
			fis.seek(totalOffset);
			if ( fis.read(table,0,1024) == -1 )
			{
				handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
						resources.getString(R.string.msg_errordict)));
				setErrorDict(true);
				return false;
			}	
			if ( table[( 0xFF &routerESSID[1] )*4] == routerESSID[1] )
			{
				int i = ( 0xFF &routerESSID[1] )*4;
				offset =( (0xFF & table[i + 1])  << 16 ) |
						( (0xFF & table[i + 2])  << 8 ) | (0xFF & table[i + 3]);
				length =  ( (0xFF & table[i + 5])  << 16 ) |
						( (0xFF & table[i + 6])  << 8 ) | (0xFF & table[i + 7]);
				
			}
			totalOffset += offset;
			length -= offset;
			if ( ( lastLength != 0 ) && ( (0xFF & routerESSID[1] ) == 0xFF ) )
			{
				/*Only for SSID starting with XXFF. We use the next item on the main table
			 	to know the length of the sector we are looking for. */
				lastLength -= totalOffset;
				length = lastLength;
			}
			fis.seek(totalOffset );
			if ( ( (0xFF & routerESSID[0] ) != 0xFF ) || ( (0xFF & routerESSID[1] ) != 0xFF  ) )
			{
				this.entry = new byte[length];
				len = fis.read(entry,0, length);
			}
			else
			{ /*Only for SSID starting with FFFF as we don't have a marker of the end.*/
					this.entry = new byte[2000];
					len = fis.read( entry );
			}
			if ( len == -1 )
			{
				handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
						resources.getString(R.string.msg_errordict)));
				setErrorDict(true);
				return false;
			}
		} catch (IOException e1) {
			setErrorDict(true);
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_errordict)));
			return false;
		}
		if ( version > 3 )
		{
			handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
					resources.getString(R.string.msg_errversion)));
			setErrorDict(true);
			return false;
		}
		
		if ( version == 1 )
			firstDic();
		else if ( version == 2 )
			secondDic();
		else if ( version == 3 )
			return thirdDic();
		
		return true;
	}
	

	private File getDictionaryFile() throws FileNotFoundException {
		String firstName = folderSelect + File.separator + "RouterKeygen.dic";
		String secondName = folderSelect + File.separator + "RKDictionary.dic";
		try{
			File dic = new File(firstName);
			if ( dic.exists() )
				return dic;
			dic = new File(secondName);
			if ( dic.exists() )
				return dic;
			else
				throw new FileNotFoundException("Permissions Error");
		} catch(SecurityException e  ){
			e.printStackTrace();
			throw new FileNotFoundException("Permissions Error");
		}
	}


	static {
		System.loadLibrary("thomson");
    }
	
	private native String [] thirdDicNative( byte [] essid  ,
												byte [] entry , int size);

	//This has been implemented natively for instant resolution!
	private boolean thirdDic(){
		String [] results;
			try{
				results = 	this.thirdDicNative(routerESSID , entry , entry.length);
			}catch (Exception e) {
				handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
						resources.getString(R.string.msg_err_native)));
				return false;
			}catch (LinkageError e) {
				handler.sendMessage(Message.obtain(handler, ERROR_MSG , 
						resources.getString(R.string.err_misbuilt_apk)));
				return false;
			}
			if ( isStopRequested() )
				return false;
			for (int i = 0 ; i < results.length ; ++i  )
				pwList.add(results[i]);
			return true;
	}
	
	
	private void secondDic(){
		cp[0] = (byte) (char) 'C';
		cp[1] = (byte) (char) 'P';
		for (int offset = 0; offset < len ; offset += 3 )
		{
			if ( isStopRequested() )
				return;
			sequenceNumber = ( (0xFF & entry[offset + 0]) << 16 ) | 
			( (0xFF & entry[offset + 1])  << 8 ) | (0xFF & entry[offset + 2]) ;
			c = sequenceNumber % 36;
			b = sequenceNumber/36 % 36;
			a = sequenceNumber/(36*36) % 36;
			year = sequenceNumber / ( 36*36*36*52 ) + 4 ;
			week = ( sequenceNumber / ( 36*36*36 ) ) % 52 + 1 ;				
			cp[2] = (byte) Character.forDigit((year / 10), 10);
			cp[3] = (byte) Character.forDigit((year % 10), 10);
			cp[4] = (byte) Character.forDigit((week / 10), 10);
			cp[5] = (byte) Character.forDigit((week % 10), 10);
			cp[6] = charectbytes0[a];
			cp[7] = charectbytes1[a];
			cp[8] = charectbytes0[b];
			cp[9] = charectbytes1[b];
			cp[10] = charectbytes0[c];
			cp[11] = charectbytes1[c];
			md.reset();
			md.update(cp);
			hash = md.digest();
			if ( hash[19] != routerESSID[2])
				continue;
			if ( hash[18] != routerESSID[1])
				continue;
			if ( hash[17] != routerESSID[0])
				continue;
			
			try {
				pwList.add(StringUtils.getHexString(hash).substring(0, 10).toUpperCase());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	private void firstDic(){
		cp[0] = (byte) (char) 'C';
		cp[1] = (byte) (char) 'P';
		for (int offset = 0; offset < len ; offset += 4 )
		{
			if ( isStopRequested() )
				return;

			if ( entry[offset] != routerESSID[2])
				continue;
			sequenceNumber = ( (0xFF & entry[offset + 1]) << 16 ) | 
			( (0xFF & entry[offset + 2])  << 8 ) | (0xFF & entry[offset + 3]) ;
			c = sequenceNumber % 36;
			b = sequenceNumber/36 % 36;
			a = sequenceNumber/(36*36) % 36;
			year = sequenceNumber / ( 36*36*36*52 ) + 4 ;
			week = ( sequenceNumber / ( 36*36*36 ) ) % 52 + 1 ;				
			cp[2] = (byte) Character.forDigit((year / 10), 10);
			cp[3] = (byte) Character.forDigit((year % 10), 10);
			cp[4] = (byte) Character.forDigit((week / 10), 10);
			cp[5] = (byte) Character.forDigit((week % 10), 10);
			cp[6] = charectbytes0[a];
			cp[7] = charectbytes1[a];
			cp[8] = charectbytes0[b];
			cp[9] = charectbytes1[b];
			cp[10] = charectbytes0[c];
			cp[11] = charectbytes1[c];
			md.reset();
			md.update(cp);
			hash = md.digest();
			
			try {
				pwList.add(StringUtils.getHexString(hash).substring(0, 10).toUpperCase());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
    public boolean isErrorDict() {
        return errorDict;
    }

    public void setErrorDict(boolean errorDict) {
        this.errorDict = errorDict;
    }
    static byte[] charectbytes0 = {
        '3','3','3','3','3','3',
        '3','3','3','3','4','4',
        '4','4','4','4','4','4',
        '4','4','4','4','4','4',
        '4','5','5','5','5','5',
        '5','5','5','5','5','5',
        };
    
    static byte[] charectbytes1 = {
        '0','1','2','3','4','5',
        '6','7','8','9','1','2',
        '3','4','5','6','7','8',
        '9','A','B','C','D','E',
        'F','0','1','2','3','4',
        '5','6','7','8','9','A',
        };
}
