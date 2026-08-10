import { useCallback, useState } from 'react';
import { RACK_SIZE } from '@/types/rack';
import { validCharSchema, type ValidChar } from '@/types/validChar';

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

  const clear = useCallback(() => setLetters([]), []);

  return {
    letters,
    add,
    removeAt,
    removeLast,
    clear,
    isEmpty: letters.length === 0,
    isFull: letters.length >= RACK_SIZE,
  };
}
