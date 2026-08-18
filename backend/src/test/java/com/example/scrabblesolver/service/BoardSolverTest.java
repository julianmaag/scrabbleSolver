package com.example.scrabblesolver.service;

import com.example.scrabblesolver.model.Move;
import com.example.scrabblesolver.model.tiles.LetterTile;
import com.example.scrabblesolver.model.tiles.SpecialTile;
import com.example.scrabblesolver.model.tiles.Tile;
import com.example.scrabblesolver.repository.IAvailableWords;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardSolverTest {

    @Mock
    private IAvailableWords availableWords;

    @Test
    void getSolutionsReturnsNoMovesWhenDictionaryIsEmpty() {
        BoardSolver solver = solverWithWords();

        List<Move> moves = solver.getSolutions("cat", board(5, 5));

        assertTrue(moves.isEmpty());
    }

    @Test
    void getSolutionsPlacesWordAcrossAndDownUsingExistingLetter() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);
        board[2][2] = new LetterTile('a');

        List<Move> moves = solver.getSolutions("ct", board);

        assertTrue(findMove(moves, letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).isPresent());
        assertTrue(findMove(moves, letters(
                cell(1, 2, 'c'), cell(2, 2, 'a'), cell(3, 2, 't'))).isPresent());
    }

    @Test
    void getSolutionsConsumesRepeatedRackLettersOneAtATime() {
        BoardSolver solver = solverWithWords("all");
        Tile[][] board = board(5, 5);
        board[2][1] = new LetterTile('a');

        List<Move> moves = solver.getSolutions("ll", board);

        assertTrue(findMove(moves, letters(
                cell(2, 1, 'a'), cell(2, 2, 'l'), cell(2, 3, 'l'))).isPresent());
    }

    @Test
    void getSolutionsUsesJokerWhenRequiredLetterIsMissing() {
        BoardSolver solver = solverWithWords("axe");
        Tile[][] board = board(5, 5);
        board[2][1] = new LetterTile('a');

        Move move = findMove(solver.getSolutions("?e", board), letters(
                cell(2, 1, 'a'), cell(2, 2, 'x'), cell(2, 3, 'e'))).orElseThrow();

        LetterTile joker = assertInstanceOf(LetterTile.class, move.getBoard()[2][2]);
        assertTrue(joker.joker());
        assertFalse(assertInstanceOf(LetterTile.class, move.getBoard()[2][3]).joker());
        assertEquals(2, move.getPoints());
    }

    @Test
    void getSolutionsCanConsumeMultipleJokers() {
        BoardSolver solver = solverWithWords("add");
        Tile[][] board = board(5, 5);
        board[2][1] = new LetterTile('a');

        Move move = findMove(solver.getSolutions("??", board), letters(
                cell(2, 1, 'a'), cell(2, 2, 'd'), cell(2, 3, 'd'))).orElseThrow();

        assertTrue(assertInstanceOf(LetterTile.class, move.getBoard()[2][2]).joker());
        assertTrue(assertInstanceOf(LetterTile.class, move.getBoard()[2][3]).joker());
    }

    @Test
    void getSolutionsRejectsPlacementWhenRackCannotSupplyRequiredLetter() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);
        board[2][2] = new LetterTile('a');

        List<Move> moves = solver.getSolutions("c", board);

        assertTrue(findMove(moves, letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).isEmpty());
    }

    @Test
    void getSolutionsRejectsPlacementThatConflictsWithExistingLetter() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(3, 3);
        board[1][1] = new LetterTile('x');

        List<Move> moves = solver.getSolutions("cat", board);

        assertTrue(findMove(moves, letters(
                cell(1, 0, 'c'), cell(1, 1, 'a'), cell(1, 2, 't'))).isEmpty());
    }

    @Test
    void getSolutionsRejectsPlacementThatUsesNoRackTiles() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(3, 3);
        board[1][0] = new LetterTile('c');
        board[1][1] = new LetterTile('a');
        board[1][2] = new LetterTile('t');

        List<Move> moves = solver.getSolutions("", board);

        assertTrue(moves.isEmpty());
    }

    @Test
    void getSolutionsRejectsMainWordWithLetterImmediatelyBeforeOrAfterIt() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);
        board[2][0] = new LetterTile('x');
        board[2][2] = new LetterTile('a');
        board[2][4] = new LetterTile('s');

        List<Move> moves = solver.getSolutions("ct", board);

        assertTrue(findMove(moves, letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).isEmpty());
    }

    @Test
    void getSolutionsRequiresFirstMoveToCoverCenterSquare() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);

        List<Move> moves = solver.getSolutions("cat", board);

        assertFalse(moves.isEmpty());
        assertTrue(moves.stream().allMatch(move -> move.getBoard()[2][2] instanceof LetterTile));
    }

    @Test
    void getSolutionsSupportsRectangularBoardsAndRejectsOutOfBoundsPlacements() {
        BoardSolver solver = solverWithWords("late");
        Tile[][] board = board(3, 5);
        board[1][2] = new LetterTile('a');

        List<Move> moves = solver.getSolutions("lte", board);

        assertTrue(findMove(moves, letters(
                cell(1, 1, 'l'), cell(1, 2, 'a'), cell(1, 3, 't'), cell(1, 4, 'e'))).isPresent());
    }

    @Test
    void getSolutionsRejectsInvalidPerpendicularWord() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);
        board[1][2] = new LetterTile('h');

        List<Move> moves = solver.getSolutions("cat", board);

        assertTrue(findMove(moves, letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).isEmpty());
    }

    @Test
    void getSolutionsAcceptsValidPerpendicularWordAndScoresBothWords() {
        BoardSolver solver = solverWithWords("cat", "ha");
        Tile[][] board = board(5, 5);
        board[1][2] = new LetterTile('h');

        Move move = findMove(solver.getSolutions("cat", board), letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).orElseThrow();

        assertEquals(10, move.getPoints());
    }

    @Test
    void getSolutionsAppliesPremiumsOnlyToNewTiles() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);
        board[2][1] = SpecialTile.LETTER2;
        board[2][2] = new LetterTile('a');
        board[2][3] = SpecialTile.WORD2;

        Move move = findMove(solver.getSolutions("ct", board), letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).orElseThrow();

        assertEquals(16, move.getPoints());
    }

    @Test
    void getSolutionsAddsBingoBonusWhenAllSevenRackTilesAreUsed() {
        BoardSolver solver = solverWithWords("another", "at");
        Tile[][] board = board(9, 9);
        board[3][4] = new LetterTile('a');

        Move move = findMove(solver.getSolutions("another", board), letters(
                cell(4, 1, 'a'), cell(4, 2, 'n'), cell(4, 3, 'o'), cell(4, 4, 't'),
                cell(4, 5, 'h'), cell(4, 6, 'e'), cell(4, 7, 'r'))).orElseThrow();

        assertEquals(62, move.getPoints());
    }

    @Test
    void getSolutionsDoesNotMutateInputBoard() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);
        LetterTile existing = new LetterTile('a');
        board[2][2] = existing;

        Move move = findMove(solver.getSolutions("ct", board), letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).orElseThrow();

        assertEquals(SpecialTile.NONE, board[2][1]);
        assertEquals(existing, board[2][2]);
        assertEquals(SpecialTile.NONE, board[2][3]);
        assertNotSame(board, move.getBoard());
        assertNotSame(board[2], move.getBoard()[2]);
    }

    @Test
    void getSolutionsReturnsAtMostTenMovesOrderedByDescendingScore() {
        BoardSolver solver = solverWithWords(
                "ab", "ac", "ad", "af", "ag", "ah", "aj", "ak", "am", "aq", "ax", "az");
        Tile[][] board = board(5, 5);
        board[2][2] = new LetterTile('a');

        List<Move> moves = solver.getSolutions("bcdfghjkmqxz", board);

        assertEquals(10, moves.size());
        for (int index = 1; index < moves.size(); index++) {
            assertTrue(moves.get(index - 1).getPoints() >= moves.get(index).getPoints());
        }
    }

    private BoardSolver solverWithWords(String... words) {
        when(availableWords.getAvailableWords()).thenReturn(Set.of(words));
        return new BoardSolver(availableWords);
    }

    private static Tile[][] board(int rows, int columns) {
        Tile[][] board = new Tile[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                board[row][column] = SpecialTile.NONE;
            }
        }
        return board;
    }

    private static Map<Cell, Character> letters(Cell... cells) {
        return java.util.Arrays.stream(cells)
                .collect(java.util.stream.Collectors.toMap(cell -> cell, Cell::letter));
    }

    private static Cell cell(int row, int column, char letter) {
        return new Cell(row, column, letter);
    }

    private static Optional<Move> findMove(List<Move> moves, Map<Cell, Character> expectedLetters) {
        return moves.stream()
                .filter(move -> expectedLetters.entrySet().stream().allMatch(entry -> {
                    Cell cell = entry.getKey();
                    Tile tile = move.getBoard()[cell.row()][cell.column()];
                    return tile instanceof LetterTile letterTile
                            && letterTile.letter() == entry.getValue();
                }))
                .findFirst();
    }

    private record Cell(int row, int column, char letter) {
    }
}
