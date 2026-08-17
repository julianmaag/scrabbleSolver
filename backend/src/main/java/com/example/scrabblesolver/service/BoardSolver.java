package com.example.scrabblesolver.service;

import com.example.scrabblesolver.model.DIRECTION;
import com.example.scrabblesolver.model.Move;
import com.example.scrabblesolver.model.Word;
import com.example.scrabblesolver.model.tiles.LetterTile;
import com.example.scrabblesolver.model.tiles.SpecialTile;
import com.example.scrabblesolver.model.tiles.Tile;
import com.example.scrabblesolver.repository.IAvailableWords;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.scrabblesolver.repository.LetterValues.getLetterValue;

@Service
public class BoardSolver implements ISolverService {
    private IAvailableWords availableWords;
    private static final int SOLUTIONSCOUNT = 10;

    public BoardSolver(IAvailableWords availableWords) {
        this.availableWords = availableWords;
    }

    @Override
    public List<Move> getSolutions(String inputChars, Tile[][] board) {
        List<Move> possibleMoves = new ArrayList<>();
        boolean isFirstMove = false; // TODO
        HashMap<Character, Integer> input = toHashmap(inputChars.toCharArray());


        for(String word : availableWords.getAvailableWords()){
            for(DIRECTION direction : DIRECTION.values()) {
                for(int r = 0; r < board.length ; r++){
                    for(int c = 0; c < board[0].length; c++){
                        matchWord(board, input, word, direction, r, c, isFirstMove)
                                .ifPresent(possibleMoves::add);
                    }
                }
            }
        }

        Collections.sort(possibleMoves);
        return possibleMoves.subList(
                0,
                Math.min(SOLUTIONSCOUNT, possibleMoves.size())
        );
    }

    private Optional<Move> matchWord(Tile[][] board, HashMap<Character, Integer> input, String word, DIRECTION direction, int row, int column, boolean isFirstMove) {
        ArrayList<LetterTile> wordToApply = new ArrayList<>();
        ArrayList<String> adjacantWords = new ArrayList<>();


        int consumedFromRack = 0;
        boolean usedExistingLetter = false;

        int jokerCount = 0;
        if (input.containsKey('?')) {
            jokerCount = input.get('?');
        }

        // how long is word if longer then board. length -> cancel
        int wordLength = word.length();
        if(wordLength > board.length){
            return Optional.empty();
        }

        // check tiles for word length and direction, if there is a letter and not in word -> cancel

        for (int i = 0; i < word.length(); i++) {
            int currentRow = direction == DIRECTION.ACCROSS ? row : row + i;
            int currentColumn = direction == DIRECTION.ACCROSS ? column + i : column;

            // check if position is in the board
            if (currentRow >= board.length || currentColumn >= board[0].length) {
                return Optional.empty();
            }

            if (direction == DIRECTION.ACCROSS &&
                column + word.length() > board[0].length) {
                return Optional.empty();
            }
            if (direction == DIRECTION.DOWN &&
                row + word.length() > board.length) {
                return Optional.empty();
            }

            // check if there is an existing letter at location -> if yes check if the word doesnt contain it
            Tile existingTile = board[currentRow][currentColumn];
            if (existingTile instanceof LetterTile) {
                char existingChar = ((LetterTile) existingTile).letter();

                // if existing Tile is a wrong position -> fail
                if (word.charAt(i) != existingChar) {
                    return Optional.empty();
                }
                usedExistingLetter = true;
            }
            // else consume letter
            else {
                int remaining = input.getOrDefault(word.charAt(i), 0);

                if (remaining > 0) {
                    if (remaining == 1) {
                        input.remove(word.charAt(i));
                    } else {
                        input.put(word.charAt(i), remaining - 1);
                    }
                    wordToApply.add(new LetterTile(word.charAt(i), false));
                    consumedFromRack++;
                    String adjacantWord = findAdjacantWord(board, direction, currentRow, currentColumn, word.charAt(i));
                    if(adjacantWord.length() > 1){
                        if(isWordLegal(adjacantWord)){
                            adjacantWords.add(adjacantWord);
                        }else {
                            return Optional.empty();
                        }
                    }
                } else if (jokerCount > 0) {
                    wordToApply.add(new LetterTile(word.charAt(i), true));
                    jokerCount--;
                    String adjacantWord = findAdjacantWord(board, direction, currentRow, currentColumn, word.charAt(i));
                    if(adjacantWord.length() > 1) {
                        if (isWordLegal(adjacantWord)) {
                            adjacantWords.add(adjacantWord);
                        }else {
                            return Optional.empty();
                        }
                    }
                } else {
                    return Optional.empty();
                }
            }
        }


        // check if word uses existing letters, first move!!
        if (!usedExistingLetter) {
            if (!isFirstMove) {
                return Optional.empty();
            }
        }

        // check if at least one tile came from rack
        if (!(consumedFromRack > 0)) {
           return Optional.empty();
        }


        // check if there are tiles with chars directly after/ before word
        try {
            Tile tileBefore;
            Tile tileAfter;
            if (direction == DIRECTION.ACCROSS) {
                 tileBefore = board[row - 1][column];
                 tileAfter = board[row + wordLength][column];
            } else {
                 tileBefore = board[row][column - 1];
                 tileAfter = board[row][column + wordLength];
            }
            if(tileBefore instanceof LetterTile || tileAfter instanceof LetterTile){
                return Optional.empty();
            }
        } catch (Exception e){
            return Optional.empty();
        }



        // calculate points for move
        Tile[][] appliedBoard = Arrays.stream(board)
                .map(Tile[]::clone)
                .toArray(Tile[][]::new);

        int points = calculatePoints(board, adjacantWords, row, column, word);

        System.out.println(Arrays.deepToString(appliedBoard) + ".");

        Move move = new Move(appliedBoard, new Word(new ArrayList<>()), points);
        return Optional.of(move);
    }

    private int calculatePoints(Tile[][] board, ArrayList<String> adjacantWords, int row, int column, String word) {
        return 20;
    }

    private boolean isWordLegal(String adjacantWord) {
        return availableWords.getAvailableWords().contains(adjacantWord);
    }

    private String findAdjacantWord(Tile[][] board, DIRECTION direction, int currentRow, int currentColumn, char c) {
        int rowStep = direction == DIRECTION.ACCROSS ? 1 : 0;
        int columnStep = direction == DIRECTION.ACCROSS ? 0 : 1;
        StringBuilder word = new StringBuilder().append(c);

        int row = currentRow - rowStep;
        int column = currentColumn - columnStep;
        while (row >= 0 && column >= 0 && board[row][column] instanceof LetterTile letterTile) {
            word.insert(0, letterTile.letter());
            row -= rowStep;
            column -= columnStep;
        }

        row = currentRow + rowStep;
        column = currentColumn + columnStep;
        while (row < board.length
                && column < board[0].length
                && board[row][column] instanceof LetterTile letterTile) {
            word.append(letterTile.letter());
            row += rowStep;
            column += columnStep;
        }

        return word.toString();
    }


    private HashMap<Character, Integer> toHashmap(char[] inputChars) {
        HashMap<Character, Integer> chars = new HashMap<>();
        for (Character c : inputChars) {
            chars.merge(c, 1, Integer::sum);
        }
        return chars;
    }
}
