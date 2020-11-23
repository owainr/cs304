package ca.ubc.cs304.model;

public class UserModel {
    private String username;
    private String email;
    private String favGenreCategory;
    private int watchlistID;
    private int historyID;

    public UserModel(String username, String email, String favGenreCategory, int watchlistID, int historyID) {
        this.username = username;
        this.email = email;
        this.favGenreCategory = favGenreCategory;
        this.watchlistID = watchlistID;
        this.historyID = historyID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFavGenreCategory() {
        return favGenreCategory;
    }

    public int getHistoryID() {
        return historyID;
    }

    public int getWatchlistID() {
        return watchlistID;
    }
}
