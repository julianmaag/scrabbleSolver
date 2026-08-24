package com.example.scrabblesolver.model;

import com.example.scrabblesolver.model.tiles.LetterTile;

public record Placement(int row, int column, char rackTile, char playedAs) {
    public boolean isBlank() { return rackTile == '?'; }
    public LetterTile toTile() { return new LetterTile(playedAs, isBlank()); }
}