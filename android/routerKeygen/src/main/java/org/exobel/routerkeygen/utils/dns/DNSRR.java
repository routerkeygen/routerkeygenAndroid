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

public abstract class DNSRR {
    private String rrName;
    private int rrType, rrClass;
    private long rrTTL, rrCreated;

    void init(String name, int type, int clas, long ttl, DNSInputStream dnsIn) throws IOException {
        rrName = name;
        rrType = type;
        rrClass = clas;
        rrTTL = ttl;
        rrCreated = System.currentTimeMillis();
        decode(dnsIn);
    }

    protected abstract void decode(DNSInputStream dnsIn) throws IOException;

    String getRRName() {
        return rrName;
    }

    public int getRRType() {
        return rrType;
    }

    public int getRRClass() {
        return rrClass;
    }

    public long getRRTTL() {
        return rrTTL;
    }

    public boolean isValid() {
        return rrTTL * 1000 > System.currentTimeMillis() - rrCreated;
    }
}
