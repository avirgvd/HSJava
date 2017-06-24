package org.gov.file;

import org.gov.file.PDF.PDFMetaData;
import org.gov.elasticsearch.ESClient;
import org.gov.file.imaging.ExifJSON;
import org.gov.hssmclient.HSSMClient;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class FileIngest {

    public static void main(String[] args) {
	// write your code here

        ESClient esclient = new ESClient();
        HSSMClient hssmclient = new HSSMClient();

        esclient.createClientSession();

        // Look for staged files to ingest
        JSONObject stagedFiles = hssmclient.getStagedFiles();

        System.out.println("stagedFiles: " + stagedFiles.toString());

        int count = stagedFiles.getJSONObject("result").getInt("count");
        ArrayList<JSONObject> arrUpdateStaged = new ArrayList<JSONObject>(count);
        ArrayList<JSONObject> arrFinal = new ArrayList<JSONObject>(count);

        System.out.println("Result count: " + count);
//        System.out.println("Result 2nd: " + stagedFiles.getJSONObject("result").getJSONArray("items").get(2));

        if(count == 0) {
            System.out.println("NO STAGED FILES FOUND SO EXITING.....");
            return;
        }

        for(int i = 0; i < count; i++) {
            JSONObject stagedFile = stagedFiles.getJSONObject("result").getJSONArray("items").getJSONObject(i);
            JSONObject jsonHit = new JSONObject(stagedFile);
            System.out.println("Staged file ===> " + stagedFile.toString());

            String mimetype = stagedFile.getString("mimetype");
            System.out.println("mimetype: " + mimetype);

            JSONObject jsonFinal = null;
            JSONObject jsonIndexCategory = null;
            String indexName = "";

            if (mimetype.compareTo("image/jpeg") == 0) {
                indexName = "photos";

                System.out.println("image/jpeg found!!!!!!!!");

                JSONObject jsonExif = ExifJSON.getExifJSON(
                        hssmclient.getFile(
                                stagedFile.getString("container"),
                                stagedFile.getString("id")
                        )
                );

                System.out.println("jsonExif: " + jsonExif.toString());

                stagedFile.put("exif", jsonExif);
                stagedFile.put("status", "staged");
                stagedFile.put("file_date", jsonExif.getJSONObject("exif").getJSONObject("Exif IFD0").getString("Date/Time"));

                jsonIndexCategory = new JSONObject();
//                jsonIndexCategory.put("index", "photos");
                jsonIndexCategory.put("index", "sm_objectstoreindex_staging");
                jsonIndexCategory.put("id", stagedFile.getString("id"));

                jsonIndexCategory.put("data", stagedFile);

                System.out.println("Updated stagedFile: " + jsonIndexCategory.toString());

            } else if (mimetype.compareTo("application/pdf") == 0 ) {
                indexName = "documents";

                System.out.println("application/pdf found!!!!!!!!");
//                JSONObject jsonMeta = PDFMetaData.extractPDFMetaData(jsonHit.getString("filename"), filePath);
//                jsonFinal = (new JSONObject(hit)).put("meta", jsonMeta);
//                System.out.println("jsonFinal: " + jsonFinal.toString());
            }


            arrFinal.add(jsonIndexCategory);
        }

//        esclient.updateBulkDocuments( arrFinal);
        HSSMClient.bulkMove(arrFinal, "media1");


    }
}
