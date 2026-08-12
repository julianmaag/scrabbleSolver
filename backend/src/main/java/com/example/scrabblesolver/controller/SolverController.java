package com.example.scrabblesolver.controller;

import com.example.scrabblesolver.dto.SimpleSolveRequest;
import com.example.scrabblesolver.dto.SolutionsResponse;
import com.example.scrabblesolver.model.Solutions;
import com.example.scrabblesolver.service.ISolverService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/api")
public class SolverController {

    private final ISolverService solverService;

    public SolverController(ISolverService solverService) {
        this.solverService = solverService;
    }

    @PostMapping("/solve")
    public SolutionsResponse solve(@RequestBody SimpleSolveRequest request) {
        String letters = request.letters() == null ? "" : request.letters().trim();

        // Solutions solutions = solverService.getSolutions(letters.toLowerCase(Locale.ROOT));

        // return SolutionsResponse.from(solutions);
        return null;
    }
}
