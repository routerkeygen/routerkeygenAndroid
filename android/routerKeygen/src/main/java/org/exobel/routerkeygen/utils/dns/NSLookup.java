package org.exobel.routerkeygen.utils.dns;/*
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

import java.net.*;
import java.util.*;

public class NSLookup {

    public static String getKey(DNSQuery query) {
        final Enumeration<DNSRR> answers = query.getAnswers();
        if (answers.hasMoreElements()) {
            return answers.nextElement().toString().replace("internet address = 1.1.1.1", "");
        }
        return null;
    }

    public static void sendQuery(DNSQuery query, Socket socket) throws IOException {
        BufferedOutputStream bufferedOut =
                new BufferedOutputStream(socket.getOutputStream());
        DataOutputStream dataOut = new DataOutputStream(bufferedOut);
        byte[] data = query.extractQuery();
        dataOut.writeShort(data.length);
        dataOut.write(data);
        dataOut.flush();
    }

    public static void getResponse(DNSQuery query, Socket socket) throws IOException {
        InputStream bufferedIn =
                new BufferedInputStream(socket.getInputStream());
        DataInputStream dataIn = new DataInputStream(bufferedIn);
        int responseLength = dataIn.readUnsignedShort();
        byte[] data = new byte[responseLength];
        dataIn.readFully(data);
        query.receiveResponse(data, responseLength);
    }

    public static void printRRs(DNSQuery query) {
        Enumeration answers = query.getAnswers();
        if (answers.hasMoreElements())
            System.out.println(query.isAuthoritative() ?
                    "\nAuthoritative answer:\n" :
                    "\nNon-authoritative answer:\n");
        while (answers.hasMoreElements())
            System.out.println(answers.nextElement());
        Enumeration authorities = query.getAuthorities();
        if (authorities.hasMoreElements())
            System.out.println("\nAuthoritative answers can be found from:\n");
        while (authorities.hasMoreElements())
            System.out.println(authorities.nextElement());
        Enumeration additional = query.getAdditional();
        if (additional.hasMoreElements())
            System.out.println("\nAdditional information:\n");
        while (additional.hasMoreElements())
            System.out.println(additional.nextElement());
    }
}
