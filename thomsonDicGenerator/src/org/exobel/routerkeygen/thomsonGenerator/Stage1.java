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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exobel.routerkeygen.thomsonGenerator;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Stage1 {
	static MessageDigest md;
    public static void main(String[] args)
    {
    	FileOutputManager files = new FileOutputManager();
    	files.initAllFiles();
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }

        long begin = System.currentTimeMillis();
        byte[] cp = new byte[12];
        byte[] hash = new byte[19];
        byte firstByte ;
        int sequenceNumber = 0;
        byte [] ret = new byte [5];
    	cp[0] = (byte) (char)'C';
    	cp[1] = (byte) (char)'P';
        cp[2] = (byte) (char)'0';
    	System.out.println("Stage1");
		System.out.println("Calculating possibles ESSID's.");

        int offset = 0;
        for(int w = 1; w <= 52; w++)
        {
            cp[4] = (byte) Character.forDigit((w / 10), 10);
            cp[5] = (byte) Character.forDigit((w % 10), 10);
            System.out.println("Done .. " + 100*(w-1)/52 + "%");

            if (  ((w-1)%3) == 0 )
            	sequenceNumber = 0;
	        for(int y = 4; y < 13; y++)
	        {
	            cp[3] = (byte) Character.forDigit((y % 10), 10);
                for(int a = 0; a < AlphabetCodes.charectbytes.length; a++)
                {
                    cp[6] = AlphabetCodes.charectbytes[a][0];
                    cp[7] = AlphabetCodes.charectbytes[a][1];
                    for(int b = 0; b < AlphabetCodes.charectbytes.length; b++)
                    {
                        cp[8] = AlphabetCodes.charectbytes[b][0];
                        cp[9] = AlphabetCodes.charectbytes[b][1];
                        for(int c = 0; c < AlphabetCodes.charectbytes.length ; c++)
                        {
                            offset += 3;
                            cp[10] = AlphabetCodes.charectbytes[c][0];
                            cp[11] = AlphabetCodes.charectbytes[c][1];
                            md.reset();
                            md.update(cp);
                            hash = md.digest();
                            firstByte = hash[17];
                			ret[0] = hash[18];
                			ret[1] = hash[19];
                			ret[2] = (byte) ( (0xFF0000 & sequenceNumber) >> 16) ;
                			ret[3] = (byte) ( (0xFF00 & sequenceNumber) >> 8) ;
                			ret[4] =(byte) (0xFF & sequenceNumber);
                			sequenceNumber++;
                			try {
								files.sendFile(AlphabetCodes.getHexString(firstByte)+".dat", ret , 5);
							} catch (UnsupportedEncodingException e) {}
                        }
                    }
                }
            }
        }     
        files.close();
        long time = System.currentTimeMillis() - begin;
        System.out.println("Done .. 100%! It took " + time + " miliseconds.");
    }

}