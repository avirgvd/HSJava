package org.gov.email;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

import org.gov.elasticsearch.ESClient;

public class EmailProcessor {

	private EMailReceiver emailReceiver;
	
	public EmailProcessor(){
		emailReceiver = new EMailReceiver();
		
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

	private void processEmails() {



		return;
	}

	private void stageNewEmails(ESClient esclient) {

		// TODO
		emailReceiver.bulkAddToStaging(null);



		return;
	}

	private void loadEmailFilters() {

		return;
	}


	public static void main(String[] args) {

		ESClient esclient = new ESClient();
		esclient.createClientSession();

		EmailProcessor eMailProcessor = new EmailProcessor();

		// Query the configured email accounts that has read access to inbox
		// The accounts settings should allow the user to specify if the emails can be imported to HS
		// And this query should return only such accounts

		ArrayList<String> accounts = esclient.searchIndex1("accounts", null);

		int count = accounts.size();

		for (String account : accounts) {

			JSONObject jsonAccount = new JSONObject(account);

			System.out.println("The account network is " + jsonAccount.getString("network"));

			eMailProcessor.syncEmails(jsonAccount);


		}





		// The filters that should be used to avoid junk emails
		eMailProcessor.loadEmailFilters();

		// Read latest emails and save them to message staging
		eMailProcessor.stageNewEmails(esclient);

		// Process staged emails by checking if email is known type or unknown type
		// if known type, then use the pre-defined actions for the email type
		// if unknown then these messages should be listed under 'Messages' to the user and ask for training to process
		eMailProcessor.processEmails();
		
	}

	private void syncEmails(JSONObject accountNetwork) {

		List<JSONObject> emails = EMailReceiver.getMails(accountNetwork);

	}


}
