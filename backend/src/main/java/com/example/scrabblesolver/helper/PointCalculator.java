package com.example.scrabblesolver.helper;

import static com.example.scrabblesolver.repository.LetterValues.getLetterValue;

public class PointCalculator {
    public static int  calculatePointsForWord(String name){
        int sum = 0;
        for (char letter : name.toCharArray()){
            sum += getLetterValue(letter);
        }
        return sum;
    }
}
