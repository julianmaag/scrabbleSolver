package com.example.scrabblesolver.controller;

import com.example.scrabblesolver.service.ISolverService;
import org.springframework.web.bind.annotation.PostMapping;
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
    public void solve() {

    }
}
