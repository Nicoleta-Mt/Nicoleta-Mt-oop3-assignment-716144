package com.watchlist.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MovieApiService {

    @Value("${omdb.api.key}")
    private String omdbApiKey;

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    private final RestTemplate restTemplate = new RestTemplate();


    public Map<String, Object> fetchBasicMovieData(String title) {
        String url = String.format("http://www.omdbapi.com/?t=%s&apikey=%s", title, omdbApiKey);
        return restTemplate.getForObject(url, Map.class);
    }

    public Map<String, Object> searchTmdbMovie(String title) {
        String url = String.format(
            "https://api.themoviedb.org/3/search/movie?api_key=%s&query=%s",
            tmdbApiKey,
            title
        );
        return restTemplate.getForObject(url, Map.class);
    }

    public List<Map<String, Object>> fetchSimilarMovies(int tmdbId) {
        String url = String.format(
            "https://api.themoviedb.org/3/movie/%d/similar?api_key=%s",
            tmdbId,
            tmdbApiKey
        );
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return (List<Map<String, Object>>) response.getOrDefault("results", List.of());
    }

    public List<String> downloadImages(int tmdbId, String savePath) {
        String url = String.format(
            "https://api.themoviedb.org/3/movie/%d/images?api_key=%s",
            tmdbId,
            tmdbApiKey
        );
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> backdrops = (List<Map<String, Object>>) response.getOrDefault("backdrops", List.of());
        List<String> savedImagePaths = new ArrayList<>();

        int limit = Math.min(backdrops.size(), 3);
        for (int i = 0; i < limit; i++) {
            String filePath = (String) backdrops.get(i).get("file_path");
            String imageUrl = "https://image.tmdb.org/t/p/w500" + filePath;
            try {
                byte[] imageBytes = restTemplate.getForObject(imageUrl, byte[].class);
                String localPath = savePath + "/image_" + i + ".jpg";
                Files.write(Paths.get(localPath), imageBytes);
                savedImagePaths.add(localPath);
            } catch (Exception e) {
                
            }
        }

        return savedImagePaths;
    }
}
