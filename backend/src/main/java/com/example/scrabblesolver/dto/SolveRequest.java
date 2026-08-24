package com.example.scrabblesolver.dto;

import com.example.scrabblesolver.model.tiles.SpecialTile;
import com.example.scrabblesolver.model.tiles.Tile;

import java.util.Locale;

public record SolveRequest(String rack, CellDto[][] board) {

    private static final int SIZE = 15;

    public String normalisedRack() {
        if (rack == null) {
            throw new IllegalArgumentException("Rack must be present");
        }
        return rack.toLowerCase(Locale.ROOT);
    }

    public Tile[][] toBoard() {
        if (board == null || board.length != SIZE) {
            throw new IllegalArgumentException("Board must have %d rows".formatted(SIZE));
        }
        Tile[][] tiles = new Tile[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            if (board[row] == null || board[row].length != SIZE) {
                throw new IllegalArgumentException("Row %d must have %d cells".formatted(row, SIZE));
            }
            for (int column = 0; column < SIZE; column++) {
                CellDto cell = board[row][column];
                tiles[row][column] = cell == null ? SpecialTile.NONE : cell.toTile();
            }
        }
        return tiles;
    }
}
