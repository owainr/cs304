package ca.ubc.cs304.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;

import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.model.PictureModel;

/**
 * The class is only responsible for handling terminal text inputs. 
 */
public class TerminalTransactions {
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";
	private static final int INVALID_INPUT = Integer.MIN_VALUE;
	private static final int EMPTY_INPUT = 0;
	private static final Date INVALID_DATE = null;
	private static final String INVALID_STRING = "";
	
	private BufferedReader bufferedReader = null;
	private TerminalTransactionsDelegate delegate = null;

	public TerminalTransactions() {
	}
	
	/**
	 * Sets up the database to have a branch table with two tuples so we can insert/update/delete from it.
	 * Refer to the databaseSetup.sql file to determine what tuples are going to be in the table.
	 */
	public void setupDatabase(TerminalTransactionsDelegate delegate) {
		this.delegate = delegate;
		
		bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		int choice = INVALID_INPUT;
		
		while(choice != 1 && choice != 2) {
			System.out.println("If you have a table called Branch in your database (capitialization of the name does not matter), it will be dropped and a new Branch table will be created.\nIf you want to proceed, enter 1; if you want to quit, enter 2.");
			
			choice = readInteger(false);
			
			if (choice != INVALID_INPUT) {
				switch (choice) {
				case 1:  
					delegate.databaseSetup(); 
					break;
				case 2:  
					handleQuitOption();
					break;
				default:
					System.out.println(WARNING_TAG + " The number that you entered was not a valid option.\n");
					break;
				}
			}
		}
	}

	/**
	 * Displays simple text interface
	 */ 
	public void showMainMenu(TerminalTransactionsDelegate delegate) {
		this.delegate = delegate;
		
	    bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		int choice = INVALID_INPUT;
		
		while (choice != 5) {
			System.out.println();
			System.out.println("1. Insert branch");
			System.out.println("2. Delete branch");
			System.out.println("3. Update branch name");
			System.out.println("4. Show branch");
			System.out.println("5. Quit");
			System.out.print("Please choose one of the above 5 options: ");

			choice = readInteger(false);

			System.out.println(" ");

			if (choice != INVALID_INPUT) {
				switch (choice) {
				case 1:  
					handleInsertOption(); 
					break;
				case 2:  
					handleDeleteOption(); 
					break;
				case 3: 
					handleUpdateOption();
					break;
				case 4:  
					delegate.showPicture();
					break;
				case 5:
					handleQuitOption();
					break;
				default:
					System.out.println(WARNING_TAG + " The number that you entered was not a valid option.");
					break;
				}
			}
		}		
	}
	
	private void handleDeleteOption() {
		String branchId = INVALID_STRING;
        Date date = INVALID_DATE;
		while (branchId.equals(INVALID_STRING)) {
			System.out.print("Please enter the branch ID you wish to delete: ");
			branchId = readLine();
			if (!branchId.equals(INVALID_STRING)) {
			    while (date == INVALID_DATE) {
                    System.out.println("Please enter year");
                    int year = readInteger(false);
                    System.out.println("Please enter Month");
                    int month = readInteger(false);
                    System.out.println("Please enter day");
                    int day = readInteger(false);
                    date = new Date(year,month,day);

                    delegate.deletePicture(branchId, date);
                }

			}
		}
	}
	
	private void handleInsertOption() {
        String title = null;
        while (title == null || title.length() <= 0) {
            System.out.print("Please enter the branch title you wish to insert: ");
            title = readLine().trim();
        }
		
		Date date = null;
		while (date == null) {
            System.out.println("Please enter year");
            int year = readInteger(false);
            System.out.println("Please enter Month");
            int month = readInteger(false);
            System.out.println("Please enter day");
            int day = readInteger(false);
            date = new Date(year,month,day);
		}
		
		// branch address is allowed to be null so we don't need to repeatedly ask for the address
		int length = INVALID_INPUT;
        while (length == INVALID_INPUT) {
            System.out.print("Please enter the branch length you wish to insert: ");
            length = readInteger(true);
        }
		
		String director = null;
        while (director == null || director.length() <= 0) {
            System.out.print("Please enter the branch director you wish to insert: ");
            director = readLine().trim();
        }
		
		int seriesID = INVALID_INPUT;
		while (seriesID == INVALID_INPUT) {
			System.out.print("Please enter the branch seriesid you wish to insert: ");
			seriesID = readInteger(true);
		}

        String genre = null;
        while (genre == null || genre.length() <= 0) {
            System.out.print("Please enter the genre you wish to insert: ");
            genre = readLine().trim();
        }
		
		PictureModel model = new PictureModel(title,
											date,
											length,
											director,
											seriesID, genre);
		delegate.insertPicture(model);
	}
	
	private void handleQuitOption() {
		System.out.println("Good Bye!");
		
		if (bufferedReader != null) {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				System.out.println("IOException!");
			}
		}
		
		delegate.terminalTransactionsFinished();
	}
	
	private void handleUpdateOption() {

        String title = null;
        while (title == null || title.length() <= 0) {
            System.out.print("Please enter the branch title you wish to update: ");
            title = readLine().trim();
        }

		Date date = INVALID_DATE;
		while (date == INVALID_DATE) {
            System.out.println("Please enter year");
            int year = readInteger(false);
            System.out.println("Please enter Month");
            int month = readInteger(false);
            System.out.println("Please enter day");
            int day = readInteger(false);
            date = new Date(year,month,day);
		}

        String director = null;
        while (director == null || director.length() <= 0) {
            System.out.print("Please enter the branch director you wish to update: ");
            director = readLine().trim();
        }
		


		delegate.updatePictureDirector(title, date, director);
	}
	
	private int readInteger(boolean allowEmpty) {
		String line = null;
		int input = INVALID_INPUT;
		try {
			line = bufferedReader.readLine();
			input = Integer.parseInt(line);
		} catch (IOException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		} catch (NumberFormatException e) {
			if (allowEmpty && line.length() == 0) {
				input = EMPTY_INPUT;
			} else {
				System.out.println(WARNING_TAG + " Your input was not an integer");
			}
		}
		return input;
	}
	
	private String readLine() {
		String result = null;
		try {
			result = bufferedReader.readLine();
		} catch (IOException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
		return result;
	}
}
