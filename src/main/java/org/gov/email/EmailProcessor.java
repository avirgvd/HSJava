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
