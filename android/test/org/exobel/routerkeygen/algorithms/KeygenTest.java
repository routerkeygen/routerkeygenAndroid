package org.exobel.routerkeygen.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.exobel.routerkeygen.WirelessMatcher;
import org.junit.Test;

public class KeygenTest {

	@Test
	public void testAlice() throws FileNotFoundException {
		Keygen keygen = WirelessMatcher.getKeygen("Alice-37588990",
				"00:23:8e:48:e7:d4", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertTrue("Keygen should be Alice", keygen instanceof AliceKeygen);
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
	public void testCONN() throws FileNotFoundException {
		Keygen keygen = WirelessMatcher.getKeygen("CONN-X", "", 0, "",
				new ZipInputStream(new FileInputStream(
						"../res/raw/magic_info.zip")));
		assertTrue("Keygen should be Conn", keygen instanceof ConnKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be 1234567890123", "1234567890123",
				results.get(0));
	}

	@Test
	public void testDiscus() throws FileNotFoundException {
		Keygen keygen = WirelessMatcher.getKeygen("Discus--DA1CC5",
				"00:1C:A2:DA:1C:C5", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertTrue("Keygen should be Discus", keygen instanceof DiscusKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be YW0150565", "YW0150565",
				results.get(0));
	}

	@Test
	public void testDlink() throws FileNotFoundException {
		Keygen keygen = WirelessMatcher.getKeygen("DLink-123456",
				"12:34:56:78:9a:bc", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertTrue("Keygen should be Dlink", keygen instanceof DlinkKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be 6r8qwaYHSNdpqdYw6aN8",
				"6r8qwaYHSNdpqdYw6aN8", results.get(0));
	}

	@Test
	public void testEasyBox() throws FileNotFoundException {
		Keygen keygen = WirelessMatcher.getKeygen("Arcor-910B02",
				"00:12:BF:91:0B:EC", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertTrue("Keygen should be Easybox", keygen instanceof EasyBoxKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be F9C8C9DEF", "F9C8C9DEF",
				results.get(0));
	}

	@Test
	public void testEircom() throws FileNotFoundException {
		Keygen keygen = WirelessMatcher.getKeygen("eircom2633 7520",
				"00:0f:cc:59:b0:9c", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertTrue("Keygen should be Eircom", keygen instanceof EircomKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be 29b2e9560b3a83a187ec5f2057",
				"29b2e9560b3a83a187ec5f2057", results.get(0));
	}

	@Test
	public void testHuawei() throws FileNotFoundException {
		Keygen keygen = WirelessMatcher.getKeygen("INFINITUM1be2",
				"64:16:F0:35:1C:FD", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertTrue("Keygen should be Huawei", keygen instanceof HuaweiKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be 3432333133", "3432333133",
				results.get(0));
	}

	@Test
	public void testMegared() throws FileNotFoundException {
		Keygen keygen = WirelessMatcher.getKeygen("Megared60EC",
				"FC:75:16:9F:60:EC", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertTrue("Keygen should be Megared", keygen instanceof MegaredKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be 75169F60EC", "75169F60EC",
				results.get(0));
		assertFalse(
				"Keygen should not be Megared",
				WirelessMatcher.getKeygen("Megared60EC", "FC:75:16:9F:60:EB",
						0, "", new ZipInputStream(new FileInputStream(
								"../res/raw/magic_info.zip"))) instanceof MegaredKeygen);
	}

	@Test
	public void testOTE() throws FileNotFoundException {
		Keygen keygen = WirelessMatcher.getKeygen("OTE37cb4c",
				"B0:75:D5:37:CB:4C", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertTrue("Keygen should be OTE", keygen instanceof OteKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be b075d537cb4c", "b075d537cb4c",
				results.get(0));
	}

	@Test
	public void testOTEBAUD() throws FileNotFoundException {
		Keygen keygen = WirelessMatcher.getKeygen("OTEcb4c",
				"00:13:33:37:CB:4C", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertTrue("Keygen should be OTEBaud", keygen instanceof OteBAUDKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be b075d537cb4c", "000133337cb4c",
				results.get(0));
	}

	@Test
	public void testOTEHuawei() throws FileNotFoundException {
		Keygen keygen = WirelessMatcher.getKeygen("OTEcb4c",
				"E8:39:DF:F5:12:34", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
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
		Keygen keygen = WirelessMatcher.getKeygen("PBS-11222E",
				"38:22:9D:11:22:2E", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertTrue("Keygen should be PBS", keygen instanceof PBSKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals("The password should be PcL2PgUcX0VhV", "PcL2PgUcX0VhV",
				results.get(0));

	}

	@Test
	public void testTeletu() throws FileNotFoundException {
		Keygen keygen = WirelessMatcher.getKeygen("TeleTu_00238EE528C7",
				"00:23:8E:E5:28:C7", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
		assertTrue("Keygen should be TeleTu", keygen instanceof TeleTuKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen", 0, keygen.getErrorCode());
		assertEquals("There should be only one result", 1, results.size());
		assertEquals(
				"The password should be 15301Y0013305, not " + results.get(0),
				"15301Y0013305", results.get(0));
	}

	@Test
	public void testWifimediaR() throws FileNotFoundException {
		Keygen keygen = WirelessMatcher.getKeygen("wifimedia_R-1234",
				"00:26:5B:1E:28:A5", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
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
		Keygen keygen = WirelessMatcher.getKeygen("WLAN123456",
				"11:22:33:44:55:66", 0, "", new ZipInputStream(
						new FileInputStream("../res/raw/magic_info.zip")));
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
