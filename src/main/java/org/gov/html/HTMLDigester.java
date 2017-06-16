package org.gov.html;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.gov.utils.StringUtil;
import org.htmlparser.Node;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.ParserUtils;


public class HTMLDigester {
	
	public StringBuffer inputRefinedData;
	public StringBuffer outputXMLData;
	private String strFlatHTMLPath;
	private String strSearchText;
	public String matchPath;
//	public String scanPath;
	public StringBuffer scanPathList;
	int ileafcount; // used only for scan mode
	
	private HashMap<String, Boolean> hashtableIgnoreTags;
	private int isearchmode;
	
	public LineWriter lineWriter;
	public String strDataHomePath;
	
	public HTMLDigester()
	{
		// Specify list of tags that need to be ignored from content search
		hashtableIgnoreTags = new HashMap<String, Boolean>();
		
		hashtableIgnoreTags.put("org.htmlparser.tags.ScriptTag", true);
		hashtableIgnoreTags.put("org.htmlparser.tags.StyleTag", true);
	}
	
	
	public int scanHTMLContent(Node node) {
		
		ileafcount = 0;

		scanContent(node, "");
		return 0;
	}
	
	private int scanContent(Node node, String parentpath) {
		
		String localscanpath = "";
		// Check for ignores like ScriptTag
		if(hashtableIgnoreTags.get(node.getClass().getName()) != null) return 0;

    	NodeList nodes = node.getChildren();
    	String strValue = "";
    	
    	// if this is not the leaf node then search recursively
    	if(nodes == null) 
    	{
    		localscanpath = parentpath;
    		if(node instanceof TextNode)
    		{
    			// remove line carriage from the node text.
    			strValue = node.getText().trim();
    			strValue = strValue.replace("\n", " ");
    			strValue = strValue.replace("\r", " ");
    			
    			
    			localscanpath = localscanpath + "=" + strValue;
    		}
    		
//    		System.out.println(strValue);
    		System.out.println("SCAN: " + localscanpath);
    		lineWriter.write("SCAN:" + localscanpath);
    		ileafcount ++;
    		
   			return 2; // reached scan reached end of the tree
    	}
    	
    	int size = nodes.size();
    	
    	Node nextnode;
    	
    	for (int i = 0; i < size; i++)
    	{
    		nextnode = nodes.elementAt(i);

    		String strTagPattern = "";

    		strTagPattern = HTMLParser.getTagMatchPattern(nextnode);
			localscanpath = parentpath + ":" + strTagPattern + "#" + String.valueOf(i);
    		
    		// RECURSION
    		scanContent(nextnode, localscanpath);
    		
//    		localscanpath = localscanpath + ":" + strret;
    	}
    			
		return 0;
	}	// public int searchByTextContent(Node node) {
	
	
	public int searchByTextContent(Node nextnode, String searchStr) {
		
		strSearchText = searchStr;
		matchPath = "";
//		scanPath = "";
		
		return searchByTextContent(nextnode);
	}
	
	private int searchByTextContent(Node node) {
		
		// Check for ignores like ScriptTag
		if(hashtableIgnoreTags.get(node.getClass().getName()) != null) return 0;
		
//    	System.out.println( node.getClass().getName());
//    	System.out.println( "<" + node.getText() + ">");
    	
    	if( 0 == compareHTMLText(node, strSearchText))
    	{
    		System.out.println( "MATCH!");
    		int iret = analyseMatchLocation(node);
    		return 1;
    	}
    	
    	// if this node text is not matched then search recursively 
    	// under this node using depth-first approach
    	NodeList nodes = node.getChildren();
    	
    	if(nodes == null)    return 0; // reached end of the tree nodes so go back and try your luck elsewhere
    	
    	int size = nodes.size();
    	
    	Node nextnode;
    	
    	for (int i = 0; i < size; i++)
    	{
    		nextnode = nodes.elementAt(i);
    		
    		String strTagPattern = "";
    		
    		// RECURSION
    		int iret = searchByTextContent(nextnode);
    		if(1 == iret) 
    		{
    			strTagPattern = HTMLParser.getTagMatchPattern(nextnode);
    			
    			// There is a match so we need to log all the steps backwards to root node
    			// to learn how to access this element in general from the HTML as a rule
    			// Append the element number to the match string for each level of the hierarchy
    			matchPath = strTagPattern + "#" + String.valueOf(i) + ":" + matchPath;
    			return 1; // Indicate there was a match indeed!
    		}
    	}
    			
		return 0;
	}	// public int searchByTextContent(Node node) {

	
	/**
	 * Inspect where this element is at and find out how to locate it in relation to 
	 * the other elements in the same cluster if there are duplicate elements of same type
	 * like in table row/columns 
	 * This can be achieved by going to the parent node and then figure out the other aspects
	 * based on the parent tag
	 */
	private int analyseMatchLocation(Node thisnode) {
		
		System.out.println("analyseMatchLocation: " + thisnode.getParent().getClass().toString());
		
		Node node1up = thisnode.getParent();
		
		if(null == node1up) return -1; // nothing to analyze as no parent. Must be non-html document

		if(node1up instanceof TableColumn)
		{
			// Need to find the relative position of this cell in the table row and column
			// in terms of table header. 
			// Case1: Table with headers. All the rows should be transferred with selected columns
			// Case2: The table has rows like name-value pairs and only need to be mapped with the name of the matched item
			// Case3: 
			
			/**
			 * Get the column count in the row
			 */
			Node node2up = node1up.getParent();
			
			if(node2up instanceof TableRow)
			{
				// Now that we got the node for the row, find the position of thisnode in the row
				int matchpos = 0;
				NodeList nl = node2up.getChildren();
				int size = nl.size();
				
				for(int i = 0; i < size; i++)
				{
					if( 0 == compareHTMLText(nl.elementAt(i), strSearchText))
						matchpos = i;// this is column in the table where the match occurred 
				}
			}
			
		}
		
		
		return 0;
	}



	private int compareHTMLText(Node node, String strSearchText2) {
    	String strNodeText = node.toHtml();
    	
    	String[] nodeTypes = {"DIV", "P"};
    	
    	try {
			strNodeText = ParserUtils.trimTags(strNodeText, nodeTypes);
			strNodeText = strNodeText.trim();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return strNodeText.compareTo(strSearchText);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		Parser parser;
        
        HTMLDigester htmlextractor = new HTMLDigester();

        String strDigestedData = htmlextractor.digest("/home/govind/temp/Presa1.htm");
            	
//        int iret = htmlextractor.searchByTextContent(nextnode, "Dean Nellis");
            	
	}


	public String digest(String strInputFilePath) {
		
        strDataHomePath = "/home/govind/temp";
            
//        String strInputHTMLFileName = strInputFilePath.substring(1 + strInputFilePath.lastIndexOf(File.separator));
        String strInputHTMLFileName = StringUtil.GetLastRightOf(strInputFilePath, File.separator);
        lineWriter = new LineWriter(strDataHomePath, strInputHTMLFileName, "out");
            
        lineWriter.open();


       	Node nextnode = HTMLParser.getRootHTMLNode(strInputFilePath);
            	
       	int iret = scanHTMLContent(nextnode);
            	
        lineWriter.close();
        strFlatHTMLPath = lineWriter.getGeneraterFilePath();
        return strFlatHTMLPath;
	}


	public String getNextMatchFromHTML(String strValue) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * This function get all the matched HTML content items matching with the
	 * input string value
	 * @param strValue
	 * @return
	 */
	public Iterator<LineDetail> getAllMatches(String strHTMLPath) {
		ArrayList<LineDetail> lines = new ArrayList<LineDetail>();
		
		LineReader reader = new LineReader(strFlatHTMLPath);
		reader.open();
		
		String strLine = "";
		String strCurrentHTMLPath = "";
		
		for(long i = 0; ; i++){
		
			strLine = reader.readNextLine();
			
			if(strLine == null) break; // reached end of the file
			
			// get the html content from the current line
			strCurrentHTMLPath = StringUtil.GetFirstRightOf(strLine, "=");
			
			if(strHTMLPath.compareTo(strCurrentHTMLPath) == 0)
			{
				lines.add(new LineDetail(i, strLine, strHTMLPath));
			}
		}
		
		reader.close();
		
		
		return lines.iterator();
	}


	/**
	 * This function returns list of matched HTML content items
	 * matching with the HTML path as in the input parameter string
	 * @param strHTMLPath
	 */
	public Iterator<LineDetail> getHTMLContentByPath(String strHTMLPath) {
		LineReader reader = new LineReader(strFlatHTMLPath);
		reader.open();
		
		ArrayList<LineDetail> lines = new ArrayList<LineDetail>();
		
		String strLine = "";
		String strCurrentHTMLPath = "";
		
		for(long i = 0; ; i++){
		
			strLine = reader.readNextLine();
			
			if(strLine == null) break; // reached end of the file
			
			// Get the HTML path from the current line. It will be 
			// the left hand side value of the first = char in the line
			strCurrentHTMLPath = StringUtil.GetLeftOf(strLine, "=");
			
			if(strHTMLPath.compareTo(strCurrentHTMLPath) == 0)
			{
				lines.add(new LineDetail(i, strLine, strHTMLPath));
			}
		}
		
		reader.close();
		
		return lines.iterator();
		
	}

}
