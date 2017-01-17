import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Class that deals with the GUI that displays a JTable containing the database information.
 * Handles the JTable itself, as well as the menus having to do with Utilities and Queries.
 * 
 * 
 * @author Steven Wojsnis
 *
 */
public class BooksGUI {
	
	private BooksTableModel btm;
	private JFrame frame;
	private JTable table;
	private JMenuBar menuBar = new JMenuBar();
	Path outputFilePath;
	Utilities ut;
	
	/**
	 * Constructor for BooksGUI. Creates a new JFrame to house the JTable that will display the data.
	 * Also creates the JTable, and connects it to the BooksTableModel class.
	 * 
	 * @param rs : ResultSet containing the data to be displayed
	 * @param outputFile : Output file to which changes to the table will be recorded in.
	 */
	public BooksGUI(ResultSet rs, Path outputFile){
		outputFilePath = outputFile; // Instantiates the outputFilePath variable.
		ut = new Utilities(outputFilePath); // Instantiates new Utilities instance.
		frame = new JFrame("Books");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Allows user to exit window
		frame.setSize(1000, 500); // Sets the size of the window
		frame.setLocation(200, 200); // Sets the location of the window
		
		
		table = new JTable();	
		
		// Connects the JTable to the BooksTableModel class, which contains information needed by the JTable
		try{
			btm = new BooksTableModel(rs, outputFile);
			table.setModel(btm);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		// Methods that create the Utilities and Search By menus, which users will use to interact with the data.
		createUtilitiesMenu(rs);
		createSearchByMenu(rs);
		
		table.setFillsViewportHeight(true);
		frame.getContentPane().add(scrollPane);
		frame.setVisible(true);
	}
	
	/**
	 * Method that creates and adds the Utilities Menu to the JFrame. The Utilities Menu contains
	 * ways for the user to make changes to the database. Including a sign-in option, an option to add
	 * rows, an option to delete rows, and an option to refresh the data being displayed.
	 * 
	 * @param rs : The ResultSet whose data will be modified by the utility options.
	 */
	private void createUtilitiesMenu(ResultSet rs){
		JMenuItem item;
		JMenu fileMenu = new JMenu("Utilities");
		
		// Creates the Sign in option and adds it to the Utilities FileMenu
		item = new JMenuItem("Sign in");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//Calls the password() method from the BooksTableModel class to allow the user to sign in
				btm.password();		
			}	
		});
		fileMenu.add(item);
		
		fileMenu.addSeparator();
		
		// Creates the Add Row option and adds it to the Utilities FileMenu
		item = new JMenuItem("Add Row");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				// Calls the retriveNewRowItems method from the Utilities class, which also calls the addNewRow method in the
				// Utilities class. The return value of the Utilities methods is passed into to the updateJTable method to
				// refresh the data in the JTable to reflect the new changes.
				updateJTable(ut.retrieveNewRowItems(rs));
			}	
		});
		fileMenu.add(item);
		
		fileMenu.addSeparator();
		
		// Creates the Delete Row option and adds it to the Utilities FileMenu
		item = new JMenuItem("Delete Row");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					//Checks privileges of the user
					if(btm.getAdmin()){
						//Obtains the ISBN of the book that the user wishes to remove, and then calls the appropriate Utility
						//methods to remove it and refresh the table.
						String deletedISBN = JOptionPane.showInputDialog(null, "Enter the ISBN of the book to be removed.");
						updateJTable(ut.deleteRow(rs, deletedISBN));
					}
					//In case of insufficient privileges, no deletions are made and the user is informed they must sign in. 
					else{
						JOptionPane.showMessageDialog(null,
							    "Only admins can delete data. Please log in (found under 'Utilities' tab).",
							    "Not Admin",
							    JOptionPane.ERROR_MESSAGE);
					}

			}	
		});
		fileMenu.add(item);
		
		fileMenu.addSeparator();
		
		// Creates the Refresh Database option and adds it to the Utilities FileMenu
		item = new JMenuItem("Refresh Database");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//Allows the user to refresh the table, to reflect any changes made, or to re-obtain a view of the entire
				//table, rather than the results of a more specific query.
				updateJTable(retrieveNewResultSet(rs));
			}	
		});
		fileMenu.add(item);
		
		// Adds the Utilities menu to the JFrame
		frame.setJMenuBar(menuBar);
		menuBar.add(fileMenu);
	}
	
	/**
	 * Method that creates and adds various query options to a "Search By" menu in the JFrame.
	 * Makes the appropriate calls to the Queries class to display the correct ResultSet given the
	 * requested query from the user.
	 * 
	 * @param rs : ResultSet that will display the data requested from the user
	 */
	private void createSearchByMenu(ResultSet rs){
		
		//Calls the Initialization constructor that doesn't create a new table/schema etc. but rather just gives access to
		//the Initialization methods.
		Connection conn = null;
		Initialization init = new Initialization(conn);
		
		//Retrieves info needed to make a new connection to the database
		String nURL = init.getURL();
		String nUsername = init.getUsername();
		String nPassword = init.getPassword();
		
		//Queries class makes queries to the database, and as such, needs appropriate information to make connections and execute queries.
		Queries queries = new Queries(nURL, nUsername, nPassword, rs);
		
		JMenuItem item;
		JMenu fileMenu = new JMenu("Search by");
		
		// Creates the ISBN option and adds it to the Search By FileMenu
		item = new JMenuItem("ISBN");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					//Makes appropriate method calls to query for desired ISBN and update the displayed table
					String desiredISBN = queries.isbnQueryPanel();
					updateJTable(queries.searchForDesiredISBN(desiredISBN));
			}	
		});
		fileMenu.add(item);
		
		fileMenu.addSeparator();
		
		// Creates the Title option and adds it to the Search By FileMenu
		item = new JMenuItem("Title");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {		
					//Makes the appropriate method calls to query for desired Title and update the displayed table
					String desiredTitle = queries.titleQueryPanel();
					updateJTable(queries.searchForDesiredTitle(desiredTitle));
			}	
		});
		fileMenu.add(item);
		
		fileMenu.addSeparator();
		
		// Creates the Author option and adds it to the Search By FileMenu
		item = new JMenuItem("Author");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
					//Makes the appropriate method calls to query for the desired Author and update the displayed table
					String desiredAuthor = queries.authorQueryPanel();
					updateJTable(queries.searchForDesiredAuthor(desiredAuthor));
			}	
		});
		fileMenu.add(item);
		
		fileMenu.addSeparator();
		
		// Creates the Year Published option and adds it to the Search By FileMenu
		item = new JMenuItem("Year Published");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {		
					//Makes the appropriate method calls to query for the desired year and update the displayed table	
					String[] desiredYearSpecs = queries.yearQueryPanel();
					updateJTable(queries.searchForDesiredYear(desiredYearSpecs));
			}	
		});
		fileMenu.add(item);
		
		fileMenu.addSeparator();
		
		// Creates the Pages option and adds it to the Search By FileMenu
		item = new JMenuItem("Pages");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {		
					//Makes the appropriate method calls to query for the desired number of pages and update the displayed table
					String[] desiredPageSpecs = queries.pageQueryPanel();
					updateJTable(queries.searchForDesiredPage(desiredPageSpecs));
			}	
		});
		fileMenu.add(item);
		
		fileMenu.addSeparator();
		
		// Creates the In Stock option and adds it to the Search By FileMenu
		item = new JMenuItem("In Stock");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {		
					//Makes the appropriate method calls to query for books that are in stock, and update the displayed table
					String desiredStockChoice = "in";
					updateJTable(queries.searchByStock(desiredStockChoice));
			}	
		});
		fileMenu.add(item);
		
		// Creates the Out of Stock option and adds it to the Search By FileMenu
		item = new JMenuItem("Out of Stock");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {	
					//Makes the appropriate method calls to query for books that are out of stock, and update the displayed table
					String desiredStockChoice = "out";
					updateJTable(queries.searchByStock(desiredStockChoice));
			}	
		});
		fileMenu.add(item);
		
		fileMenu.addSeparator();
		
		// Creates the Price option and adds it to the Search By FileMenu
		item = new JMenuItem("Price");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {	
					//Makes the appropriate method calls to query for books given a specific price, and update the displayed table
					String[] desiredPrice = queries.priceQueryPanel() ;
					updateJTable(queries.searchForDesiredPrice(desiredPrice));
			}	
		});
		fileMenu.add(item);
		
		
		// Adds the Search By menu bar to the JFrame
		frame.setJMenuBar(menuBar);
		menuBar.add(fileMenu);
	}
	
	/**
	 * Method that retrieves a fresh ResultSet that contains all the data in the stored table.
	 * Used for refreshing the table to display the entirety of the data again after the user is
	 * finished viewing the results of a query.
	 *  
	 * @param oldRS : The old ResultSet which will be overwritten by a fresh ResultSet of the entire stored table
	 * @return : The new ResultSet containing data from the entire stored table
	 */
	private ResultSet retrieveNewResultSet(ResultSet oldRS){
		Connection conn = null;
		ResultSet rs = oldRS;
		Initialization init = new Initialization(conn);
		
		//Retrieves a fresh ResultSet
		rs = init.createResultSet();
		
		return rs; 
	}
	
	/**
	 * Method that refreshes the data being displayed in the JTable.
	 * Will be used to actually implement the results of retrieveNewResultSet or any of the 
	 * Utilities methods that update the table, and Queries methods that should change the data
	 * that is displayed.
	 * 
	 * @param rs : The new ResultSet that will be displayed in the JTable
	 */
	private void updateJTable(ResultSet rs){
		try{
			boolean tempAdmin = btm.getAdmin();
			//Gets a new BooksTableModel to reflect the new ResultSet
			btm = new BooksTableModel(rs, outputFilePath);
			//Retrieving a new BooksTableModel resets the user's admin status. So we carry over their
			//status from the previous BooksTableModel instance.
			btm.setAdmin(tempAdmin);
			table.setModel(btm);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	
}
