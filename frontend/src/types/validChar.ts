import { z } from 'zod';

/** A single letter a-z (case-insensitive), normalised to lowercase. */
export const letterSchema = z
  .string()
  .length(1)
  .regex(/^[a-zA-Z]$/, 'Must be a letter (a-z, A-Z)')
  .transform((char) => char.toLowerCase());

/** The blank tile, which stands in for any letter. */
export const BLANK = '?';

/** A single rack character: a letter, or `?` for a blank tile. */
export const validCharSchema = z.union([letterSchema, z.literal(BLANK)]);

export type Letter = z.infer<typeof letterSchema>;
export type ValidChar = z.infer<typeof validCharSchema>;
