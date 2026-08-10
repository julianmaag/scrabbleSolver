package com.example.scrabblesolver.service;

import com.example.scrabblesolver.model.Solutions;
import com.example.scrabblesolver.model.Word;
import com.example.scrabblesolver.repository.IAvailableWords;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Primary
public class SimplesolverWithJoker implements ISolverService {
    private HashMap<Character, Integer> chars;
    private ArrayList<Word> possibleWords;
    private IAvailableWords availableWords;

    public SimplesolverWithJoker(IAvailableWords availableWords) {
        this.chars = new HashMap<>();
        this.possibleWords = new ArrayList<>();
        this.availableWords = availableWords;
    }

    @Override
    public Solutions getSolutions(String inputChars) {
        initialize(inputChars);

        for (String validWord : availableWords.getAvailableWords()) {
            // count the letters in validWord
            HashMap<Character, Integer> mappedWord = toHashmap(validWord.toCharArray());

            boolean contains = true;
            int jokerCount = 0;
            if(chars.containsKey('?')) {
                jokerCount = chars.get('?');
            }
            int tilesOffWord = 0;
            // interate over all words and see if they match the count of the input letters
            for(Map.Entry<Character, Integer> entry : mappedWord.entrySet()) {
                if (!(this.chars.containsKey(entry.getKey()) && this.chars.get(entry.getKey()) >= entry.getValue())) {

                    if(this.chars.containsKey(entry.getKey())) {
                        tilesOffWord += (entry.getValue() - this.chars.get(entry.getKey()));
                    }else {
                        tilesOffWord += entry.getValue();
                    }
                 if (tilesOffWord > jokerCount) {
                     contains = false;
                     break;
                 }
                }
            }
            if (contains) {this.possibleWords.add(new Word(validWord));}
        }

        return new Solutions(this.possibleWords);
    }


    private void initialize(String inputChars) {
        chars.clear();
        possibleWords.clear();

        // count how many times a letter is available
        this.chars = toHashmap(inputChars.toCharArray());
    }

    private HashMap<Character,Integer> toHashmap(char[] inputChars) {
        HashMap<Character, Integer> chars = new HashMap<>();
        for (Character c : inputChars) {
            chars.merge(c, 1, Integer::sum);
        }
        return chars;
    }


}
