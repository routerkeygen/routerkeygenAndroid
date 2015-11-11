package org.exobel.routerkeygen;

import org.exobel.routerkeygen.algorithms.AlcatelLucentKeygen;
import org.exobel.routerkeygen.algorithms.AliceGermanyKeygen;
import org.exobel.routerkeygen.algorithms.AliceItalyKeygen;
import org.exobel.routerkeygen.algorithms.AndaredKeygen;
import org.exobel.routerkeygen.algorithms.ArcadyanKeygen;
import org.exobel.routerkeygen.algorithms.ArnetPirelliKeygen;
import org.exobel.routerkeygen.algorithms.AxtelKeygen;
import org.exobel.routerkeygen.algorithms.BelkinKeygen;
import org.exobel.routerkeygen.algorithms.CabovisaoSagemKeygen;
import org.exobel.routerkeygen.algorithms.ComtrendKeygen;
import org.exobel.routerkeygen.algorithms.ConnKeygen;
import org.exobel.routerkeygen.algorithms.CytaKeygen;
import org.exobel.routerkeygen.algorithms.CytaZTEKeygen;
import org.exobel.routerkeygen.algorithms.DiscusKeygen;
import org.exobel.routerkeygen.algorithms.DlinkKeygen;
import org.exobel.routerkeygen.algorithms.EircomKeygen;
import org.exobel.routerkeygen.algorithms.HG824xKeygen;
import org.exobel.routerkeygen.algorithms.HuaweiKeygen;
import org.exobel.routerkeygen.algorithms.InfostradaKeygen;
import org.exobel.routerkeygen.algorithms.InterCableKeygen;
import org.exobel.routerkeygen.algorithms.Keygen;
import org.exobel.routerkeygen.algorithms.MaxcomKeygen;
import org.exobel.routerkeygen.algorithms.MegaredKeygen;
import org.exobel.routerkeygen.algorithms.MeoPirelliKeygen;
import org.exobel.routerkeygen.algorithms.NetFasterKeygen;
import org.exobel.routerkeygen.algorithms.OnoKeygen;
import org.exobel.routerkeygen.algorithms.OteBAUDKeygen;
import org.exobel.routerkeygen.algorithms.OteHuaweiKeygen;
import org.exobel.routerkeygen.algorithms.OteKeygen;
import org.exobel.routerkeygen.algorithms.PBSKeygen;
import org.exobel.routerkeygen.algorithms.PirelliKeygen;
import org.exobel.routerkeygen.algorithms.PtvKeygen;
import org.exobel.routerkeygen.algorithms.Sitecom2100Keygen;
import org.exobel.routerkeygen.algorithms.SitecomX500Keygen;
import org.exobel.routerkeygen.algorithms.SitecomWLR341_400xKeygen;
import org.exobel.routerkeygen.algorithms.SkyV1Keygen;
import org.exobel.routerkeygen.algorithms.Speedport500Keygen;
import org.exobel.routerkeygen.algorithms.TecomKeygen;
import org.exobel.routerkeygen.algorithms.TeleTuKeygen;
import org.exobel.routerkeygen.algorithms.TelseyKeygen;
import org.exobel.routerkeygen.algorithms.ThomsonKeygen;
import org.exobel.routerkeygen.algorithms.TplinkKeygen;
import org.exobel.routerkeygen.algorithms.VerizonKeygen;
import org.exobel.routerkeygen.algorithms.WifimediaRKeygen;
import org.exobel.routerkeygen.algorithms.Wlan2Keygen;
import org.exobel.routerkeygen.algorithms.Wlan6Keygen;
import org.exobel.routerkeygen.algorithms.ZyxelKeygen;
import org.exobel.routerkeygen.config.AliceConfigParser;
import org.exobel.routerkeygen.config.AliceMagicInfo;
import org.exobel.routerkeygen.config.CytaConfigParser;
import org.exobel.routerkeygen.config.CytaMagicInfo;
import org.exobel.routerkeygen.config.CytaZTEConfigParser;
import org.exobel.routerkeygen.config.NetfasterConfigParser;
import org.exobel.routerkeygen.config.NetfasterMagicInfo;
import org.exobel.routerkeygen.config.OTEHuaweiConfigParser;
import org.exobel.routerkeygen.config.TeleTuConfigParser;
import org.exobel.routerkeygen.config.TeleTuMagicInfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class WirelessMatcher {

    private static Map<String, ArrayList<AliceMagicInfo>> supportedAlices = null;
    private static Map<String, ArrayList<TeleTuMagicInfo>> supportedTeletu = null;
    private static Map<String, ArrayList<CytaMagicInfo>> supportedCytaZTEs = null;
    private static Map<String, ArrayList<CytaMagicInfo>> supportedCytas = null;
    private static ArrayList<NetfasterMagicInfo> supportedNetfasters;
    private static String[] supportedOTE = null;

    public synchronized static ArrayList<Keygen> getKeygen(String ssid,
                                                           String mac, ZipInputStream magicInfo) {
        final ArrayList<Keygen> keygens = new ArrayList<>();

        if (mac.startsWith("00:19:C7") || mac.startsWith("18:80:F5") || mac.startsWith("A4:C7:DE")
                || mac.startsWith("A8:AD:3D") || mac.startsWith("AC:9C:E4") || mac.startsWith("D0:54:2D")
                || mac.startsWith("E0:1D:3B") || mac.startsWith("E0:30:05"))
            keygens.add(new AlcatelLucentKeygen(ssid, mac));

        if (ssid.matches("[aA]lice-[0-9]{8}")) {
            if (supportedAlices == null) {
                supportedAlices = AliceConfigParser.parse(getEntry("alice.txt",
                        magicInfo));
            }
            final List<AliceMagicInfo> supported = supportedAlices.get(ssid
                    .substring(6, 9));
            if (supported != null && supported.size() > 0) {
                String macProcessed = mac.replace(":", "").toUpperCase(
                        Locale.getDefault());
                if (macProcessed.length() < 6
                        || !macProcessed.substring(0, 6).equals(
                        supported.get(0).getMac())) {
                    macProcessed = supported.get(0).getMac();
                } else {
                    macProcessed = mac;
                }
                keygens.add(new AliceItalyKeygen(ssid, macProcessed, supported));
            }
        }
        if (mac.startsWith("00:1E:40") || mac.startsWith("00:25:5E"))
            keygens.add(new AliceGermanyKeygen(ssid, mac));

        if (ssid.equals("Andared"))
            keygens.add(new AndaredKeygen(ssid, mac));

        if (mac.startsWith("00:12:BF") || mac.startsWith("00:1A:2A")
                || mac.startsWith("00:1D:19") || mac.startsWith("00:23:08")
                || mac.startsWith("00:26:4D") || mac.startsWith("50:7E:5D")
                || mac.startsWith("1C:C6:3C") || mac.startsWith("74:31:70")
                || mac.startsWith("7C:4F:B5") || mac.startsWith("7E:4F:B5")
                || mac.startsWith("88:25:2C") || mac.startsWith("84:9C:A6")
                || mac.startsWith("88:03:55"))
            keygens.add(new ArcadyanKeygen(ssid, mac));

        if (mac.startsWith("00:08:27") || mac.startsWith("00:13:C8")
                || mac.startsWith("00:17:C2") || mac.startsWith("00:19:3E")
                || mac.startsWith("00:1C:A2") || mac.startsWith("00:1D:8B")
                || mac.startsWith("00:22:33") || mac.startsWith("00:23:8E")
                || mac.startsWith("00:25:53") || mac.startsWith("00:8C:54")
                || mac.startsWith("30:39:F2") || mac.startsWith("38:22:9D")
                || mac.startsWith("64:87:D7") || mac.startsWith("74:88:8B")
                || mac.startsWith("84:26:15") || mac.startsWith("A4:52:6F")
                || mac.startsWith("A4:5D:A1") || mac.startsWith("D0:D4:12")
                || mac.startsWith("D4:D1:84") || mac.startsWith("DC:0B:1A")
                || mac.startsWith("F0:84:2F")) {
            keygens.add(new ArnetPirelliKeygen(ssid, mac));
            keygens.add(new MeoPirelliKeygen(ssid, mac));
        }

        if (ssid.matches("(AXTEL|AXTEL-XTREMO)-[0-9a-fA-F]{4}")) {
            final String ssidSubpart = ssid.substring(ssid.length() - 4);
            final String macShort = mac.replace(":", "");
            if (macShort.length() < 12
                    || ssidSubpart.equalsIgnoreCase(macShort.substring(8)))
                keygens.add(new AxtelKeygen(ssid, mac));
        }

        if (ssid.matches("^(B|b)elkin(\\.|_)[0-9a-fA-F]{3,6}$")
                || mac.startsWith("94:44:52") || mac.startsWith("08:86:3B")
                || mac.startsWith("EC:1A:59"))
            keygens.add(new BelkinKeygen(ssid, mac));

        if (ssid.matches("Cabovisao-[0-9a-fA-F]{4}")) {
            if (mac.length() == 0 || mac.startsWith("C0:AC:54"))
                keygens.add(new CabovisaoSagemKeygen(ssid, mac));
        }

        if (ssid.equals("CONN-X"))
            keygens.add(new ConnKeygen(ssid, mac));

        if (ssid.matches("conn-x[0-9a-fA-F]{6}") ||
                mac.startsWith("48:28:2F") || mac.startsWith("B0:75:D5") ||
                mac.startsWith("C8:7B:5B") || mac.startsWith("FC:C8:97") ||
                mac.startsWith("68:1A:B2") || mac.startsWith("38:46:08") ||
                mac.startsWith("4C:09:9B") || mac.startsWith("4C:09:B4") ||
                mac.startsWith("8C:E0:81") || mac.startsWith("DC:02:8E") ||
                mac.startsWith("2C:26:C5") || mac.startsWith("FC:C8:97") ||
                mac.startsWith("CC:1A:FA") || mac.startsWith("A0:EC:80") ||
                mac.startsWith("54:22:F8") || mac.startsWith("14:60:80")) {
            keygens.add(new ConnKeygen(ssid, mac));
        }

        if (mac.startsWith("00:1C:A2") || mac.startsWith("00:17:C2") || mac.startsWith("00:19:3E") ||
                mac.startsWith("00:23:8E") || mac.startsWith("00:25:53") || mac.startsWith("38:22:9D") || mac.startsWith("64:87:D7") ||
                mac.startsWith("DC:0B:1A")) {
            if (supportedCytas == null) {
                supportedCytas = CytaConfigParser.parse(getEntry("cyta_bases.txt", magicInfo));
            }
            final String filteredMac = mac.replace(":", "");
            if (filteredMac.length() == 12) {
                final String key = filteredMac.substring(0, 8);
                final ArrayList<CytaMagicInfo> supportedCyta = supportedCytas.get(key);
                if (supportedCyta != null) {
                    keygens.add(new CytaKeygen(ssid, mac, supportedCyta));
                }
            }
        }

        if (mac.startsWith("CC:1A:FA") || mac.startsWith("14:60:80") || mac.startsWith("DC:02:8E") || mac.startsWith("CC:7B:35") ||
                mac.startsWith("20:89:86") || mac.startsWith("2C:95:7F") || mac.startsWith("F8:DF:A8") || mac.startsWith("EC:8A:4C")) {
            if (supportedCytaZTEs == null) {
                supportedCytaZTEs = CytaZTEConfigParser.parse(getEntry("cyta_zte_bases.txt", magicInfo));
            }
            keygens.add(new CytaZTEKeygen(ssid, mac, supportedCytaZTEs));
        }


        if (ssid.matches("Discus--?[0-9a-fA-F]{6}"))
            keygens.add(new DiscusKeygen(ssid, mac));

        if (ssid.matches("(DL|dl)ink-[0-9a-fA-F]{6}")
                || mac.startsWith("00:05:5D") || mac.startsWith("00:0D:88")
                || mac.startsWith("00:0F:3D") || mac.startsWith("00:11:95")
                || mac.startsWith("00:13:46") || mac.startsWith("00:15:E9")
                || mac.startsWith("00:17:9A") || mac.startsWith("00:19:5B")
                || mac.startsWith("00:1B:11") || mac.startsWith("00:1C:F0")
                || mac.startsWith("00:1E:58") || mac.startsWith("00:21:91")
                || mac.startsWith("00:22:B0") || mac.startsWith("00:24:01")
                || mac.startsWith("00:26:5A") || mac.startsWith("14:D6:4D")
                || mac.startsWith("1C:7E:E5") || mac.startsWith("28:10:7B")
                || mac.startsWith("34:08:04") || mac.startsWith("5C:D9:98")
                || mac.startsWith("84:C9:B2") || mac.startsWith("90:94:E4")
                || mac.startsWith("AC:F1:DF") || mac.startsWith("B8:A3:86")
                || mac.startsWith("BC:F6:85") || mac.startsWith("C8:BE:19")
                || mac.startsWith("CC:B2:55") || mac.startsWith("F0:7D:68")
                || mac.startsWith("FC:75:16"))
            keygens.add(new DlinkKeygen(ssid, mac));

        if (ssid.matches("[eE]ircom[0-7]{4} ?[0-7]{4}")) {
            if (mac.length() == 0) {
                final String filteredSsid = ssid.replace(" ", "");
                final String end = Integer
                        .toHexString(Integer.parseInt(filteredSsid
                                .substring(filteredSsid.length() - 8), 8) ^ 0x000fcc);
                mac = "00:0F:CC" + ":" + end.substring(0, 2) + ":"
                        + end.substring(2, 4) + ":" + end.substring(4, 6);
            }
            keygens.add(new EircomKeygen(ssid, mac));
        }
        if (ssid.matches("INFINITUM[0-9a-zA-Z]{4}") || (mac.startsWith("00:18:82") || mac.startsWith("00:1E:10")
                || mac.startsWith("00:22:A1") || mac.startsWith("00:25:68") || mac.startsWith("00:25:9E")
                || mac.startsWith("00:34:FE") || mac.startsWith("00:46:4B") || mac.startsWith("00:66:4B")
                || mac.startsWith("00:E0:FC") || mac.startsWith("00:F8:1C") || mac.startsWith("04:02:1F")
                || mac.startsWith("04:BD:70") || mac.startsWith("04:C0:6F") || mac.startsWith("04:F9:38")
                || mac.startsWith("08:19:A6") || mac.startsWith("08:63:61") || mac.startsWith("08:7A:4C")
                || mac.startsWith("08:E8:4F") || mac.startsWith("0C:37:DC") || mac.startsWith("0C:96:BF")
                || mac.startsWith("0C:D6:BD") || mac.startsWith("10:1B:54") || mac.startsWith("10:47:80")
                || mac.startsWith("10:51:72") || mac.startsWith("10:C6:1F") || mac.startsWith("14:B9:68")
                || mac.startsWith("18:C5:8A") || mac.startsWith("1C:1D:67") || mac.startsWith("1C:8E:5C")
                || mac.startsWith("20:08:ED") || mac.startsWith("20:0B:C7") || mac.startsWith("20:2B:C1")
                || mac.startsWith("20:F3:A3") || mac.startsWith("24:09:95") || mac.startsWith("24:1F:A0")
                || mac.startsWith("24:69:A5") || mac.startsWith("24:7F:3C") || mac.startsWith("24:9E:AB")
                || mac.startsWith("24:DB:AC") || mac.startsWith("28:31:52") || mac.startsWith("28:3C:E4")
                || mac.startsWith("28:5F:DB") || mac.startsWith("28:6E:D4") || mac.startsWith("2C:CF:58")
                || mac.startsWith("30:87:30") || mac.startsWith("30:D1:7E") || mac.startsWith("30:F3:35")
                || mac.startsWith("34:00:A3") || mac.startsWith("34:6B:D3") || mac.startsWith("34:CD:BE")
                || mac.startsWith("38:F8:89") || mac.startsWith("3C:47:11") || mac.startsWith("3C:DF:BD")
                || mac.startsWith("3C:F8:08") || mac.startsWith("40:4D:8E") || mac.startsWith("40:CB:A8")
                || mac.startsWith("44:55:B1") || mac.startsWith("48:46:FB") || mac.startsWith("48:62:76")
                || mac.startsWith("4C:1F:CC") || mac.startsWith("4C:54:99") || mac.startsWith("4C:8B:EF")
                || mac.startsWith("4C:B1:6C") || mac.startsWith("50:9F:27") || mac.startsWith("50:A7:2B")
                || mac.startsWith("54:39:DF") || mac.startsWith("54:89:98") || mac.startsWith("54:A5:1B")
                || mac.startsWith("58:1F:28") || mac.startsWith("58:2A:F7") || mac.startsWith("58:7F:66")
                || mac.startsWith("5C:4C:A9") || mac.startsWith("5C:7D:5E") || mac.startsWith("5C:B3:95")
                || mac.startsWith("5C:B4:3E") || mac.startsWith("5C:F9:6A") || mac.startsWith("60:DE:44")
                || mac.startsWith("60:E7:01") || mac.startsWith("64:16:F0") || mac.startsWith("64:3E:8C")
                || mac.startsWith("64:A6:51") || mac.startsWith("68:89:C1") || mac.startsWith("68:8F:84")
                || mac.startsWith("68:A0:F6") || mac.startsWith("68:A8:28") || mac.startsWith("70:54:F5")
                || mac.startsWith("70:72:3C") || mac.startsWith("70:7B:E8") || mac.startsWith("70:A8:E3")
                || mac.startsWith("74:88:2A") || mac.startsWith("74:A0:63") || mac.startsWith("78:1D:BA")
                || mac.startsWith("78:6A:89") || mac.startsWith("78:D7:52") || mac.startsWith("78:F5:FD")
                || mac.startsWith("7C:60:97") || mac.startsWith("7C:A2:3E") || mac.startsWith("80:38:BC")
                || mac.startsWith("80:71:7A") || mac.startsWith("80:B6:86") || mac.startsWith("80:D0:9B")
                || mac.startsWith("80:FB:06") || mac.startsWith("84:5B:12") || mac.startsWith("84:A8:E4")
                || mac.startsWith("84:DB:AC") || mac.startsWith("88:53:D4") || mac.startsWith("88:86:03")
                || mac.startsWith("88:A2:D7") || mac.startsWith("88:CE:FA") || mac.startsWith("88:E3:AB")
                || mac.startsWith("8C:34:FD") || mac.startsWith("90:17:AC") || mac.startsWith("90:4E:2B")
                || mac.startsWith("90:67:1C") || mac.startsWith("94:04:9C") || mac.startsWith("94:77:2B")
                || mac.startsWith("9C:28:EF") || mac.startsWith("9C:37:F4") || mac.startsWith("9C:C1:72")
                || mac.startsWith("A4:99:47") || mac.startsWith("A4:DC:BE") || mac.startsWith("AC:4E:91")
                || mac.startsWith("AC:85:3D") || mac.startsWith("AC:E2:15") || mac.startsWith("AC:E8:7B")
                || mac.startsWith("B0:5B:67") || mac.startsWith("B4:15:13") || mac.startsWith("B4:30:52")
                || mac.startsWith("B8:BC:1B") || mac.startsWith("BC:25:E0") || mac.startsWith("BC:76:70")
                || mac.startsWith("BC:9C:31") || mac.startsWith("C0:70:09") || mac.startsWith("C4:05:28")
                || mac.startsWith("C4:07:2F") || mac.startsWith("C8:51:95") || mac.startsWith("C8:D1:5E")
                || mac.startsWith("CC:53:B5") || mac.startsWith("CC:96:A0") || mac.startsWith("CC:A2:23")
                || mac.startsWith("CC:CC:81") || mac.startsWith("D0:2D:B3") || mac.startsWith("D0:3E:5C")
                || mac.startsWith("D0:7A:B5") || mac.startsWith("D4:40:F0") || mac.startsWith("D4:6A:A8")
                || mac.startsWith("D4:6E:5C") || mac.startsWith("D4:94:E8") || mac.startsWith("D4:B1:10")
                || mac.startsWith("D4:F9:A1") || mac.startsWith("D8:49:0B") || mac.startsWith("DC:D2:FC")
                || mac.startsWith("E0:19:1D") || mac.startsWith("E0:24:7F") || mac.startsWith("E0:36:76")
                || mac.startsWith("E0:97:96") || mac.startsWith("E4:35:C8") || mac.startsWith("E4:68:A3")
                || mac.startsWith("E4:C2:D1") || mac.startsWith("E8:08:8B") || mac.startsWith("E8:BD:D1")
                || mac.startsWith("E8:CD:2D") || mac.startsWith("EC:23:3D") || mac.startsWith("EC:38:8F")
                || mac.startsWith("EC:CB:30") || mac.startsWith("F4:55:9C") || mac.startsWith("F4:8E:92")
                || mac.startsWith("F4:9F:F3") || mac.startsWith("F4:C7:14") || mac.startsWith("F4:DC:F9")
                || mac.startsWith("F4:E3:FB") || mac.startsWith("F8:01:13") || mac.startsWith("F8:3D:FF")
                || mac.startsWith("F8:4A:BF") || mac.startsWith("F8:98:B9") || mac.startsWith("F8:BF:09")
                || mac.startsWith("F8:E8:11") || mac.startsWith("FC:48:EF") || mac.startsWith("FC:E3:3C")))
            keygens.add(new HuaweiKeygen(ssid, mac));

        if (mac.startsWith("00:18:82") || mac.startsWith("00:1E:10") || mac.startsWith("00:22:A1")
                || mac.startsWith("00:25:68") || mac.startsWith("00:25:9E") || mac.startsWith("00:34:FE")
                || mac.startsWith("00:46:4B") || mac.startsWith("00:66:4B") || mac.startsWith("00:E0:FC")
                || mac.startsWith("00:F8:1C") || mac.startsWith("08:19:A6") || mac.startsWith("08:63:61")
                || mac.startsWith("08:7A:4C") || mac.startsWith("08:E8:4F") || mac.startsWith("10:1B:54")
                || mac.startsWith("10:47:80") || mac.startsWith("10:51:72") || mac.startsWith("10:C6:1F")
                || mac.startsWith("20:08:ED") || mac.startsWith("20:0B:C7") || mac.startsWith("20:2B:C1")
                || mac.startsWith("20:F3:A3") || mac.startsWith("28:31:52") || mac.startsWith("28:3C:E4")
                || mac.startsWith("28:5F:DB") || mac.startsWith("28:6E:D4") || mac.startsWith("48:46:FB")
                || mac.startsWith("48:62:76") || mac.startsWith("70:54:F5") || mac.startsWith("70:72:3C")
                || mac.startsWith("70:7B:E8") || mac.startsWith("70:A8:E3") || mac.startsWith("80:38:BC")
                || mac.startsWith("80:71:7A") || mac.startsWith("80:B6:86") || mac.startsWith("80:D0:9B")
                || mac.startsWith("80:FB:06") || mac.startsWith("AC:4E:91") || mac.startsWith("AC:85:3D")
                || mac.startsWith("AC:E2:15") || mac.startsWith("AC:E8:7B") || mac.startsWith("CC:53:B5")
                || mac.startsWith("CC:96:A0") || mac.startsWith("CC:A2:23") || mac.startsWith("CC:CC:81")
                || mac.startsWith("D4:40:F0") || mac.startsWith("D4:6A:A8") || mac.startsWith("D4:6E:5C")
                || mac.startsWith("D4:94:E8") || mac.startsWith("D4:B1:10") || mac.startsWith("D4:F9:A1")
                || mac.startsWith("E0:19:1D") || mac.startsWith("E0:24:7F") || mac.startsWith("E0:36:76")
                || mac.startsWith("E0:97:96") || mac.startsWith("F8:01:13") || mac.startsWith("F8:3D:FF")
                || mac.startsWith("F8:4A:BF") || mac.startsWith("F8:98:B9") || mac.startsWith("F8:BF:09")
                || mac.startsWith("F8:E8:11"))
            keygens.add(new HG824xKeygen(ssid, mac));

        if (ssid.matches("InfostradaWiFi-[0-9a-zA-Z]{6}"))
            keygens.add(new InfostradaKeygen(ssid, mac));

        if (ssid.startsWith("InterCable")
                && (mac.startsWith("00:15") || mac.startsWith("00:1D")))
            keygens.add(new InterCableKeygen(ssid, mac));

        if (ssid.matches("MAXCOM[0-9a-zA-Z]{4}"))
            keygens.add(new MaxcomKeygen(ssid, mac));

        if (ssid.matches("Megared[0-9a-fA-F]{4}")) {
            // the final 4 characters of the SSID should match the final
            if (mac.length() == 0
                    || ssid.substring(ssid.length() - 4).equals(
                    mac.replace(":", "").substring(8)))
                keygens.add(new MegaredKeygen(ssid, mac));
        }

        if (mac.startsWith("00:05:59")) {
            if (supportedNetfasters == null) {
                supportedNetfasters = NetfasterConfigParser.parse(getEntry(
                        "netfaster_bases.txt", magicInfo));
            }
            keygens.add(new NetFasterKeygen(ssid, mac, supportedNetfasters));
        }

		/* ssid must be of the form P1XXXXXX0000X or p1XXXXXX0000X */
        if (ssid.matches("[Pp]1[0-9]{6}0{4}[0-9]"))
            keygens.add(new OnoKeygen(ssid, mac));

        if (ssid.matches("OTE[0-9a-fA-F]{4}") && (mac.startsWith("00:13:33")))
            keygens.add(new OteBAUDKeygen(ssid, mac));

        if (ssid.matches("OTE[0-9a-fA-F]{6}"))
            /*
             * && ((mac.startsWith("C8:7B:5B")) || (mac.startsWith("FC:C8:97"))
			 * || (mac.startsWith("68:1A:B2")) || (mac.startsWith("B0:75:D5"))
			 * || (mac .startsWith("38:46:08"))))
			 */
            keygens.add(new OteKeygen(ssid, mac));

        if (ssid.toUpperCase(Locale.getDefault()).startsWith("OTE")
                && (mac.startsWith("E8:39:DF:F5")
                || mac.startsWith("E8:39:DF:F6") || mac
                .startsWith("E8:39:DF:FD"))) {
            if (supportedOTE == null) {
                supportedOTE = OTEHuaweiConfigParser.parse(getEntry(
                        "ote_huawei.txt", magicInfo));
            }
            final String filteredMac = mac.replace(":", "");
            final int target = Integer.parseInt(filteredMac.substring(8), 16);
            if (filteredMac.length() == 12
                    && target > (OteHuaweiKeygen.MAGIC_NUMBER - supportedOTE.length))
                keygens.add(new OteHuaweiKeygen(ssid, mac,
                        supportedOTE[OteHuaweiKeygen.MAGIC_NUMBER - target]));
        }

        if (ssid.matches("PBS-[0-9a-fA-F]{6}") || mac.startsWith("00:08:27")
                || mac.startsWith("00:13:C8") || mac.startsWith("00:17:C2")
                || mac.startsWith("00:19:3E") || mac.startsWith("00:1C:A2")
                || mac.startsWith("00:1D:8B") || mac.startsWith("00:22:33")
                || mac.startsWith("00:23:8E") || mac.startsWith("00:25:53")
                || mac.startsWith("30:39:F2") || mac.startsWith("38:22:9D")
                || mac.startsWith("64:87:D7") || mac.startsWith("74:88:8B")
                || mac.startsWith("A4:52:6F") || mac.startsWith("D4:D1:84"))
            keygens.add(new PBSKeygen(ssid, mac));

        if (ssid.matches("FASTWEB-1-(000827|0013C8|0017C2|00193E|001CA2|001D8B|"
                + "002233|00238E|002553|00A02F|080018|3039F2|38229D|6487D7)[0-9A-Fa-f]{6}")) {
            if (mac.length() == 0) {
                final String end = ssid.substring(ssid.length() - 12);
                mac = end.substring(0, 2) + ":" + end.substring(2, 4) + ":"
                        + end.substring(4, 6) + ":" + end.substring(6, 8) + ":"
                        + end.substring(8, 10) + ":" + end.substring(10, 12);
            }
            keygens.add(new PirelliKeygen(ssid, mac));
        }

        if (ssid.matches("(PTV-|ptv|ptv-)[0-9a-zA-Z]{6}"))
            keygens.add(new PtvKeygen(ssid, mac));

        if (mac.startsWith("00:0C:F6") || mac.startsWith("64:D1:A3")) {
            keygens.add(new SitecomX500Keygen(ssid, mac));
            keygens.add(new Sitecom2100Keygen(ssid, mac));
        }

        if (ssid.toLowerCase(Locale.getDefault()).matches("^sitecom[0-9a-f]{6}$") ||
                (mac.startsWith("00:0C:F6") || mac.startsWith("64:D1:A3"))) {
            if (mac.replace(":", "").length() != 12) {
                keygens.add(new SitecomWLR341_400xKeygen(ssid, "00:0C:F6" + ssid.substring(7)));
                keygens.add(new SitecomWLR341_400xKeygen(ssid, "64:D1:A3" + ssid.substring(7)));
            } else {
                keygens.add(new SitecomWLR341_400xKeygen(ssid, mac));
            }
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
            keygens.add(new SkyV1Keygen(ssid, mac));

        if (ssid.matches("WLAN-[0-9a-fA-F]{6}")
                && (mac.startsWith("00:12:BF") || mac.startsWith("00:1A:2A") || mac
                .startsWith("00:1D:19")))
            keygens.add(new Speedport500Keygen(ssid, mac));

        if (ssid.matches("TECOM-AH4(021|222)-[0-9a-zA-Z]{6}"))
            keygens.add(new TecomKeygen(ssid, mac));

        if (ssid.toLowerCase(Locale.getDefault()).startsWith("teletu")) {
            if (supportedTeletu == null) {
                supportedTeletu = TeleTuConfigParser.parse(getEntry(
                        "tele2.txt", magicInfo));
            }
            String filteredMac = mac.replace(":", "");
            if (filteredMac.length() != 12
                    && ssid.matches("TeleTu_[0-9a-fA-F]{12}"))
                mac = filteredMac = ssid.substring(7);
            if (filteredMac.length() == 12) {
                final List<TeleTuMagicInfo> supported = supportedTeletu
                        .get(filteredMac.substring(0, 6));
                if (supported != null && supported.size() > 0) {
                    final int macIntValue = Integer.parseInt(
                            filteredMac.substring(6), 16);
                    for (TeleTuMagicInfo magic : supported) {
                        if (macIntValue >= magic.getRange()[0]
                                && macIntValue <= magic.getRange()[1]) {
                            keygens.add(new TeleTuKeygen(ssid, mac, magic));
                        }
                    }
                }
            }
        }

        if (ssid.matches("FASTWEB-(1|2)-(002196|00036F)[0-9A-Fa-f]{6}")) {
            if (mac.length() == 0) {
                final String end = ssid.substring(ssid.length() - 12);
                mac = end.substring(0, 2) + ":" + end.substring(2, 4) + ":"
                        + end.substring(4, 6) + ":" + end.substring(6, 8) + ":"
                        + end.substring(8, 10) + ":" + end.substring(10, 12);
            }
            keygens.add(new TelseyKeygen(ssid, mac));
        }

        if (ssid.matches("(Thomson|Blink|SpeedTouch|O2Wireless|O2wireless|Orange-|ORANGE-|INFINITUM|"
                + "BigPond|Otenet|Bbox-|DMAX|privat|TN_private_|CYTA|Vodafone-|Optimus|OptimusFibra|MEO-)[0-9a-fA-F]{6}"))
            keygens.add(new ThomsonKeygen(ssid, mac));

        if (mac.startsWith("F8:D1:11"))
            keygens.add(new TplinkKeygen(ssid, mac));

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
            keygens.add(new VerizonKeygen(ssid, mac));

        if (ssid.matches("wifimedia_R-[0-9a-zA-Z]{4}")
                && mac.replace(":", "").length() == 12)
            keygens.add(new WifimediaRKeygen(ssid, mac));

        if (ssid.matches("WLAN_[0-9a-fA-F]{2}")
                && (mac.startsWith("00:01:38") || mac.startsWith("00:16:38")
                || mac.startsWith("00:01:13")
                || mac.startsWith("00:01:1B") || mac
                .startsWith("00:19:5B")))
            keygens.add(new Wlan2Keygen(ssid, mac));

        if (ssid.matches("(WLAN|WiFi|YaCom)[0-9a-zA-Z]{6}"))
            keygens.add(new Wlan6Keygen(ssid, mac));

        if (ssid.matches("(WLAN|JAZZTEL)_[0-9a-fA-F]{4}")) {
            if (mac.startsWith("00:1F:A4") || mac.startsWith("F4:3E:61")
                    || mac.startsWith("40:4A:03"))
                keygens.add(new ZyxelKeygen(ssid, mac));

            if (mac.startsWith("00:1B:20") || mac.startsWith("64:68:0C")
                    || mac.startsWith("00:1D:20") || mac.startsWith("00:23:F8")
                    || mac.startsWith("38:72:C0") || mac.startsWith("30:39:F2")
                    || mac.startsWith("8C:0C:A3") || mac.startsWith("5C:33:8E")
                    || mac.startsWith("C8:6C:87") || mac.startsWith("D0:AE:EC")
                    || mac.startsWith("00:19:15") || mac.startsWith("00:1A:2B"))
                keygens.add(new ComtrendKeygen(ssid, mac));
        }

        return keygens;
    }

    private static InputStream getEntry(String filename,
                                        ZipInputStream magicInfo) {
        ZipEntry entry = null;
        try {
            do {
                entry = magicInfo.getNextEntry();
            } while (entry != null && !filename.equals(entry.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (entry != null)
            return magicInfo;
        return null;
    }
}
