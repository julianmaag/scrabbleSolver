package com.example.scrabblesolver.dto;

import com.example.scrabblesolver.model.tiles.LetterTile;
import com.example.scrabblesolver.model.tiles.SpecialTile;
import com.example.scrabblesolver.model.tiles.Tile;

public record CellDto(Character letter, boolean joker, String specialTile) {
    public Tile toTile() {
        if (letter != null) {
            return new LetterTile(letter, joker);
        }
        return SpecialTile.valueOf(specialTile);
    }
}
