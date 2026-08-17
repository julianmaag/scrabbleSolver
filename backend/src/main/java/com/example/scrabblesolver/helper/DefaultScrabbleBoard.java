package com.example.scrabblesolver.helper;

import com.example.scrabblesolver.model.tiles.LetterTile;
import com.example.scrabblesolver.model.tiles.SpecialTile;
import com.example.scrabblesolver.model.tiles.Tile;


import static com.example.scrabblesolver.model.tiles.SpecialTile.LETTER2;
import static com.example.scrabblesolver.model.tiles.SpecialTile.LETTER3;
import static com.example.scrabblesolver.model.tiles.SpecialTile.NONE;
import static com.example.scrabblesolver.model.tiles.SpecialTile.WORD2;
import static com.example.scrabblesolver.model.tiles.SpecialTile.WORD3;

public final class DefaultScrabbleBoard {
    public static final int SIZE = 15;

    public static final Tile[][] BOARD = {
        {WORD3, NONE, NONE, LETTER2, NONE, NONE, NONE, WORD3, NONE, NONE, NONE, LETTER2, NONE, NONE, WORD3},
        {NONE, WORD2, NONE, NONE, NONE, LETTER3, NONE, NONE, NONE, LETTER3, NONE, NONE, NONE, WORD2, NONE},
        {NONE, NONE, WORD2, NONE, NONE, NONE, LETTER2, NONE, LETTER2, NONE, NONE, NONE, WORD2, NONE, NONE},
        {LETTER2, NONE, NONE, WORD2, NONE, NONE, NONE, LETTER2, NONE, NONE, NONE, WORD2, NONE, NONE, LETTER2},
        {NONE, NONE, NONE, NONE, WORD2, NONE, NONE, NONE, NONE, NONE, WORD2, NONE, NONE, NONE, NONE},
        {NONE, LETTER3, NONE, NONE, NONE, LETTER3, NONE, NONE, NONE, LETTER3, NONE, NONE, NONE, LETTER3, NONE},
        {NONE, NONE, LETTER2, NONE, NONE, NONE, LETTER2, NONE, LETTER2, NONE, NONE, NONE, LETTER2, NONE, NONE},
        {WORD3, NONE, NONE, LETTER2, NONE, NONE, NONE, WORD2, NONE, NONE, NONE, LETTER2, NONE, NONE, WORD3},
        {NONE, NONE, LETTER2, NONE, NONE, NONE, LETTER2, NONE, LETTER2, NONE, NONE, NONE, LETTER2, NONE, NONE},
        {NONE, LETTER3, NONE, NONE, NONE, LETTER3, NONE, NONE, NONE, LETTER3, NONE, NONE, NONE, LETTER3, NONE},
        {NONE, NONE, NONE, NONE, WORD2, NONE, NONE, NONE, NONE, NONE, WORD2, NONE, NONE, NONE, NONE},
        {LETTER2, NONE, NONE, WORD2, NONE, NONE, NONE, LETTER2, NONE, NONE, NONE, WORD2, NONE, NONE, LETTER2},
        {NONE, NONE, WORD2, NONE, NONE, NONE, LETTER2, NONE, LETTER2, new LetterTile('t', false), new LetterTile('e', false), new LetterTile('l', false), new LetterTile('l', false), NONE, NONE},
        {NONE, WORD2, NONE, NONE, NONE, LETTER3, NONE, NONE, NONE, LETTER3, NONE, NONE, NONE, WORD2, NONE},
        {WORD3, NONE, NONE, LETTER2, NONE, NONE, NONE, WORD3, NONE, NONE, NONE, LETTER2, NONE, NONE, WORD3}
    };

    private DefaultScrabbleBoard() {
    }

    public static Tile[][] empty() {
        Tile[][] board = new Tile[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            board[row] = BOARD[row].clone();
        }
        return board;
    }
}