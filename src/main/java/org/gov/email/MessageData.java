package org.gov.email;

import org.json.JSONObject;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Enumeration;

public class MessageData {

	public String subject;
	public String body;
	public String to;
	public String from;
	public String sentDate;
	public String contentType;
	public String processingStatus;
	public String id;
	public MimeMessage rawMessage;
	
	// semi-colon ";" separated document IDs for all the attachment files for the message 
	public String attachment_IDs;

	public MessageData(){
		processingStatus = "NEW";
		body = "";
		subject = "";
		to = "";
		from = "";
		attachment_IDs = "";
		contentType = "";
		id = "";
		
	}
	public MessageData(MimeMessage message) throws MessagingException {
		processingStatus = "NEW";
		body = "";

		attachment_IDs = "";
		contentType = message.getContentType();
		from = EmailUtils.parseEmailAddress(message.getFrom());
		to = EmailUtils.parseEmailAddress(message.getAllRecipients());
		subject = message.getSubject();
//		sentDate = message.getSentDate();
		rawMessage = message;

		final Enumeration allHeaderLines = message.getAllHeaderLines();

		while (allHeaderLines.hasMoreElements()) {
			String param = allHeaderLines.nextElement().toString();
			System.out.println(param);
		}


	}

	public void print(){
		System.out.println("From: " + from);
		System.out.println("Subject: " + subject);
		System.out.println("Sent Date: " + sentDate);
		System.out.println("Content Type: " + contentType);
		System.out.println("Body: " + body);
		System.out.println("***************************");
	}

	public JSONObject toJSONObject() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("id", id);
		jsonObj.put("date", sentDate);
		jsonObj.put("from", from);
		jsonObj.put("to", to);
		jsonObj.put("content-type", contentType);
		jsonObj.put("subject", subject);
		jsonObj.put("body", body);
		jsonObj.put("attachment_ids", attachment_IDs);


		return jsonObj;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
