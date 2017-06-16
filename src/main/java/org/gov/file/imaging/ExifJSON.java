package org.gov.file.imaging;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by govind on 8/14/16.
 */
public class ExifJSON {

//    public static JSONObject getExifJSON(File file) {
    public static JSONObject getExifJSON(InputStream filestream) {

        JSONObject jsonObject = new JSONObject("{\"exif\":{}}");

        try {
            Metadata metadata = ImageMetadataReader.readMetadata(filestream);

            for (Directory directory : metadata.getDirectories()) {

                //
                // Each Directory stores values in Tag objects
                //
                for (Tag tag : directory.getTags()) {

                    if (jsonObject.getJSONObject("exif").isNull(tag.getDirectoryName())) {
                        // If JSON property is not present create it
                        jsonObject.getJSONObject("exif").put(tag.getDirectoryName(), new JSONObject("{}"));
                    }

                    jsonObject.getJSONObject("exif").getJSONObject(tag.getDirectoryName()).put(tag.getTagName(), tag.getDescription());
                }

                //
                // Each Directory may also contain error messages
                //
                if (directory.hasErrors()) {
                    for (String error : directory.getErrors()) {
                        System.err.println("ERROR: " + error);
                    }
                }
            }

        } catch (ImageProcessingException e) {
            // handle exception
            System.err.println(e);
        } catch (IOException e) {
            // handle exception
            System.err.println(e);
        }

        return jsonObject;
    }

}
