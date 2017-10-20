package org.gov.email;


import java.io.*;
import java.nio.file.Files;
import java.text.DateFormat;
import java.util.*;

//import javax.json.Json;
//import javax.json.JsonObject;
import javax.mail.*;
import javax.mail.internet.*;

//import org.gov.database.NOSQLConnector;

import com.google.api.services.gmail.Gmail;
import org.gov.email.google.GMail;
import org.json.JSONObject;

import org.gov.elasticsearch.ESClient;

public class EMailReceiver {
	
	/**
	 * Member variables
	 */
	public Session emailSession;
	
	public String fromAddress;

	public static EMailReceiver eMailReceiver;
	
	public static int postMessage(Message message){
		
		eMailReceiver.messageExtract(message);
		
		return 0;
	}


	/**
	 * 
	 * @return
	 */
	public Session createEmailSession(){
	      

	      // Sender's email ID needs to be mentioned
	      fromAddress = "govind.avireddi@gmail.com";

	      // Get system properties
	      Properties properties = System.getProperties();

	      // Setup mail server
	      properties.put("mail.smtp.host", "smtp.gmail.com");
	      properties.put("mail.smtp.socketFactory.port", "465");
	      properties.put("mail.user", "govind.avireddi");
	      properties.put("mail.password", "MyBotKaAddress");
	      properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	      properties.put("mail.smtp.auth", "true");
	      properties.put("mail.smtp.port", "465");
	      
	      // Get the default Session object.
	      emailSession = Session.getDefaultInstance(properties,	new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("govind.avireddi","MyBotKaAddress");
				}
			});

		return emailSession;
	}
	
	/**
	 * 
	 */
	public void readInbox(){
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
			try {
//				Session session = Session.getDefaultInstance(props, null);
				Store store = emailSession.getStore("imaps");
//				Store store = emailSession.getStore("imap");
				store.connect("imap.gmail.com", "govind.avireddi@gmail.com", "MyBotKaAddress");
				System.out.println("EMAIL Store is" + store);

				Folder inbox = store.getFolder("Inbox");
				if (inbox == null) {
					System.out.println("Invalid folder");
					System.exit(1);
				}

//				inbox.open(Folder.READ_WRITE);
				
				// try to open read/write and if that fails try read-only
				try {
					inbox.open(Folder.READ_WRITE);
				} catch (MessagingException ex) {
					inbox.open(Folder.READ_ONLY);
				}


				System.out.println("MessageCount: " + inbox.getMessageCount());
				System.out.println("NewMessageCount: " + inbox.getNewMessageCount());

				readNewMessages(inbox);
				
	            // Close the inbox
	            inbox.close(false);
	            // Close the store
	            store.close();
	            
//				inbox.addMessageCountListener(new MessageCountAdapter() {
//					public void messagesAdded(MessageCountEvent ev) {
//						Message[] msgs = ev.getMessages();
//						System.out.println("Got " + msgs.length +
//						" new messages");
//						// Just dump out the new messages
//						for (int i = 0; i < msgs.length; i++) {
//							EmailConnector.postMessage(msgs[i]);
//						}
//					}
//				});
//
//				// Check mail once in "freq" MILLIseconds
//				int freq = 10000;
//				int i = 0;
//				for (; ;) {
//					Thread.sleep(freq); // sleep for freq milliseconds
//					// This is to force the IMAP server to send us
//					// EXISTS notifications.
//					i = inbox.getMessageCount();
//					System.out.println("Message Count is: " + i);
//				}
//
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (MessagingException e) {
			e.printStackTrace();
			System.exit(2);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
	
	/**
	 * Method: readNewMessages
	 * 
	 * @param inbox
	 * @return
	 */
	public int readNewMessages(Folder inbox){
		
		Message messages[];
		try {
			messages = inbox.getMessages();
			
			int iLen = messages.length;
			
//			for(Message message:messages) {

			Message message = null;
			
			if(iLen > 0) // only if there are any messages
			for(int i = iLen -1; i >= 0; i--) {
			
				message = messages[i];
				
				int iRet = messageExtract(message);
				
//				int iRet = postMessageToDB(messagedata);
//				int iRet = postMessageToNOSQL(messagedata);
//				System.out.println(message);
				
				// TEST CODE
				Date date = message.getReceivedDate();
				
				Date when = new Date("Dec 27, 2011");
				if(date.before(when)) break; // get only new messages
				// TEST CODE - END

				
//				System.out.println(InternetAddress.toString(message.getFrom() ) );
//				System.out.println(message.getSubject());
//				System.out.println(message.getContentType());
//				System.out.println(message.getReceivedDate().toString());
//				System.out.println("***********************************");
			} // for(int i = iLen -1; i >= 0; i--) {
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}

		return 0;
		
	}

	/**
	 * Method: messageDigest
	 * Description:  
	 * 
	 * @param message
	 * @return
	 */
	public int messageExtract(Message message){
		
		MessageData messagedata = new MessageData();
		
		// First extract message envelope data 
		try {
//			messagedata.from = message.getFrom()[0].toString();
			
			InternetAddress[] a;
			
			// FROM
			if ((a = (InternetAddress[])message.getFrom()) != null) {
				for (int j = 0; j < a.length; j++){
					System.out.println("FROM: " + a[j].getPersonal() + "<:>" + a[j].getAddress());
					
					

					messagedata.from = EmailUtils.parseEmailAddress(a[j]);
//					messagedata.from.concat(a[j].toString() + ";");
				}
			}
			
			// TO
			if ((a = (InternetAddress[])message.getRecipients(Message.RecipientType.TO)) != null) {
				for (int j = 0; j < a.length; j++) {
					System.out.println("TO: " + a[j].getPersonal() + "<:>" + a[j].getAddress());
					messagedata.to = EmailUtils.parseEmailAddress(a[j]);
//					messagedata.to.concat(a[j].toString() + ";");
					
					
					
					InternetAddress ia = (InternetAddress)a[j];
					if (ia.isGroup()) {
						InternetAddress[] aa = ia.getGroup(false);
						for (int k = 0; k < aa.length; k++)
							System.out.println(" GROUP: " + aa[k].toString());
					}
				}
			}


			
			messagedata.subject = message.getSubject();
			messagedata.receivedDate = message.getReceivedDate();
			messagedata.sentDate = message.getSentDate();

			System.out.println("Message content type: " + message.getContentType());
			
// CONTENT EXTRACTION:
			String strContentType = message.getContentType();
			// can also check "message.isMimeType("text/plain")", "message.isMimeType("multipart/*")" "message.isMimeType("message/rfc822")"
			if(strContentType.startsWith("multipart/ALTERNATIVE") == true ){
				
				processMultipartAlternative(message, messagedata);

				
			} // if(strContentType.startsWith("multipart/ALTERNATIVE") == true)
			else if(strContentType.startsWith("multipart/MIXED") == true ){
				
				processMultipartMixed(message, messagedata);

				
			}
			else if(strContentType.startsWith("TEXT/PLAIN") == true || 	
					strContentType.startsWith("TEXT/HTML") == true ){

				// This is a plain text so treat it appropriately
				messagedata.body = (String) message.getContent();
				messagedata.contentType = "TEXT/PLAIN";
				System.out.println("***********plain message*******************");
				System.out.println("TEXT/PLAIN");
				messagedata.print();
				System.out.println("**************************************");

			} 
			else {
				System.out.println("Un-supported content type: " + strContentType);
				System.out.println("*****NEED TO ADD SUPPORT FOR THIS CONTENT TYPE****");
			}
			
			// Set the message flag as SEEN to avoid future re-reads of this message
			message.setFlag(Flags.Flag.SEEN, true);
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
		return storeEmailContent(messagedata);
	}
	
	private void processMultipartMixed(Message message, MessageData messagedata) throws IOException, MessagingException {
		// Multipart/mixed is used for sending files with different "Content-Type" headers inline (or as attachments). 
		// If sending pictures or other easily readable files, most mail clients will display them inline 
		// (unless otherwise specified with the "Content-disposition" header). Otherwise it will offer them as attachments. 
		// The default content-type for each part is "text/plain".
		
		System.out.println("processMultipartMixed: start");
		
		/**
		 * MULTIPART/MIXED text and other embedded objects like images 
		 * should be seperated with text stored as body and others as attachments
		 * 
		 */
		Multipart multipart = (Multipart) message.getContent();
		
		for(int i = 0; i < multipart.getCount(); i++){
			BodyPart bodypart = multipart.getBodyPart(i); 
			System.out.println(bodypart.getContentType());
			
			if(bodypart.getContentType().startsWith("multipart/ALTERNATIVE") == true){
				processMultipartAlternative(message, messagedata);
			}
			else {
				System.out.println("processMultipartMixed: contentType=" + bodypart.getContentType());
				String disposition = bodypart.getDisposition();
				System.out.println("processMultipartMixed: disposition=" + disposition);

				if (disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT))) {
					String strID = saveEmailAttachment(bodypart, "EMAIL_ATTACHMENT");
//					messagedata.attachment_IDs.concat(strID + ";");
					messagedata.attachment_IDs = messagedata.attachment_IDs + strID + ";"; 
				}
				
			}
		}

		System.out.println("processMultipartMixed: end");
		
	}


	private void processMultipartAlternative(Message message,
			MessageData messagedata) throws IOException, MessagingException {
		
		System.out.println("processMultipartAlternative: start");

		// This is a multi-part content
//		Many email servers are configured to automatically generate a plain text version 
//		of a message and send it along with the HTML version, to ensure that it can be read 
//		even by text-only email clients, using the Content-Type: multipart/alternative, 
//		as specified in RFC 1521.[11][12][13] The message itself is of type multipart/alternative, 
//		and contains two parts, the first of type text/plain, which is read by text-only clients, 
//		and the second with text/html, which is read by HTML-capable clients. 
//		The plain text version may be missing important formatting information, however. 
//		(For example, an equation may lose a superscript and take on an entirely new meaning.)
//		Many mailing lists deliberately block HTML email, either stripping out the HTML part to 
//		just leave the plain text part or rejecting the entire message.
//		Source: http://en.wikipedia.org/wiki/HTML_email
		
		Multipart multipart = (Multipart) message.getContent();
		
		for(int i = 0; i < multipart.getCount(); i++){
			BodyPart bodypart = multipart.getBodyPart(i); 
			
			if(bodypart.getContentType().startsWith("multipart/ALTERNATIVE") == true){
				
				// This is endless recursion sometimes so to be safe lets break the recursion
				// Setting email body to empty string as there may not be one found in some cases
				messagedata.body = "";
			}
			else if (bodypart.getContentType().startsWith("TEXT/HTML") == true ){ 
				
					messagedata.body = (String) bodypart.getContent();
					messagedata.contentType = "TEXT/HTML";
				
					System.out.println("***********bodypart*******************");
					System.out.println(bodypart.getContentType());
					messagedata.print();
					System.out.println("**************************************");
			} else if (bodypart.getContentType().startsWith("TEXT/PLAIN") == true ){ 
				// skip this if email body is already found 
				// as this is plain text alternate copy of the content. Interested only in TEXT/HTML
				
				if(messagedata.body == null){
					System.out.println("processMultipartAlternative: TEXT/PLAIN but getting email body!");
					messagedata.body = (String) bodypart.getContent();
				}
			
				System.out.println("***********TEXT/PLAIN bodypart*******************");
				System.out.println(bodypart.getContentType());
				messagedata.print();
				System.out.println("**************************************");
			} else {
				System.out.println("processMultipartAlternative: contentType=" + bodypart.getContentType());
				String disposition = bodypart.getDisposition();
				
				if (disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT))) {
					String strID = saveEmailAttachment(bodypart, "EMAIL_ATTACHMENT");
//					messagedata.attachment_IDs.concat(strID + ";");
					messagedata.attachment_IDs = messagedata.attachment_IDs + strID + ";"; 
					// To query for this object, the following code snippet should help
//					GridFSDBFile out = fs.findOne( new BasicDBObject("_id", new ObjectId(strDoc_ID));
//					DataHandler dh = null;
				}
				
			}
		} // for(int i = 0; i < multipart.getCount(); i++){
		
		System.out.println("processMultipartAlternative: end");
	}

	/**
	 * Method: storeEmailContent
	 * Description: Implemented for ElasticSearch
	 * @param messagedata
	 * @return
	 */
	private int storeEmailContent(MessageData messagedata) {

//		NOSQLConnector connector = NOSQLConnector.DBConnection("mydb");

		ESClient esclient = new ESClient();

		//TODO: eliminate multiple initializations of esclient
		esclient.createClientSession();

		JSONObject jsonObject = new JSONObject("{}");

		assign(jsonObject, "FromAddress", messagedata.from);
		assign(jsonObject, "ToAddress", messagedata.to);
		assign(jsonObject, "CCList", "");
		assign(jsonObject, "BCCList", "");
		assign(jsonObject, "RecvDate", messagedata.receivedDate.toString());
		assign(jsonObject, "SentDate", messagedata.sentDate.toString());
		assign(jsonObject, "Subject", messagedata.subject);
		assign(jsonObject, "Boby", messagedata.body);
		assign(jsonObject, "RecordStatus", "NEW");
		assign(jsonObject, "Attachments", messagedata.attachment_IDs);

		esclient.indexDocument("messages", jsonObject);

//		JsonObject jsonData = Json.createObjectBuilder()
//				assign(jsonObject, "FromAddress", messagedata.from)
//				assign(jsonObject, "ToAddress", messagedata.to)
//				assign(jsonObject, "CCList", "")
//				assign(jsonObject, "BCCList", "")
//				assign(jsonObject, "RecvDate", messagedata.receivedDate.toString())
//				assign(jsonObject, "SentDate", messagedata.sentDate.toString())
//				assign(jsonObject, "Subject", messagedata.subject)
//				assign(jsonObject, "Boby", messagedata.body)
//				assign(jsonObject, "RecordStatus", "NEW")
//				assign(jsonObject, "Attachments", messagedata.attachment_IDs)
//				.build();

//		connector.addRecord("RAWEMailRec", jsonData);

		return 1;
	}

	private static String format(Object o)
	{
		if (o instanceof Calendar)
		{
			Calendar cal = (Calendar) o;
			return DateFormat.getDateInstance().format(cal.getTime());
		}
		else
		{
			return o.toString();
		}
	}


	private static void assign(JSONObject jsonObject, String title, Object value)
	{
		if (value != null)
		{
			System.out.println(title + " " + format(value));
			jsonObject.put(title, value);
		}
	}


	/**
	 * Method: saveEmailAttachment
	 * Description: posts the document container email bodypart into the database
	 * @param bodypart
	 * @param partType
	 * @return String containing the object_ID for the document inserted into the database
	 */
	private String saveEmailAttachment(BodyPart bodypart, String partType) {
		
		Object doc_ID = null;
		
//		NOSQLConnector connector = NOSQLConnector.DBConnection("mydb");
//
//		JSONObject jsonMetaData1 = null;

		ESClient esclient = new ESClient();

		//TODO: eliminate multiple initializations of esclient
		esclient.createClientSession();

		JSONObject jsonObject = new JSONObject("{}");



//		JsonObject jsonMetaData;
		try {

			InputStream in = bodypart.getInputStream();
			File out = new File("/home/govind/HomeServer/storage/emailstaging" + bodypart.getFileName());
			Files.copy(in, out.toPath());


			assign(jsonObject, "mimetype", bodypart.getContentType());
			assign(jsonObject, "destination", "/home/govind/HomeServer/storage/emailstaging");
			assign(jsonObject, "filename", bodypart.getFileName());
			assign(jsonObject, "path", out.getAbsolutePath());
			assign(jsonObject, "status", "unstaged");

			esclient.indexDocument("stagedfiles", jsonObject);


//			assign(jsonMetaData1, "contentType", bodypart.getContentType());
//			assign(jsonMetaData1, "fileName", bodypart.getFileName());
//
//			doc_ID = connector.addFile(partType, bodypart.getInputStream(), jsonMetaData);
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (doc_ID != null)
			return doc_ID.toString();
		else
			return null;
		
	}

//	/**
//	 * RAWEMailRec structure
//	 
//	-- Table Name: RAWEMailRec
//	MsgID 	INTEGER PRIMARY KEY DEFAULT nextval('serial'), 
//	CREATE TABLE RAWEMailRec (
//	FromAddress VARCHAR(80),
//	ToAddress VARCHAR(200),
//	CCList VARCHAR(200),
//	BCCList VARCHAR(200),
//	RCVDDate DATE,
//	Subject VARCHAR(200),
//	Boby VARCHAR(100000), -- need to revisit this for right datatype and size
//	RecordStatus	VARCHAR(200)
//	);
//	*/
//
//	/**
//	 * 
//	 * @param messagedata
//	 * @return
//	 */
//	private int postMessageToDB(MessageData messagedata) {
//
//		DBConnector connector = new DBConnector();
//		
//		Connection conn = connector.connect();
//		
//		try{
//			// skipped first column is primary key which is autoincrement field
//			String strInsertStatement = 
//			"INSERT INTO RAWEMailRec (FromAddress, ToAddress, CCList, BCCList, RCVDDate, Subject, Boby, RecordStatus) " + 
//						"VALUES(?,?,?,?,?,?,?,?)"; // 8 ?'s here
//			PreparedStatement insertStmt = conn.prepareStatement(strInsertStatement.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
//	
//			int i  = 1; 
//			insertStmt.setString(i++, messagedata.from);
//			insertStmt.setString(i++, ""); // ToAddress
//			insertStmt.setString(i++, ""); // CCList
//			insertStmt.setString(i++, ""); // BCCList
//			insertStmt.setDate(i++, new java.sql.Date(messagedata.receivedDate.getTime())); // DATE
//			insertStmt.setString(i++, messagedata.subject);
//			insertStmt.setString(i++, messagedata.body);
//			insertStmt.setString(i++, "NEW");
//			
//			int result = insertStmt.executeUpdate();
//	
//			conn.close();
//			
//		} catch (SQLException e){
//			e.printStackTrace();
//			
//			return -1;
//			
//		}
//		
//		
//		return 0;
//	}


	/**
	 * 
	 */
	public void sendMail(){
		
		// Recipient's email ID needs to be mentioned.
	      String to = "govind.avireddi@gmail.com";


	      try{
	         // Create a default MimeMessage object.
	         MimeMessage message = new MimeMessage(emailSession);

	         // Set From: header field of the header.
	         message.setFrom(new InternetAddress(fromAddress));

	         // Set To: header field of the header.
	         message.addRecipient(Message.RecipientType.TO,
	                                  new InternetAddress(to));

	         // Set Subject: header field
	         message.setSubject("This is the Subject Line2!");

	         // Now set the actual message
	         message.setText("This is actual message");

	         // Send message
	         Transport.send(message);
	         System.out.println("Sent message successfully....");
	      }catch (MessagingException mex) {
	         mex.printStackTrace();
	      }

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
					Gmail service = GMail.getGmailService(jsonAccount.getJSONObject("authResponse"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


		ArrayList<String> arrHits = esclient.searchIndex("messages", null);

		EMailReceiver.eMailReceiver = new EMailReceiver();
//		EmailProcessor eMailProcessor = new EmailProcessor();
		
		eMailReceiver.createEmailSession();
		
//		emailConnector.sendMail();
		
		eMailReceiver.readInbox();
		
//		eMailProcessor.processNewEmails();
		
	}	
	
	
}
