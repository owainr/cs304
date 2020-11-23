package ca.ubc.cs304.controller;

import ca.ubc.cs304.database.DatabaseConnectionHandler;
import ca.ubc.cs304.delegates.LoginWindowDelegate;
import ca.ubc.cs304.delegates.TerminalTransactionsDelegate;
import ca.ubc.cs304.model.PictureModel;
import ca.ubc.cs304.model.UserModel;
import ca.ubc.cs304.ui.LoginWindow;
import ca.ubc.cs304.ui.TerminalTransactions;

import java.sql.Date;
/**
 * This is the main controller class that will orchestrate everything.
 */
public class Streaming implements LoginWindowDelegate, TerminalTransactionsDelegate {
	private DatabaseConnectionHandler dbHandler = null;
	private LoginWindow loginWindow = null;

	public Streaming() {
		dbHandler = new DatabaseConnectionHandler();
	}
	
	private void start() {
        loginWindow = new LoginWindow();
        loginWindow.showFrame(this);
    }
	
	/**
	 * LoginWindowDelegate Implementation
	 * 
     * connects to Oracle database with supplied username and password
     */ 
	public void login(String username, String password) {
		boolean didConnect = dbHandler.login(username, password);

		if (didConnect) {
			// Once connected, remove login window and start text transaction flow
			loginWindow.dispose();

			TerminalTransactions transaction = new TerminalTransactions();
			transaction.setupDatabase(this);
			transaction.showMainMenu(this);
		} else {
			loginWindow.handleLoginFailed();

			if (loginWindow.hasReachedMaxLoginAttempts()) {
				loginWindow.dispose();
				System.out.println("You have exceeded your number of allowed attempts");
				System.exit(-1);
			}
		}
	}
	
	/**
	 * TermainalTransactionsDelegate Implementation
	 * 
	 * Insert a branch with the given info
	 */
    public void insertPicture(PictureModel model) {
    	dbHandler.insertPicture(model);
    }

    /**
	 * TermainalTransactionsDelegate Implementation
	 * 
	 * Delete branch with given branch ID.
	 */ 
    public void deletePicture(String title, Date releaseDate) {
    	dbHandler.deletePicture(title, releaseDate);
    }
    
    /**
	 * TermainalTransactionsDelegate Implementation
	 * 
	 * Update the branch name for a specific ID
	 */

    public void updatePictureDirector(String title, Date releaseDate, String director) {
    	dbHandler.updatePictureDirector(title, releaseDate, director);
    }

    /**
	 * TermainalTransactionsDelegate Implementation
	 * 
	 * Displays information about varies bank branches.
	 */

    public void selectUsersFavGenreHorror() {
        UserModel[] models = dbHandler.selectUsersFavGenreHorror();

        for (int i = 0; i < models.length; i++) {
            UserModel model = models[i];

            // simplified output formatting; truncation may occur
            System.out.printf("%-10.10s", model.getUsername());
            System.out.printf("%-20.20s", model.getEmail());
            if (model.getFavGenreCategory() == null) {
                System.out.printf("%-20.20s", " ");
            } else {
                System.out.printf("%-20.20s", model.getFavGenreCategory());
            }
            System.out.printf("%-15.15s", model.getWatchlistID());
            if (model.getHistoryID() == 0) {
                System.out.printf("%-15.15s", " ");
            } else {
                System.out.printf("%-15.15s", model.getHistoryID());
            }

            System.out.println();
        }

    }

    public void projectPictureSeriesID() {
        UserModel[] models = dbHandler.projectPictureSeriesID();

        for (int i = 0; i < models.length; i++) {
            UserModel model = models[i];

            // simplified output formatting; truncation may occur
            System.out.printf("%-10.10s", model.getUsername());
            System.out.printf("%-20.20s", model.getEmail());
            if (model.getFavGenreCategory() == null) {
                System.out.printf("%-20.20s", " ");
            } else {
                System.out.printf("%-20.20s", model.getFavGenreCategory());
            }
            System.out.printf("%-15.15s", model.getWatchlistID());
            if (model.getHistoryID() == 0) {
                System.out.printf("%-15.15s", " ");
            } else {
                System.out.printf("%-15.15s", model.getHistoryID());
            }

            System.out.println();
        }
    }


    public void showPicture() {
    	PictureModel[] models = dbHandler.getPictureInfo();
    	
    	for (int i = 0; i < models.length; i++) {
    		PictureModel model = models[i];
    		
    		// simplified output formatting; truncation may occur
    		System.out.printf("%-10.10s", model.getTitle());
    		System.out.printf("%-20.20s", model.getDirector());
    		System.out.printf("%-20.20s", model.getLength());

    		System.out.printf("%-15.15s", model.getReleaseDate());
    		if (model.getSeriesID() == 0) {
    			System.out.printf("%-15.15s", " ");
    		} else {
    			System.out.printf("%-15.15s", model.getSeriesID());
    		}
    		
    		System.out.println();
    	}
    }
	
    /**
	 * TerminalTransactionsDelegate Implementation
	 * 
     * The TerminalTransaction instance tells us that it is done with what it's 
     * doing so we are cleaning up the connection since it's no longer needed.
     */ 
    public void terminalTransactionsFinished() {
    	dbHandler.close();
    	dbHandler = null;
    	
    	System.exit(0);
    }
    
    /**
	 * TerminalTransactionsDelegate Implementation
	 * 
     * The TerminalTransaction instance tells us that the user is fine with dropping any existing table
     * called branch and creating a new one for this project to use
     */ 
	public void databaseSetup() {
		dbHandler.databaseSetup();;
		
	}
    
	/**
	 * Main method called at launch time
	 */
	public static void main(String args[]) {
		Streaming streaming = new Streaming();
		streaming.start();
	}
}
