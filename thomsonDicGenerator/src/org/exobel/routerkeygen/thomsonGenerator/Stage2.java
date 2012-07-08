package org.exobel.routerkeygen.thomsonGenerator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Stage2 {
	public static void main(String[] args){
		
		FileInputStream fis;
		FileOutputManager files = new FileOutputManager();
		String file;
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
				int count = 0;
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
				while ( it.hasNext() )
						files.sendFile( file, it.next().toFile() , 5);
				entries.clear();
				//System.out.println(it.next());				
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
			if ( ( this.hash[0] == o.hash[0] &&  this.hash[1] == o.hash[1] ) ||
					( this.hash[0] > o.hash[0] ))
				return 1;
			else
				if ( this.hash[0] < o.hash[0] )
					return -1;
				else
					if ( this.hash[1] >= o.hash[1] )
						return 1;
					else
						if ( this.hash[1] < o.hash[1] )
							return -1;
			return 0;
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