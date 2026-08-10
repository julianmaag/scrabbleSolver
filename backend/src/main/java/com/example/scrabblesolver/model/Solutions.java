package com.example.scrabblesolver.model;

import java.util.ArrayList;
import java.util.Collections;


public class Solutions {
    private ArrayList<Word> words;

    public Solutions(ArrayList<Word> words) {
        this.words = words;
        sortWords();
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    private void sortWords(){
        Collections.sort(words);
    }

    @Override
    public String toString() {
        return "Solutions{" + "words=" + words + '}';
    }
}
