/*
 * Java Network Programming, Second Edition
 * Merlin Hughes, Michael Shoffner, Derek Hamner
 * Manning Publications Company; ISBN 188477749X
 *
 * http://nitric.com/jnp/
 *
 * Copyright (c) 1997-1999 Merlin Hughes, Michael Shoffner, Derek Hamner;
 * all rights reserved; see license.txt for details.
 */

package org.exobel.routerkeygen.utils.dns;

import java.io.*;
import java.net.*;

public class Address extends DNSRR {
    private final int[] ipAddress = new int[4];

    protected void decode(DNSInputStream dnsIn) throws IOException {
        for (int i = 0; i < 4; ++i)
            ipAddress[i] = dnsIn.readByte();
    }

    public byte[] getAddress() {
        byte[] ip = new byte[4];
        for (int j = 0; j < 4; ++j)
            ip[j] = (byte) ipAddress[j];
        return ip;
    }

    public InetAddress getInetAddress() throws UnknownHostException {
        return InetAddress.getByName(toByteString());
    }

    private String toByteString() {
        return ipAddress[0] + "." + ipAddress[1] + "." +
                ipAddress[2] + "." + ipAddress[3];
    }

    public String toString() {
        return getRRName() + "\tinternet address = " + toByteString();
    }
}
