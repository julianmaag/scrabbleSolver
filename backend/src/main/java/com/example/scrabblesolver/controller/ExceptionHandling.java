package com.example.scrabblesolver.controller;

import com.example.scrabblesolver.dto.ProblemDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDto> handleIllegalArgumentException() {
        ProblemDto problem = new ProblemDto(HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND.value(), "No resource found.");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDto> handleIllegalStateException() {
        ProblemDto problem = new ProblemDto(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(), "Request can't be processed.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }
}
