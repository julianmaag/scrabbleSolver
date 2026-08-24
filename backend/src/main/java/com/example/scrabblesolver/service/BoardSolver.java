package com.example.scrabblesolver.service;

import com.example.scrabblesolver.model.*;
import com.example.scrabblesolver.model.tiles.LetterTile;
import com.example.scrabblesolver.model.tiles.SpecialTile;
import com.example.scrabblesolver.model.tiles.Tile;
import com.example.scrabblesolver.repository.IAvailableWords;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.scrabblesolver.repository.LetterValues.getLetterValue;

@Service
public class BoardSolver implements ISolverService {
    private final IAvailableWords availableWords;
    private static final int SOLUTION_COUNT = 10;
    private static final int BINGO_POINTS = 50;
    private static final int RACK_SIZE = 7;

    public BoardSolver(IAvailableWords availableWords) {
        this.availableWords = availableWords;
    }

    @Override
    public List<Move> getSolutions(String inputChars, Tile[][] board) {
        List<Move> possibleMoves = new ArrayList<>();
        boolean isFirstMove = isBoardEmpty(board);
        Rack rack = Rack.of(inputChars);

        for(String word : availableWords.getAvailableWords()){
            for(DIRECTION direction : DIRECTION.values()) {
                for(int r = 0; r < board.length ; r++){
                    for(int c = 0; c < board[0].length; c++){
                        matchWord(board, rack.copy(), word, direction, r, c, isFirstMove)
                                .ifPresent(possibleMoves::add);
                    }
                }
            }
        }

        Collections.sort(possibleMoves);
        return possibleMoves.subList(
                0,
                Math.min(SOLUTION_COUNT, possibleMoves.size())
        );
    }

    private Optional<Move> matchWord(Tile[][] board, Rack rack, String wordString, DIRECTION direction, int row, int column, boolean isFirstMove) {
        ArrayList<LetterTile> wordToApply = new ArrayList<>();
        ArrayList<Word> adjacentWords = new ArrayList<>();
        ArrayList<Placement> placements = new ArrayList<>();

        int usedExistingLetters = 0;


        // how long is word if longer then board. length -> cancel
        int wordLength = wordString.length();
        if(wordLength > board.length && wordLength > board[0].length){
            return Optional.empty();
        }

        // check tiles for word length and direction, if there is a letter and not in word -> cancel

        for (int i = 0; i < wordString.length(); i++) {
            int currentRow = direction == DIRECTION.ACROSS ? row : row + i;
            int currentColumn = direction == DIRECTION.ACROSS ? column + i : column;

            // check if position is in the board
            if (currentRow >= board.length || currentColumn >= board[0].length) {
                return Optional.empty();
            }

            if (direction == DIRECTION.ACROSS &&
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
                char letter = wordString.charAt(i);
                char rackTile = rack.consume(letter);
                if (rackTile == Rack.NONE) {
                    return Optional.empty();
                }

                Placement placement = new Placement(currentRow, currentColumn, rackTile, letter);
                LetterTile placedTile = placement.toTile();
                placements.add(placement);
                wordToApply.add(placedTile);

                List<LetterTile> adjacentWordLetters = findAdjacentWord(board, direction, currentRow, currentColumn, placedTile);
                if (adjacentWordLetters.size() > 1) {
                    if (!isWordLegal(adjacentWordLetters)) {
                        return Optional.empty();
                    }
                    Word adjacantWord = new Word(adjacentWordLetters, currentRow, currentColumn, direction);
                    adjacentWords.add(adjacantWord);
                }
            }
        }


        // check if word uses existing letters, first move!!
        if (!(usedExistingLetters > 0) && adjacentWords.isEmpty()) {
            if (!isFirstMove) {
                return Optional.empty();
            } else{
                int centerRow = board.length / 2;
                int centerColumn = board[0].length / 2;

                boolean coversCenter = direction == DIRECTION.ACROSS
                        ? row == centerRow && column <= centerColumn && centerColumn < column + wordLength
                        : column == centerColumn && row <= centerRow && centerRow < row + wordLength;
                if(!coversCenter){
                    return Optional.empty();
                }
            }
        }

        // check if at least one tile came from rack
        if (placements.isEmpty()) {
            return Optional.empty();
        }


        // check if there are tiles with chars directly after/ before word
        try {
            Tile tileBefore;
            if (direction == DIRECTION.DOWN) {
                 tileBefore = board[row - 1][column];
            } else {
                 tileBefore = board[row][column - 1];
            }
            if(tileBefore instanceof LetterTile){
                return Optional.empty();
            }
        }
        catch (Exception _){// exception would be thrown because of out of bounds, this is fine as if behind/ in front of the letter is nothing its also valid
        }
        try{
            Tile tileAfter;
            if (direction == DIRECTION.DOWN) {
                tileAfter = board[row + wordLength][column];
            } else {
                tileAfter = board[row][column + wordLength];
            }
            if(tileAfter instanceof LetterTile){
                return Optional.empty();
            }
        } catch (Exception _){// exception would be thrown because of out of bounds, this is fine as if behind/ in front of the letter is nothing its also valid
        }

        // calculate points for move
        Tile[][] appliedBoard = Arrays.stream(board)
                .map(Tile[]::clone)
                .toArray(Tile[][]::new);

        Word word = new Word(wordToApply, row, column, direction);

        int points = calculatePoints(board, adjacentWords, row, column, word, direction);
        if (placements.size() == RACK_SIZE) {
            points += BINGO_POINTS;
        }

        applyWordToBoard(appliedBoard, row, column, direction, wordToApply);

        Move move = new Move(appliedBoard, word, points, placements);
        return Optional.of(move);
    }


    private void applyWordToBoard(Tile[][] appliedBoard, int row, int column, DIRECTION direction, ArrayList<LetterTile> wordToApply) {
        int rowStep = direction == DIRECTION.ACROSS ? 0 : 1;
        int columnStep = direction == DIRECTION.ACROSS ? 1 : 0;

        for (LetterTile tile : wordToApply) {
            appliedBoard[row][column] = tile;
            row += rowStep;
            column += columnStep;
        }
    }

    private int calculatePoints(Tile[][] board, ArrayList<Word> adjacentWords, int r, int c, Word word, DIRECTION direction) {
        int pointsForMove = 0;
        pointsForMove += calculatePointForWord(board, r, c, word, direction);

        for(Word adjacantWord : adjacentWords){
            DIRECTION dir = direction == DIRECTION.ACROSS ? DIRECTION.DOWN : DIRECTION.ACROSS;
            pointsForMove += calculatePointForWord(board, word.getRow(), word.getColumn(), adjacantWord, dir);
        }

        return pointsForMove;
    }

    private static int calculatePointForWord(Tile[][] board, int r, int c, Word word, DIRECTION direction) {
        int points = 0;
        int wordMultiplier = 1;


        for (int i = 0; i < word.getLength(); i++) {
            int row = direction == DIRECTION.ACROSS ? r : r + i;
            int column = direction == DIRECTION.ACROSS ? c + i : c;
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

    private boolean isWordLegal(List<LetterTile> adjacentWord) {
        return availableWords.getAvailableWords().contains(toWord(adjacentWord));
    }

    private static String toWord(List<LetterTile> tiles) {
        StringBuilder word = new StringBuilder(tiles.size());
        for (LetterTile tile : tiles) {
            word.append(tile.letter());
        }
        return word.toString();
    }

    private List<LetterTile> findAdjacentWord(Tile[][] board, DIRECTION direction, int currentRow, int currentColumn, LetterTile placedTile) {
        int rowStep = direction == DIRECTION.ACROSS ? 1 : 0;
        int columnStep = direction == DIRECTION.ACROSS ? 0 : 1;
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

    private boolean isBoardEmpty(Tile[][] board) {
        for (Tile[] row : board) {
            for (Tile tile : row) {
                if (tile instanceof LetterTile) {
                    return false;
                }
            }
        }
        return true;
    }
}
