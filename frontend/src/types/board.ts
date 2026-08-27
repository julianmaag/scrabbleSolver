/** The static board geometry, and the mutable letters played onto it. */

export const BOARD_SIZE = 15;

export const CELL_COUNT = BOARD_SIZE * BOARD_SIZE;

/** Index of the centre square, which the very first move has to cover. */
export const CENTER_INDEX = Math.floor(CELL_COUNT / 2);

/** Mirrors the backend `SpecialTile` enum — these names go on the wire. */
export type Premium = 'NONE' | 'LETTER2' | 'LETTER3' | 'WORD2' | 'WORD3';

/**
 * The premium squares of a standard 15x15 board, mirroring the backend
 * `DefaultScrabbleBoard.BOARD`. One character per square:
 * `T` triple word, `D` double word, `t` triple letter, `d` double letter, `.` plain.
 */
const PREMIUM_ROWS = [
  'T..d...T...d..T',
  '.D...t...t...D.',
  '..D...d.d...D..',
  'd..D...d...D..d',
  '....D.....D....',
  '.t...t...t...t.',
  '..d...d.d...d..',
  'T..d...D...d..T',
  '..d...d.d...d..',
  '.t...t...t...t.',
  '....D.....D....',
  'd..D...d...D..d',
  '..D...d.d...D..',
  '.D...t...t...D.',
  'T..d...T...d..T',
] as const;

const PREMIUM_BY_SYMBOL: Readonly<Record<string, Premium>> = {
  T: 'WORD3',
  D: 'WORD2',
  t: 'LETTER3',
  d: 'LETTER2',
  '.': 'NONE',
};

/** Premium of every square, flat and indexed the same way as `BoardState`. */
export const PREMIUM_LAYOUT: readonly Premium[] = PREMIUM_ROWS.flatMap((row) =>
  [...row].map((symbol) => PREMIUM_BY_SYMBOL[symbol]),
);

/** Short label for the premium squares; plain squares get nothing. */
export const PREMIUM_LABELS: Readonly<Record<Premium, string>> = {
  NONE: '',
  LETTER2: '2L',
  LETTER3: '3L',
  WORD2: '2W',
  WORD3: '3W',
};

/** A letter sitting on the board. `joker` means it was played from a blank tile. */
export type BoardLetter = {
  readonly letter: string;
  readonly joker: boolean;
};

/**
 * The played letters, flat and indexed by `toIndex(row, column)`. Flat rather than nested
 * keeps immutable updates to a single `map`, and 225 entries makes the cost irrelevant.
 */
export type BoardState = readonly (BoardLetter | null)[];

export function emptyBoard(): BoardState {
  return Array.from({ length: CELL_COUNT }, () => null);
}

export function toIndex(row: number, column: number): number {
  return row * BOARD_SIZE + column;
}

export function toRow(index: number): number {
  return Math.floor(index / BOARD_SIZE);
}

export function toColumn(index: number): number {
  return index % BOARD_SIZE;
}

export function isBoardEmpty(board: BoardState): boolean {
  return board.every((cell) => cell === null);
}
