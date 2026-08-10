package com.example.scrabblesolver.repository;

import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
public class LetterValues {

    public LetterValues() {
    }

    public static final Map<Character, Integer> LETTER_VALUES;

    static {
        Map<Character, Integer> values = new HashMap<>();
        putChars(values, "aeioulnstr", 1);
        putChars(values, "dg", 2);
        putChars(values, "bcmp", 3);
        putChars(values, "fhvwy", 4);
        putChars(values, "k", 5);
        putChars(values, "jx", 8);
        putChars(values, "qz", 10);
        LETTER_VALUES = Collections.unmodifiableMap(values);
    }

    private static void putChars(Map<Character, Integer> map, String chars, int score) {
        for (char c : chars.toCharArray()) {
            map.put(c, score);
        }
    }

    public static int getLetterValue(char letter) {
        return LETTER_VALUES.get(letter);
    }
}
