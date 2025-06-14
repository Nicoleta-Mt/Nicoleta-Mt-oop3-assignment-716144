package com.watchlist.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.watchlist.model.Movie;

public interface IMovieService {
    Page<Movie> getAllMovies(Pageable pageable);
    Optional<Movie> getMovieById(Long id);
    Movie importMovieByTitle(String title);
    Movie saveMovie(Movie movie);
    Movie addMovie(Movie movie);
    void deleteMovie(Long id);
}
