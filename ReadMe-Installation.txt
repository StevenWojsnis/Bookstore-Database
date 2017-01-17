########################
# Installation process #
########################

####################
Installing MySQL
####################

First and foremost, the BookStore application uses a MySQL database. As such, the user must download and install
MySQL. This section of the ReadMe document will walk the user through the installation process.

To download MySQL, navigate to the mysql products page (http://www.mysql.com/products/)
On this page, select "Download the MySQL Community Edition"

At this point, you will have to determine what to download based on your operating system. The rest of
this document will assume the user is using Windows.

Scroll down and download the latest version of "Windows(x86, 32-bit), MySQL Installer MSI" (This will most likely
be listed as their "recommended download"

Download and run the file.

Upon running the MySQL installer: 

You should select the "Developer Default" option in the "Choosing a Setup Type" screen.

For this application, you may skip the contents of the "Check Requirements" screen and just hit next.

In the "Installation" screen, ensure that all items have status "Ready to Install" and then press "Execute"
At this point, the MySQL files should begin to install.

Upon installation completion, you will be brought to a "Product Configuration" screen. Hit next.

On the "Type and Networking" screen, ensure that the "Port Number" is 3306. The BookStore application will not
be able to connect to the database server if this port number is different from 3306.

On the "Accounts and Roles" screen, enter "root" for your MySQL Root Password. Similarly, the BookStore application
will not work if another value is chosen for the password other than "root".

You may skip the contents of the "Windows Service" and "Plugins and Extensions" screen, and just hit next on
both of them.

Once on the "Apply Server Configuration" screen, hit "Execute"

After completion, hit next, and you should be brought to the setup of samples series of screens.
This shouldn't be strictly necessary, but for the sake of completion, this document will walk you through this
process as well.

On the "Connect to server" screen, hit the "Check" button to make sure you are using the correct credentials.
(The credential fields should be pre-filled for you, but if they aren't both Username and Password should be "root")
Hit Next upon receiving the "Connecting successful" message.

On the "Apply Server Configuration" screen, hit "Execute". And then upon completion, hit "Finish".

After this, you should be brought back to the "Product Configuration" Screen, on which you will hit next.

This should bring you to the "Installation Complete" on which you will be prompted to restart your machine.
Press finish, and your machine should restart, unless you opted to restart it manually later.

After this, the MySQL server should be set up, and the application should be ready to run.

***NOTE*** This document does not support operating systems other than Windows, but if a user is installing
MySQL on a different operating system, they must ensure that their server has a port number of 3306, and their
server password is "root".


####################
Installing JDK
####################

This application is written in java, and thus needs to be compiled and run using the JDK.
To do this, search for the Java Se Development kit (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
and download the latest version. Upon download, you must locate the Java file on your machine (most likely in
Program Files). From the Java file, navigate into the jdk(version number) file and obtain the path of the bin
folder within the jdk folder.

You must now add the path of your jdk bin folder to your Path Environmental Variable.

An easy way to do this on Windows, is to navigate to your control panel -> System -> Advanced -> Environmental Variables
Then, add the path of the JDK bin folder to the "PATH" variable in System Variables.