import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.watchlist.controller.MovieController;
import com.watchlist.model.Movie;
import com.watchlist.service.IMovieService;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    @Mock
    private IMovieService movieService;

    @InjectMocks
    private MovieController movieController;

    // @BeforeEach
    // void setUp() {
    //     movieController = new MovieController(movieService);
    // }

    @Test
    void testGetAllMovies() {
        List<Movie> movies = Arrays.asList(
            new Movie(1L, "Inception", "Sci-Fi", "Christopher Nolan", 2010, false, 0, null),
            new Movie(2L, "The Matrix", "Action", "Wachowski Sisters", 1999, false, 0, null)
        );

        Page<Movie> moviePage = new PageImpl<>(movies);

        when(movieService.getAllMovies(any(Pageable.class))).thenReturn(moviePage);

        Pageable pageable = PageRequest.of(0, 10);

        var response = movieController.getAllMovies(pageable);

        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetMovieById_Found() {
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", "Christopher Nolan", 2010, false, 0, null);
        when(movieService.getMovieById(1L)).thenReturn(Optional.of(movie));

        ResponseEntity<Movie> response = movieController.getMovieById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Inception", response.getBody().getTitle());
    }

    @Test
    void testGetMovieById_NotFound() {
        when(movieService.getMovieById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Movie> response = movieController.getMovieById(999L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testAddMovie() {
        Movie movie = new Movie(null, "Inception", "Sci-Fi", "Christopher Nolan", 2010, false, 0, null);

        when(movieService.addMovie(any(Movie.class))).thenReturn(movie);

        ResponseEntity<Movie> response = movieController.addMovie(movie);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Inception", response.getBody().getTitle());
    }

    @Test
    void testDeleteMovie() {
        doNothing().when(movieService).deleteMovie(1L);

        ResponseEntity<Void> response = movieController.deleteMovie(1L);

        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testUpdateWatched() {
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", "Christopher Nolan", 2010, true, 0, null);

        when(movieService.getMovieById(1L)).thenReturn(Optional.of(movie));
        when(movieService.saveMovie(any(Movie.class))).thenReturn(movie);

        ResponseEntity<Movie> response = movieController.updateWatched(1L, true);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(true, response.getBody().isWatched());
    }

    @Test
    void testUpdateRating() {
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", "Christopher Nolan", 2010, false, 9, null);

        when(movieService.getMovieById(1L)).thenReturn(Optional.of(movie));
        when(movieService.saveMovie(any(Movie.class))).thenReturn(movie);

        ResponseEntity<Movie> response = movieController.updateRating(1L, 9);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(9, response.getBody().getRating());
    }

    @Test
    void testImportMovieByTitle() {
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", "Christopher Nolan", 2010, false, 0, null);

        when(movieService.importMovieByTitle("Inception")).thenReturn(movie);

        ResponseEntity<Movie> response = movieController.importMovieByTitle("Inception");

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Inception", response.getBody().getTitle());
    }
}
