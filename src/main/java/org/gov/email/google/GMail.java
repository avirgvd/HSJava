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
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

// Implementing Server-Side Authorization
// https://developers.google.com/gmail/api/auth/web-server


public class GMail {
    /** Application name. */
    private static final String APPLICATION_NAME =
            "Gmail API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/gmail-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/gmail-java-quickstart
     */
    private static final List<String> SCOPES =
            Arrays.asList(GmailScopes.GMAIL_LABELS);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
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
                GMail.class.getResourceAsStream("/org/govi/email/google/client_secret_971270578758-skffl0pv1dkm2iqnt536uh7reqqtd0er.apps.googleusercontent.com.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
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
        // Load client secrets.
        InputStream in =
                GMail.class.getResourceAsStream("/client_secret_971270578758.json");kjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));




        System.out.println("clientSecrets: " + clientSecrets.getDetails());

//        Credential = new GoogleCredential.Builder()
//                .setTransport(new NetHttpTransport())
//                .setJsonFactory(new JacksonFactory())
//                .setClientAuthentication( clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getClientSecret())
//                .build();
//
//
//
//
//        Credential credential1 = new Credential.Builder(
//                setTransport(new NetHttpTransport()),
//                setJsonFactory(new JacksonFactory()),
//                .setClientSecrets(clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getClientSecret()).build();
//        credential1.setAccessToken(auth_data.getString("access_token"));
//        credential1.setRefreshToken(auth_data.getString("refresh_token"));
//        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential1).build();


        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setAccessType("offline")
                        .build();

        Credential = new new Credential.Builder(
                setTransport(new NetHttpTransport()),
                setJsonFactory(new JacksonFactory()),
                setClientSecrets( clientSecrets.getDetails().getClientId(), clientSecrets.getDetails().getClientSecret()))
                .build();


//        Credential credential = new GoogleCredential();
//        credential.setAccessToken(auth_data.getString("access_token"));
//        credential.setRefreshToken(auth_data.getString("refresh_token"));
        credential.setRefreshToken("4/kKE3XSKk-G13p7CM04gX4WWOu8fNFKDU5bVTRfwuYkU");

        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
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