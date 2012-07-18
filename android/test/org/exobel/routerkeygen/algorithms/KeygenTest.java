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
		matcher = new WirelessMatcher(new FileInputStream("res/raw/alice.xml"));
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
		Keygen keygen = matcher.getKeygen("CONN-1", "", 0, "");
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
}
