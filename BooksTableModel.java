import javax.sql.rowset.CachedRowSet;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

/**
 * Class that extends AbstractTableModel, and contains methods that deal with data that
 * are required by JTable.
 * 
 * @author Steven Wojsnis
 *
 */
public class BooksTableModel extends AbstractTableModel {
	
	Path outputFilePath;
	private ResultSet booksResultSet;
	private ResultSetMetaData metadata;
	int numCols, numRows;
	private boolean admin;
	
	/**
	 * Constructor for BooksTableModel.
	 * 
	 * Determines how many rows are in the resultSet argument, and also adds a Listener to this
	 * TableModel.
	 * 
	 * @param resultSet : the resultSet containing the data from the table
	 * @param outputPath : outputFile, passed into constructor for instantiation of instance variable
	 * @throws SQLException
	 */
	public BooksTableModel(ResultSet resultSet, Path outputPath) throws SQLException {
		
		// Instantiates variables
		outputFilePath = outputPath;
		booksResultSet = resultSet;
		metadata = booksResultSet.getMetaData();
		numCols = metadata.getColumnCount();
		
		// Counts the rows in the result set
		booksResultSet.beforeFirst();
		numRows = 0;
		while(booksResultSet.next()){
			numRows++;
		}
		booksResultSet.beforeFirst();
		
		// Adds TableModel Listener to this TableModel to detect when the user attempts to make a change on the table
		this.addTableModelListener(new TableModelListener(){

			public void tableChanged(TableModelEvent e) {	
				
			}
		});
	}
	
	/**
	 * Override of the AbstractTableModel class, returns String.class for all columns
	 * for the sake of simplicity. (Basically says that each column contains a string).
	 */
	@Override
	public Class<?> getColumnClass(int col) {
		//We say each column consists of String objects for simplicity
		return String.class; 
	}
	
	/**
	 * Returns the number of columns in the ResultSet
	 */
	@Override
	public int getColumnCount() {
		return numCols;
	}
	
	/**
	 * Returns the name of a column based on the desired column number
	 */
	@Override
	public String getColumnName(int col) {
		try {
			  // Note: have to use "col + 1" because JTable starts 
		      return metadata.getColumnLabel(col + 1);
		    } catch (SQLException e) {
		    	return e.toString();
		    }
	}
	
	/**
	 * Returns the number of rows, previously counted in the constructor
	 */
	@Override
	public int getRowCount() {
		return numRows;
	}
	
	/**
	 * Returns whatever object is in the given row/column coordinate
	 */
	@Override
	public Object getValueAt(int row, int col) {
		try {
			//Moves the cursor to the appropriate row in the ResultSet
			booksResultSet.absolute(row+1);
			//Retrieves item from the appropriate cell in the ResultSet
			Object cellItem = booksResultSet.getObject(col+1);
			
			if (cellItem == null)
				return null;
			else
				//Again, we convert our cell items to String for simplicity
				return cellItem.toString();
		} catch(SQLException e){
			return e.toString();
		}
	}
	
	/**
	 * Method that returns a boolean corresponding to whether a given cell at the provided row and column is
	 * editable. This overridden version of the method tailored to this specific program allows only some values
	 * to be editable. Namely, all cells are editable unless they are contained in a column with the name "ISBN",
	 * "BOOK_NAME", "YEAR_PUBLISHED", "AUTHOR", or "PAGES". Reasoning for this has to do with the nature of a bookstore.
	 * In other words, some characteristics of a book should never change - those characteristics are uneditable.
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		
		String colName = getColumnName(col);
		
		// Checks the column name of the cell, and determines if it can be edited or not.
		if(colName.equals("ISBN")
				|| colName.equals("BOOK_NAME")
				|| colName.equals("YEAR_PUBLISHED")
				|| colName.equals("AUTHOR")
				|| colName.equals("PAGES")
		)
			return false;
		else
			return true;
	}
	
	/**
	 * Method is called from the TableModelListener. When someone attempts to edit a cell in the table, and it is determined
	 * to be editable, the user will enter a value, press enter, and this method will be called.
	 * 
	 * It takes an attribute of type Object, which is the new value which the user wishes to insert into the modified cell.
	 * The method then takes that attribute and inserts it into the cell, at row "row" and column "col".
	 * 
	 * Also writes to the output file with information regarding the modification.
	 * 
	 * Contains some measures to prevent invalid input, such as an invalid date format, by notifying the user that an
	 * incorrect format was used, and not allowing the change.
	 * 
	 * Finally, disallows any changes if the User is not currently "signed in". Also notifies the user that they must sign in.
	 */
	@Override
	public void setValueAt(Object attribute, int row, int col) {
		
		try{
			//If it's determined that the user has signed in with legitimate credentials
			if(getAdmin()){
				
				//Moves the cursor to the appropriate row in the ResultSet
				booksResultSet.absolute(row+1);
				
				//Wrties to the output file details about the newly updated cell and what modification took place.
				try (BufferedWriter writer = new BufferedWriter( new FileWriter(outputFilePath.toString(), true))) {
				    writer.write("MODIFY - value at row: "+row+" (starting from zero)"+
				    		 " under column: "+getColumnName(col)+": "+booksResultSet.getString(col+1)+
				    		 " was changed to: "+attribute.toString()+"."); 
				    writer.write(System.getProperty( "line.separator" ));
				} catch (IOException x) {
				    x.printStackTrace();;
				}
				
				//Updates the cell
				booksResultSet.updateObject(getColumnName(col), attribute);
				booksResultSet.updateRow();
			
				fireTableCellUpdated(row+1, col+1);
			}
			//Notifies the user that they can't make any changes unless they're signed in.
			else
				JOptionPane.showMessageDialog(null,
					    "Only admins can modify data. Please log in (found under 'Utilities' tab).",
					    "Not Admin",
					    JOptionPane.ERROR_MESSAGE);
		
			//Various Catches to prevent incorrect formats depending on the cell in which data is being entered. Notifies the user of the error.
		} catch (DataTruncation dt){
			
			if(getColumnName(col).equals("LAST_SHIP_RECEIVED")){
				
				JOptionPane.showMessageDialog(null,
					    "Incorrect format. NOTE: Date format is: YYYY/MM/DD",
					    "Input error",
					    JOptionPane.ERROR_MESSAGE);
				
			} else {
				JOptionPane.showMessageDialog(null,
					    "Incorrect format.",
					    "Input error",
					    JOptionPane.ERROR_MESSAGE);
			}
			
			// Also catches SQL errors, in case there was some kind of error in the update that was caused by the input
		} catch (SQLException e){
			
			JOptionPane.showMessageDialog(null,
				    "Incorrect format.",
				    "Input error",
				    JOptionPane.ERROR_MESSAGE);
		} 
	}
	
	/**
	 * Method that asks a user to enter a password. If the user enters the correct pass, "root", then
	 * the setAdmin flag is set to true. If the user entered the wrong password, the setAdmin flag is set to false.
	 * 
	 * The setAdmin flag is used to determine the permissions of the user, and bars the user from making certain changes
	 * depending on their access level.
	 */
	public void password(){
		
		//Asks the user to enter a password
		String enteredPass = JOptionPane.showInputDialog(null, "Enter the Administrative password.");
		//Determines if they entered the correct password
		if(enteredPass.equalsIgnoreCase("root"))
			setAdmin(true);
		else
			setAdmin(false);
	}
	
	// GETTERS AND SETTERS
	public boolean getAdmin(){
		return admin;
	}
	public void setAdmin(boolean a){
		admin = a;
	}
}
