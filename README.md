# HSFileDigester
Generate file meta data and update ElasticSearch


For org.gov.html the html parser library is from: 
http://htmlparser.sourceforge.net

Staging Process

**stage1** - Extract basic metadata information and set to property 'metadata'. For media category, the metadata fields extracted are camera model, file date, GPS coordinates if available
After Stage1 processing the following updates are expected for the documents:
For photos/video: get original file date, camera model, GPS coordinates if available
For documents: get document creation date, author, title, keywords
For unknown type: do nothing...

After stage1 processing, the files should be moved to the appropriate container. Currently staging container is not cleared after the move, but there should be a periodic job that should clear these files during the idle time.

**stage2** - Further process to get user readable information from raw metadata. For example, for media category, get location address from GPS coordinates
**stage3** - Relate this document with other entities like for example, link a photo with people based on facial recognition, link the document with another category, like a photo with travel trip based on time and location

