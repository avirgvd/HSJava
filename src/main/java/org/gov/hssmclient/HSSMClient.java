package org.gov.hssmclient;

/**
 * Created by govind on 6/13/17.
 */

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

// RESTFul API client implementation to talk to HSStorageManager

public class HSSMClient {


    public static JSONObject getStagedFiles() {

        JSONObject result = null;

        try {

            URL url = new URL("http://localhost:3040/rest/objects");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");

            // TODO: Below line changed temporarily. Should be reverted
            JSONObject body = new JSONObject("{\"params\": {\"bucket\": \"staging\"}, \"query\": {\"match\": {\"status\": \"staging\"}}}}");
//            JSONObject body = new JSONObject("{\"params\": {\"bucket\": \"staging\"}, \"query\": {\"match\": {\"status\": \"staged\"}}}}");

            OutputStream os = conn.getOutputStream();
            os.write(body.toString().getBytes());
            os.flush();
            os.close();


            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            System.out.println("Output from Server .... \n");
            String output = br.readLine();
            System.out.println(output);
            result = new JSONObject(output);


//            while ((output = br.readLine()) != null) {
//                System.out.println(output);
//                result = new JSONObject(output);
//                System.out.println("JSON Data" + json.toString());
//            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return result;

    }

    public static InputStream getFile(String bucket, String objID){

        try {

            URL url = new URL("http://localhost:3040/rest/file");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject body = new JSONObject("{\"params\": {\"bucket\": " + bucket + ", \"objid\": " + objID + "}}");

            OutputStream os = conn.getOutputStream();
            os.write(body.toString().getBytes());
            os.flush();
            os.close();

            System.out.println("Before making the rest call");


            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            System.out.println("After making the rest call");

            return conn.getInputStream();

//            BufferedInputStream in = new BufferedInputStream(((conn.getInputStream())));
//
//            System.out.println("Output from Server .... \n");
//
//            OutputStream fo = new FileOutputStream("abc");
//
//            int length = 0;
//
//            byte[] buffer = new byte[4096];
//            while ((length = in.read(buffer)) != -1) {
//                fo.write(buffer);
//            }
//
//            System.out.println("End of saving the file");

//            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return null;

    }

    public static void bulkMove(ArrayList<JSONObject> arrMoveList) {


        try {

            URL url = new URL("http://localhost:3040/rest/bulkmove");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");

//            JSONObject body = new JSONObject("{\"params\": {\"bucket\": " + bucket + ", \"objid\": " + objID + "}}");

            OutputStream os = conn.getOutputStream();
            os.write(arrMoveList.toString().getBytes());
            os.flush();
            os.close();

            System.out.println("Before making the rest call");


            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            System.out.println("After making the rest call");

            conn.disconnect();


        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public static void bulkUpdate(ArrayList<JSONObject> arrItems, String bucket) {


        System.out.println("bulkUpdate: arrItems: " + arrItems.toString());

        try {

            URL url = new URL("http://localhost:3040/rest/bulkupdate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(arrItems.toString().getBytes());
            os.flush();
            os.close();

            System.out.println("Before making the rest call");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            System.out.println("After making the rest call");

            conn.disconnect();


        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    // http://localhost:8080/RESTfulExample/json/product/get
    public static void main(String[] args) {

        HSSMClient.getFile("staging", "6cd1b67b-2095-4164-91ab-cc2d8d11b1ea");

    }


}
