package org.gov.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LineWriter {

	
	private BufferedWriter outFileStream;
	
	private String strOutFilePath;
	
	public LineWriter(String strDataRoot, String strFileName, String strExt) {

		strOutFilePath = strDataRoot + File.separator + strFileName + "." + strExt;
	}
	
	public int close()
	{
		if(null != outFileStream)
		{
			try {
				outFileStream.flush();
				outFileStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}
		}
		
		return 0;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

		String strExt = "out";
		String strDataRoot = "/home/govind/temp";
		String strFileName = "Presa.xml";
		
		LineWriter lineWriter = new LineWriter(strDataRoot, strFileName, strExt);
		
		lineWriter.write("");
	}


	public int write(String strLine) {
		
		try {
			outFileStream.write(strLine + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	
		return 0;
	}

	public String getGeneraterFilePath() {
		
		return strOutFilePath;
	}

	public void open() {
		try {
			outFileStream = new BufferedWriter(new FileWriter(strOutFilePath));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

} //public class LineWriter
