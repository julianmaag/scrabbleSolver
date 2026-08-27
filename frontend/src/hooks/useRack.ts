import { useCallback, useState } from 'react';
import type { BoardLetter } from '@/types/board';
import { RACK_SIZE } from '@/types/rack';
import { BLANK, validCharSchema, type ValidChar } from '@/types/validChar';

/** Owns the rack state and the rules for changing it. */
export function useRack() {
  const [letters, setLetters] = useState<ValidChar[]>([]);

  const add = useCallback((char: string) => {
    const parsed = validCharSchema.safeParse(char);
    if (!parsed.success) return;
    // Functional update so the "rack is full" check always sees the latest state.
    setLetters((current) => (current.length >= RACK_SIZE ? current : [...current, parsed.data]));
  }, []);

  const removeAt = useCallback((index: number) => {
    setLetters((current) => current.filter((_, i) => i !== index));
  }, []);

  const removeLast = useCallback(() => {
    setLetters((current) => current.slice(0, -1));
  }, []);

  /** Spends exactly the tiles a played move used: a joker comes off the rack as a blank. */
  const consume = useCallback((tiles: readonly BoardLetter[]) => {
    setLetters((current) => {
      const remaining = [...current];
      for (const tile of tiles) {
        const index = remaining.indexOf(tile.joker ? BLANK : tile.letter);
        if (index !== -1) remaining.splice(index, 1);
      }
      return remaining;
    });
  }, []);

  const clear = useCallback(() => setLetters([]), []);

  return {
    letters,
    add,
    removeAt,
    removeLast,
    consume,
    clear,
    isEmpty: letters.length === 0,
    isFull: letters.length >= RACK_SIZE,
  };
}
