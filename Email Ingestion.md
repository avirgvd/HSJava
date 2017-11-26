

Emails from GMail are converted into javax.mail.internet.MimeMessage and 
then the contents are extracted and processed.

###Email content-types handled so far are:
**multipart/alternative** - Typically has 2 parts on as HTML or Rich text and another being plain text type
**multipart/mixed** - This type means there is an attachment. However should not assume about the attachment. Check for presence of one or more attachments.
**text/html or text/plain** - usually included as backup to Mime encoded email contents. 
But in somme cases this can be the only type of content available in the message object. 

#Email ingestion process:

##Extracting the email
###Case: Has attachments
Save the attachments in the staging area and save the message content in Messages index with references to the FileID of the attachments stored in the Staging. Set the message type as staging.

###Case: No Attachments
Save the message content to the Messages index. Set the message status as staging. 

_Note: Prefer storing HTML content-type instead of Plain text type when both are available. HTML makes the message content parsing easier._

##Processing the email content
First detect the message for known message type. 

If KNOWN then process the message using the message-type specific action. Post processing the message, delete this message item from Messages index and store reference to the actual email where ever this contents are used.

If UNKNOWN then this message should be listed under 'New Messages' in the UI. Allow the user to train how to process the messages of this type in the future and store the user training inputs for future processing of this message type. 