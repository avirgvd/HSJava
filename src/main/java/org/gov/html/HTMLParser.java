package org.gov.html;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.TableTag;
import org.htmlparser.tags.TextareaTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.ParserException;

public class HTMLParser {

	
	public static String getTagMatchPattern(Node nextnode) {

		if(nextnode instanceof BodyTag)
		{
			return ((BodyTag) nextnode).getTagName();
		}
		else if(nextnode instanceof HeadingTag)
		{
			return ((HeadingTag) nextnode).getRawTagName();
		}
		else if(nextnode instanceof TextareaTag)
		{
			return ((TextareaTag) nextnode).getTagName();
		}
		else if (nextnode instanceof TextNode)
		{
			// TODO : for text nodes, the class is not derived from TagNode
			// Need to check the HTML parser code to correct this inconsistency if possible
			return "TextNode";
		}
		else if (nextnode instanceof TableTag)
		{
			// TODO need to introduce the code inspect table here to understand
			// how to relate the matching field with table structure and other table values
			return ((TableTag) nextnode).getTagName();
		}
		else if (nextnode instanceof org.htmlparser.tags.TableRow)
		{
			return ((org.htmlparser.tags.TableRow) nextnode).getTagName();
		}
		else if (nextnode instanceof org.htmlparser.tags.TableColumn)
		{
			return ((org.htmlparser.tags.TableColumn) nextnode).getTagName();
		}
		else if (nextnode instanceof org.htmlparser.tags.TableHeader)
		{
			return ((org.htmlparser.tags.TableHeader) nextnode).getTagName();
		}
		else if (nextnode instanceof org.htmlparser.tags.Bullet)
		{
			return ((org.htmlparser.tags.Bullet) nextnode).getTagName();
		}
		else if (nextnode instanceof org.htmlparser.tags.BulletList)
		{
			return ((org.htmlparser.tags.BulletList) nextnode).getTagName();
		}
		else if (nextnode instanceof org.htmlparser.tags.Div)
		{
			return ((org.htmlparser.tags.Div) nextnode).getTagName();
		}
		else if (nextnode instanceof org.htmlparser.tags.ParagraphTag)
		{
			return ((org.htmlparser.tags.ParagraphTag) nextnode).getTagName();
		}
		else if (nextnode instanceof org.htmlparser.nodes.TagNode)
		{
			return ((org.htmlparser.nodes.TagNode) nextnode).getTagName();
		}
		else if (nextnode instanceof org.htmlparser.nodes.RemarkNode)
		{
			return "REMARKNODE";
		}
		else
		{
			return nextnode.getClass().toString();
			
		}
		
	}

	public static Node getRootHTMLNode(String strInputFilePath) {
		Parser parser = null;

		Node rootnode = null;

		try
        {
            parser = new Parser ();
            {
                // for a simple dump, use more verbose settings
                parser.setFeedback (Parser.STDOUT);
                Parser.getConnectionManager().setMonitor (parser);
            }
            
            parser.setResource (strInputFilePath);
        	
            for(NodeIterator nodes = parser.elements(); nodes.hasMoreNodes();)
            {
            	rootnode = nodes.nextNode();
            	System.out.println(rootnode.getText());
            	break;
            	
            }

        }
        catch (ParserException e)
        {
            e.printStackTrace ();
        }

		return rootnode;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


}
