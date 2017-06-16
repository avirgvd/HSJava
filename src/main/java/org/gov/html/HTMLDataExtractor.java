package org.gov.html;

import java.util.Iterator;

import org.gov.utils.StringUtil;

public class HTMLDataExtractor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		HTMLDigester htmlextractor = new HTMLDigester();
        
        String strDataHome = "/home/govind/temp";
        String strInputHTMLFilePath = "/home/govind/temp/Presa1.htm";

        String strRuleFileName = "/home/govind/temp/Presa1.htm.rules.xml";
        
        String resultFilePathHTML = htmlextractor.digest("/home/govind/temp/Presa2.htm");

		XMLScanner xmlscanner = new XMLScanner();
		
		xmlscanner.setDataHome(strDataHome);
		
		String resultFilePathXML = xmlscanner.flattenXML("/home/govind/temp/Presa1.xml");
		
		ExtractionRules extractionRules = new ExtractionRules();
		extractionRules.open(strRuleFileName);
		
		// make a loop for the code below
		String strXMLItem = "";
		String strHTMLPath = "";
		XMLGenerator xmlGenerator = new XMLGenerator();
		xmlGenerator.open();
		
		for(int i = 0; ; i++){
			strXMLItem = xmlscanner.getNextItem();
			
			if(strXMLItem == null) break;
			
			strHTMLPath = extractionRules.getHTMLPathForXMLItem(strXMLItem);
	
			System.out.println("strHTMLPath = " + strHTMLPath + "\nand strXMLItem = " + strXMLItem);
			
			Iterator<LineDetail> lines = htmlextractor.getHTMLContentByPath(strHTMLPath);
			
			String linetext = lines.next().strLineText;
			System.out.println("MATCH = " + linetext);
			
			String value = StringUtil.GetFirstRightOf(linetext, "=");
			
			int iret = xmlGenerator.addXMLData(strXMLItem, value);
		}
		// Now with the lines read I just need to write a function that 
		// generates XML element for each line
		
		xmlGenerator.save("/home/govind/temp/Presa2.xml");
		
		xmlscanner.close();
		

	}

}
