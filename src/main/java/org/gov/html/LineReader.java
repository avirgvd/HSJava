package org.gov.html;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class LineReader {
	
	
	private String strFilePath;
	private LineNumberReader reader;

	public LineReader(String str){
		strFilePath = str;
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void open() {
		try {
			reader = new LineNumberReader(new FileReader(strFilePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String readNextLine() {

		String strLine = "";
		
		try {
			strLine = reader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strLine;
//		return "ROOT/#node:submitter/#node:fieldelement/#attr:Name=Dean Nellis:2";
	}

	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
