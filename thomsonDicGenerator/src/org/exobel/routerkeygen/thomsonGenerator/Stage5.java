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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;


public class Stage5 {
	public static void main(String[] args) {
    	System.out.println("Stage5");
		System.out.println("Creating Complex File Verification (cfv).");
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			InputStream is = new FileInputStream("RouterKeygen.dic");
			is = new DigestInputStream(is, md);
			byte []  buffer = new byte [16384] ; 
			while ( is.read ( buffer )  != -1 );
			
			byte[] digest = md.digest();
			is.close();

			FileOutputStream fos = new FileOutputStream("RouterKeygen.cfv");
			fos.write(new byte[]{0, 3});
			fos.write(digest);
			fos.close();
		}
		catch(Exception e)
		{
			System.out.println("Error!" + e.getMessage());
			return;
		}
		System.out.println("Done.");
	}

}
