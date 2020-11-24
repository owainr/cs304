package ca.ubc.cs304.model;

public class GenreAvgLengthModel {
    private String genre;
    private int avgLength;

    public GenreAvgLengthModel(String genre, int avgLength) {
        this.genre = genre;
        this.avgLength = avgLength;
    }

    public int getAvgLength() {
        return avgLength;
    }

    public String getGenre() {
        return genre;
    }
}
