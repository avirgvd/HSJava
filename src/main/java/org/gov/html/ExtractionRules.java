package org.gov.html;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gov.utils.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

/**
 * This class creates the data extraction rules for HTML to XML
 * @author govind
 *
 */
public class ExtractionRules {

	private String strRuleFileName;
	private String strDataHome;
	private Document doc;
	private Element root;
	private NodeList actionList;
	Hashtable<String, ExtractionAction> extractionActions = null;

	public ExtractionRules(String strDataHomeParam, String strRuleFileNameParam) {

		strDataHome = strDataHomeParam;
		strRuleFileName = strRuleFileNameParam;
	}

	public ExtractionRules() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        String strDataHome = "/home/govind/temp";
        String strInputHTMLFilePath = "/home/govind/temp/Presa1.htm";

        String strRuleFileName = strInputHTMLFilePath + ".rules.xml";
        // This object creates the data extraction rules for HTML to XML
		ExtractionRules extractionRules = new ExtractionRules(strDataHome, strRuleFileName);
		extractionRules.create();
		
		extractionRules.save();

	}

	public void addAction(String strXMLLine, Iterator<LineDetail> matchedlines) {

		Element item = doc.createElementNS(null, "Action");   // Create element
		
		Element node = doc.createElementNS(null, "target-xml");
		node.setTextContent(strXMLLine);
		item.appendChild(node);
		
		int iDuplicatesFlag = 0;
		while(matchedlines.hasNext()){
			LineDetail ld = matchedlines.next();
			
			Element node1 = doc.createElementNS(null, "source-html");
			
			node1.setTextContent(ld.strLineText);
			item.appendChild(node1);
			
			iDuplicatesFlag++;
			
			System.out.println(ld.strLineText);
		}
		
		if(iDuplicatesFlag > 1)
			item.setAttribute("warning", "WRN001: duplicate matches"); // indicate that there are duplicate matches with XML values in HTML
        root.appendChild( item );
		
	}

	public void create() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.newDocument();
            
            root = doc.createElementNS(null, "root"); // Create Root Element
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
	}

	public void save() {
		
		
        
        doc.appendChild( root );                            // Add Root to Document

        DOMImplementationRegistry registry;

        try {
			registry = DOMImplementationRegistry.newInstance();
	        DOMImplementationLS domImplLS = (DOMImplementationLS)registry.getDOMImplementation("LS");
	        
	        LSSerializer ser = domImplLS.createLSSerializer();  // Create a serializer for the DOM
	        LSOutput out = domImplLS.createLSOutput();
	        FileWriter writer = new FileWriter(strRuleFileName);        // Writer will be a String
	        out.setCharacterStream(writer);
	        ser.write(doc, out);                                // Serialize the DOM
	        
	        writer.close();

        } catch (ClassCastException e) {
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int open(String strRuleFileName2) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			Document dom = db.parse(strRuleFileName2);
			
			// get root element
			Element docEle = dom.getDocumentElement();
			
			String strNodeName = docEle.getNodeName();
			
			if(strNodeName.compareTo("root") == 0)
			{
				// This XML document has root element so looks like valid XML
				// TODO: wish I know better way to validate XML documents
				// Since we got root node, lets flatten all the nodes under this
				
				actionList = docEle.getChildNodes();
				
				if(null == actionList) return -1;
				
			}
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public String getHTMLPathForXMLItem(String strXMLItem) {
		
		String strHTMLPath = null;
		
		if(extractionActions == null){
			loadExtractionActions();
		}
		
		ExtractionAction action = extractionActions.get(strXMLItem);
		
		if(null != action){
			strHTMLPath = action.sourceHTMLItems.get(0).strSourceHTMLItem;
		}
		
		return StringUtil.GetLeftOf(strHTMLPath, "=");
	}

	private void loadExtractionActions() {
		
		extractionActions = new Hashtable<String, ExtractionAction>();
		
		int icount = actionList.getLength();
		for(int i = 0; i < actionList.getLength(); i++){ // for AAA
			Node node = actionList.item(i);
			
			NodeList nl = node.getChildNodes();
			ExtractionAction action = new ExtractionAction();
			
			int iCount1 = nl.getLength();
			for(int k = 0; k < nl.getLength(); k++){ // for BBB
				Node node1 = nl.item(k);
				
				String nodename = node1.getNodeName();
				
				if(nodename == "target-xml"){
					action.strTargetXMLItem = node1.getTextContent(); 
				}
				else if(nodename == "source-html"){
					action.sourceHTMLItems.add(new FlatHTMLItem(node1.getTextContent()));
				}
			} // for BBB
			
			extractionActions.put(action.strTargetXMLItem, action);
			
		} // for AAA
		
	} // private void loadExtractionActions()

}
