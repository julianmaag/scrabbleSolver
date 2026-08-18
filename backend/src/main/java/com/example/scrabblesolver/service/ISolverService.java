package com.example.scrabblesolver.service;

import com.example.scrabblesolver.model.Move;
import com.example.scrabblesolver.model.tiles.Tile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ISolverService {
    List<Move> getSolutions(String chars, Tile[][] board);
}
