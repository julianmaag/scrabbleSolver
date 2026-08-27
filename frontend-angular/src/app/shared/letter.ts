import { z } from 'zod';

/** The blank tile, which stands in for any letter. */
export const BLANK = '?';

/** The 26 letters a-z. */
export const LETTERS = [
  'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
  'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
] as const;

/** A single lowercase letter a-z, or `?` for a blank tile. */
export const strictLetterSchema = z.enum([...LETTERS, BLANK], {
  error: 'Must be a letter (a-z) or ? for a blank',
});

/** Same, but accepts surrounding whitespace and uppercase, normalising to lowercase. */
export const letterSchema = z.string().trim().toLowerCase().pipe(strictLetterSchema);

export type Letter = z.infer<typeof strictLetterSchema>;

export function isLetter(value: unknown): value is Letter {
  return strictLetterSchema.safeParse(value).success;
}

export function safeParse(value: unknown): Letter {
  const result = strictLetterSchema.safeParse(value);
  if (!result.success) {
    throw new Error('Invalid letter');
  }
  return result.data;
}
