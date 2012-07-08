/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exobel.routerkeygen.thomsonGenerator;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.management.RuntimeErrorException;

public class Stage1 {
	static MessageDigest md;

	public static void main(String[] args) {
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
		byte firstByte;
		int sequenceNumber = 0;
		byte[] ret = new byte[5];
		cp[0] = (byte) (char) 'C';
		cp[1] = (byte) (char) 'P';
		cp[2] = (byte) (char) '0';
		System.out.println("Stage1");
		System.out.println("Calculating possibles ESSID's.");

		for (int y = 4; y < 13; y++) {

			System.out.println("Done .. " + 100 * (y - 4) / (13 - 4) + "%");
			cp[2] = (byte) Character.forDigit((y / 10), 10);
			cp[3] = (byte) Character.forDigit((y % 10), 10);
			for (int w = 1; w <= 52; w++) {
				cp[4] = (byte) Character.forDigit((w / 10), 10);
				cp[5] = (byte) Character.forDigit((w % 10), 10);
				for (int a = 0; a < AlphabetCodes.charectbytes.length; a++) {
					cp[6] = AlphabetCodes.charectbytes[a][0];
					cp[7] = AlphabetCodes.charectbytes[a][1];
					for (int b = 0; b < AlphabetCodes.charectbytes.length; b++) {
						cp[8] = AlphabetCodes.charectbytes[b][0];
						cp[9] = AlphabetCodes.charectbytes[b][1];
						for (int c = 0; c < AlphabetCodes.charectbytes.length; c++) {
							cp[10] = AlphabetCodes.charectbytes[c][0];
							cp[11] = AlphabetCodes.charectbytes[c][1];
							md.reset();
							md.update(cp);
							hash = md.digest();
							firstByte = hash[17];
							ret[0] = hash[18];
							ret[1] = hash[19];
							int sequenceNumber_tmp = sequenceNumber/2;//to make sure it only takes 3 bytes
							if ( ( 0xFF000000 &sequenceNumber_tmp ) != 0)
								throw new RuntimeErrorException(null, "Sequence Number cannot have more than 3 bytes");
							ret[2] = (byte) ((0xFF0000 & sequenceNumber_tmp) >> 16);
							ret[3] = (byte) ((0xFF00 & sequenceNumber_tmp) >> 8);
							ret[4] = (byte) (0xFF & sequenceNumber_tmp);
							sequenceNumber++;
							try {
								files.sendFile(AlphabetCodes.getHexString(firstByte)+ ".dat", ret, 5);
							} catch (UnsupportedEncodingException e) {
							}
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