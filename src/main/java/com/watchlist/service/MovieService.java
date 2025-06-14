package com.watchlist.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.watchlist.exceptions.DatabaseException;
import com.watchlist.exceptions.ExternalApiException;
import com.watchlist.exceptions.MovieImportException;
import com.watchlist.model.Movie;
import com.watchlist.repository.MovieRepository;


@Service
public class MovieService implements IMovieService {

    private final MovieRepository movieRepository;
    private final MovieApiService movieApiService;
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);

    @Autowired
    public MovieService(MovieRepository movieRepository, MovieApiService movieApiService) {
        this.movieRepository = movieRepository;
        this.movieApiService = movieApiService;
    }

    @Override
    public Movie importMovieByTitle(String title) {
        logger.info("Importing movie by title: {}", title);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<Map<String, Object>> omdbFuture = executor.submit(() -> movieApiService.fetchBasicMovieData(title));
        Future<Map<String, Object>> tmdbFuture = executor.submit(() -> movieApiService.searchTmdbMovie(title));

        try {
            Map<String, Object> omdbData = omdbFuture.get();
            Map<String, Object> tmdbSearch = tmdbFuture.get();

            List<Map<String, Object>> results = (List<Map<String, Object>>) tmdbSearch.get("results");
            if (results.isEmpty()) {
                logger.warn("No TMDB results found for title: {}", title);
                return null;
            }

            int tmdbId = (int) ((Number) results.get(0).get("id")).intValue();
            logger.info("TMDB ID found: {} for title: {}", tmdbId, title);

            String savePath = "./images"; 
            List<String> imagePaths = movieApiService.downloadImages(tmdbId, savePath);

            Movie movie = new Movie();
            movie.setTitle((String) omdbData.getOrDefault("Title", title));
            movie.setYear(Integer.parseInt((String) omdbData.getOrDefault("Year", "0")));
            movie.setDirector((String) omdbData.getOrDefault("Director", "Unknown"));
            movie.setGenre((String) omdbData.getOrDefault("Genre", "Unknown"));
            movie.setImagePaths(imagePaths);
            movie.setPlot((String) omdbData.getOrDefault("Plot", "Unknown"));

            Movie savedMovie = movieRepository.save(movie);
            logger.info("Movie saved successfully: {} (ID: {})", savedMovie.getTitle(), savedMovie.getId());

            return savedMovie;

        } catch (HttpClientErrorException e) {
            logger.error("OMDb API call failed for title {}: {}", title, e.getStatusCode(), e);
            throw new ExternalApiException("OMDb API call failed: " + e.getStatusCode(), e);
        } catch (DataAccessException e) {
            logger.error("Database error while saving movie: {}", title, e);
            throw new DatabaseException("Failed to save movie to database", e);
        } catch (Exception e) {
            logger.error("Unexpected error importing movie: {}", title, e);
            throw new MovieImportException("Unexpected error importing movie: " + e.getMessage(), e);
        } finally {
            executor.shutdown();
        }
    }

    @Override
    public Page<Movie> getAllMovies(Pageable pageable) {
        logger.info("Fetching all movies with pagination: page number {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        return movieRepository.findAll(pageable);
    }

    @Override
    public Optional<Movie> getMovieById(Long id) {
        logger.info("Fetching movie by id: {}", id);
        return movieRepository.findById(id);
    }

    @Override
    public Movie saveMovie(Movie movie) {
        logger.info("Saving movie: {}", movie.getTitle());
        Movie savedMovie = movieRepository.save(movie);
        logger.info("Movie saved with id: {}", savedMovie.getId());
        return savedMovie;
    }

    @Override
    public Movie addMovie(Movie movie) {
        logger.info("Adding new movie: {}", movie.getTitle());
        Movie savedMovie = movieRepository.save(movie);
        logger.info("New movie added with id: {}", savedMovie.getId());
        return savedMovie;
    }

    @Override
    public void deleteMovie(Long id) {
        logger.info("Deleting movie with id: {}", id);
        movieRepository.deleteById(id);
        logger.info("Movie deleted with id: {}", id);
    }

    public Movie updateWatchedStatus(Long id, boolean watched) {
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        movie.setWatched(watched);
        return movieRepository.save(movie);
    }

    public Movie updateRating(Long id, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        movie.setRating(rating);
        return movieRepository.save(movie);
    }
}

