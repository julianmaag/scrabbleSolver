import { z } from 'zod';

/** A single rack character: a-z (case-insensitive) or `?` for a blank tile. Normalised to lowercase. */
export const validCharSchema = z
  .string()
  .length(1)
  .regex(/^[a-zA-Z?]$/, 'Must be a letter (a-z, A-Z) or ?')
  .transform((char) => char.toLowerCase());

export type ValidChar = z.infer<typeof validCharSchema>;
