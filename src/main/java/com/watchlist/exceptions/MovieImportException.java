package com.watchlist.exceptions;

public class MovieImportException extends RuntimeException {
    public MovieImportException(String message, Throwable cause) {
        super(message, cause);
    }
}