package org.gov.html;

import java.util.Iterator;

public class HTMLDataExtractionTrainer extends ConversionTrainer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/**
		 * 
		 * 	Description: The training requires one input HTML with valid contents and XML that contains 
		 *	the HTML data extracted from the input HTML file. 
		 * 	The training function learns from the sample extracted XML to understand
		 * 	which parts of HTML needs to be extracted and what is target XML element that 
		 * 	should get the data
		 */
        HTMLDigester htmlextractor = new HTMLDigester();
        
        String strDataHome = "/home/govind/temp";
        String strInputHTMLFilePath = "/home/govind/temp/Presa1.htm";

        String strRuleFileName = strInputHTMLFilePath + ".rules.xml";
        // This object creates the data extraction rules for HTML to XML
		ExtractionRules extractionRules = new ExtractionRules(strDataHome, strRuleFileName);
		extractionRules.create();
        
        String resultFilePathHTML = htmlextractor.digest("/home/govind/temp/Presa1.htm");

		XMLScanner xmlscanner = new XMLScanner();
		
		xmlscanner.setDataHome(strDataHome);
		
		String resultFilePathXML = xmlscanner.flattenXML("/home/govind/temp/Presa1.xml");
		
		LineReader linereaderXMLFile = new LineReader(resultFilePathXML);
//		LineReader linereaderHTMLFile = new LineReader(resultFilePathHTML);
		
		linereaderXMLFile.open();
//		linereaderHTMLFile.open();
		
		String strXMLLine = "";
//		String strHTMLLine = "";
		Iterator<LineDetail> matchedlines = null;

		int iFlag = 1;
		String strValue = "";
		
		do{
		
			strXMLLine = linereaderXMLFile.readNextLine();
//			System.out.println("strXMLLine=" + strXMLLine);
			
			if(null == strXMLLine) iFlag = 0;
			else{
				strValue = XMLScanner.getXMLValueFromFlatNode(strXMLLine);
				
				matchedlines = htmlextractor.getAllMatches(strValue);
				
				extractionRules.addAction(strXMLLine, matchedlines);
				
				System.out.println(strValue);
			}
		
		}while(1 == iFlag);
		
//		strHTMLLine = linereaderHTMLFile.readNextLine();
		
		extractionRules.save();
//		linereaderHTMLFile.close();
		linereaderXMLFile.close();
		
		
		

	}

}
