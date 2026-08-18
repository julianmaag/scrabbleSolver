package com.example.scrabblesolver.model;

import com.example.scrabblesolver.model.tiles.LetterTile;

import java.util.List;

public class Word implements Comparable<Word>{
    private final String name;
    private final List<LetterTile> letters;
    private final int row;
    private final int column;
    private final DIRECTION direction;

    public Word(List<LetterTile> letters, int row, int column, DIRECTION direction){
        this.letters = List.copyOf(letters);
        this.row = row;
        this.column = column;
        this.direction = direction;
        StringBuilder nameBuilder = new StringBuilder(letters.size());
        for (LetterTile letter : letters) {
            nameBuilder.append(letter.letter());
        }
        this.name = nameBuilder.toString();
    }

    public List<LetterTile> getLetters() {
        return letters;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return name.length();
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public DIRECTION getDirection() {
        return direction;
    }


    @Override
    public int compareTo(Word compareWord) {
        return compareWord.getLength() - getLength();
    }
}
