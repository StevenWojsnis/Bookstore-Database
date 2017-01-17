/**
 * This application is a bookstore database system that allows users to
 * interact with a bookstore-themed database via a GUI.
 * 
 * Note that this application makes use of a MySQL database.
 * 
 * See Readme files for instructions on usage and installation.
 * 
 * CS 370 Software Engineering, Mon-Wed 10:45-12:00
 * Project 1
 * Due Date: 10/26/16
 * 
 * @author Steven Wojsnis
 *
 */
public class Main {
	/**
	 * Initiates the application
	 * @param args
	 */
	public static void main(String[] args){
		
		
		//Calls the appropriate Initialization constructor depending on if the user provided command line arguments
		if(args.length == 2){
			String input = args[0];
			String output = args[1];
			Initialization database = new Initialization(input, output);
		}
		else{
			Initialization database = new Initialization();
		}
	}
}
