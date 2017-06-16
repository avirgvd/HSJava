package org.gov.html;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gov.utils.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class XMLGenerator {

	 Document doc = null;
	 Element rootelement = null;
	 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		XMLGenerator xmlgenerator = new XMLGenerator();
		
		xmlgenerator.addXMLData("/#node:root/#node:submitter/#node:fieldelement/#attr:Name=Dean Nellis:2", "Adem Prasad");

	}

	public int open() {
		try {
			rootelement = null;
			doc = null;
			
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        dbf.setNamespaceAware(true);
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        doc = db.newDocument();
	        
		} catch ( Exception ex ) {
		    ex.printStackTrace();
		    return -1;
		}
			
		return 0;
	}

	public int addXMLData(String xmlelementpath, String value) {

		if(null == doc) return -1; // need to call open method before calling this method
		
	    // Typical patterns in xmlelementpath are     
		// root/#node:submitter/#node:fieldelement/#attr:Name=Dean Nellis:2
		// /#node:root/#node:submitter/#node:age=USD:3
		
		String path = xmlelementpath;
		String strElementDetail = "";
		
		String strElementName = "";
		String strElementType = "";
		
		HashMap<String, Element> tableXMLElements = new HashMap<String, Element>();
		
		ArrayList<Element> elementlist = new ArrayList<Element>();
		Element ele = null;
		
		
		path = StringUtil.GetFirstRightOf(path, "/#");
		
		Element prevElement = null;
		
		boolean flagLoopOver = false;
		for(int i = 0; flagLoopOver == false; i++){
//			strElementDetail = StringUtil.GetLeftOf(path, ":");
			
			if(path == "") break; // all the nodes must be created by now and nothing left to process
			
			strElementDetail = StringUtil.GetLeftOf(path, "/#");
			
			path = StringUtil.GetFirstRightOf(path, "/#");
			
			strElementType = StringUtil.GetLeftOf(strElementDetail, ":");
			
			strElementName = StringUtil.GetFirstRightOf(strElementDetail, ":");

			
			if(strElementType.compareTo("node") == 0){
				
				// check if this node is already created
				
				if(i == 0){
					// This  corresponds to root node so check if root node already created
					if(rootelement != null){
						// dont need to create root element again
						ele = rootelement;
					}
						
				}
				else {
					// this is obviously not the root element 
					// however the variable prevElement will have parent node 
					// check the parent node if it already has the child with this node name
//					prevElement.get
				}
				
				
				// Lookup to see if this node was already created. 
				// TODO: I dont like this approach as there can be two nodes at different levels with same name
//				ele = tableXMLElements.get(strElementName);
//				elementlist.add(i, strElementName);
				
				// check if this node was already created before
				if(null == ele)	{
					
					// check if this is the final node and it has node value
					if(path == ""){
						// this is the final node
						strElementName = StringUtil.GetLeftOf(strElementName, "=");
						ele = doc.createElementNS(null, strElementName);
						// node value exists so add it now
//						ele.setNodeValue(value);
						ele.setTextContent(value);
						// now set the flag that this loop is over
						flagLoopOver = true;
						
					}
					else
						ele = doc.createElementNS(null, strElementName);
					
					tableXMLElements.put(strElementName, ele);

					if(i > 0){
						// add child node
//						String strPrevNode = elementlist.get(i - 1);
//						Element prevElement = tableXMLElements.get(strPrevNode);
						prevElement.appendChild(ele);
					}
					else {
						// this must be the root node
						if(rootelement == null)
							rootelement = ele;
						
					}
					
				} //if(null == ele)	{
				
				// set the prev element just before going to the next child node
				prevElement = ele;
				ele = null;
				
			}
			else if(strElementType.compareTo("attr") == 0){
				prevElement.setAttribute(StringUtil.GetLeftOf(strElementName, "="), value);
				// now set the flag that this loop is over
				flagLoopOver = true;
				
			}
			
		} // for(int i = 0; ; i++)

//		rootelement.appendChild(tableXMLElements.get(1));
//		doc.appendChild(rootelement);

		
		return 0;
	}

	public void save(String xmlfilepath) {
		try {
			
			rootelement.normalize();
			doc.appendChild(rootelement);
			
			DOMImplementationRegistry registry;
			registry = DOMImplementationRegistry.newInstance();
			DOMImplementationLS domImplLS = (DOMImplementationLS)registry.getDOMImplementation("LS");
			
			LSSerializer ser = domImplLS.createLSSerializer();  // Create a serializer for the DOM
			LSOutput out = domImplLS.createLSOutput();
			FileWriter writer;

			writer = new FileWriter(xmlfilepath);
			out.setCharacterStream(writer);
	        ser.write(doc, out);        

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        // Writer will be a String
		catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
