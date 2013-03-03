package org.exobel.routerkeygen;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.exobel.routerkeygen.algorithms.AliceKeygen;
import org.exobel.routerkeygen.algorithms.AndaredKeygen;
import org.exobel.routerkeygen.algorithms.AxtelKeygen;
import org.exobel.routerkeygen.algorithms.CabovisaoSagemKeygen;
import org.exobel.routerkeygen.algorithms.ComtrendKeygen;
import org.exobel.routerkeygen.algorithms.ConnKeygen;
import org.exobel.routerkeygen.algorithms.DiscusKeygen;
import org.exobel.routerkeygen.algorithms.DlinkKeygen;
import org.exobel.routerkeygen.algorithms.EasyBoxKeygen;
import org.exobel.routerkeygen.algorithms.EircomKeygen;
import org.exobel.routerkeygen.algorithms.HuaweiKeygen;
import org.exobel.routerkeygen.algorithms.InfostradaKeygen;
import org.exobel.routerkeygen.algorithms.InterCableKeygen;
import org.exobel.routerkeygen.algorithms.Keygen;
import org.exobel.routerkeygen.algorithms.MaxcomKeygen;
import org.exobel.routerkeygen.algorithms.MegaredKeygen;
import org.exobel.routerkeygen.algorithms.OnoKeygen;
import org.exobel.routerkeygen.algorithms.OteBAUDKeygen;
import org.exobel.routerkeygen.algorithms.OteKeygen;
import org.exobel.routerkeygen.algorithms.PBSKeygen;
import org.exobel.routerkeygen.algorithms.PirelliKeygen;
import org.exobel.routerkeygen.algorithms.PtvKeygen;
import org.exobel.routerkeygen.algorithms.SkyV1Keygen;
import org.exobel.routerkeygen.algorithms.TecomKeygen;
import org.exobel.routerkeygen.algorithms.TeletuKeygen;
import org.exobel.routerkeygen.algorithms.TelseyKeygen;
import org.exobel.routerkeygen.algorithms.ThomsonKeygen;
import org.exobel.routerkeygen.algorithms.UnsupportedKeygen;
import org.exobel.routerkeygen.algorithms.VerizonKeygen;
import org.exobel.routerkeygen.algorithms.WifimediaRKeygen;
import org.exobel.routerkeygen.algorithms.Wlan2Keygen;
import org.exobel.routerkeygen.algorithms.Wlan6Keygen;
import org.exobel.routerkeygen.algorithms.ZyxelKeygen;
import org.exobel.routerkeygen.config.AliceConfigParser;
import org.exobel.routerkeygen.config.AliceMagicInfo;
import org.exobel.routerkeygen.config.TeleTuConfigParser;
import org.exobel.routerkeygen.config.TeleTuMagicInfo;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Parcel;
import android.os.Parcelable;

public class WirelessMatcher implements Parcelable {

	private final Map<String, ArrayList<AliceMagicInfo>> supportedAlices;
	private final Map<String, ArrayList<TeleTuMagicInfo>> supportedTeletu;

	public WirelessMatcher(InputStream alice, InputStream teletu) {
		supportedAlices = AliceConfigParser.parse(alice);
		supportedTeletu = TeleTuConfigParser.parse(teletu);
	}

	public Keygen getKeygen(ScanResult result) {
		final Keygen keygen = getKeygen(result.SSID,
				result.BSSID.toUpperCase(Locale.getDefault()),
				WifiManager.calculateSignalLevel(result.level, 4),
				result.capabilities);
		keygen.setScanResult(result);
		return keygen;
	}

	public Keygen getKeygen(String ssid, String mac, int level, String enc) {
		mac = mac.toUpperCase(Locale.getDefault());
		if (enc.equals(""))
			enc = Keygen.OPEN;
		if (ssid.matches("(AXTEL|AXTEL-XTREMO)-[0-9a-fA-F]{4}")) {
			final String ssidSubpart = ssid.substring(ssid.length() - 4);
			final String macShort = mac.replace(":", "");
			if (macShort.length() < 12
					|| ssidSubpart.equalsIgnoreCase(macShort.substring(8)))
				return new AxtelKeygen(ssid, mac, level, enc);
		}
		if (ssid.startsWith("InterCable") && mac.startsWith("00:15"))
			return new InterCableKeygen(ssid, mac, level, enc);
		if (ssid.matches("Discus--?[0-9a-fA-F]{6}"))
			return new DiscusKeygen(ssid, mac, level, enc);

		if (ssid.matches("[eE]ircom[0-7]{4} ?[0-7]{4}")) {
			if (mac.length() == 0) {
				final String filteredSsid = ssid.replace(" ", "");
				final String end = Integer
						.toHexString(Integer.parseInt(filteredSsid
								.substring(filteredSsid.length() - 8), 8) ^ 0x000fcc);
				mac = "00:0F:CC" + ":" + end.substring(0, 2) + ":"
						+ end.substring(2, 4) + ":" + end.substring(4, 6);
			}
			return new EircomKeygen(ssid, mac, level, enc);
		}

		/*
		 * This test MUST be done before the Thomson one because some SSID are
		 * common and this test checks for the MAC addresses
		 */
		if (ssid.matches("(Arcor|EasyBox|Vodafone)(-| )[0-9a-fA-F]{6}")
				&& (mac.startsWith("00:12:BF") || mac.startsWith("00:1A:2A")
						|| mac.startsWith("00:1D:19")
						|| mac.startsWith("00:23:08")
						|| mac.startsWith("00:26:4D")
						|| mac.startsWith("50:7E:5D")
						|| mac.startsWith("1C:C6:3C")
						|| mac.startsWith("74:31:70")
						|| mac.startsWith("7C:4F:B5") || mac
							.startsWith("88:25:2C")))
			return new EasyBoxKeygen(ssid, mac, level, enc);

		if (ssid.matches("(Thomson|Blink|SpeedTouch|O2Wireless|Orange-|INFINITUM|"
				+ "BigPond|Otenet|Bbox-|DMAX|privat|TN_private_|CYTA|Vodafone-|Optimus|OptimusFibra|MEO-)[0-9a-fA-F]{6}"))
			return new ThomsonKeygen(ssid, mac, level, enc);

		if (ssid.matches("DLink-[0-9a-fA-F]{6}"))
			return new DlinkKeygen(ssid, mac, level, enc);

		if (ssid.matches("FASTWEB-1-(000827|0013C8|0017C2|00193E|001CA2|001D8B|"
				+ "002233|00238E|002553|00A02F|080018|3039F2|38229D|6487D7)[0-9A-Fa-f]{6}")) {
			if (mac.length() == 0) {
				final String end = ssid.substring(ssid.length() - 12);
				mac = end.substring(0, 2) + ":" + end.substring(2, 4) + ":"
						+ end.substring(4, 6) + ":" + end.substring(6, 8) + ":"
						+ end.substring(8, 10) + ":" + end.substring(10, 12);
			}
			return new PirelliKeygen(ssid, mac, level, enc);
		}

		if (ssid.matches("FASTWEB-(1|2)-(002196|00036F)[0-9A-Fa-f]{6}")) {
			if (mac.length() == 0) {
				final String end = ssid.substring(ssid.length() - 12);
				mac = end.substring(0, 2) + ":" + end.substring(2, 4) + ":"
						+ end.substring(4, 6) + ":" + end.substring(6, 8) + ":"
						+ end.substring(8, 10) + ":" + end.substring(10, 12);
			}
			return new TelseyKeygen(ssid, mac, level, enc);
		}
		if (ssid.matches("[aA]lice-[0-9]{8}")) {

			final List<AliceMagicInfo> supported = supportedAlices.get(ssid
					.substring(6, 9));
			if (supported != null && supported.size() > 0) {
				if (mac.length() < 6)
					mac = supported.get(0).getMac();
				return new AliceKeygen(ssid, mac, level, enc, supported);
			}
		}

		if (ssid.toLowerCase(Locale.getDefault()).contains("teletu")) {
			final String filteredMac = mac.replace(":", "");
			if (filteredMac.length() == 12) {
				final List<TeleTuMagicInfo> supported = supportedTeletu
						.get(filteredMac.substring(0, 6));
				if (supported != null && supported.size() > 0) {
					final int macIntValue = Integer.parseInt(
							filteredMac.substring(6), 16);
					for (TeleTuMagicInfo magic : supported) {
						if (macIntValue >= magic.getRange()[0]
								&& macIntValue <= magic.getRange()[1]) {
							return new TeletuKeygen(ssid, mac, level, enc,
									magic);
						}
					}
				}
			}
		}
		/* ssid must be of the form P1XXXXXX0000X or p1XXXXXX0000X */
		if (ssid.matches("[Pp]1[0-9]{6}0{4}[0-9]"))
			return new OnoKeygen(ssid, mac, level, enc);

		if (ssid.matches("(WLAN|JAZZTEL)_[0-9a-fA-F]{4}")) {
			if (mac.startsWith("00:1F:A4") || mac.startsWith("F4:3E:61")
					|| mac.startsWith("40:4A:03"))
				return new ZyxelKeygen(ssid, mac, level, enc);

			if (mac.startsWith("00:1B:20") || mac.startsWith("64:68:0C")
					|| mac.startsWith("00:1D:20") || mac.startsWith("00:23:F8")
					|| mac.startsWith("38:72:C0") || mac.startsWith("30:39:F2"))
				return new ComtrendKeygen(ssid, mac, level, enc);
		}
		if (ssid.matches("SKY[0-9]{5}")
				&& (mac.startsWith("C4:3D:C7") || mac.startsWith("E0:46:9A")
						|| mac.startsWith("E0:91:F5")
						|| mac.startsWith("00:09:5B")
						|| mac.startsWith("00:0F:B5")
						|| mac.startsWith("00:14:6C")
						|| mac.startsWith("00:18:4D")
						|| mac.startsWith("00:26:F2")
						|| mac.startsWith("C0:3F:0E")
						|| mac.startsWith("30:46:9A")
						|| mac.startsWith("00:1B:2F")
						|| mac.startsWith("A0:21:B7")
						|| mac.startsWith("00:1E:2A")
						|| mac.startsWith("00:1F:33")
						|| mac.startsWith("00:22:3F") || mac
							.startsWith("00:24:B2")))
			return new SkyV1Keygen(ssid, mac, level, enc);

		if (ssid.matches("TECOM-AH4(021|222)-[0-9a-zA-Z]{6}"))
			return new TecomKeygen(ssid, mac, level, enc);

		if (ssid.matches("InfostradaWiFi-[0-9a-zA-Z]{6}"))
			return new InfostradaKeygen(ssid, mac, level, enc);

		if (ssid.matches("WLAN_[0-9a-fA-F]{2}")
				&& (mac.startsWith("00:01:38") || mac.startsWith("00:16:38")
						|| mac.startsWith("00:01:13")
						|| mac.startsWith("00:01:1B") || mac
							.startsWith("00:19:5B")))
			return new Wlan2Keygen(ssid, mac, level, enc);

		if (ssid.matches("(WLAN|WiFi|YaCom)[0-9a-zA-Z]{6}"))
			return new Wlan6Keygen(ssid, mac, level, enc);

		if ((ssid.matches("OTE[0-9a-fA-F]{4}")) && (mac.startsWith("00:13:33")))
			return new OteBAUDKeygen(ssid, mac, level, enc);
		if ((ssid.matches("OTE[0-9a-fA-F]{6}"))
				&& ((mac.startsWith("C8:7B:5B"))
						|| (mac.startsWith("FC:C8:97"))
						|| (mac.startsWith("68:1A:B2"))
						|| (mac.startsWith("B0:75:D5")) || (mac
							.startsWith("38:46:08"))))
			return new OteKeygen(ssid, mac, level, enc);

		if (ssid.matches("MAXCOM[0-9a-zA-Z]{4}"))
			return new MaxcomKeygen(ssid, mac, level, enc);

		if (ssid.matches("PBS-[0-9a-fA-F]{6}"))
			return new PBSKeygen(ssid, mac, level, enc);

		if (ssid.matches("(PTV-|ptv|ptv-)[0-9a-zA-Z]{6}"))
			return new PtvKeygen(ssid, mac, level, enc);

		if (ssid.matches("Cabovisao-[0-9a-fA-F]{4}")) {
			if (mac.length() == 0 || mac.startsWith("C0:AC:54"))
				return new CabovisaoSagemKeygen(ssid, mac, level, enc);
		}

		if (ssid.equals("CONN-X"))
			return new ConnKeygen(ssid, mac, level, enc);

		if (ssid.equals("Andared"))
			return new AndaredKeygen(ssid, mac, level, enc);

		if (ssid.matches("Megared[0-9a-fA-F]{4}")) {
			// the final 4 characters of the SSID should match the final
			if (mac.length() == 0
					|| ssid.substring(ssid.length() - 4).equals(
							mac.replace(":", "").substring(8)))
				return new MegaredKeygen(ssid, mac, level, enc);
		}

		if (ssid.matches("wifimedia_R-[0-9a-zA-Z]{4}")
				&& mac.replace(":", "").length() == 12)
			return new WifimediaRKeygen(ssid, mac, level, enc);

		if (ssid.length() == 5
				&& (mac.startsWith("00:1F:90") || mac.startsWith("A8:39:44")
						|| mac.startsWith("00:18:01")
						|| mac.startsWith("00:20:E0")
						|| mac.startsWith("00:0F:B3")
						|| mac.startsWith("00:1E:A7")
						|| mac.startsWith("00:15:05")
						|| mac.startsWith("00:24:7B")
						|| mac.startsWith("00:26:62") || mac
							.startsWith("00:26:B8")))
			return new VerizonKeygen(ssid, mac, level, enc);

		if (ssid.matches("INFINITUM[0-9a-zA-Z]{4}")
				&& (mac.startsWith("00:25:9E") || mac.startsWith("00:25:68")
						|| mac.startsWith("00:22:A1")
						|| mac.startsWith("00:1E:10")
						|| mac.startsWith("00:18:82")
						|| mac.startsWith("00:0F:F2")
						|| mac.startsWith("00:E0:FC")
						|| mac.startsWith("28:6E:D4")
						|| mac.startsWith("54:A5:1B")
						|| mac.startsWith("F4:C7:14")
						|| mac.startsWith("28:5F:DB")
						|| mac.startsWith("30:87:30")
						|| mac.startsWith("4C:54:99")
						|| mac.startsWith("40:4D:8E")
						|| mac.startsWith("64:16:F0")
						|| mac.startsWith("78:1D:BA")
						|| mac.startsWith("84:A8:E4")
						|| mac.startsWith("04:C0:6F")
						|| mac.startsWith("5C:4C:A9")
						|| mac.startsWith("1C:1D:67")
						|| mac.startsWith("CC:96:A0") || mac
							.startsWith("20:2B:C1")))
			return new HuaweiKeygen(ssid, mac, level, enc);
		return new UnsupportedKeygen(ssid, mac, level, enc);

	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		final Set<String> keySetAlice = supportedAlices.keySet();
		final String[] arrayAlice = keySetAlice.toArray(new String[keySetAlice
				.size()]);
		dest.writeStringArray(arrayAlice);
		for (String key : keySetAlice)
			dest.writeTypedList(supportedAlices.get(key));
		final Set<String> keySetTeletu = supportedAlices.keySet();
		final String[] arrayTeletu = keySetTeletu
				.toArray(new String[keySetTeletu.size()]);
		dest.writeStringArray(arrayTeletu);
		for (String key : keySetTeletu)
			dest.writeTypedList(supportedAlices.get(key));
	}

	private WirelessMatcher(Parcel in) {
		supportedAlices = new HashMap<String, ArrayList<AliceMagicInfo>>();
		String[] keysAlice = in.createStringArray();
		for (String key : keysAlice) {
			final ArrayList<AliceMagicInfo> supportedAlicesList = new ArrayList<AliceMagicInfo>();
			in.readTypedList(supportedAlicesList, AliceMagicInfo.CREATOR);
			supportedAlices.put(key, supportedAlicesList);
		}
		supportedTeletu = new HashMap<String, ArrayList<TeleTuMagicInfo>>();
		String[] keysTeletu = in.createStringArray();
		for (String key : keysTeletu) {
			final ArrayList<TeleTuMagicInfo> supportedTeletuList = new ArrayList<TeleTuMagicInfo>();
			in.readTypedList(supportedTeletuList, TeleTuMagicInfo.CREATOR);
			supportedTeletu.put(key, supportedTeletuList);
		}
	}

	public static final Parcelable.Creator<WirelessMatcher> CREATOR = new Parcelable.Creator<WirelessMatcher>() {
		public WirelessMatcher createFromParcel(Parcel in) {
			return new WirelessMatcher(in);
		}

		public WirelessMatcher[] newArray(int size) {
			return new WirelessMatcher[size];
		}
	};

}
