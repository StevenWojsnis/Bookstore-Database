###################################
# Using the BookStore application #
###################################

Firstly, one must compile the program. To do this, a user must navigate to the program directory, and run
the command:

"javac Main.java"

This will then compile all of the necessary .java files into .class files that are needed to execute the 
application.

################
JConnector for SQL
################

It is also important to note that you must temporarily modify the classpath of the program to include the 
JConnector for SQL. Without including this .jar file in the classpath, the application will fail to run. 
The necessary .jar file is included in the product folder, along with the .java files, and sample input/output 
text files. To include the .jar file in the classpath, the user must include the following after “java” and 
before “Main”:

"-cp .;mysql-connector-java-5.1.39-bin.jar"


Therefore, the complete command to run the application should look like this:

"java -cp .;mysql-connector-java-5.1.39-bin.jar Main"


NOTE: Do not remove the .jar file from the directory in which the application works, otherwise the user will 
need to modify the classpath command.

########################################
Command line arguments vs GUI selection
########################################

There are two ways to properly run the BookStore application.
Regardless of the way chosen to run the BookStore application, one must supply two files 
(an input file to initialize the database, and an output file to record changes made to the database). 
The only difference between the two execution methods is one method allows you to select your two files
as command line arguments, while the other one allows you to select files via a GUI system (JFileChooser).

To run the application with command line arguments, they should be run in the following format:

"java -cp .;mysql-connector-java-5.1.39-bin.jar Main inputFileBooks.txt outputFileBooks.txt"


And to run the application without command line arguments (selecting the files via the GUI system), the 
should be run in the following format:

"java -cp .;mysql-connector-java-5.1.39-bin.jar Main"


NOTE: For the command line arguments option, it is assumed that the text files are included in the directory from
which the application is being executed. If text files that aren’t in the current directory are to be used, then 
their full path must be stated, rather than simply “inputFile.txt”

It’s important that the input file is in the correct format. See the sample file included in the product 
folder to see the proper format.

NOTE: Once database is created with initialization file, running the application with a new or modified 
input file will not change the database for security reasons. The only way to start fresh, using a new input
file to create a new database is to delete the pre-existing database schema named "bookstore". Then the application
will create a new schema and table with the desired new input file.


#################################
Interacting with the application
#################################

After executing the application, a GUI will appear that will display the data included in the input file. 
The table displayed on the screen reflects the information that is currently being stored in a MySQL 
database (the input file was used to create and initialize the database). Therefore, any changes made in 
the table (Additions, Deletions, and Modifications) will also be reflected in the database.


############################
Signing in
############################

It is important to note that some table modifications are restricted depending on the current user.
Upon opening the table, a user should navigate to the “Utilities” menu in the upper left corner of the 
application and select the “sign in” option. To receive the proper permission to Modify cell data in the 
table, or delete tables, the user should input the word “root” in the prompt for a password. Upon 
successfully signing in, the user will now be able to modify cells and delete rows. Signing in again 
with an incorrect password, or no password, for that matter, will effectively sign the user out, taking 
away the recently elevated permissions.

It should be noted that permission is not needed to add rows to the table, as no significant damage can be 
done by the addition of rows, whereas the changing of cell data or deletion of rows can cause disaster if 
done by the wrong individual.

############################
Adding rows
############################

To add a new row to the database, the user must navigate to the "Utilities" menu and select the "Add Row" option.
Upon selection, the user will be prompted with a series of windows asking the user for information about the book
that they wish to insert. The application was designed such that every column must have a value, so, the user must
enter a value for each of the prompts. Because of the fact that the "date" format can often be rather finicky, the user
is given multiple attempts to enter the correct format of the date. Note: The correct format is YYYY/MM/DD.

Upon successful insertion of a row, a line is written to the given output file with the details of the newly
added row.

############################
Deleting rows
############################

To delete a row in the database, the user must navigate to the "Utilities" menu and select the "Delete Row" option.
Upon selection, the user will be prompted for an ISBN. The ISBN entered should correspond to the book that the user
wishes to delete. Upon the entering of an ISBN contained within the table, the book and row corresponding to the
given ISBN will be removed from the database. If the user fails to enter an ISBN that is contained within the 
database, then the user will be notified that the book couldn't be found, and that nothing was deleted.

Upon successful deletion of a row, a line is written to the given output file with the ISBN, Title, and Author
of the recently deleted book.

NOTE: A User must be signed in with the proper credentials to delete a row. See the "Signing in" section of this
document.

############################
Modifying rows
############################

The table is set up such that editable rows can simply be double-clicked and have their values changed manually
to modify the data in the database.

If, for example, you wanted to change the price of a book, you would simply select the appropriate cell under the
price column by double clicking it, and replacing the value in the cell with the new value.

Only the following columns can be modified: CUR_STOCK, PRICE, AMOUNT_SOLD, and LAST_SHIP_RECEIVED

The reasoning for this is that these are the only attributes of a book that can possibly change. Values such as
the ISBN, or the name of the book shouldn't change, and so the user must create a new row with the modified values
if they wish to change something such as the book's name.

Upon successful modification of a cell, a line is written to the given output file with the location of the modified
cell, and the details of the modification, such as the old value, and the new value.

NOTE: A User must be signed in with the proper credentials to modify a cell. See the "Signing in" section of this
document.

############################
Searching the database
############################

The "Search by" menu allows the user to change what is displayed in the table depending on what the user wants to
see. For instance, if the user wants to only view books written by a certain author, they would select the "Author"
option under the "Search by" menu, and enter in the author's name. The table will then change to display all books
written by that author. If there are no books written by that author, then the table will be empty.

After executing a search, the user may want to view the entire contents of the database again, rather than just 
the search results of the most recent search. If this is so, then the user should navigate to the "Utilities" menu
and select "Refresh Database" option. This will display the entire table stored in the database, rather than the
results of a query.

Details and instructions about each query are displayed when the user selects a specific "Search by" option.
In some cases, the user will simply be prompted for a value, whereas in other cases they may be prompted for a
value, as well as a selection of a button to indicate a lesser/greater search. For example, if a price is searched
for, the user must enter a price, and then decide if they want to view books with a higher price, or a lower price
than that which they entered. It should be noted that for all lesser/greater options, the table will always display
any book in which the value being searched for exactly matches that which the user entered. So, again, for example,
if a user searches for books with prices greater than 9.99, the table will also display books that have an exact
price of 9.99.