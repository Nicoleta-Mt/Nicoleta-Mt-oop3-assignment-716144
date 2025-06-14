package com.watchlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.watchlist.model.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {}
