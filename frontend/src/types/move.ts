import { z } from 'zod';
import { letterSchema } from './validChar';

/** Mirrors the backend `DIRECTION` enum. */
export const directionSchema = z.enum(['ACROSS', 'DOWN']);

/**
 * Mirrors the backend `PlacedTile`: one tile the player has to lay down. For a blank
 * (`joker`), `letter` is the letter it stands for, not `?`.
 */
export const placedTileSchema = z.object({
  row: z.number().int().nonnegative(),
  column: z.number().int().nonnegative(),
  letter: letterSchema,
  joker: z.boolean(),
});

/** Mirrors the backend `MoveDto`. */
export const moveSchema = z.object({
  /** The full word, including letters already on the board. */
  word: z.string().min(1),
  /** Main word plus every crossword formed, plus the bingo bonus. */
  points: z.number().int().nonnegative(),
  /** Start square of the full word. */
  row: z.number().int().nonnegative(),
  column: z.number().int().nonnegative(),
  direction: directionSchema,
  /** Only the new tiles — squares the word merely reuses are not listed. */
  placements: z.array(placedTileSchema),
});

/** Mirrors the backend `SolveResponse`. */
export const solveResponseSchema = z.object({
  moves: z.array(moveSchema),
});

export type Direction = z.infer<typeof directionSchema>;
export type PlacedTile = z.infer<typeof placedTileSchema>;
export type Move = z.infer<typeof moveSchema>;
