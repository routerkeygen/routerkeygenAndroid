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
package org.exobel.routerkeygen.thomsonGenerator;

public class Generator {

	public static void main(String[] args) {
		Stage1.main(args);
		Stage2.main(args);
		Stage3.main(args);
		Stage4.main(args);
		Stage5.main(args);
    	System.out.println("Thomson Dictionary created. It's a file named 'RouterKeygen.dic'.");
    	System.out.println("Also it was created a a file named 'RouterKeygen.cfv' which is used for" +
    			" verification when downloading.");
    	System.out.println("The 'webdic.dic' file has informations needed for the Online fetching service.");
	}

}
