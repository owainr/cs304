package ca.ubc.cs304.delegates;

import ca.ubc.cs304.model.GenreAvgLengthModel;
import ca.ubc.cs304.model.GenreCountModel;
import ca.ubc.cs304.model.PictureModel;
import ca.ubc.cs304.model.UserModel;

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
    public PictureModel[] showPicture();
    public UserModel[] showUser();
    public void updatePictureDirector(String title, Date releaseDate, String director);
    public UserModel[] selectUsersFavGenre(String favGenre);
    public String[] projectPictureSeriesID();
    public UserModel[] allUsersThatWatchedAPicture(String title, Date releaseDate);
    public GenreAvgLengthModel[] avgLengthByGenre();
    public GenreCountModel[] numPicturesByGenreOver(int cutoff);
    public UserModel[] usersThatWatchedAllPictures();
    public String[] genreWithLowestAvgLength();

    public void terminalTransactionsFinished();
}
