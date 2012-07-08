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
