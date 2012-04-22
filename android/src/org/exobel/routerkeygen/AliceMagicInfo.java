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

public class AliceMagicInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1640975633984337261L;
	String alice;
	private int [] magic;
	private String serial;
	String mac;
	public AliceMagicInfo(String alice,  int[] magic,
			String serial, String mac) {
		this.alice = alice;
		this.setMagic(magic);
		this.setSerial(serial);
		this.mac = mac;
	}
    public int [] getMagic() {
        return magic;
    }
    public void setMagic(int [] magic) {
        this.magic = magic;
    }
    public String getSerial() {
        return serial;
    }
    public void setSerial(String serial) {
        this.serial = serial;
    }
}
