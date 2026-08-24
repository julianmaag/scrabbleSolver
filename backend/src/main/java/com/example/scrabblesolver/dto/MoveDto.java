package com.example.scrabblesolver.dto;

import com.example.scrabblesolver.model.DIRECTION;
import com.example.scrabblesolver.model.Move;
import com.example.scrabblesolver.model.Placement;
import com.example.scrabblesolver.model.Word;
import com.example.scrabblesolver.model.tiles.LetterTile;

import java.util.List;

public record MoveDto(
        String word,
        int points,
        int row,
        int column,
        DIRECTION direction,
        List<Placement> placements
) {
    public static MoveDto from(Move move) {
        Word word = move.word();
        return new MoveDto(
                asString(word.getLetters()),
                move.points(),
                word.getRow(),
                word.getColumn(),
                word.getDirection(),
                List.copyOf(move.placements())
        );
    }

    private static String asString(List<LetterTile> letters) {
        StringBuilder sb = new StringBuilder(letters.size());
        for (LetterTile tile : letters) {
            sb.append(tile.letter());
        }
        return sb.toString();
    }
}