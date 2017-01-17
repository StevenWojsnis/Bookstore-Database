import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

/**
 * Class that contains the Add Row and Delete row options of the "Utilities" options 
 * that can be selected by the user via the "Utilities" menu in the BooksGUI class.
 * 
 * All methods in this class will return a ResultSet, as the goal of this class is to
 * designate an area where BooksGUI can pull a new ResultSet from given a requested command,
 * and then use it to make appropriate changes to the JTable.
 * 
 * @author Steven Wojsnis
 *
 */
public class Utilities {
	
	private Path outputFilePath;
	
	/**
	 * Contructor for Utilities. 
	 * Instantiates outputFilePath, which will be written to with details of any changes made to the data
	 * @param outputFile
	 */
	public Utilities(Path outputFile){
		outputFilePath = outputFile;
	}
	
	 /**
	  * Method that obtains various new book details via the User to be inserted into the database
	  * via the addRow method (which is called at the end of the this method).
	  * 
	  * @param rs : The original ResultSet, that doesn't yet contain the added row
	  * @return The new ResultSet that will contain the added row
	  */
	 public ResultSet retrieveNewRowItems(ResultSet rs){
		 try{
			 //Requests the required information about the soon-to-be inserted book via user input.
			 Object isbn = JOptionPane.showInputDialog(null, "Please enter an ISBN");
			 Object bookName = JOptionPane.showInputDialog(null, "Please enter a book name");
			 Object yearPub = JOptionPane.showInputDialog(null, "Please enter the year in which the book was published");
			 Object author = JOptionPane.showInputDialog(null, "Please enter the author's name");
			 Object pages = JOptionPane.showInputDialog(null, "Please enter the number of pages in the book");
			 Object stock = JOptionPane.showInputDialog(null, "Please enter the amount currently in stock");
			 Object price = JOptionPane.showInputDialog(null, "Please enter the price");
			 Object amtSold = JOptionPane.showInputDialog(null, "Please enter the quantity sold of this book");
		
			 //Due to the specific format required for text, a regex pattern is used to ensure the user entered
			 //a correct format. If an incorrect format is used, the user is reminded of the correct format to use,
			 //and a date is requested again. A new date is continuously requested until the correct format is used.
			 String pattern = "\\d{4}[/]\\d{2}[/]\\d{2}";
			 Object lastShip = JOptionPane.showInputDialog(null, "Please enter the date of the last received shipment of this book. Note: Please use the format: YYYY/MM/DD");
			 while(!lastShip.toString().matches(pattern))
				 lastShip = JOptionPane.showInputDialog(null, "Incorrect format used. Please enter the date of the last received shipment of this book. Note: Please use the format: YYYY/MM/DD");
		
			 Object[] items = {isbn, bookName, yearPub, author, pages, stock, 
					 price, amtSold, lastShip};
		
			 //Calls addRow to actually add the new row with the recently obtained items to the ResultSet
			 return addRow(rs, items);
		 }catch(NullPointerException e){
				JOptionPane.showMessageDialog(null,
						"Unable to add new row, user didn't input value for each column.",
					    "Error During Add Row.",
					    JOptionPane.ERROR_MESSAGE);
		 }
		 return rs;
		
	}
	
	 /**
	  * Adds a new row to a ResultSet containing information from an Objects Array as well as the Database
	  * 
	  * @param rs : The ResultSet to have a row added to
	  * @param items : The array containing the details of the row to be added
	  * @return The ResultSet containing the added row.
	  */
	public ResultSet addRow(ResultSet rs, Object[] items){
		
		try{
			//Moves to an insertRow, fills data into said insertRow, and the inserts the insertRow
			//into the ResultSet and Database
			rs.moveToInsertRow();
			rs.updateObject("ISBN", items[0]);
			rs.updateObject("BOOK_NAME", items[1]);
			rs.updateObject("YEAR_PUBLISHED", items[2]);
			rs.updateObject("AUTHOR", items[3]);
			rs.updateObject("PAGES", items[4]);
			rs.updateObject("CUR_STOCK", items[5]);
			rs.updateObject("PRICE", items[6]);
			rs.updateObject("AMOUNT_SOLD", items[7]);
			rs.updateObject("LAST_SHIP_RECEIVED", items[8]);
			rs.insertRow();
			rs.moveToCurrentRow();
			
			//Writes the details of the change made to the Database to the designated output file.
			try (BufferedWriter writer = new BufferedWriter( new FileWriter(outputFilePath.toString(), true))) {
			    writer.write("ADD ROW - New row was added with the following values: ISBN: "
			    		+ items[0] + ", BOOK_NAME: " + items[1] + ", YEAR_PUBLISHED: " + items[2] +
			    		", AUTHOR: " + items[3] + ", PAGES: " + items[4] + ", CUR_STOCK: " + items[5] +
			    		", PRICE: " + items[6] + ", AMOUNT_SOLD: " + items[7] + ", LAST_SHIP_RECEIVED: "
			    		+ items[8]); 
			    writer.write(System.getProperty( "line.separator" ));
			} catch (IOException x) {
				x.printStackTrace();
			} 
			
			return rs;
			
		}catch(SQLException e){
			JOptionPane.showMessageDialog(null,
					"Unable to add new row, incorrect input value(s)",
				    "Error During Add Row.",
				    JOptionPane.ERROR_MESSAGE);
		}catch(NullPointerException e){
			JOptionPane.showMessageDialog(null,
					"Unable to add new row, user didn't input value for each column.",
				    "Error During Add Row.",
				    JOptionPane.ERROR_MESSAGE);
		}
		
		return rs;
	}
	
	/**
	 * Deletes a row from the ResultSet given a primary key (ISBN), as well as the Database.
	 * 
	 * 
	 * @param rs : ResultSet that will have row deleted from
	 * @param isbn : primary key used to identify the row to be deleted
	 * @return The ResultSet that will no longer contain the deleted row.
	 */
	public ResultSet deleteRow(ResultSet rs, String isbn){
		try{
			
			if(isbn == null)
				return rs;
			//Ensures that the ResultSet is not empty
			else if(rs.first()){
				
				//Finds the row with the given ISBN
				while(!rs.getString("ISBN").equals(isbn)){
					if(!rs.next()){
						 
						//If the row isn't found, an error message is reported to the user, informing
						//them that the row doesn't exist in the table.
						JOptionPane.showMessageDialog(null,
								"Book with ISBN: "+isbn+" doesn't exist in table.",
								"Not in Table",
								JOptionPane.ERROR_MESSAGE);
					
						return rs;
					}
				}
				
				//Takes pertinent information to be used in the documentation of the change
				String deletedRowISBN = rs.getString(1);
				String deletedRowTitle = rs.getString(2);
				String deletedRowAuthor = rs.getString(4);
				//removes the row from the ResultSet and database
				rs.deleteRow();
				
				//Writes the changes made to the Database to the designated Output File, with relevant information
				//such as the book's ISBN, Title, and Author.
				try (BufferedWriter writer = new BufferedWriter( new FileWriter(outputFilePath.toString(), true))) {
				    writer.write("DELETED ROW - Book with ISBN: " + deletedRowISBN + 
				    		" and Title: " + deletedRowTitle + " by: " + deletedRowAuthor +
				    		" was removed from the table."); 
				    writer.write(System.getProperty( "line.separator" ));
				} catch (IOException x) {
				    x.printStackTrace();;
				}
				return rs;
			}
			//Informs the user that the table was empty
			else{
				JOptionPane.showMessageDialog(null,
						"Table currently empty.",
						"Empty Table",
						JOptionPane.ERROR_MESSAGE);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return rs;
	}
	
}
