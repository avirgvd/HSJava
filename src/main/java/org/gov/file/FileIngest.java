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

        int count = stagedFiles.getJSONObject("result").getInt("count");
        ArrayList<JSONObject> arrUpdateStaged = new ArrayList<JSONObject>(count);
        ArrayList<JSONObject> arrFinal = new ArrayList<JSONObject>(count);

        System.out.println("Result count: " + count);
        System.out.println("Result 2nd: " + stagedFiles.getJSONObject("result").getJSONArray("items").get(2));

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

                jsonIndexCategory = new JSONObject();
                jsonIndexCategory.put("index", "photos");
                jsonIndexCategory.put("id", stagedFile.getString("id"));
                jsonIndexCategory.put("data", jsonExif);

//                jsonFinal = (new JSONObject(stagedFile)).put("exif", jsonExif.getJSONObject("exif"));
                System.out.println("jsonIndexCategory: " + jsonIndexCategory.toString());

            } else if (mimetype.compareTo("application/pdf") == 0 ) {
                indexName = "documents";

                System.out.println("application/pdf found!!!!!!!!");
//                JSONObject jsonMeta = PDFMetaData.extractPDFMetaData(jsonHit.getString("filename"), filePath);
//                jsonFinal = (new JSONObject(hit)).put("meta", jsonMeta);
//                System.out.println("jsonFinal: " + jsonFinal.toString());
            }


            arrFinal.add(jsonIndexCategory);
        }

//             this below line is working
        esclient.indexBuilkDocuments( arrFinal);
//
//        // TODO: this below line has issues updating
//        if (arrUpdateStaged.size() > 0)
//            esclient.updateBuilkDocuments("stagedfiles", arrUpdateStaged);
//
//
//        }

    }
}
