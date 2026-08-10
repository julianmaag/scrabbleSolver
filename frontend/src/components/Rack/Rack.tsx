import { useEffect, useRef, type ChangeEvent, type KeyboardEvent } from 'react';
import { EmptySlot } from '@/components/Rack/EmptySlot';
import { RackSlot } from '@/components/Rack/RackSlot';
import { RACK_SIZE } from '@/types/rack';

type RackProps = {
  letters: string[];
  onAdd: (char: string) => void;
  onRemoveAt: (index: number) => void;
  onRemoveLast: () => void;
};

/**
 * The player's rack. A visually hidden input captures typing (and opens the
 * on-screen keyboard on touch devices); the `letters` prop stays the source of truth.
 */
export function Rack({ letters, onAdd, onRemoveAt, onRemoveLast }: RackProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const focusInput = () => inputRef.current?.focus();

  useEffect(focusInput, []);

  function handleChange(event: ChangeEvent<HTMLInputElement>) {
    // The input is pinned to "" on every render, so this only ever sees newly typed characters.
    for (const char of event.target.value) onAdd(char);
  }

  function handleKeyDown(event: KeyboardEvent<HTMLInputElement>) {
    if (event.key === 'Backspace') onRemoveLast();
  }

  return (
    <div className="flex min-h-15 items-center gap-2" onClick={focusInput}>
      {letters.map((letter, index) => (
        <RackSlot key={index} letter={letter} onRemove={() => onRemoveAt(index)} />
      ))}
      {Array.from({ length: RACK_SIZE - letters.length }, (_, index) => (
        <EmptySlot key={index} />
      ))}

      <input
        ref={inputRef}
        value=""
        onChange={handleChange}
        onKeyDown={handleKeyDown}
        aria-label="Type letters to add tiles to your rack"
        autoCapitalize="none"
        autoCorrect="off"
        spellCheck={false}
        className="sr-only"
      />
    </div>
  );
}
