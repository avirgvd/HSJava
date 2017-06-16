package org.gov.email;

import java.util.Date;

public class MessageData {

	public String subject;
	public String body;
	public String to;
	public String from;
	public Date receivedDate;
	public String contentType;
	public String processingStatus;
	
	// semi-colon ";" separated document IDs for all the attachment files for the message 
	public String attachment_IDs;
	public Date sentDate;
	
	public MessageData(){
		processingStatus = "NEW";
		body = "";
		subject = "";
		to = "";
		from = "";
		attachment_IDs = "";
		contentType = "";
		
	}
	
	public void print(){
		System.out.println("From: " + from);
		System.out.println("Subject: " + subject);
		System.out.println("Received Date: " + receivedDate.toString());
		System.out.println("Content Type: " + contentType);
		System.out.println("Body: " + body);
		System.out.println("***************************");
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
