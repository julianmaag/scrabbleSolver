import { useEffect, useMemo, useRef, type ChangeEvent, type KeyboardEvent } from 'react';
import { BoardCell } from '@/components/Board/BoardCell';
import type { Cursor } from '@/hooks/useBoard';
import { BOARD_SIZE, PREMIUM_LAYOUT, toIndex, type BoardLetter, type BoardState } from '@/types/board';
import type { PlacedTile } from '@/types/move';

/** Arrow keys move the cursor; the axis moved along also sets the typing direction. */
const ARROW_DELTAS: Readonly<Record<string, readonly [row: number, column: number]>> = {
  ArrowUp: [-1, 0],
  ArrowDown: [1, 0],
  ArrowLeft: [0, -1],
  ArrowRight: [0, 1],
};

type BoardProps = {
  letters: BoardState;
  cursor: Cursor;
  /** Tiles a candidate move would add, ghosted onto the empty squares they would fill. */
  preview: readonly PlacedTile[];
  onFocusCell: (index: number) => void;
  onClearCell: (index: number) => void;
  onType: (char: string) => void;
  onDeleteBack: () => void;
  onNudgeCursor: (rowDelta: number, columnDelta: number) => void;
};

/**
 * The 15x15 board. A visually hidden input captures typing (and opens the on-screen keyboard
 * on touch devices), mirroring the Rack; the `letters` prop stays the source of truth.
 */
export function Board({
  letters,
  cursor,
  preview,
  onFocusCell,
  onClearCell,
  onType,
  onDeleteBack,
  onNudgeCursor,
}: BoardProps) {
  const inputRef = useRef<HTMLInputElement>(null);
  const focusInput = () => inputRef.current?.focus();

  useEffect(focusInput, []);

  const previewByIndex = useMemo(() => {
    const byIndex = new Map<number, BoardLetter>();
    for (const { row, column, letter, joker } of preview) {
      byIndex.set(toIndex(row, column), { letter, joker });
    }
    return byIndex;
  }, [preview]);

  function handleChange(event: ChangeEvent<HTMLInputElement>) {
    // The input is pinned to "" on every render, so this only ever sees newly typed characters.
    for (const char of event.target.value) onType(char);
  }

  function handleKeyDown(event: KeyboardEvent<HTMLInputElement>) {
    if (event.key === 'Backspace' || event.key === 'Delete') {
      onDeleteBack();
      return;
    }
    const delta = ARROW_DELTAS[event.key];
    if (delta) {
      event.preventDefault();
      onNudgeCursor(...delta);
    }
  }

  return (
    <div onClick={focusInput}>
      <div
        className="grid gap-px overflow-hidden rounded-md border border-line-strong bg-line"
        style={{ gridTemplateColumns: `repeat(${BOARD_SIZE}, minmax(0, 1fr))` }}
      >
        {letters.map((letter, index) => (
          <BoardCell
            key={index}
            index={index}
            premium={PREMIUM_LAYOUT[index]}
            letter={letter}
            preview={previewByIndex.get(index) ?? null}
            isCursor={index === cursor.index}
            onSelect={() => onFocusCell(index)}
            onClear={() => onClearCell(index)}
          />
        ))}
      </div>

      <input
        ref={inputRef}
        value=""
        onChange={handleChange}
        onKeyDown={handleKeyDown}
        aria-label="Type letters to place tiles on the board"
        autoCapitalize="none"
        autoCorrect="off"
        spellCheck={false}
        className="sr-only"
      />
    </div>
  );
}
