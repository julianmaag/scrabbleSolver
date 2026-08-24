package com.example.scrabblesolver.controller;

import com.example.scrabblesolver.dto.MoveDto;
import com.example.scrabblesolver.dto.SolveRequest;
import com.example.scrabblesolver.dto.SolveResponse;
import com.example.scrabblesolver.model.Move;
import com.example.scrabblesolver.model.tiles.Tile;
import com.example.scrabblesolver.service.ISolverService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SolverController {

    private final ISolverService solverService;

    public SolverController(ISolverService solverService) {
        this.solverService = solverService;
    }

    @PostMapping("/solve")
    public SolveResponse solve(@RequestBody SolveRequest request) {
        Tile[][] board = request.toBoard();
        List<Move> moves = solverService.getSolutions(request.normalisedRack(), board);
        return new SolveResponse(moves.stream().map(MoveDto::from).toList());
    }

}
