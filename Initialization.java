import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Class that contains initializers for the database, including connecting to the sql server,
 * creating the schema and table, and filling up the table with input from text file.
 * 
 * @author Steven Wojsnis
 *
 */
public class Initialization {
	private String url, username, password; 
	private Path inputFile, outputFile;
	private boolean badInputFileFlag;
	
	/**
	 * Constructor for when the user doesn't provide command line arguments
	 */
	public Initialization(){
		
		badInputFileFlag = false;
		// Called method prompts user for input/output files
		if(inputFile == null || outputFile == null){
			selectInputAndOutputFiles();
		}
		
		// Calls method that connects user to SQL server
		connectToServer();
	}
	
	/**
	 * Constructor that is used as a way of gaining access to Initialization methods without actually initializing a new server connection.
	 * @param conn
	 */
	public Initialization(Connection conn){
		url = "jdbc:mysql://localhost:3306/?autoReconnect=true&useSSL=false";
		username = "root";
		password = "root";
	}
	
	/**
	 * Constructor for when a user does provide command line arguments
	 * @param input : inputFile
	 * @param output : outputFile
	 */
	public Initialization(String input, String output){
		inputFile = Paths.get(input); 
		outputFile = Paths.get(output);
		badInputFileFlag = false;
		connectToServer();
		
	}
	
	/**
	 * Method that calls the connectToDb method to connect to the server, and then
	 * calls a method to create a new table in the database, as well as calling a method
	 * to instantiate the GUI.
	 */
	
	private void connectToServer(){
		url = "jdbc:mysql://localhost:3306/?autoReconnect=true&useSSL=false";
		username = "root";
		password = "root";
		
		// Connects to the server
		connectToDb(url, username, password);
		
		try {
			// Creates initial table in the database
			createTable(DriverManager.getConnection(url, username, password));
			// Instantiates the GUI
			new BooksGUI(createResultSet(), outputFile);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that connects the Java code to the MySQL database via the DriverManager.getConnection method.
	 * Also creates a new schema within the recently connected to database called "BOOKSTORE."
	 * @param url : url for connecting to the server
	 * @param username : username that is used as a credential for connecting to the server
	 * @param password : password that is used as a credential for connecting to the server
	 */
	private void connectToDb(String url, String username, String password){
		System.out.println("Connecting to database...");
	
		// Connects to server and creates new schema, BOOKSTORE
		try(Connection conn = DriverManager.getConnection(url, username, password)){
			System.out.println("Connected\n\n");
		
			String createBooksSchema = "create schema BOOKSTORE";
			Statement statement = conn.createStatement();
			statement.executeUpdate(createBooksSchema);
			
			
		} catch (SQLException e){
			System.out.println("Connecting to pre-existing database.");
			//throw new IllegalStateException("cannot connect", e);
		}
		
	}
	
	/**
	 * Method that allows a user to provide input and output files via JFileChooser, rather than as command line
	 * arguments.
	 */
	private void selectInputAndOutputFiles(){
		JFileChooser fd = new JFileChooser(); //input file
		JFileChooser fd2 = new JFileChooser(); //output file
		
		// Choosing input file
		while(inputFile == null || badInputFileFlag == true){
			JOptionPane.showMessageDialog(null, "Select an input file.");
				fd.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fd.showOpenDialog(null);
				if(fd.getSelectedFile() != null){
					inputFile = fd.getSelectedFile().toPath();
					badInputFileFlag = false;
				}
		}
		
		// Choosing output file
		while(outputFile == null){
			JOptionPane.showMessageDialog(null, "Select an output file.");
				fd2.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				fd2.showOpenDialog(null);
				if(fd2.getSelectedFile()!= null)
					outputFile = fd2.getSelectedFile().toPath();
		}
	}
	
	/**
	 * Method creates a new table within the newly created schema in the database.
	 * The table name is "Books" and it is designed to contain information about various
	 * books.
	 * 
	 * Also calls the readFile method, to fill the newly created table with data from an input file
	 * 
	 * @param conn : Connection to the MySql server
	 * @throws SQLException : throws SQLException in case of an error in the SQL statement.
	 */
	public void createTable(Connection conn) throws SQLException {
		
		// String that will be used to create the table
	    String createString =
	        "create table BOOKSTORE.BOOKS " +
	        "(ISBN varchar(13) NOT NULL, " +
	        "BOOK_NAME varchar(255) NOT NULL, " +
	        "YEAR_PUBLISHED integer NOT NULL, " +
	        "AUTHOR varchar(40) NOT NULL, " +
	        "PAGES integer NOT NULL, " +
	        "CUR_STOCK integer NOT NULL, " +
	        "PRICE decimal(10,2) NOT NULL, " +
	        "AMOUNT_SOLD integer NOT NULL, " +
	        "LAST_SHIP_RECEIVED date NOT NULL, " +
	        "PRIMARY KEY (ISBN))";
	    
	    Statement statement = null;
	    
	    try {
	    	//execution of the statement
	        statement = conn.createStatement();
	        statement.executeUpdate(createString);
	        
	        // Method called to fill newly created table with data from an input file
	        readFile(inputFile);
	    
	    } catch (SQLException e) {
	    	System.out.println("Table 'books' already exists - Initialization Input File is ignored.");
	        //System.out.println("Error with SQL statement");
	        //e.printStackTrace();
	    } finally {
	        if (statement != null) { statement.close(); }
	    }
	}
	
	/**
	 * Creates a ResultSet that is later used to display the table data. The ResultSet is
	 * filled via a Query that returns the entire table. Not only is this method used to
	 * initially fill the table, but it can also be called the update the table when changes occur.
	 * 
	 * @return : the ResultSet that contains the table data
	 */
	public ResultSet createResultSet() {
		
		ResultSet rs = null;
		
		try {
			Connection conn = DriverManager.getConnection(url, username, password);
			Statement statement = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			// Query that returns the entire table
			rs = statement.executeQuery("SELECT * FROM BOOKSTORE.BOOKS");
			return rs;
			} catch (SQLException e){
				e.printStackTrace();
			}
		
		return rs;
	}
	
	/**
	 * Method that takes the contents of an input file and stores it in the table. This is used to take
	 * data from a text file and instantiate the table with it.
	 * 
	 * @param inputFilePath : File that contains the data to be added
	 */
	public void readFile(Path inputFilePath){
		String[] items = new String[9]; // Will temporarily hold the items to be added
		
		//Reads in the file contents
		try (BufferedReader br = new BufferedReader(Files.newBufferedReader(inputFilePath))){
			
			String line = br.readLine();
			
			//Used to cycle through each line in file
			while(line != null){
				
				//Tokenizer to separate the items on each line
				StringTokenizer tokenizer = new StringTokenizer(line, "::");
				int i = 0;
				//Used to cycle through each item in line
				while(tokenizer.hasMoreTokens()){
					items[i] = tokenizer.nextToken();
					i++;
				}
				
				try {
					//Executes query that inserts the new data
					Connection conn = DriverManager.getConnection(url, username, password);
					Statement statement = conn.createStatement(
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_UPDATABLE);
					statement.executeUpdate("insert into BOOKSTORE.BOOKS values "
					        + "('"+items[0]+"', '"+items[1]+"', "+items[2]+", '"+items[3]+"', "+
							items[4]+", "+items[5]+", "+items[6]+", "+items[7]+", '"+items[8]+"')");
					
					conn.close();
					} catch(DataTruncation dt){
						System.out.println("Error processing book with ISBN: "+items[0]+" , ensure correct format.");
					}catch (SQLException e){
						e.printStackTrace();
					}
				line = br.readLine();
			}
			
		} catch(Exception e){
			e.printStackTrace();
			int i = 0;
			while(items[i] != null){
				System.out.println(items[i]);
			i++;}
			JOptionPane.showMessageDialog(null,
					"There was an error with the selected input file",
				    "Error With Input File",
				    JOptionPane.ERROR_MESSAGE);
			int n = JOptionPane.showConfirmDialog(null, 
					"Would you like to select another file?", 
					"Select New File or Exit", 
					JOptionPane.YES_NO_OPTION);
			if(n == JOptionPane.YES_OPTION){
				badInputFileFlag = true;
				selectInputAndOutputFiles();
				readFile(inputFile);
			}
			else{
				JOptionPane.showMessageDialog(null,
						"Application cannot run without an input file. Application will now close.",
					    "No Input File",
					    JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}
		
		
	}
	
	// GETTERS 
	
	public String getURL(){
		return url;
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getPassword(){
		return password;
	}
	
	public Path getInputFile(){
		return inputFile;
	}
	
	public Path getOutputFile(){
		return outputFile;
	}
}