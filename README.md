# sendmesmb
it means : send me a file store on my cifs server.

I don't know if such a project had already exist. I did it for training a few java apis and play with a github free account.
Anyway it's usefull to have access to a document in the train with your phone. 

How it works?

a - A user send an email with a request to find a file in the cifs server.
b - The user receive a mail with the list of files corresponding to the search.
c - The user reply a request to give the file.
d - The user receive the file attached or a link on a web server.

A mailbox on the mail server is dedicated to receive all the users requests.
The request are action1 and action2 parameters that you can localized in the sendme.props file.
SEARCH mean search a file in the cifs server.
SEND mean give me the file.
It only take into account the messages with the flag unread. Messages are flagged read and kept on the imap server.

How to setup SendMeSmb ?
1/ Configure the file sendme.props
Syntax of the sendme.props file :
smbserver is the name of the cifs server.
share is the share resource on the cifs server (not used)
depthcrawl is how many directory levels to scan on the cifs server
imapdir is the folder of the dedicated mailbox on the imap server.
imaphost is the imap server internet name.
imapusername is the owner of the dedicated mailbox.
imappassword his password.
mailhost is the smtp server to reply to the users.
imapdebug for debugging the imap function.
debug a flag to print variable on console

2/ Configure the users access
Use the encodeco class to populate the file sendme.props. 
java -cp jcifs-krb5-1.3.17.jar:./android-sdk-linux/tools/lib/commons-codec-1.4.jar:. encodeco emailuser authorized_share smbuser pwdsmbuser >>sendme.props
it will encrypt the pwdsmbuser argument.


Syntax of the section [users] (A feature could be to ask authorisation to an ldap server.)
fromuser1=share:usersmb1:xxxx
fromuser1 is the from part of the address of a user.
share is the share ressource is allowed to access.
usersmb1:xxxx is the username and password of fromuser1 on the cifs server.


3/ Start the SendMeSmb 
After having modify the sendme.props file, the administrator setup the crontab to call SendMeSmb at regular interval using the following commandline :
java -cp jcifs-krb5-1.3.17.jar:javamail-1.3/javax.mail.jar:android-sdk-linux/tools/lib/commons-codec-1.4.jar:. SendMeSmb



