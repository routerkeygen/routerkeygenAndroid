package org.exobel.routerkeygen.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.junit.Test;

public class KeygenTest {

	@Test
	public void testAliceItaly() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("Alice-37588990",
				"00:23:8e:48:e7:d4", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be Alice", keygen instanceof AliceItalyKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only 4 result", 4, results.size());
		assertEquals("The password should be djfveeeqyasxhhcqar8ypkcv",
				"djfveeeqyasxhhcqar8ypkcv", results.get(0));
		assertEquals("The password should be fsvcl1ujd3coikm49qowthn8",
				"fsvcl1ujd3coikm49qowthn8", results.get(1));
		assertEquals("The password should be y7xysqmqs9jooa7rersi7ayi",
				"y7xysqmqs9jooa7rersi7ayi", results.get(2));
		assertEquals("The password should be 9j4hm3ojq4brfdy6wcsuglwu",
				"9j4hm3ojq4brfdy6wcsuglwu", results.get(3));
	}

	@Test
	public void testAliceGermany() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("ALICE-WLANC3",
				"00:1E:40:A0:84:C4", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be Alice",
				keygen instanceof AliceGermanyKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only 1 result", 1, results.size());
		assertEquals("The password should be MGIwMjhjYTYzZmM0",
				"MGIwMjhjYTYzZmM0", results.get(0));
	}

	@Test
	public void testCONN() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("CONN-X", "", 0, "",
				new ZipInputStream(new FileInputStream(
						"../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be Conn", keygen instanceof ConnKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be 1234567890123", "1234567890123",
				results.get(0));
	}

	@Test
	public void testComtrend() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("JAZZTEL_1234",
				"00:1A:2B:11:22:33", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be Conn", keygen instanceof ComtrendKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only 512 result", 512, results.size());
		assertEquals("The password should be 9e701814299bf841e288",
				"9e701814299bf841e288", results.get(0));
		assertEquals("The password should be cb20cce51c5bc7457e08",
				"cb20cce51c5bc7457e08", results.get(511));
	}

	@Test
	public void testDiscus() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("Discus--DA1CC5",
				"00:1C:A2:DA:1C:C5", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be Discus", keygen instanceof DiscusKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be YW0150565", "YW0150565",
				results.get(0));
	}

	@Test
	public void testDlink() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("DLink-123456",
				"12:34:56:78:9a:bc", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be Dlink", keygen instanceof DlinkKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be 6r8qwaYHSNdpqdYw6aN8",
				"6r8qwaYHSNdpqdYw6aN8", results.get(0));
	}

	@Test
	public void testEasyBox() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("Arcor-910B02",
				"00:12:BF:91:0B:EC", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be Easybox", keygen instanceof EasyBoxKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be F9C8C9DEF", "F9C8C9DEF",
				results.get(0));
	}

	@Test
	public void testEircom() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("eircom2633 7520",
				"00:0f:cc:59:b0:9c", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be Eircom", keygen instanceof EircomKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be 29b2e9560b3a83a187ec5f2057",
				"29b2e9560b3a83a187ec5f2057", results.get(0));
	}

	@Test
	public void testHuawei() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("INFINITUM1be2",
				"64:16:F0:35:1C:FD", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be Huawei", keygen instanceof HuaweiKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be 3432333133", "3432333133",
				results.get(0));
	}

	@Test
	public void testMegared() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("Megared60EC",
				"FC:75:16:9F:60:EC", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be Megared", keygen instanceof MegaredKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be 75169F60EC", "75169F60EC",
				results.get(0));
		assertFalse("This network should not be supported",
				new WiFiNetwork("Megared60EC", "FC:75:16:9F:60:EB", 0, "",
						new ZipInputStream(new FileInputStream(
								"../res/raw/magic_info.zip")))
						.getSupportState() != Keygen.UNSUPPORTED);
	}

	@Test
	public void testOTE() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("OTE37cb4c",
				"B0:75:D5:37:CB:4C", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be OTE", keygen instanceof OteKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be b075d537cb4c", "b075d537cb4c",
				results.get(0));
	}

	@Test
	public void testOTEBAUD() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("OTEcb4c",
				"00:13:33:37:CB:4C", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be OTEBaud", keygen instanceof OteBAUDKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be b075d537cb4c", "000133337cb4c",
				results.get(0));
	}

	@Test
	public void testOTEHuawei() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("OTEcb4c",
				"E8:39:DF:F5:12:34", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be OTEHuawei",
				keygen instanceof OteHuaweiKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be 54919345", "54919345",
				results.get(0));
	}

	@Test
	public void testPBS() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("PBS-11222E",
				"38:22:9D:11:22:33", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be PBS", keygen instanceof PBSKeygen);
		assertEquals("Keygen should be supported", Keygen.UNLIKELY_SUPPORTED,
				keygen.getSupportState());
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be PcL2PgUcX0VhV", "PcL2PgUcX0VhV",
				results.get(0));

	}

	@Test
	public void testSpeedport500() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("WLAN-903704",
				"00:1D:19:90:37:DD", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 2 keygens", 2, wifi.getKeygens()
				.size());
		assertTrue("Keygen should be EasyBoxKeygen",
				wifi.getKeygens().get(0) instanceof EasyBoxKeygen);
		final Keygen keygen = wifi.getKeygens().get(1);
		assertTrue("Keygen should be Speedport500",
				keygen instanceof Speedport500Keygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be 1000 results", 1000, results.size());
		boolean found = false;
		for (String k : results) {
			if (k.equals("SP-0947DD059")) {
				found = true;
				break;
			}
		}
		assertTrue("SP-0947DD059 should have been found", found);

	}

	@Test
	public void testTeletu() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("TeleTu_00238EE528C7",
				"00:23:8E:E5:28:C7", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be TeleTu", keygen instanceof TeleTuKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals(
				"The password should be 15301Y0013305, not " + results.get(0),
				"15301Y0013305", results.get(0));
	}

	private static void downloadFromUrl(URL url, String localFilename)
			throws IOException {
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URLConnection urlConn = url.openConnection();// connect

			is = urlConn.getInputStream(); // get connection inputstream
			fos = new FileOutputStream(localFilename); // open outputstream to
														// local file

			byte[] buffer = new byte[4096]; // declare 4KB buffer
			int len;

			// while we have availble data, continue downloading and storing to
			// local file
			while ((len = is.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
		}
	}

	private final static String[] DICTIONARY_FILES = { "RouterKeygen_v3.dic",
			/*"RKDictionary.dic", */"RouterKeygen.dic" };
	private final static String[] DICTIONARY_URL = {
			"https://android-thomson-key-solver.googlecode.com/files/RouterKeygen_v3.dic",
			/*"https://android-thomson-key-solver.googlecode.com/files/RKDictionary.dic",*/ //this version uses native code
			"https://android-thomson-key-solver.googlecode.com/files/RouterKeygen.dic" };

	@Test
	public void testThomson() throws MalformedURLException, IOException {
		final WiFiNetwork wifi = new WiFiNetwork("Thomson41518c", "", 0, "",
				new ZipInputStream(new FileInputStream(
						"../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be Conn", keygen instanceof ThomsonKeygen);
		((ThomsonKeygen) keygen).setInternetAlgorithm(false);
		((ThomsonKeygen) keygen).setWebdic(new FileInputStream(
				"../res/raw/webdic.zip"));
		// Testing the internet version
		((ThomsonKeygen) keygen).setInternetAlgorithm(true);
		testThomsonResults(keygen);
		((ThomsonKeygen) keygen).setInternetAlgorithm(false);
		for (int i = 0; i < DICTIONARY_FILES.length; ++i) {
			if (!new File(DICTIONARY_FILES[i]).exists()) {
				System.out.println("Downloading " + DICTIONARY_FILES[i]);
				downloadFromUrl(new URL(DICTIONARY_URL[i]), DICTIONARY_FILES[i]);
			}
			((ThomsonKeygen) keygen).setDictionary(DICTIONARY_FILES[i]);
			testThomsonResults(keygen);
		}
	}

	private void testThomsonResults(Keygen keygen) {
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only three result", 3, results.size());
		assertEquals("The password should be 69237B667A", "69237B667A",
				results.get(0));
		assertEquals("The password should be 1C2D56E083", "1C2D56E083",
				results.get(1));
		assertEquals("The password should be 8F524DED99", "8F524DED99",
				results.get(2));

	}

	@Test
	public void testWifimediaR() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("wifimedia_R-1234",
				"00:26:5B:1E:28:A5", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be WifimediaR",
				keygen instanceof WifimediaRKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be two results", 2, results.size());
		assertEquals("The password should be 00265b1e28a0", "00265b1e28a0",
				results.get(0));
		assertEquals("The password should be 00265B1E28A0", "00265B1E28A0",
				results.get(1));
	}

	@Test
	public void testWAN6X() throws FileNotFoundException {
		final WiFiNetwork wifi = new WiFiNetwork("WLAN123456",
				"11:22:33:44:55:66", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertEquals("There should be only 1 keygen", 1, wifi.getKeygens()
				.size());
		final Keygen keygen = wifi.getKeygens().get(0);
		assertTrue("Keygen should be Wlan6", keygen instanceof Wlan6Keygen);
		List<String> results = keygen.getKeys();
		assertTrue("Error should  happen", keygen.getErrorCode() != 0);
		assertEquals("There should be only one result", 10, results.size());
		assertEquals("The password should be 5630556304607", "5630556304607",
				results.get(0));
		assertEquals("The password should be 5730446305616", "5730446305616",
				results.get(1));
		assertEquals("The password should be 5430776306625", "5430776306625",
				results.get(2));
		assertEquals("The password should be 5530666307634", "5530666307634",
				results.get(3));
		assertEquals("The password should be 5230116300643", "5230116300643",
				results.get(4));
		assertEquals("The password should be 5330006301652", "5330006301652",
				results.get(5));
		assertEquals("The password should be 5030336302661", "5030336302661",
				results.get(6));
		assertEquals("The password should be 5130226303670", "5130226303670",
				results.get(7));
		assertEquals("The password should be 5E30DD630C68F", "5E30DD630C68F",
				results.get(8));
		assertEquals("The password should be 5F30CC630D69E", "5F30CC630D69E",
				results.get(9));
	}

}
