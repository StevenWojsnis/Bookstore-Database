import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * Class containing the methods that query the database upon request from the user via
 * the "Search By" menu in BooksGUI.
 * 
 * For nearly all of the queries the user can select, a custom panel window will open upon
 * selection requesting relevant information for the query. These custom panels will then return
 * information needed to actually query the database, which is done by corresponding methods in this
 * class. These corresponding methods return ResultSets with the requested query that will be later used 
 * to update the JTable to display the requested information.
 * 
 * @author Steven Wojsnis
 *
 */
public class Queries {
	
	private String url, username, password;
	ResultSet oldRs =  null;
	
	/**
	 * Constructor for Queries. Instantiates the information needed to make a connection
	 * to the database, so that queries can be performed.
	 * 
	 * @param nURL : URL needed for connection to Database
	 * @param nUsername : Username needed for connection to Database 
	 * @param nPassword : Password needed for connection to Database
	 * @param oRS : Old ResultSet - used to instantiate ResultSets before they are updated with query
	 */
	public Queries(String nURL, String nUsername, String nPassword, ResultSet oRS){
		//Instantiates the relevant variables
		setURL(nURL);
		setUsername(nUsername);
		setPassword(nPassword);
		
		oldRs = oRS;
	}
	
	/**
	 * Requests an ISBN that the user would like to search for from the user, and returns it.
	 * @return The ISBN that the user wishes to search for
	 */
	public String isbnQueryPanel(){
		String isbn = JOptionPane.showInputDialog(null, "ISBN of book to be searched for");
		return isbn;
	}
	
	/**
	 * Takes a String parameter, and searches for all books in the Database whose ISBNs match that
	 * String parameter.
	 * 
	 * Returns the ResultSet containing only books with the specified ISBN
	 * @param isbn : ISBN to be searched for
	 * @return : ResultSet with only the books with matching ISBNs
	 */
	public ResultSet searchForDesiredISBN(String isbn){
		
		ResultSet rs = oldRs;
		try{
			Connection conn = DriverManager.getConnection(url, username, password);
			Statement statement = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			//Searches for books with the given ISBN
			rs = statement.executeQuery("SELECT * FROM BOOKSTORE.BOOKS WHERE ISBN = '" + isbn+"'");
			return rs;
		} catch (NullPointerException e){
			System.out.println("URL, Username, and Password must first be initialized");
		} catch (SQLException e1){
			e1.printStackTrace();
		}
		return rs;
		
	}
	
	/**
	 * Requests a book title that the user would like to search for from the user.
	 * 
	 * @return The Title that the user wishes to search for
	 */
	public String titleQueryPanel(){
		String title = JOptionPane.showInputDialog(null, "Title of the book to be searched for");
		return title;
	}
	
	/**
	 * Takes a String parameter and searches all the books in the Database whose title matches that
	 * String parameter
	 * 
	 * @param title : The title of the book that will be searched for
	 * @return : The ResultSet containing books with the title the user queried for.
	 */
	public ResultSet searchForDesiredTitle(String title){
		
		ResultSet rs = oldRs;
		try{
			Connection conn = DriverManager.getConnection(url, username, password);
			Statement statement = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			//Queries the database for books whose name matches that which the user is searching for.
			rs = statement.executeQuery("SELECT * FROM BOOKSTORE.BOOKS WHERE BOOK_NAME = '" + title+"'");
			return rs;
		} catch (NullPointerException e){
			System.out.println("URL, Username, and Password must first be initialized");
		} catch (SQLException e1){
			e1.printStackTrace();
		}
		return rs;
		
	}
	
	/**
	 * Requests an author of a book from the user that the user would like to search for
	 * 
	 * @return : The author of a title that is to be searched for
	 */
	public String authorQueryPanel(){
		String author = JOptionPane.showInputDialog(null, "Author of the book to be searched for");
		return author;
	}
	
	/**
	 * Searches for books that have the same AUTHOR value as the String parameter "author"
	 * Returns the results of the search in a ResultSet.
	 * 
	 * @param author : The author to be searched for
	 * @return A ResultSet containing all the books that have matching author values with the author parameter
	 */
	public ResultSet searchForDesiredAuthor(String author){
		
		ResultSet rs = oldRs;
		try{
			Connection conn = DriverManager.getConnection(url, username, password);
			Statement statement = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			//Queries the database for books whose author matches that which the user is searching for.
			rs = statement.executeQuery("SELECT * FROM BOOKSTORE.BOOKS WHERE AUTHOR = '" + author+"'");
			return rs;
		} catch (NullPointerException e){
			System.out.println("URL, Username, and Password must first be initialized");
		} catch (SQLException e1){
			e1.printStackTrace();
		}
		return rs;
		
	}
	
	/**
	 * Displays a custom panel in a JOptionPane that allows users to enter a year, and whether
	 * they want to find books published before, during, or after that year.
	 * 
	 * A custom panel is built with three radio buttons, each representing one of the available
	 * options (before, during, or after), that are part of a button group, so only one can be
	 * selected at a time.
	 * 
	 * @return A String Array that contains an indicator for which option the user chose, as well as
	 * the year that they entered.
	 */
	public String[] yearQueryPanel(){
		
		//Creates a custom panel with three radio buttons, and a label containing instructions to the user
		JPanel panel = new JPanel();
		JRadioButton beforeButton = new JRadioButton("Before", false);
		//Note: During is selected as default choice
		JRadioButton duringButton = new JRadioButton("During", true);
		JRadioButton afterButton = new JRadioButton("After", false);
		JLabel instructionsLabel = new JLabel("Type a desired year, and then select books published (inclusive) before, during, or (inclusive) after that year.");
		
		//Button group with the three buttons, such that only one can be selected at a time
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(beforeButton);
		buttonGroup.add(duringButton);
		buttonGroup.add(afterButton);
		
		panel.add(instructionsLabel);
		panel.add(beforeButton);
		panel.add(duringButton);
		panel.add(afterButton);
		
		//Stores the entered number of years in the first slot of the array
		String[] yearSpecs = new String[2];
		yearSpecs[0] = JOptionPane.showInputDialog(null,panel);
		
		//A string value is used as an indication of which button the user selected
		if(beforeButton.isSelected())
			yearSpecs[1] = "before";
		else if(duringButton.isSelected())
			yearSpecs[1] = "during";
		else if(afterButton.isSelected())
			yearSpecs[1] = "after";
		
		return yearSpecs;
	}
	/**
	 * Receives a String Array as input. This array should contain a year value in the first spot,
	 * and an indication of a button selection in the second spot.
	 * 
	 * This method should be called right after its corresponding Panel method, yearQueryPanel, and as
	 * such, its input should come from its panel method.
	 * 
	 * Depending on whether "before" "during" or "after" is in the array, the method will search for
	 * books published before, on, or after the given year.
	 * 
	 * It should be noted that both "before" and "after" are inclusive, and so they will also search
	 * for books published ON the given year.
	 * 
	 * @param yearSpecs : Array containing information needed to perform the query
	 * @return The ResultSet containing the results of the query
	 */
	public ResultSet searchForDesiredYear(String[] yearSpecs){
		
		ResultSet rs = oldRs;
		try{
			Connection conn = DriverManager.getConnection(url, username, password);
			Statement statement = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			
			//Depending on the value of yearSpecs[1], books are searched for depending on if they were
			//published before, during, or after the given year.
			//Note that before and after searches are inclusive of the given year.
			if(yearSpecs[1].equalsIgnoreCase("before"))
				rs = statement.executeQuery("SELECT * FROM BOOKSTORE.BOOKS WHERE YEAR_PUBLISHED <= '" + yearSpecs[0]+"'");
			else if(yearSpecs[1].equalsIgnoreCase("during"))
				rs = statement.executeQuery("SELECT * FROM BOOKSTORE.BOOKS WHERE YEAR_PUBLISHED = '" + yearSpecs[0]+"'");
			else if(yearSpecs[1].equalsIgnoreCase("after"))
				rs = statement.executeQuery("SELECT * FROM BOOKSTORE.BOOKS WHERE YEAR_PUBLISHED >= '" + yearSpecs[0]+"'");
			
			return rs;
		} catch (NullPointerException e){
			System.out.println("URL, Username, and Password must first be initialized");
		} catch (SQLException e1){
			e1.printStackTrace();
		}
		return rs;
		
	}
	
	/**
	 * Displays a custom panel that asks a user to enter an amount of pages, and select a radio button
	 * indicating whether they want to search for books with less or more pages than the entered amount.
	 * 
	 * Both the number of pages, and an indicator of which button the user chose are stored in an array.
	 * 
	 * It should be noted that regardless of the RadioButton chosen, any book that has the exact number
	 * of pages as the entered value will be displayed.
	 * 
	 * @return A String array containing information needed to perform the relevant query.
	 */
	public String[] pageQueryPanel(){
		
		//Adds a panel containing the relevant buttons and instructions
		JPanel panel = new JPanel();
		//Less than is the default selection
		JRadioButton lessThanButton = new JRadioButton("Less Than", true);
		JRadioButton greaterThanButton = new JRadioButton("Greater Than", false);
		JLabel instructionsLabel = new JLabel("Type a desired page amount, and then choose (inclusive) less than or (inclusive) greater than.");
		
		//Adds the buttons to a buttonGroup, such that only one can be selected.
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(lessThanButton);
		buttonGroup.add(greaterThanButton);
		
		panel.add(instructionsLabel);
		panel.add(lessThanButton);
		panel.add(greaterThanButton);
		
		//Stores the value of the pages in the first slot of the array
		String[] pageSpecs = new String[2];
		pageSpecs[0] = JOptionPane.showInputDialog(null,panel);
		
		//Stores the indicator of which button the user selected in the second slot of the array
		if(lessThanButton.isSelected())
			pageSpecs[1] = "lesser";
		else if(greaterThanButton.isSelected())
			pageSpecs[1] = "greater";
		
		return pageSpecs;
	}
	
	/**
	 * Takes a String Array as input that should contain information about the number of pages a user
	 * wishes to search for, and whether or not they want to search for books with a lesser or greater
	 * amount of pages relative to the entered amount.
	 * 
	 * It should be noted that regardless of the RadioButton chosen, any book that has the exact number
	 * of pages as the entered value will be displayed.
	 * 
	 * @param pageSpecs : String Array that contains information needed to perform the query
	 * @return : A ResultSet containing the results of the query
	 */
	public ResultSet searchForDesiredPage(String[] pageSpecs){
	
		ResultSet rs = oldRs;
		try{
			Connection conn = DriverManager.getConnection(url, username, password);
			Statement statement = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
			
			//Depending on whether the second slot of the pageSpecs array, books with less pages or more pages
			//than the given page number will be searched for.
			if(pageSpecs[1].equalsIgnoreCase("lesser"))
				rs = statement.executeQuery("SELECT * FROM BOOKSTORE.BOOKS WHERE PAGES <= '" + pageSpecs[0]+"'");
			else if(pageSpecs[1].equalsIgnoreCase("greater"))
				rs = statement.executeQuery("SELECT * FROM BOOKSTORE.BOOKS WHERE PAGES >= '" + pageSpecs[0]+"'");
		
			return rs;
		} catch (NullPointerException e){
			System.out.println("URL, Username, and Password must first be initialized");
		} catch (SQLException e1){
			e1.printStackTrace();
		}
		return rs;
		
	}

	/**
	 * Presents users with a choice of whether they want to search for books that are currently in
	 * stock or out of stock.
	 * 
	 * It should be noted that this method has no corresponding panel or query method, like most other
	 * methods in this class. Instead, this method is solely a query method, as the input doesn't
	 * require as much processing as the other queries.
	 * 
	 * Depending on which menu option the user selected in BooksGUI, a string will be passed into this
	 * method, detailing what the user wishes to search for
	 * 
	 * @param stockChoice : Indicates whether the user wishes to find books that are currently in or out of stock
	 * @return : A ResultSet that contains the results of the query
	 */
	public ResultSet searchByStock(String stockChoice){
		
		ResultSet rs = oldRs;
		try{
			Connection conn = DriverManager.getConnection(url, username, password);
			Statement statement = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		
			//Depending on the input string, either books that have a current stock greater than 0, or a
			//current stock equal to 0 will be searched for.
			if(stockChoice.equalsIgnoreCase("in"))
				rs = statement.executeQuery("SELECT * FROM BOOKSTORE.BOOKS WHERE CUR_STOCK > 0");
			else if(stockChoice.equalsIgnoreCase("out"))
				rs = statement.executeQuery("SELECT * FROM BOOKSTORE.BOOKS WHERE CUR_STOCK = 0");
		
			return rs;
		} catch (NullPointerException e){
			System.out.println("URL, Username, and Password must first be initialized");
		} catch (SQLException e1){
			e1.printStackTrace();
		}
		return rs;
		
	}
	
	/**
	 * Displays a custom panel that asks the user to enter a price, and whether or not they want to search
	 * for books that have a lesser or greater price than entered.
	 * 
	 * It should be noted that regardless of the button choice, if a book has the exact same price as
	 * provided, then it will be displayed.
	 * 
	 * @return : A String Array containing information necessary for the query.
	 */
	public String[] priceQueryPanel(){
		
		//Creates a new panel with two buttons indicating lesser/greater, and a label containing
		//instructions for the user. Note that lesser is the defaut choice.
		JPanel panel = new JPanel();
		JRadioButton lessThanButton = new JRadioButton("Less Than", true);
		JRadioButton greaterThanButton = new JRadioButton("Greater Than", false);
		JLabel instructionsLabel = new JLabel("Type a desired price, and then choose (inclusive) less than or (inclusive) greater than.");
		
		//Adds the buttons to a buttonGroup, such that only one can be chosen at a time.
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(lessThanButton);
		buttonGroup.add(greaterThanButton);
		
		panel.add(instructionsLabel);
		panel.add(lessThanButton);
		panel.add(greaterThanButton);
		
		//Stores the entered price in priceSpecs[0]
		String[] priceSpecs = new String[2];
		priceSpecs[0] = JOptionPane.showInputDialog(null,panel);
		
		//Depending on the selected button, stores "lesser" or "greater" in priceSpecs[1]
		if(lessThanButton.isSelected())
			priceSpecs[1] = "lesser";
		else if(greaterThanButton.isSelected())
			priceSpecs[1] = "greater";
		
		return priceSpecs;
	}
	
	/**
	 * Receives a String Array as input and uses it to determine an entered price amount, and whether
	 * a book with a lesser or greater price should be searched for.
	 * 
	 * This method then proceeds to search for books that fit the given criteria.
	 * 
	 * It should be noted that regardless of the button choice, if a book has the exact same price as
	 * provided, then it will be displayed.
	 * 
	 * @param priceSpecs : String Array containing information needed to perform the query
	 * @return : ResultSet containing the results of the query
	 */
	public ResultSet searchForDesiredPrice(String[] priceSpecs){
	
		ResultSet rs = oldRs;
		try{
			Connection conn = DriverManager.getConnection(url, username, password);
			Statement statement = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
			
			//Depending on the value of priceSpecs[1], will search for books with a lesser or greater price
			//than that indicated in priceSpecs[0]. It should be noted that lesser and greater are inclusive,
			//and thus include books whose price exactly match that in priceSpecs[0].
			if(priceSpecs[1].equalsIgnoreCase("lesser"))
				rs = statement.executeQuery("SELECT * FROM BOOKSTORE.BOOKS WHERE PRICE <= '" + priceSpecs[0]+"'");
			else if(priceSpecs[1].equalsIgnoreCase("greater"))
				rs = statement.executeQuery("SELECT * FROM BOOKSTORE.BOOKS WHERE PRICE >= '" + priceSpecs[0]+"'");
		
			return rs;
		} catch (NullPointerException e){
			System.out.println("URL, Username, and Password must first be initialized");
		} catch (SQLException e1){
			e1.printStackTrace();
		}
		return rs;
		
	}
	
	// SETTERS
	public void setURL(String nURL){
		url = nURL;
	}
	
	public void setUsername(String nUsername){
		username = nUsername;
	}
	
	public void setPassword(String nPassword){
		password = nPassword;
	}
}
