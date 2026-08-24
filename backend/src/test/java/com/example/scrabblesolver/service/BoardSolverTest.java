package com.example.scrabblesolver.service;

import com.example.scrabblesolver.model.DIRECTION;
import com.example.scrabblesolver.model.Move;
import com.example.scrabblesolver.model.Placement;
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

        LetterTile joker = assertInstanceOf(LetterTile.class, move.board()[2][2]);
        assertTrue(joker.joker());
        assertFalse(assertInstanceOf(LetterTile.class, move.board()[2][3]).joker());
        assertEquals(2, move.points());
    }

    @Test
    void getSolutionsCanConsumeMultipleJokers() {
        BoardSolver solver = solverWithWords("add");
        Tile[][] board = board(5, 5);
        board[2][1] = new LetterTile('a');

        Move move = findMove(solver.getSolutions("??", board), letters(
                cell(2, 1, 'a'), cell(2, 2, 'd'), cell(2, 3, 'd'))).orElseThrow();

        assertTrue(assertInstanceOf(LetterTile.class, move.board()[2][2]).joker());
        assertTrue(assertInstanceOf(LetterTile.class, move.board()[2][3]).joker());
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
        assertTrue(moves.stream().allMatch(move -> move.board()[2][2] instanceof LetterTile));
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

        assertEquals(10, move.points());
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

        assertEquals(16, move.points());
    }

    @Test
    void getSolutionsAddsBingoBonusWhenAllSevenRackTilesAreUsed() {
        BoardSolver solver = solverWithWords("another", "at");
        Tile[][] board = board(9, 9);
        board[3][4] = new LetterTile('a');

        Move move = findMove(solver.getSolutions("another", board), letters(
                cell(4, 1, 'a'), cell(4, 2, 'n'), cell(4, 3, 'o'), cell(4, 4, 't'),
                cell(4, 5, 'h'), cell(4, 6, 'e'), cell(4, 7, 'r'))).orElseThrow();

        assertEquals(62, move.points());
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
        assertNotSame(board, move.board());
        assertNotSame(board[2], move.board()[2]);
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
            assertTrue(moves.get(index - 1).points() >= moves.get(index).points());
        }
    }

    // ----- board geometry edge cases -----

    @Test
    void getSolutionsReturnsNoMovesWhenWordIsLongerThanBothBoardDimensions() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(2, 2);

        List<Move> moves = solver.getSolutions("cat", board);

        assertTrue(moves.isEmpty());
    }

    @Test
    void getSolutionsAcceptsWordThatExactlyFillsTheBoardWidth() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(3, 3);

        List<Move> moves = solver.getSolutions("cat", board);

        assertTrue(findMove(moves, letters(
                cell(1, 0, 'c'), cell(1, 1, 'a'), cell(1, 2, 't'))).isPresent());
    }

    @Test
    void getSolutionsRejectsDownWordThatWouldRunPastTheBottomEdge() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);
        board[4][2] = new LetterTile('c');
        board[4][3] = new LetterTile('x');

        List<Move> moves = solver.getSolutions("at", board);

        assertTrue(moves.isEmpty());
    }

    @Test
    void getSolutionsSupportsSingleColumnBoard() {
        BoardSolver solver = solverWithWords("at");
        Tile[][] board = board(5, 1);

        List<Move> moves = solver.getSolutions("at", board);

        assertTrue(findMove(moves, letters(
                cell(2, 0, 'a'), cell(3, 0, 't'))).isPresent());
    }

    @Test
    void getSolutionsPlacesWordTouchingTheTopEdgeWithoutIndexErrors() {
        BoardSolver solver = solverWithWords("at");
        Tile[][] board = board(3, 3);

        List<Move> moves = solver.getSolutions("at", board);

        assertTrue(findMove(moves, letters(
                cell(0, 1, 'a'), cell(1, 1, 't'))).isPresent());
    }

    @Test
    void getSolutionsReturnsNoMovesWhenBoardIsCompletelyFull() {
        BoardSolver solver = solverWithWords("at");
        Tile[][] board = board(3, 3);
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                board[row][column] = new LetterTile('a');
            }
        }

        List<Move> moves = solver.getSolutions("at", board);

        assertTrue(moves.isEmpty());
    }

    // ----- rack edge cases -----

    @Test
    void getSolutionsAcceptsUppercaseRackInput() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);
        board[2][2] = new LetterTile('a');

        List<Move> moves = solver.getSolutions("CT", board);

        assertTrue(findMove(moves, letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).isPresent());
    }

    @Test
    void getSolutionsReturnsNoMovesWhenRackIsEmpty() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);
        board[2][2] = new LetterTile('a');

        List<Move> moves = solver.getSolutions("", board);

        assertTrue(moves.isEmpty());
    }

    @Test
    void getSolutionsRejectsWordNeedingTheSameRackTileTwice() {
        BoardSolver solver = solverWithWords("aa");
        Tile[][] board = board(5, 5);

        List<Move> moves = solver.getSolutions("a", board);

        assertTrue(moves.isEmpty());
    }

    @Test
    void getSolutionsPrefersRealRackLetterOverJokerWhenBothAreAvailable() {
        BoardSolver solver = solverWithWords("aa");
        Tile[][] board = board(5, 5);

        Move move = findMove(solver.getSolutions("a?", board), letters(
                cell(2, 1, 'a'), cell(2, 2, 'a'))).orElseThrow();

        assertFalse(assertInstanceOf(LetterTile.class, move.board()[2][1]).joker());
        assertTrue(assertInstanceOf(LetterTile.class, move.board()[2][2]).joker());
    }

    @Test
    void getSolutionsIgnoresRackTilesThatAreNotNeeded() {
        BoardSolver solver = solverWithWords("at");
        Tile[][] board = board(5, 5);

        Move move = findMove(solver.getSolutions("atzzz", board), letters(
                cell(2, 1, 'a'), cell(2, 2, 't'))).orElseThrow();

        assertEquals(2, move.points());
    }

    @Test
    void getSolutionsDoesNotShareRackStateBetweenCandidatePlacements() {
        BoardSolver solver = solverWithWords("at");
        Tile[][] board = board(5, 5);

        List<Move> moves = solver.getSolutions("at", board);

        assertTrue(findMove(moves, letters(cell(2, 1, 'a'), cell(2, 2, 't'))).isPresent());
        assertTrue(findMove(moves, letters(cell(1, 2, 'a'), cell(2, 2, 't'))).isPresent());
    }

    // ----- connectivity edge cases -----

    @Test
    void getSolutionsRejectsDisconnectedWordOnNonEmptyBoard() {
        BoardSolver solver = solverWithWords("at");
        Tile[][] board = board(9, 9);
        board[0][0] = new LetterTile('z');

        List<Move> moves = solver.getSolutions("at", board);

        assertTrue(moves.isEmpty());
    }

    @Test
    void getSolutionsAcceptsWordConnectedOnlyByPerpendicularContact() {
        BoardSolver solver = solverWithWords("at", "za");
        Tile[][] board = board(5, 5);
        board[1][1] = new LetterTile('z');

        Move move = findMove(solver.getSolutions("at", board), letters(
                cell(2, 1, 'a'), cell(2, 2, 't'))).orElseThrow();

        assertEquals(13, move.points());
    }

    @Test
    void getSolutionsRejectsFirstMoveOffTheCenterSquare() {
        BoardSolver solver = solverWithWords("at");
        Tile[][] board = board(5, 5);

        List<Move> moves = solver.getSolutions("at", board);

        assertTrue(findMove(moves, letters(cell(0, 0, 'a'), cell(0, 1, 't'))).isEmpty());
    }

    @Test
    void getSolutionsAllowsFirstMovePlacedDownThroughTheCenter() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);

        List<Move> moves = solver.getSolutions("cat", board);

        assertTrue(findMove(moves, letters(
                cell(1, 2, 'c'), cell(2, 2, 'a'), cell(3, 2, 't'))).isPresent());
    }

    @Test
    void getSolutionsUsesLowerRightOfMiddleAsCenterOnEvenSizedBoards() {
        BoardSolver solver = solverWithWords("at");
        Tile[][] board = board(4, 4);

        List<Move> moves = solver.getSolutions("at", board);

        assertFalse(moves.isEmpty());
        assertTrue(moves.stream().allMatch(move -> move.board()[2][2] instanceof LetterTile));
    }

    @Test
    void getSolutionsExtendsAnExistingWordWithASingleRackTile() {
        BoardSolver solver = solverWithWords("cat", "cats");
        Tile[][] board = board(5, 5);
        board[2][0] = new LetterTile('c');
        board[2][1] = new LetterTile('a');
        board[2][2] = new LetterTile('t');

        List<Move> moves = solver.getSolutions("s", board);

        assertEquals(1, moves.size());
        assertEquals(6, moves.getFirst().points());
    }

    @Test
    void getSolutionsRejectsDownWordWithAnExistingLetterDirectlyAboveIt() {
        BoardSolver solver = solverWithWords("at");
        Tile[][] board = board(5, 5);
        board[1][2] = new LetterTile('z');

        List<Move> moves = solver.getSolutions("at", board);

        assertTrue(findMove(moves, letters(cell(2, 2, 'a'), cell(3, 2, 't'))).isEmpty());
    }

    // ----- perpendicular word edge cases -----

    @Test
    void getSolutionsValidatesPerpendicularWordFormedBelowThePlacedTile() {
        BoardSolver solver = solverWithWords("cat", "ah");
        Tile[][] board = board(5, 5);
        board[3][2] = new LetterTile('h');

        Move move = findMove(solver.getSolutions("cat", board), letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).orElseThrow();

        assertEquals(10, move.points());
    }

    @Test
    void getSolutionsValidatesPerpendicularWordSpanningBothSidesOfThePlacedTile() {
        BoardSolver solver = solverWithWords("cat", "hat");
        Tile[][] board = board(5, 5);
        board[1][2] = new LetterTile('h');
        board[3][2] = new LetterTile('t');

        Move move = findMove(solver.getSolutions("cat", board), letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).orElseThrow();

        assertEquals(11, move.points());
    }

    @Test
    void getSolutionsRejectsMoveWhenAnyPerpendicularWordIsInvalid() {
        BoardSolver solver = solverWithWords("cat", "ah");
        Tile[][] board = board(5, 5);
        board[3][2] = new LetterTile('h');
        board[3][3] = new LetterTile('z');

        List<Move> moves = solver.getSolutions("cat", board);

        assertTrue(findMove(moves, letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).isEmpty());
    }

    @Test
    void getSolutionsValidatesPerpendicularWordsForDownPlacements() {
        BoardSolver solver = solverWithWords("cat", "ha");
        Tile[][] board = board(5, 5);
        board[2][1] = new LetterTile('h');

        Move move = findMove(solver.getSolutions("cat", board), letters(
                cell(1, 2, 'c'), cell(2, 2, 'a'), cell(3, 2, 't'))).orElseThrow();

        assertEquals(10, move.points());
    }

    @Test
    void getSolutionsScoresJokerAsZeroInsidePerpendicularWords() {
        BoardSolver solver = solverWithWords("at", "za");
        Tile[][] board = board(5, 5);
        board[1][1] = new LetterTile('z');

        Move move = findMove(solver.getSolutions("?t", board), letters(
                cell(2, 1, 'a'), cell(2, 2, 't'))).orElseThrow();

        assertTrue(assertInstanceOf(LetterTile.class, move.board()[2][1]).joker());
        assertEquals(11, move.points());
    }

    // ----- scoring edge cases -----

    @Test
    void getSolutionsAppliesWordMultiplierEvenWhenTheJokerScoresZero() {
        BoardSolver solver = solverWithWords("at");
        Tile[][] board = board(5, 5);
        board[2][2] = SpecialTile.WORD2;

        Move move = findMove(solver.getSolutions("?t", board), letters(
                cell(2, 2, 'a'), cell(2, 3, 't'))).orElseThrow();

        assertTrue(assertInstanceOf(LetterTile.class, move.board()[2][2]).joker());
        assertEquals(2, move.points());
    }

    @Test
    void getSolutionsMultipliesStackedWordMultipliers() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);
        board[2][1] = SpecialTile.WORD2;
        board[2][3] = SpecialTile.WORD3;

        Move move = findMove(solver.getSolutions("cat", board), letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).orElseThrow();

        assertEquals(30, move.points());
    }

    @Test
    void getSolutionsAppliesLetterMultiplierBeforeWordMultiplier() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);
        board[2][1] = SpecialTile.LETTER3;
        board[2][3] = SpecialTile.WORD2;

        Move move = findMove(solver.getSolutions("cat", board), letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).orElseThrow();

        assertEquals(22, move.points());
    }

    @Test
    void getSolutionsDoesNotAwardBingoForSixRackTiles() {
        BoardSolver solver = solverWithWords("anther");
        Tile[][] board = board(9, 9);

        Move move = findMove(solver.getSolutions("anther", board), letters(
                cell(4, 1, 'a'), cell(4, 2, 'n'), cell(4, 3, 't'),
                cell(4, 4, 'h'), cell(4, 5, 'e'), cell(4, 6, 'r'))).orElseThrow();

        assertEquals(9, move.points());
    }

    // ----- result shape edge cases -----

    @Test
    void getSolutionsReturnsEveryMoveWhenFewerThanTenExist() {
        BoardSolver solver = solverWithWords("at");
        Tile[][] board = board(5, 5);

        List<Move> moves = solver.getSolutions("at", board);

        assertEquals(4, moves.size());
        assertTrue(moves.stream().allMatch(move -> move.points() == 2));
    }

    @Test
    void getSolutionsSkipsEmptyDictionaryEntries() {
        BoardSolver solver = solverWithWords("", "at");
        Tile[][] board = board(5, 5);

        List<Move> moves = solver.getSolutions("at", board);

        assertEquals(4, moves.size());
        assertTrue(moves.stream().noneMatch(move -> move.word().getName().isEmpty()));
    }

    @Test
    void getSolutionsReportsOnlyTheTilesTakenFromTheRackAsPlacements() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);
        board[2][2] = new LetterTile('a');

        Move move = findMove(solver.getSolutions("ct", board), letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).orElseThrow();

        assertEquals(
                List.of(new Placement(2, 1, 'c', 'c'), new Placement(2, 3, 't', 't')),
                move.placements());
    }

    @Test
    void getSolutionsMarksJokerPlacementsAsBlank() {
        BoardSolver solver = solverWithWords("at");
        Tile[][] board = board(5, 5);

        Move move = findMove(solver.getSolutions("?t", board), letters(
                cell(2, 2, 'a'), cell(2, 3, 't'))).orElseThrow();

        assertEquals(
                List.of(new Placement(2, 2, '?', 'a'), new Placement(2, 3, 't', 't')),
                move.placements());
        assertTrue(move.placements().getFirst().isBlank());
    }

    @Test
    void getSolutionsDescribesTheMainWordWithItsOriginAndDirection() {
        BoardSolver solver = solverWithWords("cat");
        Tile[][] board = board(5, 5);
        board[2][2] = new LetterTile('a');

        List<Move> moves = solver.getSolutions("ct", board);

        Move across = findMove(moves, letters(
                cell(2, 1, 'c'), cell(2, 2, 'a'), cell(2, 3, 't'))).orElseThrow();
        Move down = findMove(moves, letters(
                cell(1, 2, 'c'), cell(2, 2, 'a'), cell(3, 2, 't'))).orElseThrow();
        assertEquals("cat", across.word().getName());
        assertEquals(3, across.word().getLength());
        assertEquals(2, across.word().getRow());
        assertEquals(1, across.word().getColumn());
        assertEquals(DIRECTION.ACROSS, across.word().getDirection());
        assertEquals(DIRECTION.DOWN, down.word().getDirection());
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
                    Tile tile = move.board()[cell.row()][cell.column()];
                    return tile instanceof LetterTile letterTile
                            && letterTile.letter() == entry.getValue();
                }))
                .findFirst();
    }

    private record Cell(int row, int column, char letter) {
    }
}
