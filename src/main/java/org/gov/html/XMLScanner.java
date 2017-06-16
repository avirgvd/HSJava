package org.gov.html;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gov.utils.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLScanner {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

		XMLScanner xmlscanner = new XMLScanner();
		
		xmlscanner.setDataHome("/home/govind/temp");
		
		String resultFilePath = xmlscanner.flattenXML("/home/govind/temp/Presa1.xml");

	}


	/**
	 * Location where the generated files will be stored
	 */
	private String strDataHomePath;
	private String strInputXMLFileName;
	
	private static final String OUT_FILE_EXT = ".out";
	
	String strFlatXMLFilePath;
	private LineWriter lineWriter;
	private LineReader lineReader;


	public void setDataHome(String string) {
		
		strDataHomePath = string;
		
	}

	public String flattenXML(String xmlpath) {
		
		// Get the file name from path
		strInputXMLFileName = xmlpath.substring(1 + xmlpath.lastIndexOf(File.separator));
		
		System.out.println("file name is: " + strInputXMLFileName);

		lineWriter = new LineWriter(strDataHomePath, strInputXMLFileName, "out");
		lineWriter.open();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			Document dom = db.parse(xmlpath);
			
			// get root element
			Element docEle = dom.getDocumentElement();
			
			String strNodeName = docEle.getNodeName();
			
			if(strNodeName.isEmpty() == false)
			{
				// This XML document has root element so looks like valid XML
				// TODO: wish I know better way to validate XML documents
				// Since we got root node, lets flatten all the nodes under this
				
				NodeList nl = docEle.getChildNodes();
				
				if(null == nl) return null;
				
				int iret = flattenXML(nl, "/#node:" + strNodeName);
			}
			
//			System.out.println(docEle.getNodeName());
//			System.out.println(docEle.toString());
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch blockElement ele = null;
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		lineWriter.close();
		strFlatXMLFilePath = lineWriter.getGeneraterFilePath();
		
		return strFlatXMLFilePath;
	}

	private int flattenXML(NodeList nl, String localpath) {
		
		if(null == nl) 
		{
			return 0;
		}
		
		String thispath = "";
			
		int size = nl.getLength();
		
		for(int i = 0; i < size; i++)
		{
			Node node = nl.item(i);
			String nodename = node.getNodeName();
			
			if("" == nodename) continue;
			
			thispath = localpath + "/#node:" + nodename;
			
			NamedNodeMap attribs = node.getAttributes();
			
			if(null != attribs)
			{
			
				int attribs_size = attribs.getLength();
				
				// look for attribute name/value pairs first
				for(int k = 0; k < attribs_size; k++)
				{
					Node attrib = attribs.item(k);
					if(null == attrib) continue;
					
					thispath += "/#attr:" + attrib.getNodeName() + "=" + attrib.getNodeValue() + ":" + attrib.getNodeType();
					System.out.println(thispath);
					lineWriter.write(thispath);
				}
			}
			
			// now look for node values			
			String nodevalue = node.getNodeValue();
			
			if(null != nodevalue) 
			{
				nodevalue = nodevalue.trim();
				
				if(nodevalue.isEmpty() == false)
				{				
					thispath = localpath + "=" + nodevalue + ":" + node.getNodeType();
					System.out.println(thispath);
					lineWriter.write(thispath);
				}
			}

			NodeList nlChildren = node.getChildNodes();
			
			flattenXML(nlChildren, thispath);
						
		} //for(int i = 0; i < size; i++)
		
//		System.out.println("EXIT2");
		return 0;
	}

	/**
	 * Parses the string that holds flat XML node string like "/#node:root/#node:submitter/#node:fieldelement/#attr:Name=VALUEE:2"
	 * And returns the VALUEE that is present in the RHS of =. The RHS contains value + datatype. 
	 * This function omits datatype value and returns only VALUEE
	 * @param strXMLLine
	 * @return 
	 */
	public static String getXMLValueFromFlatNode(String strXMLLine) {
		
		String str = StringUtil.GetLastRightOf(strXMLLine, "=");
		
		return StringUtil.GetLeftOf(str, ":");
	}

	public String getNextItem() {
		
		if(lineReader == null){
			lineReader = new LineReader(strFlatXMLFilePath);
			lineReader.open();
		}
		
		return lineReader.readNextLine();
	}

	public void close() {
		if(lineReader == null)
			lineReader.close();
		
	}

}
