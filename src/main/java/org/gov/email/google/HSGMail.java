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
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import org.json.JSONObject;


import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static void receiveNewMails(Gmail service, int lastMailId)
            throws IOException, MessagingException {

        List<String> labels = Arrays.asList("INBOX");

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
                System.out.println(message.toPrettyString());
                System.out.println(message.getSnippet());

//                byte[] emailBytes = base64Url.decodeBase64(service.users().messages().get("me", message.getId()).setFormat("full").execute().getPayload().toPrettyString());
                String emailBytes = service.users().messages().get("me", message.getId()).setFormat("full").execute().getPayload().toPrettyString();

                System.out.println(emailBytes);

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