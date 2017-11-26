package org.gov.email.google;

/**
 * Created by govind on 8/10/17.
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import org.gov.email.MessageData;
import org.gov.hssmclient.HSSMClient;
import org.json.JSONObject;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;


import javax.mail.MessagingException;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

import java.io.IOException;


// Implementing Server-Side Authorization
// https://developers.google.com/gmail/api/auth/web-server

// GMail access from Java
//https://developers.google.com/gmail/api/quickstart/java

// GMail Java samples
//https://github.com/google/mail-importer/tree/master/src/main/java/to/lean/tools/gmail/importer/gmail


public class HSGMail {
//    /** Application name. */
    private static final String APPLICATION_NAME =
            "HSAppl";
//
//    /** Directory to store user credentials for this application. */
//    private static final java.io.File DATA_STORE_DIR = new java.io.File(
//            System.getProperty("user.home"), ".credentials/gmail-java-quickstart");
//
//    /** Global instance of the {@link FileDataStoreFactory}. */
//    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** OAuth 2.0 scopes. for this gmail session*/
    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/gmail.modify");


    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                HSGMail.class.getResourceAsStream("client_secret_971270578758.json");
//                HSGMail.class.getResourceAsStream("/org/govi/email/google/client_secret_971270578758-skffl0pv1dkm2iqnt536uh7reqqtd0er.apps.googleusercontent.com.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    /**
     * Creates an authorized Credential object.
     * Ref: https://tools.ietf.org/html/rfc6749#section-4.1
     *
     +----------+
     | Resource |
     |   Owner  |
     |          |
     +----------+
     ^
     |
     (B)
     +----|-----+          Client Identifier      +---------------+
     |         -+----(A)-- & Redirection URI ---->|               |
     |  User-   |                                 | Authorization |
     |  Agent  -+----(B)-- User authenticates --->|     Server    |
     |          |                                 |               |
     |         -+----(C)-- Authorization Code ---<|               |
     +-|----|---+                                 +---------------+
     |    |                                         ^      v
     (A)  (C)                                        |      |
     |    |                                         |      |
     ^    v                                         |      |
     +---------+                                      |      |
     |         |>---(D)-- Authorization Code ---------'      |
     |  Client |          & Redirection URI                  |
     |         |                                             |
     |         |<---(E)----- Access Token -------------------'
     +---------+       (w/ Optional Refresh Token)
     *
     *
     * Ref: https://developers.google.com/api-client-library/java/google-api-java-client/oauth2
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize_ES(JSONObject auth_data) throws IOException {


        // Load client secrets file from /resources
        InputStream in =
                HSGMail.class.getResourceAsStream("/client_secret_java2.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        System.out.println("clientSecrets: " + clientSecrets.getDetails());
        System.out.println("auth_data: " + auth_data);

        // Authorize Google OAuth using 'google-oauth-client'

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(new NetHttpTransport())
                .setJsonFactory(new JacksonFactory())
                .setClientSecrets( clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getClientSecret())
                .build()
                .setRefreshToken(auth_data.getString("refresh_token"));

//        credential.setAccessToken(auth_data.getString("access_token"));
//        credential.setRefreshToken(auth_data.getString("refresh_token"));

        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public static Gmail getGmailService(JSONObject auth_data) throws IOException {
//        System.out.println("getGmailService: authorization: " + auth_data.toString());
        Credential credential = authorize_ES(auth_data);
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Print message changes.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param startHistoryId Only return Histories at or after startHistoryId.
     * @throws IOException
     */
    public static void listHistory(Gmail service, String userId, BigInteger startHistoryId)
            throws IOException {
        List<History> histories = new ArrayList<History>();
        ListHistoryResponse response = service.users().history().list(userId)
                .setStartHistoryId(startHistoryId).execute();
        while (response.getHistory() != null) {
            histories.addAll(response.getHistory());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().history().list(userId).setPageToken(pageToken)
                        .setStartHistoryId(startHistoryId).execute();
            } else {
                break;
            }
        }

        for (History history : histories) {
            System.out.println(history.toPrettyString());
        }
    }

    public static List<MessageData> pullNewEmails(Gmail service, int lastMailId)
            throws IOException, MessagingException {

        List<String> labels = Arrays.asList("INBOX");
        List<MessageData> outMessages2 = new ArrayList<MessageData>();

        try {
            ListMessagesResponse response = service.users()
                    .messages()
                    .list("me")
                    .setLabelIds(labels)
                    .setMaxResults(new Long(10)).execute();

            List<Message> messages = new ArrayList<Message>();
            messages.addAll(response.getMessages());

//            while (response.getMessages() != null) {
//                messages.addAll(response.getMessages());
//                if (response.getNextPageToken() != null) {
//                    String pageToken = response.getNextPageToken();
//                    response = service.users().messages().list("me").setLabelIds(labels)
//                            .setPageToken(pageToken).execute();
//                } else {
//                    break;
//                }
//            }
//
//            Base64 base64Url = new Base64(true);


            for (Message message : messages) {
                System.out.println("Message ID: " + message.getId());
                System.out.println(message.toPrettyString());

//                java.util.List<MessagePart> msgParts = service.users().messages().get("me", message.getId()).setFormat("full").execute().getPayload().getParts();
//
//                for ( MessagePart part : msgParts) {
//                    System.out.println(part.getParts());
//                    System.out.println(part.getHeaders());
//                }


                MessageData msgData = getMessageContents(service, "me", message.getId());

//                Message m1 = service.users().messages().get("me", message.getId()).setFormat("raw").execute();
//                Base64 base64Url = new Base64(true);
//                byte[] emailBytes = base64Url.decodeBase64(m1.getRaw());
//
//                Properties props = new Properties();
//                Session session = Session.getDefaultInstance(props, null);
//
//                MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
//                MessageData msgData = new MessageData(email);

                outMessages2.add(msgData);



//                Properties props = new Properties();
//                Session session = Session.getDefaultInstance(props, null);
//
//                MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
//                System.out.println(email.getSubject());

            }

        }
        catch(IOException e) {
            e.printStackTrace();
        }


        return outMessages2;


    }

    private static MessageData getMessageContents(Gmail service, String userID, String messageId) throws IOException, MessagingException {


        MessageData msgData = new MessageData();
        msgData.id = messageId;


        JSONObject jsonMetaData = new JSONObject();
        String attachmentIDs = "";

        Message message = service.users().messages().get(userID, messageId).execute();
        List<MessagePart> parts = message.getPayload().getParts();

        System.out.println(message.getPayload().getHeaders().toString());

        msgData = getHeaderData(message.getPayload().getHeaders(), msgData);

        if(parts == null) {
            getMessageBody(message.getPayload(), msgData);
        }
        else {

            for(MessagePart part: parts) {

                String filename = part.getFilename();

                if(filename != null && filename.length() > 0) {

                    // TODO: move this section code to getAttachments function

                    // This must be email attachment
                    String attId = part.getBody().getAttachmentId();
                    MessagePartBody attachPart = service
                            .users()
                            .messages()
                            .attachments()
                            .get(userID, message.getId(), attId)
                            .execute();

                    Base64 base64Url = new Base64(true);
                    byte[] fileByteArray = base64Url.decodeBase64(attachPart.getData());
                    jsonMetaData.put("source", "google");
                    jsonMetaData.put("sourceID", message.getId());
                    jsonMetaData.put("mimetype", part.getMimeType());
                    jsonMetaData.put("orgfilename", filename);

                    JSONObject jsonFileID = HSSMClient.uploadFile(fileByteArray, jsonMetaData);
                    System.out.println("getMessageAttachments: attachment fileID " + jsonFileID.getString("FileID"));
                    attachmentIDs += jsonFileID.getString("FileID") + ",";

                }
                else {
                    // This is not attachment so check if its the email body
                    System.out.println(part.getMimeType());

                    getMessageBody(part, msgData);


                }
            }

        }


//        return attachmentIDs;
//
//
//
//        String attachmentIDs = getMessageAttachments(service, userID, message.getId());
        System.out.println("Number of attachments found: " + attachmentIDs.split(",").length);
        msgData.attachment_IDs = attachmentIDs;


        return msgData;

    }

    private static MessageData getMessageBody(MessagePart part, MessageData msgData) throws IOException, MessagingException {

        if(part.getMimeType().startsWith("text") == true) {
            msgData = processTextContent(part, msgData);
        }
        else if(part.getMimeType().contains("multipart/alternative")) {
            msgData = processMultiPartAlternative(part, msgData);

        }
        else if(part.getMimeType().contains("multipart/mixed")) {
            msgData = processMultiPartMixed(part, msgData);

        }
        else {
            // unknown email message part mime type so skip it for now
            System.out.println("Unknown message mime type found!!!");
        }

        return msgData;

    }

    private static MessageData processTextContent(MessagePart part, MessageData messageData) throws IOException, MessagingException {

        if(part.getMimeType().contains("text/plain") == true) {
            // Prefer html but if plain text is only available then get it
            if(messageData.body.length() == 0) {
                messageData.body = StringUtils.newStringUtf8(Base64.decodeBase64(part.getBody().getData()));
                messageData.contentType = part.getMimeType();
            }
        }
        else if(part.getMimeType().contains("text/html") == true) {
            // Prefer html as its easy to parse the message in this format
            messageData.body = StringUtils.newStringUtf8(Base64.decodeBase64(part.getBody().getData()));
            messageData.contentType = part.getMimeType();
        }

        return messageData;
    }


    private static MessageData processMultiPartMixed(MessagePart part, MessageData messageData) {
        // Multipart/mixed is used for sending files with different "Content-Type" headers inline (or as attachments).
        // If sending pictures or other easily readable files, most mail clients will display them inline
        // (unless otherwise specified with the "Content-disposition" header). Otherwise it will offer them as attachments.
        // The default content-type for each part is "text/plain".

        return messageData;
    }

    private static MessageData processMultiPartAlternative(MessagePart messagepart, MessageData messageData) throws IOException, MessagingException {

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

        List<MessagePart> parts =  messagepart.getParts();

        for(MessagePart part: parts) {
            System.out.println("Body part type " + part.getMimeType());
            messageData = processTextContent(part, messageData);
        }

        return messageData;
    }



    private static MessageData getHeaderData(List<MessagePartHeader> headers, MessageData msgData) {

        for(MessagePartHeader header: headers) {
            System.out.println(header.getName());
            System.out.println(header.getValue());

            if(header.getName().compareTo("From") == 0) {
                msgData.from = header.getValue();
            }
            else if(header.getName().compareTo("To") == 0) {
                msgData.to = header.getValue();
            }
            else if(header.getName().compareTo("Subject") == 0) {
                msgData.subject = header.getValue();
            }
            else if(header.getName().compareTo("Date") == 0) {
                msgData.sentDate = header.getValue();
            }
        }

        return msgData;
    }


    // Code reference site: https://developers.google.com/gmail/api/v1/reference/users/messages/attachments/get
    // Gets all attachment files and stores in the HSS and returns all the fileIDs along with filenames
    private static String getMessageAttachments(Gmail service, String userId, String messageId) throws IOException {

        JSONObject jsonMetaData = new JSONObject();
        String attachmentIDs = "";

        Message message = service.users().messages().get(userId, messageId).execute();
        List<MessagePart> parts = message.getPayload().getParts();

        for(MessagePart part: parts) {
            String filename = part.getFilename();
            if(filename != null && filename.length() > 0) {

                String partHeaderStr = part.getHeaders().toString();

                String attId = part.getBody().getAttachmentId();
                MessagePartBody attachPart = service
                                                .users()
                                                .messages()
                                                .attachments()
                                                .get(userId, messageId, attId)
                                                .execute();

                Base64 base64Url = new Base64(true);
                byte[] fileByteArray = base64Url.decodeBase64(attachPart.getData());
                jsonMetaData.put("source", "google");
                jsonMetaData.put("sourceID", messageId);
                jsonMetaData.put("mimetype", part.getMimeType());
                jsonMetaData.put("orgfilename", filename);

                JSONObject jsonFileID = HSSMClient.uploadFile(fileByteArray, jsonMetaData);
                System.out.println("getMessageAttachments: attachment fileID " + jsonFileID.getString("FileID"));
                attachmentIDs += jsonFileID.getString("FileID") + ",";

            }
        }

        return attachmentIDs;
    }

    public static void main(String[] args) throws IOException {


        // Build a new authorized API client service.
        Gmail service = getGmailService(null);

        // Print the labels in the user's account.
        String user = "me";
        ListLabelsResponse listResponse =
                service.users().labels().list(user).execute();
        List<Label> labels = listResponse.getLabels();
        if (labels.size() == 0) {
            System.out.println("No labels found.");
        } else {
            System.out.println("Labels:");
            for (Label label : labels) {
                System.out.printf("- %s\n", label.getName());
            }
        }
    }

}