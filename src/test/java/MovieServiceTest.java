import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.watchlist.model.Movie;
import com.watchlist.repository.MovieRepository;
import com.watchlist.service.MovieService;

public class MovieServiceTest {

     @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @BeforeEach
    public void setUp() {
         MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMovies() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Movie> movies = Arrays.asList(
            new Movie(1L, "Inception", "Sci-Fi", "Christopher Nolan", 2010, false, 0, null),
            new Movie(2L, "The Matrix", "Action", "Wachowski Sisters", 1999, false, 0, null)
        );
        Page<Movie> moviePage = new PageImpl<>(movies, pageable, movies.size());

        when(movieRepository.findAll(pageable)).thenReturn(moviePage);

        Page<Movie> result = movieService.getAllMovies(pageable);

        assertEquals(2, result.getContent().size());
    }

    @Test
    public void testGetMovieById() {
        Movie movie = new Movie(1L, "Inception", "Sci-Fi", "Christopher Nolan", 2010,false, 0 , null);
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));

        Optional<Movie> result = movieService.getMovieById(1L);
        assertTrue(result.isPresent());
        assertEquals("Inception", result.get().getTitle());
    }

    @Test
    public void testAddMovie() {
        Movie movie = new Movie(null, "Interstellar", "Sci-Fi", "Christopher Nolan", 2014, false, 0, null);
        Movie savedMovie = new Movie(3L, "Interstellar", "Sci-Fi", "Christopher Nolan", 2014, false, 0, null);

        when(movieRepository.save(ArgumentMatchers.any(Movie.class))).thenReturn(savedMovie);

        Movie result = movieService.addMovie(movie);
        assertNotNull(result);
        assertEquals("Interstellar", result.getTitle());
    }

    @Test
    public void testDeleteMovie() {
        Long movieId = 1L;
        doNothing().when(movieRepository).deleteById(movieId);

        movieService.deleteMovie(movieId);
        verify(movieRepository, times(1)).deleteById(movieId);
    }
}