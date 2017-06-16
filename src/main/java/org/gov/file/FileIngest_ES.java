package org.gov.file;

import org.gov.elasticsearch.ESClient;
import org.gov.file.PDF.PDFMetaData;
import org.gov.file.imaging.ExifJSON;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class FileIngest_ES {

//    public static void main(String[] args) {
//	// write your code here
//
//        ESClient esclient = new ESClient();
//
//        esclient.createClientSession();
//
//        // Look for stateg files to ingest
//        ArrayList<String> arrHits = esclient.searchIndex("stagedfiles", null);
//
//        if(arrHits.size() == 0) {
//            System.out.println("NO STAGED FILES FOUND SO EXITING.....");
//            return;
//        }
//
//        ArrayList<JSONObject> arrUpdateStaged = new ArrayList<JSONObject>(arrHits.size());
//
//        ArrayList<JSONObject> arrFinal = new ArrayList<JSONObject>(arrHits.size());
//        // For each staged file perform file specific meta data extraction and other processing
//        // and add the additional data to ES
//        for(String hit: arrHits) {
//
//            JSONObject jsonHit = new JSONObject(hit);
//            String mimetype = jsonHit.getString("mimetype");
//
////            System.out.println("[hit]: " + jsonHit.getString("path"));
//            System.out.println("[hit]: " + mimetype);
//            File file1 = new File(jsonHit.getString("path"));
//            String filePath = jsonHit.getString("path");
//
//            JSONObject jsonFinal = null;
//            String indexName = "";
//
//            if (mimetype.compareTo("image/jpeg") == 0) {
//                indexName = "photos";
//
//                System.out.println("image/jpeg found!!!!!!!!");
//
////                JSONObject jsonExif = ExifJSON.getExifJSON(file1);
////                jsonFinal = (new JSONObject(hit)).put("exif", jsonExif.getJSONObject("exif"));
//
//            } else if (mimetype.compareTo("application/pdf") == 0 ) {
//                indexName = "documents";
//
//                System.out.println("application/pdf found!!!!!!!!");
//                JSONObject jsonMeta = PDFMetaData.extractPDFMetaData(jsonHit.getString("filename"), filePath);
//
//                jsonFinal = (new JSONObject(hit)).put("meta", jsonMeta);
//
//                System.out.println("jsonFinal: " + jsonFinal.toString());
//
//            }
//
//            jsonFinal.remove("status");
//            arrFinal.add(jsonFinal);
//            jsonHit.put("status", "staged");
////            arrUpdateStaged.add(jsonHit);
//            // Index each item to ES
//            esclient.indexDocument(indexName, jsonFinal);
//
//            System.out.println("With exif: " + jsonHit.getString("status"));
//            System.out.println("With exif: " + jsonFinal.toString());
//        }
//
//        // this below line is working
////        esclient.indexBuilkDocuments("photos", arrFinal);
//
////        // TODO: this below line has issues updating
////        if (arrUpdateStaged.size() > 0)
////            esclient.updateBuilkDocuments("stagedfiles", arrUpdateStaged);
//
//
//
//
//
//
//
//
//    }
}
