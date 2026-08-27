import { Tile } from '@/components/Tile/Tile';
import { cn } from '@/lib/utils';
import {
  CENTER_INDEX,
  PREMIUM_LABELS,
  toColumn,
  toRow,
  type BoardLetter,
  type Premium,
} from '@/types/board';

/** Premium squares are tinted just enough to read: letter bonuses cool, word bonuses warm. */
const PREMIUM_STYLES: Readonly<Record<Premium, string>> = {
  NONE: 'bg-surface text-ink-faint',
  LETTER2: 'bg-bonus-letter text-ink-muted',
  LETTER3: 'bg-bonus-letter-strong text-ink-soft',
  WORD2: 'bg-bonus-word text-ink-muted',
  WORD3: 'bg-bonus-word-strong text-ink-soft',
};

type BoardCellProps = {
  index: number;
  premium: Premium;
  letter: BoardLetter | null;
  /** A tile a candidate move would place here, shown ghosted until the move is played. */
  preview: BoardLetter | null;
  isCursor: boolean;
  onSelect: () => void;
  onClear: () => void;
};

/** One square: a premium marker, or the tile on it, or a ghost of one a move would add. */
export function BoardCell({
  index,
  premium,
  letter,
  preview,
  isCursor,
  onSelect,
  onClear,
}: BoardCellProps) {
  const ghost = letter === null ? preview : null;
  const tile = letter ?? ghost;
  const marker = index === CENTER_INDEX ? '★' : PREMIUM_LABELS[premium];

  return (
    <button
      type="button"
      onClick={onSelect}
      // Right-click clears, so a mis-typed letter does not need the cursor walked back to it.
      onContextMenu={(event) => {
        event.preventDefault();
        onClear();
      }}
      aria-label={describe(index, tile, marker)}
      className={cn(
        'relative flex aspect-square cursor-pointer items-center justify-center',
        'font-mono text-[8px] transition-colors',
        PREMIUM_STYLES[premium],
        tile !== null && 'bg-surface',
        isCursor && 'z-10 ring-2 ring-ink ring-inset',
      )}
    >
      {tile ? (
        <Tile
          letter={tile.letter}
          size="xs"
          blank={tile.joker}
          className={cn(ghost && 'opacity-40')}
        />
      ) : (
        marker
      )}
    </button>
  );
}

function describe(index: number, tile: BoardLetter | null, marker: string): string {
  const square = `Row ${toRow(index) + 1} column ${toColumn(index) + 1}`;
  if (tile) return `${square}, ${tile.letter.toUpperCase()}${tile.joker ? ' (blank)' : ''}`;
  return marker ? `${square}, ${marker}` : `${square}, empty`;
}
