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
    private static final int BINGOPOINTS = 50;
    private static final int RACKSIZE = 7;

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
                        matchWord(board, (HashMap<Character, Integer>) input.clone(), word, direction, r, c, isFirstMove)
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

    private Optional<Move> matchWord(Tile[][] board, HashMap<Character, Integer> input, String wordString, DIRECTION direction, int row, int column, boolean isFirstMove) {
        ArrayList<LetterTile> wordToApply = new ArrayList<>();
        ArrayList<Word> adjacantWords = new ArrayList<>();


        int consumedFromRack = 0;
        int usedExistingLetters = 0;

        int jokerCount = 0;
        if (input.containsKey('?')) {
            jokerCount = input.get('?');
        }

        // how long is word if longer then board. length -> cancel
        int wordLength = wordString.length();
        if(wordLength > board.length && wordLength > board[0].length){
            return Optional.empty();
        }

        // check tiles for word length and direction, if there is a letter and not in word -> cancel

        for (int i = 0; i < wordString.length(); i++) {
            int currentRow = direction == DIRECTION.ACCROSS ? row : row + i;
            int currentColumn = direction == DIRECTION.ACCROSS ? column + i : column;

            // check if position is in the board
            if (currentRow >= board.length || currentColumn >= board[0].length) {
                return Optional.empty();
            }

            if (direction == DIRECTION.ACCROSS &&
                column + wordString.length() > board[0].length) {
                return Optional.empty();
            }
            if (direction == DIRECTION.DOWN &&
                row + wordString.length() > board.length) {
                return Optional.empty();
            }

            // check if there is an existing letter at location -> if yes check if the word doesnt contain it
            Tile existingTile = board[currentRow][currentColumn];
            if (existingTile instanceof LetterTile) {
                char existingChar = ((LetterTile) existingTile).letter();

                // if existing Tile is a wrong position -> fail
                if (wordString.charAt(i) != existingChar) {
                    return Optional.empty();
                }
                usedExistingLetters++;
                wordToApply.add((LetterTile) existingTile);
            }
            // else consume letter
            else {
                int remaining = input.getOrDefault(wordString.charAt(i), 0);

                LetterTile placedTile;
                if (remaining > 0) {
                    if (remaining == 1) {
                        input.remove(wordString.charAt(i));
                    } else {
                        input.put(wordString.charAt(i), remaining - 1);
                    }
                    placedTile = new LetterTile(wordString.charAt(i), false);
                } else if (jokerCount > 0) {
                    jokerCount--;
                    placedTile = new LetterTile(wordString.charAt(i), true);
                } else {
                    return Optional.empty();
                }

                wordToApply.add(placedTile);
                consumedFromRack++;

                List<LetterTile> adjacantWordLetters = findAdjacantWord(board, direction, currentRow, currentColumn, placedTile);
                if (adjacantWordLetters.size() > 1) {
                    if (!isWordLegal(adjacantWordLetters)) {
                        return Optional.empty();
                    }
                    Word adjacantWord = new Word(adjacantWordLetters, currentRow, currentColumn, direction);
                    adjacantWords.add(adjacantWord);
                }
            }
        }


        // check if word uses existing letters, first move!!
        if (!(usedExistingLetters > 0) && adjacantWords.isEmpty()) {
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
            if (direction == DIRECTION.DOWN) {
                 tileBefore = board[row - 1][column];
                 tileAfter = board[row + wordLength][column];
            } else {
                 tileBefore = board[row][column - 1];
                 tileAfter = board[row][column + wordLength];
            }
            if(tileBefore instanceof LetterTile || tileAfter instanceof LetterTile){
                return Optional.empty();
            }
        } catch (Exception _){
        }



        // calculate points for move
        Tile[][] appliedBoard = Arrays.stream(board)
                .map(Tile[]::clone)
                .toArray(Tile[][]::new);

        Word word = new Word(wordToApply, row, column, direction);

        int points = calculatePoints(board, adjacantWords, row, column, word, direction);
        if(consumedFromRack == RACKSIZE){
            points += BINGOPOINTS;
        }

        applyWordToBoard(appliedBoard, row, column, direction, wordToApply);

        Move move = createMove(appliedBoard, points, word);
        return Optional.of(move);
    }

    private Move createMove(Tile[][] appliedBoard, int points, Word word) {
        return new Move(appliedBoard, word, points);
    }

    private void applyWordToBoard(Tile[][] appliedBoard, int row, int column, DIRECTION direction, ArrayList<LetterTile> wordToApply) {
        int rowStep = direction == DIRECTION.ACCROSS ? 0 : 1;
        int columnStep = direction == DIRECTION.ACCROSS ? 1 : 0;

        for (LetterTile tile : wordToApply) {
            appliedBoard[row][column] = tile;
            row += rowStep;
            column += columnStep;
        }
    }

    private int calculatePoints(Tile[][] board, ArrayList<Word> adjacantWords, int r, int c, Word word, DIRECTION direction) {
        int pointsForMove = 0;
        pointsForMove += calculatePointForWord(board, r, c, word, direction);

        for(Word adjacantWord : adjacantWords){
            DIRECTION dir = direction == DIRECTION.ACCROSS ? DIRECTION.DOWN : DIRECTION.ACCROSS;
            pointsForMove += calculatePointForWord(board, word.getRow(), word.getColumn(), adjacantWord, dir);
        }

        return pointsForMove;
    }

    private static int calculatePointForWord(Tile[][] board, int r, int c, Word word, DIRECTION direction) {
        int points = 0;
        int wordMultiplier = 1;


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
        }
        points *= wordMultiplier;
        return points;
    }

    private boolean isWordLegal(List<LetterTile> adjacantWord) {
        return availableWords.getAvailableWords().contains(toWord(adjacantWord));
    }

    private static String toWord(List<LetterTile> tiles) {
        StringBuilder word = new StringBuilder(tiles.size());
        for (LetterTile tile : tiles) {
            word.append(tile.letter());
        }
        return word.toString();
    }

    private List<LetterTile> findAdjacantWord(Tile[][] board, DIRECTION direction, int currentRow, int currentColumn, LetterTile placedTile) {
        int rowStep = direction == DIRECTION.ACCROSS ? 1 : 0;
        int columnStep = direction == DIRECTION.ACCROSS ? 0 : 1;
        LinkedList<LetterTile> wordLetters = new LinkedList<>();
        wordLetters.add(placedTile);

        int row = currentRow - rowStep;
        int column = currentColumn - columnStep;
        while (row >= 0 && column >= 0 && board[row][column] instanceof LetterTile letterTile) {
            wordLetters.addFirst(letterTile);
            row -= rowStep;
            column -= columnStep;
        }

        row = currentRow + rowStep;
        column = currentColumn + columnStep;
        while (row < board.length
                && column < board[0].length
                && board[row][column] instanceof LetterTile letterTile) {
            wordLetters.addLast(letterTile);
            row += rowStep;
            column += columnStep;
        }

        return wordLetters;
    }


    private HashMap<Character, Integer> toHashmap(char[] inputChars) {
        HashMap<Character, Integer> chars = new HashMap<>();
        for (Character c : inputChars) {
            chars.merge(c, 1, Integer::sum);
        }
        return chars;
    }
}
