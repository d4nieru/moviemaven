package org.example;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private String title;
    private String year;

    private String description;

    private String genre;

    private String duration;

    private String actors;
    private String imdbID;
    private String posterUrl;


    public Movie(String title, String year, String imdbID, String posterUrl/*, String description*/) {
        this.title = title;
        this.year = year;
        this.imdbID = imdbID;
        this.posterUrl = posterUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    @Override
    public String toString() {
        return title + " (" + year + ")";
    }
}
