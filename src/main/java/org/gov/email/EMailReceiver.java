package org.gov.email;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.mail.*;

import com.google.api.services.gmail.Gmail;

import org.gov.email.google.HSGMail;
import org.json.JSONObject;

import org.gov.elasticsearch.ESClient;

public class EMailReceiver {

    /**
	 * Member variables
	 */
	public Session emailSession;
	
	public String fromAddress;

	public static EMailReceiver eMailReceiver;

//	public static List<JSONObject>

    public static List<JSONObject> processMessages(List<MessageData> messages) throws MessagingException, IOException {
		List<JSONObject> esItems = new ArrayList<JSONObject>(messages.size());

        for (MessageData message : messages) {
            System.out.println("Message Subject: " + message.subject);
            System.out.println("Message Subject: " + message.contentType);

			if(message.contentType.startsWith("multipart/alternative") == true) {
				message = processMultiPartAlternative(message);
			}
			else if(message.contentType.startsWith("multipart/mixed") == true) {
				// This type has attachments
				processMultiPartMixed(message);
			}
			else if(message.contentType.startsWith("text") == true) {
				processTextContent(message);
			}

			esItems.add(message.toJSONObject());
        }

        return esItems;
    }

	private static MessageData processTextContent(MessageData messageData) throws IOException, MessagingException {
		System.out.println("$$$$$$4Plain text content: " + messageData.rawMessage.getContent());
		return messageData;
	}

	private static MessageData processMultiPartMixed(MessageData messageData) {
		// Multipart/mixed is used for sending files with different "Content-Type" headers inline (or as attachments).
		// If sending pictures or other easily readable files, most mail clients will display them inline
		// (unless otherwise specified with the "Content-disposition" header). Otherwise it will offer them as attachments.
		// The default content-type for each part is "text/plain".

		return messageData;
	}

	private static MessageData processMultiPartAlternative(MessageData messageData) throws IOException, MessagingException {

		System.out.println("processMultipartAlternative: start");

		// This is a multi-part content
		// Many email servers are configured to automatically generate a plain text version
		// of a message and send it along with the HTML version, to ensure that it can be read
		// even by text-only email clients, using the Content-Type: multipart/alternative,
		// as specified in RFC 1521.[11][12][13] The message itself is of type multipart/alternative,
		// and contains two parts, the first of type text/plain, which is read by text-only clients,
		// and the second with text/html, which is read by HTML-capable clients.
		// The plain text version may be missing important formatting information, however.
		// (For example, an equation may lose a superscript and take on an entirely new meaning.)
		// Many mailing lists deliberately block HTML email, either stripping out the HTML part to
		// just leave the plain text part or rejecting the entire message.
		// Source: http://en.wikipedia.org/wiki/HTML_email

		Multipart multipart = (Multipart) messageData.rawMessage.getContent();

		System.out.println("Multipart count: " + multipart.getCount());

		for(int i = 0; i < multipart.getCount(); i++) {
			System.out.println("Body part type " + multipart.getBodyPart(i).getContentType());
			System.out.println("Body part type " + multipart.getBodyPart(i).getContent().toString());

			if(multipart.getBodyPart(i).getContentType().startsWith("text/plain")) {
				// For now accept only plan text version of the messages for ease of processing content
			}
			else if(multipart.getBodyPart(i).getContentType().startsWith("text/html")) {
				// html content is better to parse the message better
				messageData.body = multipart.getBodyPart(i).getContent().toString();
			}

		}




		return messageData;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ESClient esclient = new ESClient();
		esclient.createClientSession();

		// Query the configured email accounts that has read access to inbox
		// The accounts settings should allow the user to specify if the emails can be imported to HS
		// And this query should return only such accounts

		ArrayList<String> accounts = esclient.searchIndex1("accounts", null);

		int count = accounts.size();

		for (String account : accounts) {

			JSONObject jsonAccount = new JSONObject(account);

			String accountNetwork = jsonAccount.getString("network");
			System.out.println("The account network is " + accountNetwork);

			if(accountNetwork.compareTo("google") == 0) {
				try {
					Gmail service = HSGMail.getGmailService(jsonAccount.getJSONObject("access_data"));

                    String user = "me";
                    int lastMailId = 10;
                    List<MessageData> messages = HSGMail.pullNewEmails(service, lastMailId);

                    EMailReceiver.bulkAddToMessages(esclient, messages);

//                    List<JSONObject> esItems = EMailReceiver.processMessages(messages);

//					EMailReceiver.bulkAddToStaging(esItems);

//                    // Print the labels in the user's account.
//                    ListLabelsResponse listResponse =
//                            service.users().labels().list(user).execute();
//                    List<Label> labels = listResponse.getLabels();
//                    if (labels.size() == 0) {
//                        System.out.println("No labels found.");
//                    } else {
//                        System.out.println("Labels:");
//                        for (Label label : labels) {
//                            System.out.printf("- %s\n", label.getName());
//                        }
//                    }

                } catch (IOException e) {
					e.printStackTrace();
				} catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
		}


//		ArrayList<String> arrHits = esclient.searchIndex("messages", null);

		EMailReceiver.eMailReceiver = new EMailReceiver();
//		EmailProcessor eMailProcessor = new EmailProcessor();
		
//		eMailReceiver.createEmailSession();
		
//		emailConnector.sendMail();
		
//		eMailReceiver.readInbox();
		
//		eMailProcessor.processNewEmails();
		
	}

    private static void bulkAddToMessages(ESClient esclient, List<MessageData> messages) {


        ArrayList<JSONObject> jsonArray = new ArrayList<JSONObject>();

        for(MessageData msgData: messages) {
            jsonArray.add(msgData.toJSONObject());
        }

        esclient.indexBulkDocuments("messages", jsonArray);

        return ;
    }

    public static void bulkAddToStaging(List<MessageData> esItems) {

		List<JSONObject> jsonArr = new ArrayList<JSONObject>();
		for(MessageData msg: esItems) {
			jsonArr.add(msg.toJSONObject());
		}



		return;

	}


	public static List<JSONObject> getMails(JSONObject accountNetwork) {

		List<JSONObject> esItems = null;

		if(accountNetwork.getString("network").compareTo("google") == 0) {

			try {
				Gmail service = HSGMail.getGmailService(accountNetwork.getJSONObject("access_data"));

				String user = "me";
				int lastMailId = 10;
				List<MessageData> messages = HSGMail.pullNewEmails(service, lastMailId);

				esItems = EMailReceiver.processMessages(messages);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}



		}

		return esItems;

	}
}
