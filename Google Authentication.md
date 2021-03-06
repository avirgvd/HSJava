##Overview

Interaction with GMail needs authorizing the application to access GMail data.
Using IMAP to access gmail is not secure way as the user need to first enable less secure apps using the URL 
https://www.google.com/settings/security/lesssecureapps
Instead use the google gmail API to access the emails. 
To make this work, the client appliaction must be registered with Google and clientID should be used will all the calls to Google.

The authentication should be performed by the user from the Browser.
The authentication creds including access token and refresh token should be stored in the elastic search.


The HSJava/EMailReceiver uses the stored creds in ES to connect to GMail.

## Google API Console
https://console.developers.google.com/

##Authentication from Browser

This code resides in HSAppl/src/js/components/settings/settings.js

NPM packages used by this:
_# npm install hellojs_

CHALLENGE: 
hellojs doesn't have support to request for offline acess refresh_tokens. 
The workaround to include the option "response_type=code" with login 
request depends on external oauth_proxy https://auth-server.herokuapp.com/proxy
To enable this proxy, the application client id should be given the this proxy service site.

Date: 9/7/2017
Looking for alternatives to hellojs

Now trying direct google api from:
_https://developers.google.com/gmail/api/quickstart/nodejs_

in HSAppl client app, do the following for Google Authentication

npm install googleapis --save
npm install google-auth-library --save

Date: 09/08/2017
Ditch hellojs and use react-google-login from https://www.npmjs.com/package/react-google-login

First check https://developers.google.com/gmail/api/quickstart/nodejs

Date: 10/09/2017
The problem is, the user consent for Google auth is performed from the web browser side. But the authentication should be performed by web server Node.js.
So cannot use one client-id for both. Check the below link for potential solution to this problem:
https://developers.google.com/identity/protocols/CrossClientAuth


## Enabling Server-Side Access 
https://developers.google.com/identity/sign-in/android/offline-access



Authenticate with a backend server 
https://developers.google.com/identity/sign-in/web/backend-auth
If you use Google Sign-In with an app or site that communicates with a backend server, you might need to identify the currently signed-in user on the server. To do so securely, after a user successfully signs in, send the user's ID token to your server using HTTPS. Then, on the server, verify the integrity of the ID token and retrieve the user's ID from the sub claim of the ID token. You can use user IDs transmitted in this way to safely identity the currently signed-in user on the backend.


###IMP: 
For server side authorization, use 'postmessage' for redirect_uri. 
_var oauth2Client = new OAuth2(clientId, clientSecret, 'postmessage');_
source: https://stackoverflow.com/questions/11485271/google-oauth-2-authorization-error-redirect-uri-mismatch
Google documentation sucks!


FINALLY gooogle authentication is working

##Connecting to GMail from HSJava


Date: 29/10/2017
Trying hello.js again. Registered Google client secret at https://auth-server.herokuapp.com/#signin
Totally got rid of hello.js as it forces the app to use their site as redirect URI. 
They will gain full access to the user accounts. NOT SAFE
Finally this site gave relevant info
http://voidcanvas.com/googles-oauth-api-node-js/


###IMP:
The refresh_token is only sent when the user initially authorizes your app with their account.
So it the getToken function only returns it the first time (because you should store it).
Added approval_prompt: "force" to the generateAuthUrl options, and I can get it every time.
Without the approval_prompt you can go to the user google account security settings under
"Apps with access to your account and remove HSAppl from the list.
Once you do that you will receive refresh token.