package org.gov.email;

import java.io.StringReader;
//import java.util.ArrayList;
//
//import javax.json.Json;
//import javax.json.JsonObject;
//import javax.json.JsonReader;

import org.json.JSONObject;

import org.gov.elasticsearch.ESClient;

//import org.gov.database.NOSQLConnector;

public class EmailProcessor {
	
	
	public EmailProcessor(){
		
	}


//	public void processNewEmails() {
//		NOSQLConnector connector = NOSQLConnector.DBConnection("mydb");
//
//		JSONObject jsonData = new JSONObject("{}");
//		jsonData.put("FromAddress", messagedata.from);
//		jsonData.put("FromAddress", messagedata.from);
//		jsonData.put("ToAddress", messagedata.to);
//		jsonData.put("CCList", "");
//		jsonData.put("BCCList", "");
//		jsonData.put("RecvDate", messagedata.receivedDate.toString());
//		jsonData.put("SentDate", messagedata.sentDate.toString());
//		jsonData.put("Subject", messagedata.subject);
//		jsonData.put("Boby", messagedata.body);
//		jsonData.put("RecordStatus", "NEW");
//		jsonData.put("Attachments", messagedata.attachment_IDs);
//		Systen.out.println("jsonData: " + jsonData.toString());
//
////		JsonObject jsonData = Json.createObjectBuilder()
////				.add("FromAddress", messagedata.from)
////				.add("ToAddress", messagedata.to)
////				.add("CCList", "")
////				.add("BCCList", "")
////				.add("RecvDate", messagedata.receivedDate.toString())
////				.add("SentDate", messagedata.sentDate.toString())
////				.add("Subject", messagedata.subject)
////				.add("Boby", messagedata.body)
////				.add("RecordStatus", "NEW")
////				.add("Attachments", messagedata.attachment_IDs)
////				.build();
//
//		ArrayList<JsonObject> entries = connector.findRecord("RAWEMailRec", null);
//
//		int entriesCnt = entries.size();
//		for ( JsonObject jsonObj: entries){
//			System.out.println(jsonObj.toString());
//			String fromAddress = jsonObj.getString("FromAddress");
//
//			JsonReader reader = Json.createReader(new StringReader(fromAddress));
//
//			JsonObject jsonFromAddress = reader.readObject();
//
//			System.out.println("FromAddress: " + fromAddress);
//			System.out.println("FromAddress: " + jsonFromAddress.getString("Address"));
//
//			JsonObject jsonMetaData = Json.createObjectBuilder()
//					 .add("FromAddress", fromAddress)
//					 .build();
//			JsonObject contact = connector.findOneRecord("contacts", jsonMetaData);
//
//			if((contact != null) && (contact.isEmpty() == false)){
//				System.out.println(contact.toString());
//
//			}
//			else{
//				/**
//				 * The contact is not found so add this contact to the database
//				 */
//				connector.addRecord("contacts", jsonMetaData);
//				System.out.println("processNewEmails: Adding the address to contacts collection!");
//
//			}
//		}
//
//
//
//	}

	public static void main(String[] args) {



		EmailProcessor eMailProcessor = new EmailProcessor();
		
//		eMailReceiver.createEmailSession();
		
//		emailConnector.sendMail();
		
//		eMailReceiver.readInbox();
		
//		eMailProcessor.processNewEmails();

	}

}
