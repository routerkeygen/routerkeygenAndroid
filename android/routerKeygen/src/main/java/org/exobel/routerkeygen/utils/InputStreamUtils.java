package org.exobel.routerkeygen.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;


public class InputStreamUtils {

    public static boolean readFromInput(byte[] buf, int length,
                                        InputStream input) throws IOException {
        int check = 0;
        while (check != length) {
            int ret = input.read(buf, check, length - check);
            if (ret == -1) {
                return false;
            } else
                check += ret;
        }
        return true;
    }


    public static boolean readFromInput(byte[] buf, int length,
                                        RandomAccessFile input) throws IOException {
        int check = 0;
        while (check != length) {
            int ret = input.read(buf, check, length - check);
            if (ret == -1) {
                return false;
            } else
                check += ret;
        }
        return true;
    }
}
