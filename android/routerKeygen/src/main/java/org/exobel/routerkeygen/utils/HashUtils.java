package org.exobel.routerkeygen.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Arrays;

public class HashUtils {
    private HashUtils() {
    }

    // Check RouterKeygen.dic file through md5
    public static boolean checkDicMD5(String dicFile, final byte[] expected) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            InputStream is = new FileInputStream(dicFile);
            try {
                DigestInputStream dis = new DigestInputStream(is, md);
                byte[] buffer = new byte[1024];
                while(dis.read(buffer, 0, buffer.length) != -1) ;
                dis.close();
            } finally {
                is.close();
            }
            final byte[] hash = md.digest();
            return Arrays.equals(hash, expected);
        } catch (Exception e) {
            return false;
        }

    }
}
