package com.watchlist.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String genre;
    private String director;
    private int year;

    private boolean watched = false;
    private int rating = 0;

    @ElementCollection
    private List<String> imagePaths = new ArrayList<>();

    @Column(length = 2000)
    private String plot;

    public Movie() {}

    public Movie(Long id, String title, String genre, String director, int year, boolean watched, int rating, List<String> imagePaths) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.director = director;
        this.year = year;
        this.watched = watched;
        this.rating = rating;
        this.imagePaths = imagePaths != null ? imagePaths : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }
    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }


}

