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
package org.exobel.routerkeygen;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Context;


public class WifiNetwork implements Comparable<WifiNetwork>, Serializable{
	
	private static final long serialVersionUID = 9017619358450514547L;
	private String ssid;
	String mac;
	private String ssidSubpart;
	String encryption;
	boolean supported;
	boolean newThomson;
	int level;
	private ArrayList <AliceMagicInfo> supportedAlice;
	TYPE type;
	static enum TYPE {
		THOMSON , DLINK , DISCUS , VERIZON ,
		EIRCOM , PIRELLI , TELSEY , ALICE ,
		WLAN4 , HUAWEI, WLAN2 , ONO_WEP ,
		SKY_V1 , WLAN6 ,TECOM , INFOSTRADA };
	public WifiNetwork(String ssid, String mac, int level , String enc , Context con ){
		this.setSsid(ssid);
		this.mac = mac.toUpperCase();
		this.level  = level;
		this.encryption = enc;
		if ( this.encryption.equals(""))
			this.encryption = "Open";
		this.newThomson = false;
		this.supported =  essidFilter(con);
	}
	
	public int getLevel(){
		return level;
	}
	
	public String getSSIDsubpart(){
		return getSsidSubpart();
	}
	
	public String getMacEnd(){
		if ( mac.replace(":", "").length() < 12 )
			return mac.replace(":", "");
		return mac.replace(":", "").substring(6);
	}
	
	public String getMac(){
		return  mac.replace(":", "");
	}
	
	private boolean essidFilter(Context con) {
		if ( ( getSsid().startsWith("Thomson") && getSsid().length() == 13 )    ||
		     ( getSsid().startsWith("SpeedTouch") && getSsid().length() == 16 ) ||
		     ( getSsid().startsWith("O2Wireless") && getSsid().length() == 16 ) ||
		     ( getSsid().startsWith("Orange-") && getSsid().length() == 13 ) || 
		     ( getSsid().startsWith("INFINITUM") && getSsid().length() == 15 )  ||
		     ( getSsid().startsWith("BigPond") && getSsid().length() == 13 )  ||
		     ( getSsid().startsWith("Otenet") && getSsid().length() == 12 ) ||
		     ( getSsid().startsWith("Bbox-") && getSsid().length() == 11 ) ||
		     ( getSsid().startsWith("DMAX") && getSsid().length() == 10 )  || 
		     ( getSsid().startsWith("privat") && getSsid().length() == 12 ) ||
		     ( getSsid().startsWith("TN_private_") && getSsid().length() == 17 ) || 
		     ( getSsid().startsWith("CYTA") && getSsid().length() == 10 ) ||
		     ( getSsid().startsWith("Blink") && getSsid().length() == 11 ))
		{
			setSsidSubpart(getSsid().substring(getSsid().length()-6));
			if ( !mac.equals("") )
				if ( getSsidSubpart().equals(getMacEnd()) )
					newThomson = true;
			type = TYPE.THOMSON;
			return true;
		}
		if (  getSsid().matches("DLink-[0-9a-fA-F]{6}") )
		{
			setSsidSubpart(new String ( getSsid().substring(getSsid().length()-6)));
			type = TYPE.DLINK;
			return true;
		}
		if ( getSsid().matches("Discus--?[0-9a-fA-F]{6}") ) 
		{
			setSsidSubpart(getSsid().substring(getSsid().length()-6));
			type = TYPE.DISCUS;
			return true;
		}
		if (( getSsid().matches("eircom[0-7]{8}|eircom[0-7]{4} [0-7]{4}") )	)
		{
			if ( getSsid().length() == 14 )
				setSsidSubpart(getSsid().substring(getSsid().length()-8));
			else
				setSsidSubpart(getSsid().substring(6, 10) + getSsid().substring(getSsid().length()-4));
			if ( mac.equals("") )
				calcEircomMAC();
			type = TYPE.EIRCOM;
			return true;
		}
		if ( getSsid().length() == 5  && 
			  ( mac.startsWith("00:1F:90") || mac.startsWith("A8:39:44") ||
				mac.startsWith("00:18:01") || mac.startsWith("00:20:E0") ||
				mac.startsWith("00:0F:B3") || mac.startsWith("00:1E:A7") ||
				mac.startsWith("00:15:05") || mac.startsWith("00:24:7B") ||
				mac.startsWith("00:26:62") || mac.startsWith("00:26:B8") ) )
		{
			setSsidSubpart(getSsid());
			type = TYPE.VERIZON;
			return true;
		}
		if ( ( getSsid().toUpperCase().startsWith("FASTWEB-1-000827") && getSsid().length() == 22 ) ||
		     ( getSsid().toUpperCase().startsWith("FASTWEB-1-0013C8") && getSsid().length() == 22 )	||
		     ( getSsid().toUpperCase().startsWith("FASTWEB-1-0017C2") && getSsid().length() == 22 )	||
		     ( getSsid().toUpperCase().startsWith("FASTWEB-1-00193E") && getSsid().length() == 22 )	||
		     ( getSsid().toUpperCase().startsWith("FASTWEB-1-001CA2") && getSsid().length() == 22 )	||
		     ( getSsid().toUpperCase().startsWith("FASTWEB-1-001D8B") && getSsid().length() == 22 )	||
		     ( getSsid().toUpperCase().startsWith("FASTWEB-1-002233") && getSsid().length() == 22 )	||
		     ( getSsid().toUpperCase().startsWith("FASTWEB-1-00238E") && getSsid().length() == 22 )	||
		     ( getSsid().toUpperCase().startsWith("FASTWEB-1-002553") && getSsid().length() == 22 )	||
		     ( getSsid().toUpperCase().startsWith("FASTWEB-1-00A02F") && getSsid().length() == 22 )	||
		     ( getSsid().toUpperCase().startsWith("FASTWEB-1-080018") && getSsid().length() == 22 )	||
		     ( getSsid().toUpperCase().startsWith("FASTWEB-1-3039F2") && getSsid().length() == 22 )	||
		     ( getSsid().toUpperCase().startsWith("FASTWEB-1-38229D") && getSsid().length() == 22 )	||
		     ( getSsid().toUpperCase().startsWith("FASTWEB-1-6487D7") && getSsid().length() == 22 ))
			{
				setSsidSubpart(getSsid().substring(getSsid().length()-12));
				if ( mac.equals("") )
					calcFastwebMAC();
				type = TYPE.PIRELLI;
				return true;
			}
		if ( getSsid().matches("FASTWEB-[1-2]-002196[0-9A-Fa-f]{6}|FASTWEB-[1-2]-00036F[0-9A-Fa-f]{6}") )
		{
			setSsidSubpart(getSsid().substring(getSsid().length()-12));
			if ( mac.equals("") )
				calcFastwebMAC();
			type = TYPE.TELSEY;
			return true;
		}
		if ( getSsid().matches("[aA]lice-[0-9]{8}") )
		{
			AliceHandle aliceReader = new AliceHandle(getSsid().substring(0,9));
			SAXParserFactory factory = SAXParserFactory.newInstance();
		    SAXParser saxParser;
		    try {
		    	saxParser = factory.newSAXParser();
				saxParser.parse(con.getResources().openRawResource(R.raw.alice), aliceReader);
			} 
		    catch (Exception e) {}
			setSsidSubpart(getSsid().substring(getSsid().length()-8));
			type = TYPE.ALICE;
			if( aliceReader.supportedAlice.isEmpty() )
				return false;
			setSupportedAlice(aliceReader.supportedAlice);
			if ( getMac().length() < 6 )
				mac = getSupportedAlice().get(0).mac;
			return true;
		}
		if (  getSsid().matches("WLAN_[0-9a-fA-F]{4}|JAZZTEL_[0-9a-fA-F]{4}") &&
		    ( mac.startsWith("00:1F:A4") || mac.startsWith("64:68:0C") ||
			  mac.startsWith("00:1D:20") ) )
		{
			setSsidSubpart(getSsid().substring(getSsid().length()-4));
			type = TYPE.WLAN4;
			return true;
		}
		if ( getSsid().matches("INFINITUM[0-9a-zA-Z]{4}") && ( 
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
			if ( getSsid().startsWith("INFINITUM")  )
				setSsidSubpart(getSsid().substring(getSsid().length()-4));
			else
				setSsidSubpart("");
			type = TYPE.HUAWEI;
			return true;
		}
		if ( getSsid().startsWith("WLAN_") && getSsid().length() == 7 &&
			( mac.startsWith("00:01:38") || mac.startsWith("00:16:38") || 
			  mac.startsWith("00:01:13") || mac.startsWith("00:01:1B") || 
			  mac.startsWith("00:19:5B") ) )
		{
			setSsidSubpart(getSsid().substring(getSsid().length()-2));
			type = TYPE.WLAN2;
			return true;
		}
		/*ssid must be of the form P1XXXXXX0000X or p1XXXXXX0000X*/
		if ( getSsid().matches("[Pp]1[0-9]{6}0{4}[0-9]") )
		{
			setSsidSubpart("");
			type = TYPE.ONO_WEP;
			return true;
		}
		if ( getSsid().matches("WLAN[0-9a-zA-Z]{6}|WiFi[0-9a-zA-Z]{6}|YaCom[0-9a-zA-Z]{6}") )
		{
			setSsidSubpart(getSsid().substring(getSsid().length()-6));
			type = TYPE.WLAN6;
			return true;
		}
		if ( getSsid().matches("SKY[0-9]{5}") && (mac.startsWith("C4:3D:C7") || 
		      mac.startsWith("E0:46:9A") ||  mac.startsWith("E0:91:F5") || 
		      mac.startsWith("00:09:5B") ||  mac.startsWith("00:0F:B5") ||
		      mac.startsWith("00:14:6C") ||  mac.startsWith("00:18:4D") ||
		      mac.startsWith("00:26:F2") ||  mac.startsWith("C0:3F:0E") || 
		      mac.startsWith("30:46:9A") ||  mac.startsWith("00:1B:2F") ||
		      mac.startsWith("A0:21:B7") ||  mac.startsWith("00:1E:2A") ||
		      mac.startsWith("00:1F:33") ||  mac.startsWith("00:22:3F") ||
		      mac.startsWith("00:24:B2") ) )
		{
			setSsidSubpart(getSsid().substring(getSsid().length()-5));
			type = TYPE.SKY_V1;
			return true;
		}
		if ( getSsid().matches("TECOM-AH4021-[0-9a-zA-Z]{6}|TECOM-AH4222-[0-9a-zA-Z]{6}") )
		{
			setSsidSubpart(getSsid());
			type = TYPE.TECOM;
			return true;
		}
		if ( getSsid().matches("InfostradaWiFi-[0-9a-zA-Z]{6}") )
		{
			setSsidSubpart(getSsid());
			type = TYPE.INFOSTRADA;
			return true;
		}
		return false;
	}
	
	public void calcFastwebMAC(){
		this.mac = getSsidSubpart().substring(0,2) + ":" + getSsidSubpart().substring(2,4) + ":" + 
				   getSsidSubpart().substring(4,6) + ":" + getSsidSubpart().substring(6,8) + ":" +
				   getSsidSubpart().substring(8,10) + ":" + getSsidSubpart().substring(10,12);
	}
	
	public void calcEircomMAC(){
		String end = Integer.toHexString( Integer.parseInt(getSsidSubpart(), 8) ^ 0x000fcc );
		this.mac = "00:0F:CC" +  ":" + end.substring(0,2)+ ":" +
					end.substring(2,4)+ ":" + end.substring(4,6);
	}

	public int compareTo(WifiNetwork another) {
		if ( another.level == this.level && this.getSsid().equals(another.getSsid()) && this.mac.equals(another.mac) )
			return 0;
		if ( this.supported && !this.newThomson )
			return -1;
		return 1;
	}

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public ArrayList <AliceMagicInfo> getSupportedAlice() {
        return supportedAlice;
    }

    public void setSupportedAlice(ArrayList <AliceMagicInfo> supportedAlice) {
        this.supportedAlice = supportedAlice;
    }

    public String getSsidSubpart() {
        return ssidSubpart;
    }

    public void setSsidSubpart(String ssidSubpart) {
        this.ssidSubpart = ssidSubpart;
    }

	
}
