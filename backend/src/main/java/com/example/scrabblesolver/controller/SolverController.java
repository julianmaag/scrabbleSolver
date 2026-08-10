package com.example.scrabblesolver.controller;

import com.example.scrabblesolver.model.Solutions;
import com.example.scrabblesolver.service.ISolverService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class SolverController {

    private final ISolverService solverService;

    public SolverController(ISolverService solverService) {
        this.solverService = solverService;
    }

    @PostMapping("/solve")
    public Solutions solve(@RequestBody SolveRequest request) {
        String letters = request.letters() == null ? "" : request.letters().trim();

        return solverService.getSolutions(letters.toLowerCase(Locale.ROOT));
    }
}
