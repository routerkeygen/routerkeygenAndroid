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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


class AliceHandle extends DefaultHandler{
	private final Map<String, List<AliceMagicInfo>> supportedAlices;

	public AliceHandle(){
		supportedAlices = new HashMap<String, List<AliceMagicInfo>>();
	} 
	
	public void startElement(String uri, String localName,
	        String qName, Attributes attributes){
		int [] magic = new int[2];
		String serial;
		String mac;
		List<AliceMagicInfo> supported = supportedAlices.get(localName);
		if ( supported == null) {
			supported = new ArrayList<AliceMagicInfo>();
			supportedAlices.put(localName, supported);
		}
		serial = attributes.getValue("sn");
		mac = attributes.getValue("mac");
		magic[0] = Integer.parseInt(attributes.getValue("q"));
		magic[1] = Integer.parseInt(attributes.getValue("k"));
		supported.add(new AliceMagicInfo(localName, magic, serial, mac));

	}
	
	public void endElement( String namespaceURI,
	              String localName,
	              String qName ) throws SAXException {}
	
	public void characters( char[] ch, int start, int length )
	              throws SAXException {}
	
	
	public Map<String, List<AliceMagicInfo>> getSupportedAlices(){
		return supportedAlices;
	}
}

