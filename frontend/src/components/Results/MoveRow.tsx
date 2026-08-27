import { Tile } from '@/components/Tile/Tile';
import { cn } from '@/lib/utils';
import type { Move, PlacedTile } from '@/types/move';

type MoveRowProps = {
  move: Move;
  rank: number;
  isSelected: boolean;
  onSelect: () => void;
};

/** Which squares the tiles land on, 1-based so it matches what you count on the board. */
function formatPosition({ row, column, direction }: Move): string {
  return `R${row + 1} C${column + 1} ${direction.toLowerCase()}`;
}

/**
 * The move's placements by their offset into the word. Letters without one are already on the
 * board, so they can be dimmed — and a placement carries the blank flag its tile needs.
 */
function placementsByOffset({ placements, row, column, direction }: Move): Map<number, PlacedTile> {
  return new Map(
    placements.map((placement) => [
      direction === 'ACROSS' ? placement.column - column : placement.row - row,
      placement,
    ]),
  );
}

/** One suggested move. Selecting it ghosts the tiles it would add onto the board. */
export function MoveRow({ move, rank, isSelected, onSelect }: MoveRowProps) {
  const placements = placementsByOffset(move);

  return (
    <button
      type="button"
      onClick={onSelect}
      aria-pressed={isSelected}
      className={cn(
        'flex w-full cursor-pointer items-center gap-3 border-l-2 border-b border-b-line px-4 py-2.5 text-left transition-colors',
        isSelected ? 'border-l-ink bg-highlight' : 'border-l-transparent hover:bg-highlight/60',
      )}
    >
      <span className="w-3 shrink-0 text-right font-mono text-[10px] text-ink-faint">{rank}</span>

      <div className="flex gap-1">
        {[...move.word].map((char, offset) => {
          const placed = placements.get(offset);
          return (
            <Tile
              key={offset}
              letter={char}
              size="sm"
              blank={placed?.joker ?? false}
              className={cn(!placed && 'opacity-40')}
            />
          );
        })}
      </div>

      <span className="ml-auto hidden font-mono text-[10px] tracking-tight text-ink-muted sm:block">
        {formatPosition(move)}
      </span>

      <span
        className={cn(
          'flex min-w-10 shrink-0 items-baseline justify-center gap-px rounded-sm px-2 py-1 font-mono text-[13px] font-medium',
          isSelected ? 'bg-ink text-surface' : 'bg-highlight text-ink-soft',
        )}
      >
        {move.points}
        <span className="text-[8px] opacity-60">pts</span>
      </span>
    </button>
  );
}
