package com.example.scrabblesolver.model;

import static com.example.scrabblesolver.helper.PointCalculator.calculatePointsForWord;

public class Word implements Comparable<Word>{
    private int points;
    private String name;

    public Word(String name) {
        this.name = name;
        points = calculatePointsForWord(name);
    }

    public int getPoints() {
        return points;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return name.length();
    }

    @Override
    public String toString() {
        return name + ": " + points;
    }

    @Override
    public int compareTo(Word compareWord) {
        return compareWord.points - this.points;
    }
}
