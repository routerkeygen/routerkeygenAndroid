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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class Stage2 {
	public static void main(String[] args){
		FileInputStream fis;
		FileOutputManager files = new FileOutputManager();
		String file = "56.dat";
    	System.out.println("Stage2");
		System.out.println("Ordering Entries in the Dictionary");
		long begin = System.currentTimeMillis();
		int progress = 0;
		int c = 0;
		byte [] fileData = new  byte [3000000];
		Set<DictEntry> entries = new TreeSet<DictEntry>();
       for(int a = 0; a < AlphabetCodes.charect.length; a++)
        {
            for(int b = 0; b < AlphabetCodes.charect.length; b++, c++)
            { 
           	    file = AlphabetCodes.charect[a] + AlphabetCodes.charect[b] + ".dat";
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException e) {
		            System.out.println("Error!" + e);
					return;
				}
				byte [] entry = new  byte [5];
				long count = 0;
				try {
					count = fis.read(fileData);
					fis.close();
				} catch (IOException e) {
					System.out.println("Error!" + e);
					return;
				}
				files.addFile(file);
				int offset = 0;
				while ( offset < count)
				{
					entry[0] = fileData[offset + 0];
					entry[1] = fileData[offset + 1];
					entry[2] = fileData[offset + 2];
					entry[3] = fileData[offset + 3];
					entry[4] = fileData[offset + 4];
					entries.add(new DictEntry(entry));
					offset += 5;
				}
				Iterator<DictEntry> it = entries.iterator();
				DictEntry dict_old = it.next(), dict_now ,tmp;
				Stack<DictEntry> pot = new Stack<DictEntry>();
				pot.push(dict_old);
				int aux1, aux2;
				while ( it.hasNext() ){
					dict_now = it.next();
					if ( dict_old.hash[0] == dict_now.hash[0] )
					{
						pot.push(dict_now);
					}
					else
					{
						aux1 = pot.peek().number;	
						tmp = pot.pop();
						files.sendFile( file, tmp.toFile() , 5);
						while ( !pot.empty() )
						{
							tmp = pot.pop();
							aux2 = tmp.number;
							tmp.number -= aux1;
							if ( tmp.number > 0xFFFFFF ){
								System.out.println("OMG");
								return;
							}
							aux1 = aux2;
							files.sendFile( file, tmp.toFile() , 5);
						}
						pot.push(dict_now);
					}
					dict_old = dict_now;
				}
				aux1 = pot.peek().number;	
				tmp = pot.pop();
				files.sendFile( file, tmp.toFile() , 5);
				while ( !pot.empty())
				{
					tmp = pot.pop();
					aux2 = tmp.number;
					tmp.number -= aux1;
					if ( tmp.number > 0xFFFFFF ){
						System.out.println("OMG");
						return;
					}
					aux1 = aux2;
					files.sendFile( file, tmp.toFile() , 5);
				}
				
				
				entries.clear();
				count /= 5;
				progress = (c *100)>>8;
				System.out.println("Counted " + count + " entries in " + file +
						           "  Total done: " + progress + "%");
            }
        }
        files.close();
        long time = System.currentTimeMillis() - begin;
        System.out.println("Done .. 100%! It took " + time + " miliseconds.");
	}
	private static class DictEntry implements Comparable<DictEntry>{
		short [] hash;
		int number;
		
		public DictEntry(byte [] entry ){
			hash = new short [2];
			hash[0] = (short) (0xFF & entry[0]);
			hash[1] = (short) (0xFF & entry[1]);
			number = ( (0xFF & entry[2]) << 16 ) | 
					 ( (0xFF & entry[3])  << 8 ) |
					   (0xFF & entry[4]) ;

		}

		@Override
		public int compareTo(DictEntry o) {
			if ( this.hash[0] > o.hash[0] )
				return 1;
			if ( this.hash[0] == o.hash[0] && this.number < o.number )
				return 1;
			return -1;
		}
		
		public String toString(){
			try {
				return AlphabetCodes.getHexString(hash) + " " + number;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return "null";	
		}
		
		public byte [] toFile(){
			byte [] entry = new  byte [5];
			entry[0] = (byte) (0xFF & hash[0]);
			entry[1] = (byte) (0xFF & hash[1]);
			entry[2] = (byte) ( (0xFF0000 & number) >> 16) ;
			entry[3] = (byte) ( (0xFF00 & number) >> 8) ;
			entry[4] =(byte) (0xFF & number);
			return entry;
		}
	}
}
