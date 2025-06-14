package com.watchlist.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.watchlist.model.Movie;
import com.watchlist.service.IMovieService;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final IMovieService movieService;

    @Autowired
    public MovieController(IMovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/movies")
    public ResponseEntity<List<Movie>> getAllMovies(Pageable pageable) {
        Page<Movie> moviesPage = movieService.getAllMovies(pageable);
        return ResponseEntity.ok(moviesPage.getContent());  
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id)
                .map(movie -> ResponseEntity.ok(movie))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/watched")
    public ResponseEntity<Movie> updateWatched(@PathVariable Long id, @RequestBody Boolean watched) {
        return movieService.getMovieById(id)
            .map(movie -> {
                movie.setWatched(watched);
                movieService.saveMovie(movie);
                return ResponseEntity.ok(movie);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/rating")
    public ResponseEntity<Movie> updateRating(@PathVariable Long id, @RequestParam int rating) {
        Optional<Movie> optionalMovie = movieService.getMovieById(id);
        if (optionalMovie.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Movie movie = optionalMovie.get();
        movie.setRating(rating);
        Movie saved = movieService.saveMovie(movie);
        return ResponseEntity.ok(saved);
    }


    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
        Movie savedMovie = movieService.addMovie(movie);
        return new ResponseEntity<>(savedMovie, HttpStatus.CREATED);
    }

    @PostMapping("/import")
        public ResponseEntity<Movie> importMovieByTitle(@RequestParam String title) {
            Movie movie = movieService.importMovieByTitle(title);
            if (movie == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(movie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

}