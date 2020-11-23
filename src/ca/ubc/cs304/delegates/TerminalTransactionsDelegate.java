package ca.ubc.cs304.delegates;

import ca.ubc.cs304.model.PictureModel;

import java.sql.Date;


/**
 * This interface uses the delegation design pattern where instead of having
 * the TerminalTransactions class try to do everything, it will only
 * focus on handling the UI. The actual logic/operation will be delegated to the 
 * controller class (in this case Bank).
 * 
 * TerminalTransactions calls the methods that we have listed below but 
 * Bank is the actual class that will implement the methods.
 */
public interface TerminalTransactionsDelegate {
	public void databaseSetup();
	
	public void deletePicture(String title, Date releaseDate);
	public void insertPicture(PictureModel model);
	public void showPicture();
	public void updatePictureDirector(String title, Date releaseDate, String director);
    public void selectUsersFavGenreHorror();
    public void projectPictureSeriesID();
	
	public void terminalTransactionsFinished();
}
