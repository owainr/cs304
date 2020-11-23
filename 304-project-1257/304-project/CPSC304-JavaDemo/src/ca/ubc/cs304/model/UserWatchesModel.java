package ca.ubc.cs304.model;

import java.sql.Date;

public class UserWatchesModel {
    private String username;
    private String email;
    private String title;
    private Date releaseDate;

    public UserWatchesModel(String username, String email, String title, Date releaseDate) {
        this.username = username;
        this.email = email;
        this.title = title;
        this.releaseDate = releaseDate;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getTitle() {
        return title;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }
}
