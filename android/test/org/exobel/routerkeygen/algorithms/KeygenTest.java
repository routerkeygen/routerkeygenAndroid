package org.exobel.routerkeygen.algorithms;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.exobel.routerkeygen.WirelessMatcher;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeygenTest {
	/*Non working on this branch*/
	static WirelessMatcher matcher;
	@BeforeClass
	public static void initMatcher() throws FileNotFoundException{
		matcher = new WirelessMatcher(new FileInputStream("../res/raw/alice.txt"));
	}
	
	@Test
	public void testDiscus() {
		Keygen keygen = matcher.getKeygen("Discus--DA1CC5", "00:1C:A2:DA:1C:C5", 0, "");
		assertTrue("Keygen should be Discus",keygen instanceof DiscusKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only one result", 1, results.size());
		assertEquals("The password should be YW0150565", "YW0150565", results.get(0));		
	}

	@Test
	public void testHuawei() {
		Keygen keygen = matcher.getKeygen("INFINITUM1be2", "64:16:F0:35:1C:FD", 0, "");
		assertTrue("Keygen should be Huawei",keygen instanceof HuaweiKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only one result", 1, results.size());
		assertEquals("The password should be 3432333133", "3432333133", results.get(0));		
	}

	@Test
	public void testDlink() {
		Keygen keygen = matcher.getKeygen("DLink-123456", "12:34:56:78:9a:bc", 0, "");
		assertTrue("Keygen should be Dlink",keygen instanceof DlinkKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only one result", 1, results.size());
		assertEquals("The password should be 6r8qwaYHSNdpqdYw6aN8", "6r8qwaYHSNdpqdYw6aN8", results.get(0));		
	}

	@Test
	public void testEircom() {
		Keygen keygen = matcher.getKeygen("eircom2633 7520", "00:0f:cc:59:b0:9c", 0, "");
		assertTrue("Keygen should be Eircom",keygen instanceof EircomKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only one result", 1, results.size());
		assertEquals("The password should be 29b2e9560b3a83a187ec5f2057", "29b2e9560b3a83a187ec5f2057", results.get(0));		
	}
	
	@Test
	public void testAlice() {
		Keygen keygen = matcher.getKeygen("Alice-37588990", "00:23:8e:48:e7:d4", 0, "");
		assertTrue("Keygen should be Alice",keygen instanceof AliceKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only 4 result", 4, results.size());
		assertEquals("The password should be djfveeeqyasxhhcqar8ypkcv", "djfveeeqyasxhhcqar8ypkcv", results.get(0));
		assertEquals("The password should be fsvcl1ujd3coikm49qowthn8", "fsvcl1ujd3coikm49qowthn8", results.get(1));
		assertEquals("The password should be y7xysqmqs9jooa7rersi7ayi", "y7xysqmqs9jooa7rersi7ayi", results.get(2));
		assertEquals("The password should be 9j4hm3ojq4brfdy6wcsuglwu", "9j4hm3ojq4brfdy6wcsuglwu", results.get(3));		
	}
	@Test
	public void testEasyBox() {
		Keygen keygen = new EasyBoxKeygen("Arcor-910B02", "00:12:BF:91:0B:EC", 0, "");
		assertTrue("Keygen should be Easybox",keygen instanceof EasyBoxKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only one result", 1, results.size());
		assertEquals("The password should be F9C8C9DEF", "F9C8C9DEF", results.get(0));		
	}
	

	@Test
	public void testOTE() {
		Keygen keygen = matcher.getKeygen("OTE37cb4c", "", 0, "");
		assertTrue("Keygen should be OTE",keygen instanceof OteKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only one result", 1, results.size());
		assertEquals("The password should be b075d537cb4c", "b075d537cb4c", results.get(0));		
	}
	

	@Test
	public void testCONN() {
		Keygen keygen = matcher.getKeygen("CONN-X", "", 0, "");
		assertTrue("Keygen should be Conn",keygen instanceof ConnKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only one result", 1, results.size());
		assertEquals("The password should be 1234567890123", "1234567890123", results.get(0));		
	}
	

	@Test
	public void testPBS() {
		Keygen keygen = matcher.getKeygen("PBS-11222E", "38:22:9D:11:22:2E", 0, "");
		assertTrue("Keygen should be PBS",keygen instanceof PBSKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only one result", 1, results.size());
		assertEquals("The password should be PcL2PgUcX0VhV", "PcL2PgUcX0VhV", results.get(0));		
	}
	

	@Test
	public void testMegared() {
		Keygen keygen = matcher.getKeygen("Megared60EC", "FC:75:16:9F:60:EC", 0, "");
		assertTrue("Keygen should be Megared",keygen instanceof MegaredKeygen);
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only one result", 1, results.size());
		assertEquals("The password should be 75169F60EC", "75169F60EC", results.get(0));
		assertFalse("Keygen should not be Megared", matcher.getKeygen("Megared60EC", "FC:75:16:9F:60:EB", 0, "") instanceof MegaredKeygen);
	}
	
	@Test
	public void testLWAN6X() {
		Keygen keygen = matcher.getKeygen("WLAN123456", "11:22:33:44:55:66", 0, "");
		assertTrue("Keygen should be Wlan6",keygen instanceof Wlan6Keygen);
		List<String> results = keygen.getKeys();
		assertTrue("Error should  happen",keygen.getErrorCode() != 0);
		assertEquals("There should only one result", 10, results.size());
		assertEquals("The password should be 5630556304607", "5630556304607", results.get(0));
		assertEquals("The password should be 5730446305616", "5730446305616", results.get(1));
		assertEquals("The password should be 5430776306625", "5430776306625", results.get(2));
		assertEquals("The password should be 5530666307634", "5530666307634", results.get(3));
		assertEquals("The password should be 5230116300643", "5230116300643", results.get(4));
		assertEquals("The password should be 5330006301652", "5330006301652", results.get(5));
		assertEquals("The password should be 5030336302661", "5030336302661", results.get(6));
		assertEquals("The password should be 5130226303670", "5130226303670", results.get(7));
		assertEquals("The password should be 5E30DD630C68F", "5E30DD630C68F", results.get(8));
		assertEquals("The password should be 5F30CC630D69E", "5F30CC630D69E", results.get(9));
	}
}
