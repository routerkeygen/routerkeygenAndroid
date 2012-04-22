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
#include "wifinetwork.h"
#include <QRegExp>
WifiNetwork::WifiNetwork(QString r , QString m) : ssid(r) , mac(m.toUpper() )
{
    newThomson = false;/*Must be first*/
    supported = ssidFilter();
}

QString WifiNetwork::getSSID() const {
    return this->ssid;
}
QString WifiNetwork::getMac() const{
    QString mac = this->mac;
    return mac.replace(QChar(':') , "" );
}

QString WifiNetwork::getSSIDsubpart() const{
    return this->ssidSubpart;
}

int  WifiNetwork::getType() const{
    return this->type;
}

QVector<AliceMagicInfo *> & WifiNetwork::getSupportedAlice(){
    return this->supportedAlice;
}

bool WifiNetwork::isSupported() const{
    return this->supported && !this->newThomson;
}

bool WifiNetwork::ssidFilter(){
    if ( ( ssid.count(QRegExp("[a-fA-F0-9]{6}")) == 1) && (
             ssid.startsWith("Thomson") || ssid.startsWith("SpeedTouch") ||
             ssid.startsWith("O2Wireless") || ssid.startsWith("Orange-") ||
             ssid.startsWith("INFINITUM") || ssid.startsWith("BigPond")  ||
             ssid.startsWith("Otenet") || ssid.startsWith("Bbox-") ||
             ssid.startsWith("DMAX") || ssid.startsWith("privat") ||
             ssid.startsWith("TN_private_") || ssid.startsWith("Cyta") ) )
    {
            ssidSubpart = ssid.right(6);
            if ( !mac.isEmpty() )
                    if ( ssidSubpart == getMac().right(6) )
                            newThomson = true;
            type = THOMSON;
            return true;
    }
    if (  ( ssid.count(QRegExp("DLink-[a-fA-F0-9]{6}")) == 1) && ( ssid.size() == 12 ) )
    {
            ssidSubpart = ssid.right(6);
            type = DLINK;
            return true;
    }
    if ( ssid.count(QRegExp("Discus--?[0-9a-fA-F]{6}")) == 1)
    {
            ssidSubpart = ssid.right(6);
            type = DISCUS;
            return true;
    }
    if ( (ssid.count(QRegExp("eircom[0-7]{4} [0-7]{4}")) == 1 )||
         (ssid.count(QRegExp("eircom[0-7]{8}")) == 1))
    {
        if (  ssid.size() == 14 )
            ssidSubpart = ssid.right(8);
        else
            ssidSubpart = ssid.mid(6,4) + ssid.right(4);
            if ( mac.isEmpty() )
                    calcEircomMAC();
            type = EIRCOM;
        return true;
    }
    if ( ssid.size() == 5  &&
          ( mac.startsWith("00:1F:90") || mac.startsWith("A8:39:44") ||
            mac.startsWith("00:18:01") || mac.startsWith("00:20:E0") ||
            mac.startsWith("00:0F:B3") || mac.startsWith("00:1E:A7") ||
            mac.startsWith("00:15:05") || mac.startsWith("00:24:7B") ||
            mac.startsWith("00:26:62") || mac.startsWith("00:26:B8") ) )
    {
            ssidSubpart = ssid;
            type = VERIZON;
            return true;
    }
    if ( ssid.count(QRegExp("TECOM-AH4021-[0-9a-zA-Z]{6}|TECOM-AH4222-[0-9a-zA-Z]{6}")) == 1 )
    {
            ssidSubpart = ssid;
            type = TECOM;
            return true;
    }
    if ( ( ssid.count(QRegExp("SKY[0-9]{5}"))==1) && (mac.startsWith("C4:3D:C7") ||
          mac.startsWith("E0:46:9A") ||  mac.startsWith("E0:91:F5") ||
          mac.startsWith("00:09:5B") ||  mac.startsWith("00:0F:B5") ||
          mac.startsWith("00:14:6C") ||  mac.startsWith("00:18:4D") ||
          mac.startsWith("00:26:F2") ||  mac.startsWith("C0:3F:0E") ||
          mac.startsWith("30:46:9A") ||  mac.startsWith("00:1B:2F") ||
          mac.startsWith("A0:21:B7") ||  mac.startsWith("00:1E:2A") ||
          mac.startsWith("00:1F:33") ||  mac.startsWith("00:22:3F") ||
          mac.startsWith("00:24:B2") ) )
    {
            ssidSubpart = ssid.right(5);
            type = SKY_V1;
            return true;
    }
    if ( ssid.count(QRegExp("InfostradaWiFi-[0-9a-zA-Z]{6}")) ==   1 )
    {
            ssidSubpart = ssid;
            type = INFOSTRADA;
            return true;
    }
    if ( ssid.startsWith("WLAN_") && ssid.length() == 7 &&
            ( mac.startsWith("00:01:38") || mac.startsWith("00:16:38") ||
              mac.startsWith("00:01:13") || mac.startsWith("00:01:1B") ||
              mac.startsWith("00:19:5B") ) )
    {
            ssidSubpart = ssid.right(2);
            type = WLAN2;
            return true;
    }
    if ( ( ssid.count(QRegExp("WLAN_[0-9a-zA-Z]{4}|JAZZTEL_[0-9a-zA-Z]{4}")) == 1 ) &&
        ( mac.startsWith("00:1F:A4") || mac.startsWith("64:68:0C") ||
              mac.startsWith("00:1D:20") ) )
    {
            ssidSubpart = ssid.right(4);
            type = WLAN4;
            return true;
    }
    if ( ssid.count(QRegExp("WLAN[0-9a-zA-Z]{6}|WiFi[0-9a-zA-Z]{6}|YaCom[0-9a-zA-Z]{6}")) == 1 )
    {
            ssidSubpart = ssid.right(6);
            type = WLAN6;
            return true;
    }
    if ( ( ssid.toUpper().startsWith("FASTWEB-1-000827") && ssid.length() == 22 ) ||
        ( ssid.toUpper().startsWith("FASTWEB-1-0013C8") && ssid.length() == 22 )  ||
        ( ssid.toUpper().startsWith("FASTWEB-1-0017C2") && ssid.length() == 22 )  ||
        ( ssid.toUpper().startsWith("FASTWEB-1-00193E") && ssid.length() == 22 )  ||
        ( ssid.toUpper().startsWith("FASTWEB-1-001CA2") && ssid.length() == 22 )  ||
        ( ssid.toUpper().startsWith("FASTWEB-1-001D8B") && ssid.length() == 22 )  ||
        ( ssid.toUpper().startsWith("FASTWEB-1-002233") && ssid.length() == 22 )  ||
        ( ssid.toUpper().startsWith("FASTWEB-1-00238E") && ssid.length() == 22 )  ||
        ( ssid.toUpper().startsWith("FASTWEB-1-002553") && ssid.length() == 22 )  ||
        ( ssid.toUpper().startsWith("FASTWEB-1-00A02F") && ssid.length() == 22 )  ||
        ( ssid.toUpper().startsWith("FASTWEB-1-080018") && ssid.length() == 22 )  ||
        ( ssid.toUpper().startsWith("FASTWEB-1-3039F2") && ssid.length() == 22 )  ||
        ( ssid.toUpper().startsWith("FASTWEB-1-38229D") && ssid.length() == 22 )  ||
        ( ssid.toUpper().startsWith("FASTWEB-1-6487D7") && ssid.length() == 22 ))
    {
           ssidSubpart = ssid.right(12);
           if ( mac == "" )
                   calcFastwebMAC();
           type = PIRELLI;
           return true;
    }
    if ( ssid.count(QRegExp("FASTWEB-[1-2]-002196[0-9a-fA-F]{6}|FASTWEB-[1-2]-00036F[0-9a-fA-F]{6}")) == 1 )
    {
            ssidSubpart = ssid.right(12);
            if ( mac == "" )
                    calcFastwebMAC();
            type = TELSEY;
            return true;
    }
    /*ssid must be of the form P1XXXXXX0000X or p1XXXXXX0000X*/
    if ( ssid.count(QRegExp("[Pp]1[0-9]{6}0{4}[0-9]")) == 1  )
    {
            ssidSubpart = "";
            type = ONO_WEP;
            return true;
    }
    if ( ( ssid.count(QRegExp("INFINITUM[0-9a-zA-Z]{4}")) == 1 ) && (
        mac.startsWith("00:25:9E") || mac.startsWith("00:25:68") ||
        mac.startsWith("00:22:A1") || mac.startsWith("00:1E:10") ||
        mac.startsWith("00:18:82") || mac.startsWith("00:0F:F2") ||
        mac.startsWith("00:E0:FC") || mac.startsWith("28:6E:D4") ||
        mac.startsWith("54:A5:1B") || mac.startsWith("F4:C7:14") ||
        mac.startsWith("28:5F:DB") || mac.startsWith("30:87:30") ||
        mac.startsWith("4C:54:99") || mac.startsWith("40:4D:8E") ||
        mac.startsWith("64:16:F0") || mac.startsWith("78:1D:BA") ||
        mac.startsWith("84:A8:E4") || mac.startsWith("04:C0:6F") ||
        mac.startsWith("5C:4C:A9") || mac.startsWith("1C:1D:67") ||
        mac.startsWith("CC:96:A0") || mac.startsWith("20:2B:C1") ) )
    {
            if ( ssid.startsWith("INFINITUM")  )
                    ssidSubpart = ssid.right(4);
            else
                    ssidSubpart = "";
            type = HUAWEI;
            return true;
    }
    if ( ssid.count(QRegExp("[aA]lice-[0-9]{8}")) == 1 )
    {
        AliceHandler aliceReader(ssid.left(9));
        aliceReader.readFile(":/alice/alice.xml");
        ssidSubpart = ssid.right(8);
        type = ALICE;
        if( !aliceReader.isSupported() )
                return false;
        supportedAlice = aliceReader.getSupportedAlice();
        if ( getMac().size() < 6 )
                mac = supportedAlice.at(0)->mac;
        return true;
    }
    return false;
}
void WifiNetwork::calcEircomMAC(){
    QString end;
    bool status = false;
    int ssidNum = ssidSubpart.toInt(&status , 8 ) ^ 0x000fcc;
    end.setNum(ssidNum,16);
    while ( end.size() < 6 )
        end = "0" + end;
    end = end.toUpper();
    this->mac = "00:0F:CC:" + end.left(2)+ ":" +
                           end.mid(2,2)+ ":" + end.right(2);
}

void WifiNetwork::calcFastwebMAC(){
    this->mac = ssidSubpart.left(2) + ":" + ssidSubpart.mid(2,2) + ":" +
                   ssidSubpart.mid(4,2) + ":" + ssidSubpart.mid(6,2) + ":" +
                   ssidSubpart.mid(8,2) + ":" + ssidSubpart.right(2);
}
