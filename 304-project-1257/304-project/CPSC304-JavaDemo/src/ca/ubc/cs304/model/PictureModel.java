package ca.ubc.cs304.model;


import java.sql.Date;

/**
 * The intent for this class is to update/store information about a single branch
 */
public class PictureModel {
	private String title;
	private Date releaseDate;
	private int length;
	private String director;
    private int seriesID;
	
	public PictureModel(String title, Date releaseDate, int length, String director, int seriesID) {
		this.title = title;
		this.releaseDate = releaseDate;
		this.length = length;
		this.director = director;
        this.seriesID = seriesID;
	}

	public String getTitle() {
		return title;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public int getLength() {
		return length;
	}

	public String getDirector() {
		return director;
	}

    public int getSeriesID() {
		return seriesID;
	}
}
