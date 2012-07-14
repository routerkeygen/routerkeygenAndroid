package org.exobel.routerkeygen.algorithms;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class KeygenTest {

	@Test
	public void testDiscus() {
		Keygen keygen = new DiscusKeygen("Discus--DA1CC5", "00:1C:A2:DA:1C:C5", 0, "");
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only one result", 1, results.size());
		assertEquals("The password should be YW0150565", "YW0150565", results.get(0));		
	}

	@Test
	public void testHuawei() {
		Keygen keygen = new HuaweiKeygen("INFINITUM1858", "81:23:45:AB:CD:EF", 0, "");
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only one result", 1, results.size());
		assertEquals("The password should be 3436623865", "3436623865", results.get(0));		
	}

	@Test
	public void testDlink() {
		Keygen keygen = new DlinkKeygen("DLink-123456", "12:34:56:78:9a:bc", 0, "");
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only one result", 1, results.size());
		assertEquals("The password should be 6r8qwaYHSNdpqdYw6aN8", "6r8qwaYHSNdpqdYw6aN8", results.get(0));		
	}

	@Test
	public void testEircom() {
		Keygen keygen = new EircomKeygen("eircom2633 7520", "00:0f:cc:59:b0:9c", 0, "");
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only one result", 1, results.size());
		assertEquals("The password should be 29b2e9560b3a83a187ec5f2057", "29b2e9560b3a83a187ec5f2057", results.get(0));		
	}
	
	@Test
	public void testEasyBox() {
		Keygen keygen = new EasyBoxKeygen("Arcor-910B02", "00:12:BF:91:0B:EC", 0, "");
		List<String> results = keygen.getKeys();
		assertEquals("Errors should not happen",0, keygen.getErrorCode());
		assertEquals("There should only one result", 1, results.size());
		assertEquals("The password should be F9C8C9DEF", "F9C8C9DEF", results.get(0));		
	}
}
