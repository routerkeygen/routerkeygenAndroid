package org.exobel.routerkeygen;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.exobel.routerkeygen.algorithms.AliceKeygen;
import org.exobel.routerkeygen.algorithms.ConnKeygen;
import org.exobel.routerkeygen.algorithms.DiscusKeygen;
import org.exobel.routerkeygen.algorithms.DlinkKeygen;
import org.exobel.routerkeygen.algorithms.EasyBoxKeygen;
import org.exobel.routerkeygen.algorithms.EircomKeygen;
import org.exobel.routerkeygen.algorithms.HuaweiKeygen;
import org.exobel.routerkeygen.algorithms.InfostradaKeygen;
import org.exobel.routerkeygen.algorithms.Keygen;
import org.exobel.routerkeygen.algorithms.OnoKeygen;
import org.exobel.routerkeygen.algorithms.OteKeygen;
import org.exobel.routerkeygen.algorithms.PBSKeygen;
import org.exobel.routerkeygen.algorithms.PirelliKeygen;
import org.exobel.routerkeygen.algorithms.SkyV1Keygen;
import org.exobel.routerkeygen.algorithms.TecomKeygen;
import org.exobel.routerkeygen.algorithms.TelseyKeygen;
import org.exobel.routerkeygen.algorithms.ThomsonKeygen;
import org.exobel.routerkeygen.algorithms.UnsupportedKeygen;
import org.exobel.routerkeygen.algorithms.VerizonKeygen;
import org.exobel.routerkeygen.algorithms.Wlan2Keygen;
import org.exobel.routerkeygen.algorithms.Wlan4Keygen;
import org.exobel.routerkeygen.algorithms.Wlan6Keygen;

import android.content.res.Resources;

public class WirelessMatcher {

	private final Map<String, List<AliceMagicInfo>> supportedAlices;

	public WirelessMatcher( Resources resources ){
		AliceHandle aliceReader = new AliceHandle();
		SAXParserFactory factory = SAXParserFactory.newInstance();
	    SAXParser saxParser;
	    try {
	    	saxParser = factory.newSAXParser();
			saxParser.parse(resources.openRawResource(R.raw.alice), aliceReader);
		} 
	    catch (Exception e) {}
	    supportedAlices = aliceReader.getSupportedAlices();
	}
	
	
	public Keygen getKeygen(String ssid, String mac, int level , String enc){
		if ( enc.equals(""))
			enc = "Open";
		if ( ssid.matches("Discus--?[0-9a-fA-F]{6}"))
			return new DiscusKeygen(ssid, mac, level, enc);

		if ( ssid.matches("[eE]ircom[0-7]{4} ?[0-7]{4}")) {
			if ( mac.length() == 0 ){
				final String filteredSsid = ssid.replace(" ", "");
				final String end = Integer.toHexString( Integer.parseInt(filteredSsid.substring(filteredSsid.length()-8),8)^0x000fcc );
				mac = "00:0F:CC" +  ":" + end.substring(0,2)+ ":" +
						end.substring(2,4)+ ":" + end.substring(4,6);
			}
			return new EircomKeygen(ssid, mac, level, enc);
		}
		
		if ( ssid.matches("(Thomson|Blink|SpeedTouch|O2Wireless|Orange-|INFINITUM|" +
				"BigPond|Otenet|Bbox-|DMAX|privat|TN_private_|CYTA)[0-9a-fA-F]{6}")) {
			
			if ( mac.length() == 0 || !ssid.substring(ssid.length()-6).equals(mac.replaceAll(":", "").substring(6)))
				return new ThomsonKeygen(ssid, mac, level, enc);
		}
		if ( ssid.matches("DLink-[0-9a-fA-F]{6}")) 
			return new DlinkKeygen(ssid, mac, level, enc);


		if ( ssid.matches("FASTWEB-1-(000827|0013C8|0017C2|00193E|001CA2|001D8B|" +
				"002233|00238E|002553|00A02F|080018|3039F2|38229D|6487D7)[0-9A-Fa-f]{6}")) {
			if ( mac.length() == 0 ){
				final String end = ssid.substring(ssid.length()-12);
				mac = end.substring(0,2) + ":" + end.substring(2,4) + ":" + 
						   end.substring(4,6) + ":" + end.substring(6,8) + ":" +
						   end.substring(8,10) + ":" + end.substring(10,12);
			}
			return new PirelliKeygen(ssid, mac, level, enc);
		}

		if ( ssid.matches("FASTWEB-(1|2)-(002196|00036F)[0-9A-Fa-f]{6}")) {
			if ( mac.length() == 0 ){
				final String end = ssid.substring(ssid.length()-12);
				mac = end.substring(0,2) + ":" + end.substring(2,4) + ":" + 
						   end.substring(4,6) + ":" + end.substring(6,8) + ":" +
						   end.substring(8,10) + ":" + end.substring(10,12);
			}
			return new TelseyKeygen(ssid, mac, level, enc);
		}
		if ( ssid.matches("[aA]lice-[0-9]{8}") ) {

			final List<AliceMagicInfo> supported = supportedAlices.get(ssid.substring(0,9));
			if ( supported != null && supported.size() > 0) {
				if ( mac.length() < 6 )
					mac = supported.get(0).getMac();
				return new AliceKeygen(ssid, mac, level , enc, supported);
			}
		}

		/*ssid must be of the form P1XXXXXX0000X or p1XXXXXX0000X*/
		if ( ssid.matches("[Pp]1[0-9]{6}0{4}[0-9]") )
			return new OnoKeygen(ssid, mac, level, enc);

		if (  ssid.matches("(WLAN_|JAZZTEL_)[0-9a-fA-F]{4}") &&
		    ( mac.startsWith("00:1F:A4") || mac.startsWith("64:68:0C") ||
			  mac.startsWith("00:1D:20") ) )
			return new Wlan4Keygen(ssid, mac, level, enc);

		if ( ssid.matches("SKY[0-9]{5}") && (mac.startsWith("C4:3D:C7") || 
		      mac.startsWith("E0:46:9A") ||  mac.startsWith("E0:91:F5") || 
		      mac.startsWith("00:09:5B") ||  mac.startsWith("00:0F:B5") ||
		      mac.startsWith("00:14:6C") ||  mac.startsWith("00:18:4D") ||
		      mac.startsWith("00:26:F2") ||  mac.startsWith("C0:3F:0E") || 
		      mac.startsWith("30:46:9A") ||  mac.startsWith("00:1B:2F") ||
		      mac.startsWith("A0:21:B7") ||  mac.startsWith("00:1E:2A") ||
		      mac.startsWith("00:1F:33") ||  mac.startsWith("00:22:3F") ||
		      mac.startsWith("00:24:B2") ) )
			return new SkyV1Keygen(ssid, mac, level, enc);

		if ( ssid.matches("TECOM-AH4(021|222)-[0-9a-zA-Z]{6}") )
			return new TecomKeygen(ssid, mac, level, enc);

		if ( ssid.matches("InfostradaWiFi-[0-9a-zA-Z]{6}") )
			return new InfostradaKeygen(ssid, mac, level, enc);

		if ( ssid.startsWith("WLAN_") && ssid.length() == 7 &&
			( mac.startsWith("00:01:38") || mac.startsWith("00:16:38") || 
			  mac.startsWith("00:01:13") || mac.startsWith("00:01:1B") || 
			  mac.startsWith("00:19:5B") ) )
			return new Wlan2Keygen(ssid, mac, level, enc);
		
		if ( ssid.matches("(WLAN|WiFi|YaCom)[0-9a-zA-Z]{6}") )
			return new Wlan6Keygen(ssid, mac, level, enc);
		
		if ( ssid.matches("(Arcor|EasyBox|Vodafone)(-| )[0-9a-fA-F]{6}") )
			return new EasyBoxKeygen(ssid, mac, level, enc);

		if ( ssid.matches("OTE[0-9a-fA-F]{6}") )
			return new OteKeygen(ssid, mac, level, enc);

		if ( ssid.matches("PBS-[0-9a-fA-F]{6}") )
			return new PBSKeygen(ssid, mac, level, enc);

		if ( ssid.matches("CONN-?[0-9a-fA-F]{1}") )
			return new ConnKeygen(ssid, mac, level, enc);
		
		if ( ssid.length() == 5  && 
			  ( mac.startsWith("00:1F:90") || mac.startsWith("A8:39:44") ||
				mac.startsWith("00:18:01") || mac.startsWith("00:20:E0") ||
				mac.startsWith("00:0F:B3") || mac.startsWith("00:1E:A7") ||
				mac.startsWith("00:15:05") || mac.startsWith("00:24:7B") ||
				mac.startsWith("00:26:62") || mac.startsWith("00:26:B8") ) )
			return new VerizonKeygen(ssid, mac, level, enc);
		if ( ssid.matches("INFINITUM[0-9a-zA-Z]{4}") && ( 
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
			return new HuaweiKeygen(ssid, mac, level, enc);
		return new UnsupportedKeygen(ssid, mac, level, enc);
		
	}
	


}
