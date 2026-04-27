package com.example.urlshortener.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handle(BadRequestException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
