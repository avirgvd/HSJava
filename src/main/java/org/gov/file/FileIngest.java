package org.gov.file;

import org.gov.file.imaging.ExifJSON;
import org.gov.hssmclient.HSSMClient;
import org.gov.file.PDF.PDFMetaData;
import org.json.JSONObject;

import java.util.ArrayList;

public class FileIngest {

    protected static HSSMClient hssmclient = new HSSMClient();

    public static void main(String[] args) {
	// write your code here

        // Look for staged files to ingest
        JSONObject stagedFiles = hssmclient.getStagedFiles();

        System.out.println("stagedFiles: " + stagedFiles.toString());

        ArrayList<JSONObject> arrFinal = processStagedFiles(stagedFiles);

        moveFromStagedtoMimeBucket(arrFinal);

    }

    /**
     * Method: moveFromStagedtoMimeBucket
     * @param arrStagedFiles
     * Description: Moves the files in the list to corresponding buckets based on the file mime-type
     */
    private static void moveFromStagedtoMimeBucket(ArrayList<JSONObject> arrStagedFiles) {

        ArrayList<JSONObject> arrMoveList = new ArrayList<JSONObject>();


        for (JSONObject stagedFile: arrStagedFiles) {

            System.out.println("moveFromStagedtoMimeBucket: " + stagedFile.toString());

            String strBucket = getBucketForMimeType(stagedFile.getString("mimetype"));

            if(strBucket.compareTo("unknown") == 0) continue; // skip processing if file is unknown mime type

            JSONObject jsonMoveItem = new JSONObject();
            jsonMoveItem.put("targetbucket", strBucket);
            jsonMoveItem.put("sourcebucket", stagedFile.getString("container"));
            jsonMoveItem.put("id", stagedFile.getString("id"));

            System.out.println("jsonMoveItem: " + jsonMoveItem.toString());

            arrMoveList.add(jsonMoveItem);


        }

        HSSMClient.bulkMove(arrMoveList);
    }

    private static String getBucketForMimeType(String mimeType) {
        System.out.println("getBucketForMimeType: " + mimeType);

        String bucket = "";
//        if(mimeType.compareTo("image/jpeg") == 0) {
        if(mimeType.indexOf("image/") == 0) {
            bucket = "media1";
        }
        else if(mimeType.compareTo("application/pdf") == 0) {
            bucket = "docs";
        }
        else {
            bucket = "unknown";
        }

        return bucket;
    }

    /**
     * Method: processStagedFiles
     * @param stagedFiles
     * @return
     * Description: Extracts file metadata and update the staging index with the metadata
     */
    private static ArrayList<JSONObject> processStagedFiles(JSONObject stagedFiles) {

        int count = stagedFiles.getJSONObject("result").getInt("count");
        ArrayList<JSONObject> arrUpdateStaged = new ArrayList<JSONObject>(count);
        ArrayList<JSONObject> arrFinal = new ArrayList<JSONObject>(count);

        System.out.println("Result count: " + count);
//        System.out.println("Result 2nd: " + stagedFiles.getJSONObject("result").getJSONArray("items").get(2));

        if(count == 0) {
            System.out.println("NO STAGED FILES FOUND SO EXITING.....");
            return null;
        }

        for(int i = 0; i < count; i++) {
            JSONObject stagedFile = stagedFiles.getJSONObject("result").getJSONArray("items").getJSONObject(i);
            JSONObject jsonHit = new JSONObject(stagedFile);
            System.out.println("Staged file ===> " + stagedFile.toString());

            String mimetype = stagedFile.getString("mimetype");
            System.out.println("mimetype: " + mimetype);

//            JSONObject jsonFinal = null;
//            JSONObject jsonIndexCategory = null;
            String indexName = "";

//            if (mimetype.compareTo("image/jpeg") == 0) {
            // Look for Mimetypes image/*
            if (mimetype.indexOf("image/") == 0) {
                indexName = "photos";

                System.out.println("image/jpeg found!!!!!!!!");

                JSONObject jsonExif = ExifJSON.getExifJSON(
                        hssmclient.getFile(
                                stagedFile.getString("container"),
                                stagedFile.getString("id")
                        )
                );

                System.out.println("jsonExif: " + jsonExif.toString());

                JSONObject metadata = new JSONObject();

                // Initialize to avoid errors in the client applications
                metadata.put("camera", "");
                stagedFile.put("file_date", "");

                if(jsonExif.getJSONObject("exif").has("Exif IFD0")){
                    if(jsonExif.getJSONObject("exif").getJSONObject("Exif IFD0").has("Date/Time"))
                        stagedFile.put("file_date", jsonExif.getJSONObject("exif").getJSONObject("Exif IFD0").getString("Date/Time"));
                    if(jsonExif.getJSONObject("exif").getJSONObject("Exif IFD0").has("Model"))
                        metadata.put("camera", jsonExif.getJSONObject("exif").getJSONObject("Exif IFD0").getString("Model"));
                }
//                else {
//                    metadata.put("camera", "");
//                    stagedFile.put("file_date", "");
//                }

                if(jsonExif.getJSONObject("exif").has("GPS"))
                    metadata.put("location_gps", jsonExif.getJSONObject("exif").getJSONObject("GPS"));
                else
                    metadata.put("location_gps", new JSONObject());
                metadata.put("location_addr", "");
                metadata.put("source", "");
                metadata.put("tags", "");
                metadata.put("galleries", "");



                System.out.println("metadata: " + metadata.toString());

//                stagedFile.put("exif", jsonExif.getJSONObject("exif"));
                stagedFile.put("metadata", metadata);
                stagedFile.put("status", "stage1");

//                jsonIndexCategory = new JSONObject();
////                jsonIndexCategory.put("index", "photos");
//                jsonIndexCategory.put("index", "sm_objectstoreindex_staging");
//                jsonIndexCategory.put("id", stagedFile.getString("id"));
//
//                jsonIndexCategory.put("data", stagedFile);
//
//                System.out.println("Updated stagedFile: " + jsonIndexCategory.toString());

            } else if (mimetype.compareTo("application/pdf") == 0 ) {
                indexName = "documents";

                System.out.println("application/pdf found!!!!!!!!");
                JSONObject jsonMeta = PDFMetaData.extractPDFMetaData(
                        hssmclient.getFile(
                        stagedFile.getString("container"),
                        stagedFile.getString("id")
                ));

                System.out.println("\n jsonMeta: " + jsonMeta.toString());
//                System.out.println("\n jsonMeta: date" + jsonMeta..getString("CreationDate"));

                JSONObject metadata = new JSONObject();

                if(jsonMeta.has("Title"))
                    metadata.put("Title", jsonMeta.getString("Title"));
                else
                    metadata.put("Title", "");
                if(jsonMeta.has("Author"))
                    metadata.put("Author", jsonMeta.getString("Author"));
                else
                    metadata.put("Author", "");
                if(jsonMeta.has("Keywords"))
                    metadata.put("Keywords", jsonMeta.getString("Keywords"));
                else
                    metadata.put("Keywords", "");

                System.out.println("\n metadata: " + metadata.toString());

//                stagedFile.put("pdfmeta", jsonMeta);
                stagedFile.put("metadata", metadata);
                stagedFile.put("status", "stage1");
                if(jsonMeta.has("CreationDate"))
                    stagedFile.put("file_date", jsonMeta.getString("CreationDate"));
//                else if(jsonMeta.has("ModificationDate"))
//                    stagedFile.put("file_date", jsonMeta.getString("ModificationDate"));
                else
                    stagedFile.put("file_date", "");


//                jsonFinal = (new JSONObject(hit)).put("meta", jsonMeta);
//                System.out.println("jsonFinal: " + jsonFinal.toString());
            }


            arrFinal.add(stagedFile);
        }

        System.out.println("arrFinal: " + arrFinal.toString());
        HSSMClient.bulkUpdate(arrFinal);

        return arrFinal;

    }


}
