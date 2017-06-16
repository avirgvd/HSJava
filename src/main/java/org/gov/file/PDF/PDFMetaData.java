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
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * This is an example on how to extract metadata from a PDF document.
 *
 */
public final class PDFMetaData
{
    public static JSONObject extractPDFMetaData(String filename, String filePath)
    {
        JSONObject jsonObject = new JSONObject("{}");


        PDDocument document = null;
        try
        {
            document = PDDocument.load(new File(filePath));
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            PDPage page0 = document.getPage(0);

            // Extract PDF thumbnail image from first page
            BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);

            String thumbnailPath = filePath + ".png";
            ImageIOUtil.writeImage(bim, thumbnailPath, 300);
            // Set the thumbnail file path to the output meta data
            jsonObject.put("thumbnail", filename + ".png");

            PDDocumentCatalog catalog = document.getDocumentCatalog();

            PDMetadata meta = catalog.getMetadata();

            if (meta != null) {

                COSInputStream stream = meta.createInputStream();
                System.out.print("PDF metadata: " + meta.toString());

            }
            if (document != null) {

                // The pdf doesn't contain any metadata, try to use the
                // document information instead
                PDDocumentInformation information = document.getDocumentInformation();
                if (information != null) {
//                    showDocumentInformation(information);
                    extractDocumentInformation(jsonObject, information);
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

    private static void extractDocumentInformation(JSONObject jsonObject, PDDocumentInformation information)
    {
        assign(jsonObject, "Title", information.getTitle());
        assign(jsonObject, "Subject", information.getSubject());
        assign(jsonObject, "Author", information.getAuthor());
        assign(jsonObject, "Creator", information.getCreator());
        assign(jsonObject, "Producer", information.getProducer());
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
