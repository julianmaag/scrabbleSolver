import { z } from 'zod';

/** Mirrors the backend `Word` model. */
export const scoredWordSchema = z.object({
  name: z.string().min(1),
  points: z.number().int().nonnegative(),
});

/** Mirrors the backend `Solutions` model. */
export const solutionsSchema = z.object({
  words: z.array(scoredWordSchema),
});

export type ScoredWord = z.infer<typeof scoredWordSchema>;
export type Solutions = z.infer<typeof solutionsSchema>;
