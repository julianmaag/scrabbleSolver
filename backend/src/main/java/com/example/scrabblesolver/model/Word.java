package com.example.scrabblesolver.model;

import com.example.scrabblesolver.model.tiles.LetterTile;

import java.util.List;

public class Word implements Comparable<Word>{
    private final String name;
    private final List<LetterTile> letters;

    public Word(List<LetterTile> letters){
        this.letters = List.copyOf(letters);
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

    @Override
    public int compareTo(Word compareWord) {
        return compareWord.getLength() - getLength();
    }
}
