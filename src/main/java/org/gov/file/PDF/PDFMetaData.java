package org.gov.file.PDF;

import org.apache.pdfbox.cos.COSInputStream;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.json.JSONObject;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This is an example on how to extract metadata from a PDF document.
 *
 */
public final class PDFMetaData
{
    public static JSONObject extractPDFMetaData(InputStream inputFileStream)
    {
        JSONObject jsonObject = new JSONObject("{}");


        PDDocument document = null;
        try
        {
//            document = PDDocument.load(new File(filePath));
            document = PDDocument.load(inputFileStream);
//            PDFRenderer pdfRenderer = new PDFRenderer(document);

//            PDPage page0 = document.getPage(0);

//            // Extract PDF thumbnail image from first page
//            BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);

//            String thumbnailPath = filePath + ".png";
//            ImageIOUtil.writeImage(bim, thumbnailPath, 300);
//            // Set the thumbnail file path to the output meta data
//            jsonObject.put("thumbnail", filename + ".png");

            PDDocumentCatalog catalog = document.getDocumentCatalog();

            PDMetadata meta = catalog.getMetadata();

            if (meta != null) {

                COSInputStream stream = meta.createInputStream();
//                System.out.print("PDF metadata: " + meta.toString());

            }
            if (document != null) {

                // The pdf doesn't contain any metadata, try to use the
                // document information instead
                PDDocumentInformation information = document.getDocumentInformation();
                if (information != null) {
//                    showDocumentInformation(information);
                    jsonObject = extractDocumentInformation(information);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally
        {
            if (document != null)
            {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return jsonObject;
    }

    private static JSONObject extractDocumentInformation( PDDocumentInformation information)
    {
        JSONObject jsonObject = new JSONObject("{}");

        jsonObject.put( "Title", information.getTitle());
        jsonObject.put( "Subject", information.getSubject());
        jsonObject.put( "Author", information.getAuthor());
        jsonObject.put( "Creator", information.getCreator());
        jsonObject.put( "Producer", information.getProducer());

        Calendar calendar = information.getCreationDate();

        if(calendar != null){
            jsonObject.put( "CreationDate", calendar.getTime().toString());
        }

        calendar = information.getModificationDate();

        if(calendar != null) {
            jsonObject.put( "ModificationDate", calendar.getTime().toString());
        }

        return jsonObject;
    }

    private static String format(Object o)
    {
        if (o instanceof Calendar)
        {
            Calendar cal = (Calendar) o;
            return DateFormat.getDateInstance().format(cal.getTime());
        }
        else
        {
            return o.toString();
        }
    }

    private static void assign(JSONObject jsonObject, String title, Object value)
    {
        if (value != null)
        {
            System.out.println(title + " " + format(value));
            jsonObject.put(title, value);
        }
    }

}
