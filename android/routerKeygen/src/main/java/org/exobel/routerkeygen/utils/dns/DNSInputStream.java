package org.exobel.routerkeygen.utils.dns;

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

import java.io.*;

public class DNSInputStream extends ByteArrayInputStream {
    protected DataInputStream dataIn;

    public DNSInputStream(byte[] data, int off, int len) {
        super(data, off, len);
        dataIn = new DataInputStream(this);
    }

    public int readByte() throws IOException {
        return dataIn.readUnsignedByte();
    }

    public int readShort() throws IOException {
        return dataIn.readUnsignedShort();
    }

    public long readInt() throws IOException {
        return dataIn.readInt() & 0xffffffffL;
    }

    public String readString() throws IOException {
        int len;
        try {
            len = readByte();
        } catch (EOFException e) {
            len = 0;
        }

        if (len == 0) {
            return "";
        } else {
            byte[] buffer = new byte[len];
            dataIn.readFully(buffer);
            return new String(buffer, "latin1");
        }
    }

    public String readDomainName() throws IOException {
        if (pos >= count)
            throw new EOFException("EOF reading domain name");
        if ((buf[pos] & 0xc0) == 0) {
            String label = readString();
            if (label.length() > 0) {
                String tail = readDomainName();
                if (tail.length() > 0)
                    label = label + '.' + tail;
            }
            return label;
        } else {
            if ((buf[pos] & 0xc0) != 0xc0)
                throw new IOException("Invalid domain name compression offset");
            int offset = readShort() & 0x3fff;
            DNSInputStream dnsIn =
                    new DNSInputStream(buf, offset, buf.length - offset);
            return dnsIn.readDomainName();
        }
    }

    public DNSRR readRR() throws IOException {
        String rrName = readDomainName();
        int rrType = readShort();
        int rrClass = readShort();
        long rrTTL = readInt();
        int rrDataLen = readShort();
        DNSInputStream rrDNSIn = new DNSInputStream(buf, pos, rrDataLen);
        pos += rrDataLen;
        try {
            String myName = getClass().getName();
            int periodIndex = myName.lastIndexOf('.');
            String myPackage = myName.substring(0, 1 + periodIndex);

            Class<?> theClass = Class.forName
                    (myPackage + DNS.typeName(rrType));
            DNSRR rr = (DNSRR) theClass.newInstance();
            if (rrType != DNS.TYPE_TXT)
                rr.init(rrName, rrType, rrClass, rrTTL, rrDNSIn);
            return rr;
        } catch (ClassNotFoundException ex) {
            throw new IOException("Unknown DNSRR (type " +
                    DNS.typeName(rrType) + " (" + rrType + "))");
        } catch (IllegalAccessException ex) {
            throw new IOException("Access error creating DNSRR (type " +
                    DNS.typeName(rrType) + ')');
        } catch (InstantiationException ex) {
            throw new IOException("Instantiation error creating DNSRR (type " +
                    DNS.typeName(rrType) + ')');
        }
    }
}
