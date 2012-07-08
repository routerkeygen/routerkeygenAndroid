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
