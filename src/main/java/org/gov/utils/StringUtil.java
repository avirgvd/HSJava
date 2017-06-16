package org.gov.utils;

public class StringUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static String GetLastRightOf(String strInputFilePath,
			String separator) {
		
		return strInputFilePath.substring(1 + strInputFilePath.lastIndexOf(separator));
	}

	public static String GetLeftOf(String str, String seperator) {
		
		int index = str.indexOf(seperator);

		if(-1 == index) return str;
		else{
			return str.substring(0, index);
		}
		
	}

	public static String GetFirstRightOf(String strLine, String seperator) {
		
		int index = strLine.indexOf(seperator);
		
		if(index == -1) return "";
		else 
			return strLine.substring(seperator.length() + strLine.indexOf(seperator));
	}

}
