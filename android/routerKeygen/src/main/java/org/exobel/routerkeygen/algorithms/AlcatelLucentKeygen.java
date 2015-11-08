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
package org.exobel.routerkeygen.algorithms;

import android.os.Parcel;

import org.exobel.routerkeygen.R;
import org.exobel.routerkeygen.utils.dns.DNSQuery;
import org.exobel.routerkeygen.utils.dns.NSLookup;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

/*
 * http://www.routerpwn.com/vecinitum-de-fibra/decoded.php
 */
public class AlcatelLucentKeygen extends Keygen {

    public static final Creator<AlcatelLucentKeygen> CREATOR = new Creator<AlcatelLucentKeygen>() {
        public AlcatelLucentKeygen createFromParcel(Parcel in) {
            return new AlcatelLucentKeygen(in);
        }

        public AlcatelLucentKeygen[] newArray(int size) {
            return new AlcatelLucentKeygen[size];
        }
    };

    public AlcatelLucentKeygen(String ssid, String mac) {
        super(ssid, mac);
    }

    private AlcatelLucentKeygen(Parcel in) {
        super(in);
    }


    @Override
    public int getSupportState() {
        //It requires Internet
        return UNLIKELY_SUPPORTED;
    }


    @Override
    public List<String> getKeys() {
        if (getMacAddress().length() != 12) {
            setErrorCode(R.string.msg_errpirelli);
            return null;
        }
        DNSQuery dnsquery = new DNSQuery(getMacAddress(), 255, 1);
        DatagramSocket datagramsocket = null;
        try {
            datagramsocket = new DatagramSocket();
            datagramsocket.setSoTimeout(5000);
            int i = 0;
            boolean noReply = false;
            do {
                try {
                    sendQuery(dnsquery, datagramsocket, InetAddress.getByName("hak.im"));
                    noReply = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ++i;
                if ( i >= 3) {
                    //Give up.
                    return getResults();
                }
            } while (!noReply);
            getResponse(dnsquery, datagramsocket);
            addPassword(NSLookup.getKey(dnsquery));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (datagramsocket != null ){
                datagramsocket.close();
            }
        }
        return getResults();
    }

    private static void getResponse(DNSQuery dnsquery, DatagramSocket datagramsocket)
            throws IOException
    {
        byte packet[] = new byte[512];
        DatagramPacket datagrampacket = new DatagramPacket(packet, packet.length);
        datagramsocket.receive(datagrampacket);
        dnsquery.receiveResponse(datagrampacket.getData(), datagrampacket.getLength());
    }

    private static void sendQuery(DNSQuery dnsquery, DatagramSocket datagramsocket, InetAddress inetaddress)
            throws IOException
    {
        final byte [] query = dnsquery.extractQuery();
        datagramsocket.send(new DatagramPacket(query, query.length, inetaddress, 5353));
    }

}
