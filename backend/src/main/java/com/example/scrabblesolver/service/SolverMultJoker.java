package com.example.scrabblesolver.service;

import com.example.scrabblesolver.model.DIRECTION;
import com.example.scrabblesolver.model.Move;
import com.example.scrabblesolver.model.Solutions;
import com.example.scrabblesolver.model.Word;
import com.example.scrabblesolver.model.tiles.LetterTile;
import com.example.scrabblesolver.model.tiles.SpecialTile;
import com.example.scrabblesolver.model.tiles.Tile;
import com.example.scrabblesolver.repository.IAvailableWords;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.scrabblesolver.repository.LetterValues.getLetterValue;

@Service
public class SolverMultJoker implements ISolverService {
    private IAvailableWords availableWords;
    private static final int SOLUTIONSCOUNT = 10;

    public SolverMultJoker(IAvailableWords availableWords) {
        this.availableWords = availableWords;
    }

    @Override
    public List<Move> getSolutions(String inputChars, Tile[][] board) {

        ArrayList<Word> possibleWords = calculatePossibleSolutions(toHashmap(inputChars.toCharArray()));

        List<Move> Moves = getBestMoves(possibleWords, board);


        return Moves;
    }

    private List<Move> getBestMoves(ArrayList<Word> possibleWords, Tile[][] board) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for(Word word : possibleWords){
            possibleMoves.addAll(getMovesForWord(board, word));
        }
        Collections.sort(possibleMoves);
        return possibleMoves.subList(0,SOLUTIONSCOUNT); //TODO: handle if empty
        //return possibleMoves;
    }

    private ArrayList<Move> getMovesForWord(Tile[][] board, Word word) {
        ArrayList<Move> moves = new ArrayList<>();

        for(DIRECTION direction : DIRECTION.values()) {
            for(int r = 0; r < board.length ; r++){
                for(int c = 0; c < board[0].length; c++){
                    if(isLegalMove(r, c, board.clone(), word, direction)){
                        Move appliedMove = applyPositionToBoard(board,r,c, word, direction);
                        moves.add(appliedMove);
                    }
                }
            }
        }
        return moves;
    }

    private Move applyPositionToBoard(Tile[][] board, int r, int c, Word word, DIRECTION direction) {
        int points = 0;
        int wordMultiplier = 1;
        Tile[][] appliedBoard = Arrays.stream(board)
                .map(Tile[]::clone)
                .toArray(Tile[][]::new);

        for (int i = 0; i < word.getLength(); i++) {
            int row = direction == DIRECTION.ACCROSS ? r : r + i;
            int column = direction == DIRECTION.ACCROSS ? c + i : c;
            LetterTile letter = word.getLetters().get(i);

            Tile existingTile = board[row][column];
            switch (existingTile){
                case SpecialTile.LETTER2 -> points += letter.joker() ? 0 : getLetterValue(letter.letter()) * 2;
                case SpecialTile.LETTER3 -> points += letter.joker() ? 0 : getLetterValue(letter.letter()) * 3;
                case SpecialTile.WORD2 -> {
                    points += letter.joker() ? 0 : getLetterValue(letter.letter());
                    wordMultiplier *= 2;
                }
                case SpecialTile.WORD3 -> {
                    points += letter.joker() ? 0 : getLetterValue(letter.letter());
                    wordMultiplier *= 3;
                }
                default -> points += letter.joker() ? 0 : getLetterValue(letter.letter());
            }

            appliedBoard[row][column] = letter;
        }
        points *= wordMultiplier;
        return new Move(appliedBoard, word, points);
    }


    private boolean isLegalMove(int r, int c, Tile[][] board, Word word, DIRECTION direction){
        if(c + word.getLength() > board[0].length && r + word.getLength() > board.length){
            return false;
        }
        // check if cells are free
        if(direction == DIRECTION.ACCROSS){
            for (int i = 0; i < word.getLength(); i++){
                try {
                    Tile tile = board[r][c + i];
                    if (!(tile instanceof SpecialTile)) {
                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }
        if(direction == DIRECTION.DOWN){
            for (int i = 0; i < word.getLength(); i++){
                try {
                    Tile tile = board[r + i][c];
                    if (!(tile instanceof SpecialTile)) {
                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }
        // TODO: later check if other tile and if touching is a validword
        return true;
    }

    private ArrayList<Word> calculatePossibleSolutions(HashMap<Character, Integer> inputWordChars) {
        ArrayList<Word> possibleWords = new ArrayList<>();

        for (String validWord : availableWords.getAvailableWords()) {
            // count the letters in validWord
            HashMap<Character, Integer> mappedWord = toHashmap(validWord.toCharArray());

            boolean contains = true;
            int jokerCount = 0;
            if (inputWordChars.containsKey('?')) {
                jokerCount = inputWordChars.get('?');
            }
            int tilesOffWord = 0;
            // interate over all words and see if they match the count of the input letters
            for (Map.Entry<Character, Integer> entry : mappedWord.entrySet()) {
                if (!(inputWordChars.containsKey(entry.getKey()) && inputWordChars.get(entry.getKey()) >= entry.getValue())) {

                    if (inputWordChars.containsKey(entry.getKey())) {
                        tilesOffWord += (entry.getValue() - inputWordChars.get(entry.getKey()));
                    } else {
                        tilesOffWord += entry.getValue();
                    }
                    if (tilesOffWord > jokerCount) {
                        contains = false;
                        break;
                    }
                }
            }
            if (contains) {
                possibleWords.add(new Word(toLetterTiles(validWord, inputWordChars)));
            }
        }

        return possibleWords;
    }


    private HashMap<Character, Integer> toHashmap(char[] inputChars) {
        HashMap<Character, Integer> chars = new HashMap<>();
        for (Character c : inputChars) {
            chars.merge(c, 1, Integer::sum);
        }
        return chars;
    }

    private List<LetterTile> toLetterTiles(String word, HashMap<Character, Integer> rackLetters) {
        HashMap<Character, Integer> remainingLetters = new HashMap<>(rackLetters);
        List<LetterTile> letters = new ArrayList<>(word.length());

        for (char letter : word.toCharArray()) {
            int availableCount = remainingLetters.getOrDefault(letter, 0);
            boolean joker = availableCount == 0;
            if (!joker) {
                remainingLetters.put(letter, availableCount - 1);
            }
            letters.add(new LetterTile(letter, joker));
        }
        return letters;
    }
}
