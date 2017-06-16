package org.gov.html;

public class FlatHTMLItem {

	public String strSourceHTMLItem;
	
	/**
	 * value 1 is top rank. As the value goes higher (max limit of int) the rank goes down
	 */
	int rank; 
	public FlatHTMLItem(String textContent) {
		strSourceHTMLItem = textContent;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
