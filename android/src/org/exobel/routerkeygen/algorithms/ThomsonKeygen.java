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
import java.util.List;
import java.util.zip.ZipInputStream;

import org.exobel.routerkeygen.Preferences;
import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.StringUtils;

import android.os.Environment;

public class ThomsonKeygen extends Keygen {

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
	private String folderSelect;

	private MessageDigest md;
	final private String ssidIdentifier;
	private InputStream webdic;
	
	public ThomsonKeygen(String ssid, String mac, int level, String enc ) {
		super(ssid, mac, level, enc);
		this.cp = new byte[12];
		this.hash = new byte[19];
		this.table= new byte[1282];
		this.routerESSID = new byte[3];
		this.errorDict = false;
		this.ssidIdentifier = ssid.substring(ssid.length()-6);
	}

	@Override
	public List<String> getKeys() {
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e1) {
			setErrorCode(R.string.msg_nosha1);
			return null;
		}
		if ( ssidIdentifier.length() != 6 ) 
		{
			setErrorCode(R.string.msg_shortessid6);
			return null;
		}
		
		for (int i = 0; i < 6; i += 2)
			routerESSID[i / 2] = (byte) ((Character.digit(ssidIdentifier.charAt(i), 16) << 4)
					+ Character.digit(ssidIdentifier.charAt(i + 1), 16));

		
		if ( !thomson3g )
		{
			if (!localCalc() )
				return null;
		}
		else
		{
			if (!internetCalc())
				return null;
		}

		if( getResults().size() == 0) {
			setErrorCode(R.string.msg_errnomatches);
			return null;
		}
		return getResults();
	}
	private boolean internetCalc(){
		try{
			DataInputStream onlineFile = null;
			int lenght =0 ;
			URL url;
			ZipInputStream fis = new ZipInputStream(webdic);
			fis.getNextEntry();
			int check = 0 , ret = 0 ;
			while ( check != 1024 )/*ZipInputStream doens't seems to block.*/
			{
				ret = fis.read(table , check , 1024 - check);
				if ( ret == -1 )
				{
					setErrorCode(R.string.msg_err_webdic_table);
					errorDict = true;
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
                    setErrorCode(R.string.msg_err_webdic_table);
                    errorDict = true;
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
					setErrorCode(R.string.msg_err_webdic_table);
					errorDict = true;
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
			setErrorCode(R.string.msg_err_webdic_table);
			errorDict = true;
			return false;
		}
	}

	private boolean localCalc(){

		if ( !Environment.getExternalStorageState().equals("mounted")  && 
		     !Environment.getExternalStorageState().equals("mounted_ro")	)
		{
			setErrorCode(R.string.msg_nosdcard);
			errorDict = true;
			return false;
		}
		RandomAccessFile fis;
		try {
			File dictionay = getDictionaryFile();
			fis = new RandomAccessFile(dictionay, "r");
		} catch (FileNotFoundException e2) {
			setErrorCode(R.string.msg_dictnotfound);
			errorDict = true;
			return false;
		}
		int version = 0;
		try {
			if ( fis.read(table) == -1 )
			{
				setErrorCode(R.string.msg_errordict);
				errorDict = true;
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
				setErrorCode(R.string.msg_errordict);
				errorDict = true;
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
			}
			else
			{ /*Only for SSID starting with FFFF as we don't have a marker of the end.*/
					length = 2000;
					this.entry = new byte[length];
			}

			int bytesRead = 0;
			len = 0;
			while ( len < length ){
				bytesRead  = fis.read(entry,len, length-len);
				if ( bytesRead == -1 )
					break;
				len += bytesRead;
			}
			
			if ( len == -1 )
			{
				setErrorCode(R.string.msg_errordict);
				errorDict = true;
				return false;
			}
		} catch (IOException e1) {
			errorDict = true;
			setErrorCode(R.string.msg_errordict);
			return false;
		}
		if ( version > 4 )
		{
			setErrorCode(R.string.msg_errversion);
			errorDict = true;
			return false;
		}
		
		if ( version == 1 )
			firstDic();
		else if ( version == 2 )
			secondDic();
		else if ( version == 3 )
			return thirdDic();
		else if ( version == 4 )
			forthDic();
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
				setErrorCode(R.string.msg_err_native);
				return false;
			}catch (LinkageError e) {
				setErrorCode(R.string.err_misbuilt_apk);
				return false;
			}
			if ( isStopRequested() )
				return false;
			for (int i = 0 ; i < results.length ; ++i  )
				addPassword(results[i]);
			return true;
	}
	

	private void forthDic(){
		cp[0] = (byte) (char) 'C';
		cp[1] = (byte) (char) 'P';
		for (int offset = 0; offset < len ; offset += 3 )
		{
			for ( int i = 0; i <= 1 ; ++i  ){
				if ( isStopRequested() )
					return;
				sequenceNumber = i + (( (0xFF & entry[offset + 0]) << 16 ) | 
				( (0xFF & entry[offset + 1])  << 8 ) | (0xFF & entry[offset + 2]) )*2 ;
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
					addPassword(StringUtils.getHexString(hash).substring(0, 10).toUpperCase());
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
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
				addPassword(StringUtils.getHexString(hash).substring(0, 10).toUpperCase());
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
				addPassword(StringUtils.getHexString(hash).substring(0, 10).toUpperCase());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	public void setWebdic(InputStream webdic) {
		this.webdic = webdic;
	}

	
    public boolean isErrorDict() {
        return errorDict;
    }

    public void setFolder(String folder){
    	folderSelect = folder;
    }
    
    final private static byte[] charectbytes0 = {
        '3','3','3','3','3','3',
        '3','3','3','3','4','4',
        '4','4','4','4','4','4',
        '4','4','4','4','4','4',
        '4','5','5','5','5','5',
        '5','5','5','5','5','5',
        };
    
    final private static byte[] charectbytes1 = {
        '0','1','2','3','4','5',
        '6','7','8','9','1','2',
        '3','4','5','6','7','8',
        '9','A','B','C','D','E',
        'F','0','1','2','3','4',
        '5','6','7','8','9','A',
        };

}
