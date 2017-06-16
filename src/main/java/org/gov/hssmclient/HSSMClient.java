package org.gov.hssmclient;

/**
 * Created by govind on 6/13/17.
 */

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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

            JSONObject body = new JSONObject("{\"params\": {\"bucket\": \"staged\"}}");

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

        return null;

    }



    // http://localhost:8080/RESTfulExample/json/product/get
    public static void main(String[] args) {

        try {

            URL url = new URL("http://localhost:3040/rest/objects");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject body = new JSONObject("{\"params\": {\"bucket\": \"staged\"}}");

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

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                JSONObject json = new JSONObject(output);
                System.out.println("JSON Data" + json.toString());
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}
