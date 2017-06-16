

# this script extracts the first page image from the specified PDF file and
# save the image as jpg
java -jar pdfbox-app-2.0.3.jar PDFToImage -startPage 1 -endPage 1 $1