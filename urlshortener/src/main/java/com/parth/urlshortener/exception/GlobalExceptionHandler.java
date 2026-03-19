package com.parth.urlshortener.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(UrlNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(UrlExpiredException.class)
    public ResponseEntity<Map<String, String>> handleExpired(UrlExpiredException ex) {
        return ResponseEntity.status(HttpStatus.GONE)
            .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", ex.getMessage()));
    }
}
