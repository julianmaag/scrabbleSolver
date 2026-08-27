import {
  BOARD_SIZE,
  PREMIUM_LAYOUT,
  toIndex,
  type BoardState,
  type Premium,
} from '@/types/board';
import { solveResponseSchema, type Move } from '@/types/move';
import type { Rack } from '@/types/rack';

const SOLVE_ENDPOINT = '/api/solve';

/** Mirrors the backend `CellDto`. */
type CellPayload = {
  letter: string | null;
  joker: boolean;
  premium: Premium;
};

/**
 * The board as the backend wants it: a 15x15 grid where a square either holds a letter or
 * offers a premium. The backend ignores the premium of an occupied square, which is correct —
 * premiums only score for tiles played this turn.
 */
function toBoardPayload(board: BoardState): CellPayload[][] {
  return Array.from({ length: BOARD_SIZE }, (_, row) =>
    Array.from({ length: BOARD_SIZE }, (_, column) => {
      const index = toIndex(row, column);
      const cell = board[index];
      return {
        letter: cell?.letter ?? null,
        joker: cell?.joker ?? false,
        premium: PREMIUM_LAYOUT[index],
      };
    }),
  );
}

/** Asks the backend for the best legal moves playable from this rack onto this board. */
export async function solve(
  rack: Rack,
  board: BoardState,
  signal?: AbortSignal,
): Promise<Move[]> {
  const response = await fetch(SOLVE_ENDPOINT, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ rack: rack.join(''), board: toBoardPayload(board) }),
    signal,
  });

  if (!response.ok) {
    throw new Error(`Solver request failed with status ${response.status}`);
  }

  // Parse instead of cast: the network is a trust boundary.
  return solveResponseSchema.parse(await response.json()).moves;
}
