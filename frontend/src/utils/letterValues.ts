/** Standard English Scrabble tile values, grouped as in the backend `LetterValues` repository. */
const VALUE_GROUPS: ReadonlyArray<readonly [points: number, letters: string]> = [
  [1, 'aeioulnstr'],
  [2, 'dg'],
  [3, 'bcmp'],
  [4, 'fhvwy'],
  [5, 'k'],
  [8, 'jx'],
  [10, 'qz'],
];

export const LETTER_VALUES: Readonly<Record<string, number>> = Object.fromEntries(
  VALUE_GROUPS.flatMap(([points, letters]) => [...letters].map((letter) => [letter, points])),
);

/** Blanks (`?`) and unknown characters are worth nothing. */
export function getLetterValue(char: string): number {
  return LETTER_VALUES[char.toLowerCase()] ?? 0;
}
