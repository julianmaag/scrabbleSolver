package com.example.scrabblesolver.service;

import com.example.scrabblesolver.helper.DefaultScrabbleBoard;
import com.example.scrabblesolver.model.DIRECTION;
import com.example.scrabblesolver.model.Move;
import com.example.scrabblesolver.model.Placement;
import com.example.scrabblesolver.model.tiles.LetterTile;
import com.example.scrabblesolver.model.tiles.Tile;
import com.example.scrabblesolver.repository.IAvailableWords;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.scrabblesolver.helper.DefaultScrabbleBoard.SIZE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives the wired-up solver with the production word list (NWL2023, ~190k words) and the real
 * 15x15 board. Each scenario performs exactly one solve and asserts both the result and the
 * runtime: {@code @Timeout} is the hard stop, the elapsed assertion fails a bit earlier and
 * reports how long the solve actually took.
 */
@SpringBootTest
@Timeout(value = 30, unit = SECONDS)
class BoardSolverRealDataTest {

    private static final Duration BUDGET = Duration.ofSeconds(20);
    private static final int CENTER = SIZE / 2;

    @Autowired
    private ISolverService solverService;

    @Autowired
    private IAvailableWords availableWords;

    @Test
    void solvesOpeningRackOnTheEmptyStandardBoardWithinBudget() {
        Tile[][] board = DefaultScrabbleBoard.empty();
        Tile[][] boardBefore = copyOf(board);

        long start = System.nanoTime();
        List<Move> moves = solverService.getSolutions("retinas", board);
        Duration elapsed = elapsedSince(start);

        assertWithinBudget(elapsed, "opening rack on the empty board");
        assertEquals(10, moves.size());
        assertOrderedByDescendingPoints(moves);
        assertTrue(moves.stream().allMatch(move -> isKnownWord(move.word().getName())));
        assertTrue(moves.stream().allMatch(move -> move.board()[CENTER][CENTER] instanceof LetterTile),
                "an opening move has to cover the centre square");
        assertEquals(7, moves.getFirst().placements().size(), "best opening move should be a bingo");
        assertTrue(moves.getFirst().points() > 50, "bingo bonus missing, scored " + moves.getFirst().points());
        assertUnchanged(boardBefore, board);
    }

    @Test
    void solvesAMidGameBoardWithinBudget() {
        Tile[][] board = midGameBoard();
        Tile[][] boardBefore = copyOf(board);

        long start = System.nanoTime();
        List<Move> moves = solverService.getSolutions("goldens", board);
        Duration elapsed = elapsedSince(start);

        assertWithinBudget(elapsed, "mid-game board");
        assertEquals(10, moves.size());
        assertOrderedByDescendingPoints(moves);
        assertTrue(moves.stream().allMatch(move -> isKnownWord(move.word().getName())));
        assertTrue(moves.stream().allMatch(this::touchesExistingTiles),
                "every move on a used board has to hook onto tiles that were already there");
        for (Move move : moves) {
            assertTrue(everyWordOnBoardIsKnown(move.board()),
                    "illegal word left on the board by " + move.word().getName());
        }
        assertUnchanged(boardBefore, board);
    }

    @Test
    void solvesRackWithTwoJokersWithinBudget() {
        Tile[][] board = midGameBoard();

        long start = System.nanoTime();
        List<Move> moves = solverService.getSolutions("??aeior", board);
        Duration elapsed = elapsedSince(start);

        assertWithinBudget(elapsed, "rack with two jokers");
        assertEquals(10, moves.size());
        assertTrue(moves.stream().allMatch(move -> isKnownWord(move.word().getName())));
        assertTrue(moves.stream()
                        .flatMap(move -> move.placements().stream())
                        .filter(Placement::isBlank)
                        .allMatch(placement -> placement.rackTile() == '?'),
                "a joker placement has to keep '?' as the rack tile it came from");
    }

    @Test
    void solvesACrowdedBoardWithinBudget() {
        Tile[][] board = crowdedBoard();

        long start = System.nanoTime();
        List<Move> moves = solverService.getSolutions("cetiuml", board);
        Duration elapsed = elapsedSince(start);

        assertWithinBudget(elapsed, "crowded board");
        assertFalse(moves.isEmpty());
        assertOrderedByDescendingPoints(moves);
        assertTrue(moves.stream().allMatch(move -> isKnownWord(move.word().getName())));
        assertTrue(moves.stream().allMatch(this::touchesExistingTiles));
        for (Move move : moves) {
            assertTrue(everyWordOnBoardIsKnown(move.board()),
                    "illegal word left on the board by " + move.word().getName());
        }
    }

    // ----- boards built from real words -----

    private static Tile[][] midGameBoard() {
        Tile[][] board = DefaultScrabbleBoard.empty();
        place(board, "quartz", CENTER, 4, DIRECTION.ACROSS);
        place(board, "toe", CENTER, 8, DIRECTION.DOWN);
        return board;
    }

    private static Tile[][] crowdedBoard() {
        Tile[][] board = DefaultScrabbleBoard.empty();
        place(board, "quartz", CENTER, 4, DIRECTION.ACROSS);
        place(board, "jokes", 3, 2, DIRECTION.ACROSS);
        place(board, "wharf", 11, 5, DIRECTION.ACROSS);
        place(board, "bandit", 2, 12, DIRECTION.DOWN);
        place(board, "vixen", 8, 1, DIRECTION.DOWN);
        return board;
    }

    private static void place(Tile[][] board, String word, int row, int column, DIRECTION direction) {
        for (int i = 0; i < word.length(); i++) {
            int currentRow = direction == DIRECTION.ACROSS ? row : row + i;
            int currentColumn = direction == DIRECTION.ACROSS ? column + i : column;
            board[currentRow][currentColumn] = new LetterTile(word.charAt(i));
        }
    }

    // ----- assertions -----

    private static Duration elapsedSince(long startNanos) {
        return Duration.ofNanos(System.nanoTime() - startNanos);
    }

    private static void assertWithinBudget(Duration elapsed, String scenario) {
        assertTrue(elapsed.compareTo(BUDGET) < 0,
                "%s took %d ms, budget is %d ms".formatted(
                        scenario, elapsed.toMillis(), BUDGET.toMillis()));
    }

    private static void assertOrderedByDescendingPoints(List<Move> moves) {
        for (int index = 1; index < moves.size(); index++) {
            assertTrue(moves.get(index - 1).points() >= moves.get(index).points(),
                    "moves are not ordered by descending points");
        }
    }

    private static void assertUnchanged(Tile[][] expected, Tile[][] actual) {
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                assertEquals(expected[row][column], actual[row][column],
                        "input board changed at %d/%d".formatted(row, column));
            }
        }
    }

    // ----- helpers -----

    private boolean isKnownWord(String word) {
        return availableWords.getAvailableWords().contains(word.toLowerCase());
    }

    private boolean everyWordOnBoardIsKnown(Tile[][] board) {
        for (DIRECTION direction : DIRECTION.values()) {
            for (String word : wordsOnBoard(board, direction)) {
                if (!isKnownWord(word)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static List<String> wordsOnBoard(Tile[][] board, DIRECTION direction) {
        List<String> words = new ArrayList<>();
        for (int line = 0; line < SIZE; line++) {
            StringBuilder current = new StringBuilder();
            for (int index = 0; index < SIZE; index++) {
                int row = direction == DIRECTION.ACROSS ? line : index;
                int column = direction == DIRECTION.ACROSS ? index : line;
                if (board[row][column] instanceof LetterTile letterTile) {
                    current.append(letterTile.letter());
                } else {
                    collect(words, current);
                }
            }
            collect(words, current);
        }
        return words;
    }

    private static void collect(List<String> words, StringBuilder current) {
        if (current.length() > 1) {
            words.add(current.toString());
        }
        current.setLength(0);
    }

    private boolean touchesExistingTiles(Move move) {
        if (move.placements().size() < move.word().getLength()) {
            return true;
        }
        Set<String> placed = move.placements().stream()
                .map(placement -> placement.row() + "/" + placement.column())
                .collect(Collectors.toSet());
        return move.placements().stream()
                .anyMatch(placement -> hasNeighbourOutside(move, placement, placed));
    }

    private static boolean hasNeighbourOutside(Move move, Placement placement, Set<String> placed) {
        int[][] offsets = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] offset : offsets) {
            int row = placement.row() + offset[0];
            int column = placement.column() + offset[1];
            if (row < 0 || column < 0 || row >= SIZE || column >= SIZE) {
                continue;
            }
            if (move.board()[row][column] instanceof LetterTile && !placed.contains(row + "/" + column)) {
                return true;
            }
        }
        return false;
    }

    private static Tile[][] copyOf(Tile[][] board) {
        Tile[][] copy = new Tile[board.length][];
        for (int row = 0; row < board.length; row++) {
            copy[row] = board[row].clone();
        }
        return copy;
    }
}
