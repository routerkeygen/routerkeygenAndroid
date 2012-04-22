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
package org.exobel.routerkeygen.thomsonGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Stage4 {
	
	/*
	 * Version 1 - Initial dictionary of 55.8 MB ( downloaded 740 times )
	 * Version 2 - Second version with 41.9 MB
	 * Version 3 - Third version with 28.0 MB
	 */
	static final byte [] version = { 0, 3};
	public static void main(String[] args) {
		FileInputStream fis;
		EntryTable entry = new EntryTable(4);
    	System.out.println("Stage4");
		System.out.println("Creating main table and assembling the final file.");
		long begin = System.currentTimeMillis();
		String fileName = "56.dat";
		int progress = 0;
		int c = 0;
		short firstByte;
		byte [] fileData = new  byte [300000];
		byte [] table = new byte[1280];
		RandomAccessFile webDicIndex = null;
		try {
			File webDic = new File("webdic.dic");
			webDic.createNewFile();
			webDicIndex = new RandomAccessFile(webDic, "rw");
			webDicIndex.setLength(1024 + 256 * 768);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//size of the table
		//1282 = 256 * ( 1 + 4) + 2
		// 1byte for header and 4 for address. 2bytes for dictionary version control
		int address = 1282;
		for(int a = 0; a < AlphabetCodes.charect.length; a++)
        {
            for(int b = 0; b < AlphabetCodes.charect.length; b++)
            {
           	fileName = AlphabetCodes.charect[a] + AlphabetCodes.charect[b] + ".dat";
			firstByte = (short) (( AlphabetCodes.charectCode[a] << 4 ) | AlphabetCodes.charectCode[b]);
            entry.addEntry(firstByte, address);
            File fileDat;
           	try {
           			fileDat = new File(fileName);
					fis = new FileInputStream(fileDat);
				} catch (FileNotFoundException e) {
		            System.out.println("Error!" + e);
					return;
				}
				int count = 0;
				
				try {
					fileDat.deleteOnExit();
					count = fis.read(fileData);
					fis.close();
				} catch (IOException e) {
					System.out.println("Error!" + e);
					return;
				}
				if ( count == -1 )
				{
					System.out.println("Error while reading " + fileName + "!");
					return;
				}
				address +=count;
			}
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("RouterKeygen.dic");
			fos.write(version);
			entry.toFile(table);
			fos.write(table);
			entry.toWebDic( table );
			webDicIndex.write(table , 0 , 1024);
			webDicIndex.close();
		} catch (Exception e) {
			System.out.println("Error!" + e.getMessage());
			return;
		}

		System.out.println("Initial table finished. Assembling in a single file!");
		for(int a = 0; a < AlphabetCodes.charect.length; a++)
        {
            for(int b = 0; b < AlphabetCodes.charect.length; b++ , c++)
            {
           	fileName = AlphabetCodes.charect[a] + AlphabetCodes.charect[b] + ".dat";
           	try {
					fis = new FileInputStream(fileName);
					int count = 0;
					count = fis.read(fileData);
					fis.close();
					if ( count == -1 )
					{
						System.out.println("Error while reading " + fileName + "!");
						return;
					}
					fos.write(fileData , 0 , count);
					progress = (c *100)>>8;
					System.out.println("File " + fileName + " processed " +
				           "  Total done: " + progress + "% " );
				} catch (IOException e) {
					System.out.println("Error!" + e);
					return;
				}
			}
		}
		try {
			fos.close();
		} catch (IOException e) {
			System.out.println("Error!" + e);
			return;
		}
		long time = System.currentTimeMillis() - begin;
        System.out.println("Done .. 100%! It took " + time + " miliseconds.");
	}

}
