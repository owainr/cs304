package ca.ubc.cs304.model;

public class GenreCountModel {
    private String genre;
    private int count;

    public GenreCountModel(String genre, int count) {
        this.genre = genre;
        this.count = count;
    }

    public String getGenre() {
        return genre;
    }

    public int getCount() {
        return count;
    }
}
