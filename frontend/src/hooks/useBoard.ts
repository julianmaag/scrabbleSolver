import { useCallback, useMemo, useState } from 'react';
import {
  BOARD_SIZE,
  CENTER_INDEX,
  emptyBoard,
  isBoardEmpty,
  toColumn,
  toIndex,
  toRow,
  type BoardLetter,
  type BoardState,
} from '@/types/board';
import type { Direction, PlacedTile } from '@/types/move';
import { letterSchema } from '@/types/validChar';

/** Where the next typed letter lands, and which way the cursor then travels. */
export type Cursor = {
  readonly index: number;
  readonly direction: Direction;
};

/** Letters and cursor move together, so they are one state rather than two. */
type BoardModel = {
  readonly letters: BoardState;
  readonly cursor: Cursor;
};

const INITIAL: BoardModel = {
  letters: emptyBoard(),
  cursor: { index: CENTER_INDEX, direction: 'ACROSS' },
};

/** The neighbouring square along `direction`, or `null` at the edge of the board. */
function step(index: number, direction: Direction, forwards: boolean): number | null {
  const delta = forwards ? 1 : -1;
  const row = direction === 'DOWN' ? toRow(index) + delta : toRow(index);
  const column = direction === 'ACROSS' ? toColumn(index) + delta : toColumn(index);

  if (row < 0 || row >= BOARD_SIZE || column < 0 || column >= BOARD_SIZE) return null;
  return toIndex(row, column);
}

function replaceAt(letters: BoardState, index: number, cell: BoardLetter | null): BoardState {
  return letters.map((current, at) => (at === index ? cell : current));
}

/** Owns the board letters and the typing cursor, plus the rules for changing them. */
export function useBoard() {
  const [{ letters, cursor }, setModel] = useState<BoardModel>(INITIAL);

  /** Clicking the square the cursor is already on turns it, which is how you pick a direction. */
  const focusCell = useCallback((index: number) => {
    setModel((model) => ({
      ...model,
      cursor: {
        index,
        direction:
          model.cursor.index === index
            ? model.cursor.direction === 'ACROSS'
              ? 'DOWN'
              : 'ACROSS'
            : model.cursor.direction,
      },
    }));
  }, []);

  /** Arrow-key movement. The axis moved along also becomes the typing direction. */
  const nudgeCursor = useCallback((rowDelta: number, columnDelta: number) => {
    setModel((model) => {
      const row = toRow(model.cursor.index) + rowDelta;
      const column = toColumn(model.cursor.index) + columnDelta;
      if (row < 0 || row >= BOARD_SIZE || column < 0 || column >= BOARD_SIZE) return model;
      return {
        ...model,
        cursor: { index: toIndex(row, column), direction: rowDelta === 0 ? 'ACROSS' : 'DOWN' },
      };
    });
  }, []);

  const type = useCallback((char: string) => {
    const parsed = letterSchema.safeParse(char);
    if (!parsed.success) return;

    setModel(({ letters: current, cursor: at }) => ({
      letters: replaceAt(current, at.index, { letter: parsed.data, joker: false }),
      cursor: { ...at, index: step(at.index, at.direction, true) ?? at.index },
    }));
  }, []);

  /** Steps back onto the previous square and clears it, so Backspace undoes the last letter. */
  const deleteBack = useCallback(() => {
    setModel(({ letters: current, cursor: at }) => {
      const target = step(at.index, at.direction, false) ?? at.index;
      return {
        letters: replaceAt(current, target, null),
        cursor: { ...at, index: target },
      };
    });
  }, []);

  /** Clears one square and puts the cursor on it, so a mis-typed letter is one click to fix. */
  const clearCell = useCallback((index: number) => {
    setModel((model) => ({
      letters: replaceAt(model.letters, index, null),
      cursor: { ...model.cursor, index },
    }));
  }, []);

  const clear = useCallback(() => setModel(INITIAL), []);

  /** Commits a solved move: only the tiles the player lays down are new. */
  const applyPlacements = useCallback((placements: readonly PlacedTile[]) => {
    setModel((model) => {
      const next = [...model.letters];
      for (const { row, column, letter, joker } of placements) {
        next[toIndex(row, column)] = { letter, joker };
      }
      return { ...model, letters: next };
    });
  }, []);

  const isEmpty = useMemo(() => isBoardEmpty(letters), [letters]);

  return {
    letters,
    cursor,
    focusCell,
    nudgeCursor,
    type,
    deleteBack,
    clearCell,
    clear,
    applyPlacements,
    isEmpty,
  };
}
