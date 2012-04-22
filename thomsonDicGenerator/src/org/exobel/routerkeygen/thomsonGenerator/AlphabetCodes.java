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

import java.io.UnsupportedEncodingException;

public class AlphabetCodes {
	

	  static final byte[] HEX_CHAR_TABLE = {
	    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
	    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
	    (byte)'8', (byte)'9', (byte)'a', (byte)'b',
	    (byte)'c', (byte)'d', (byte)'e', (byte)'f'
	  };

	  public static String getHexString(byte[] raw)
	    throws UnsupportedEncodingException
	  {
	    byte[] hex = new byte[2 * raw.length];
	    int index = 0;

	    for (byte b : raw) {
	      int v = b & 0xFF;
	      hex[index++] = HEX_CHAR_TABLE[v >>> 4];
	      hex[index++] = HEX_CHAR_TABLE[v & 0xF];
	    }
	    return new String(hex, "ASCII");
	  }

	  public static String getHexString(byte raw)
	    throws UnsupportedEncodingException
	  {
	    byte[] hex = new byte[2];
	      int v = raw & 0xFF;
	      hex[0] = HEX_CHAR_TABLE[v >>> 4];
	      hex[1] = HEX_CHAR_TABLE[v & 0xF];
	    return new String(hex, "ASCII");
	  }
	  
	  public static String getHexString(short raw)
	    throws UnsupportedEncodingException
	  {
	    byte[] hex = new byte[2];
	      int v = raw & 0xFF;
	      hex[0] = HEX_CHAR_TABLE[v >>> 4];
	      hex[1] = HEX_CHAR_TABLE[v & 0xF];
	    return new String(hex, "ASCII");
	  }
	  

	  public static String getHexString(short[] raw)
	    throws UnsupportedEncodingException
	  {
	    byte[] hex = new byte[2 * raw.length];
	    int index = 0;

	    for (short b : raw) {
	      int v = b & 0xFF;
	      hex[index++] = HEX_CHAR_TABLE[v >>> 4];
	      hex[index++] = HEX_CHAR_TABLE[v & 0xF];
	    }
	    return new String(hex, "ASCII");
	  }

	static final String charect[] = {
		"0", "1", "2", "3",
		"4", "5", "6", "7",
		"8", "9", "a", "b",
		"c", "d", "e", "f", 
		};
	
	static final int charectCode[] = {
		0, 1, 2, 3,
		4, 5, 6, 7,
		8, 9, 10, 11,
		12, 13, 14, 15, 
		};

    static final byte[][] charectbytes = {
	        new byte[]{ '3', '0'},
	        new byte[]{ '3', '1'},
	        new byte[]{ '3', '2'},
	        new byte[]{ '3', '3'},
	        new byte[]{ '3', '4'},
	        new byte[]{ '3', '5'},
	        new byte[]{ '3', '6'},
	        new byte[]{ '3', '7'},
	        new byte[]{ '3', '8'},
	        new byte[]{ '3', '9'},
            new byte[]{ '4', '1'},
            new byte[]{ '4', '2'},
            new byte[]{ '4', '3'},
            new byte[]{ '4', '4'},
            new byte[]{ '4', '5'},
            new byte[]{ '4', '6'},
            new byte[]{ '4', '7'},
            new byte[]{ '4', '8'},
            new byte[]{ '4', '9'},
            new byte[]{ '4', 'A'},
            new byte[]{ '4', 'B'},
            new byte[]{ '4', 'C'},
            new byte[]{ '4', 'D'},
            new byte[]{ '4', 'E'},
            new byte[]{ '4', 'F'},
            new byte[]{ '5', '0'},
            new byte[]{ '5', '1'},
            new byte[]{ '5', '2'},
            new byte[]{ '5', '3'},
            new byte[]{ '5', '4'},
            new byte[]{ '5', '5'},
            new byte[]{ '5', '6'},
            new byte[]{ '5', '7'},
            new byte[]{ '5', '8'},
            new byte[]{ '5', '9'},
            new byte[]{ '5', 'A'},
        };
}