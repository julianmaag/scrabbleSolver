import { MoveRow } from '@/components/Results/MoveRow';
import type { Move } from '@/types/move';

type ResultsPanelProps = {
  moves: Move[];
  selectedIndex: number | null;
  onSelect: (index: number) => void;
  onPlay: (move: Move) => void;
};

/** The ranked moves. Selection is owned by the parent, which also drives the board preview. */
export function ResultsPanel({ moves, selectedIndex, onSelect, onPlay }: ResultsPanelProps) {
  const selected = selectedIndex === null ? null : moves[selectedIndex];

  if (moves.length === 0) {
    return (
      <section className="w-full max-w-xl rounded-md border border-line bg-surface px-6 py-10 text-center">
        <p className="text-sm text-ink-muted">No legal move from this rack. Try different letters.</p>
      </section>
    );
  }

  return (
    <section className="w-full max-w-xl overflow-hidden rounded-md border border-line bg-surface shadow-panel">
      <header className="border-b border-line px-4 py-3">
        <h2 className="font-mono text-[10px] font-medium uppercase tracking-[0.14em] text-ink-muted">
          {moves.length} move{moves.length === 1 ? '' : 's'} · best first
        </h2>
      </header>

      <div className="max-h-115 overflow-y-auto">
        {moves.map((move, index) => (
          <MoveRow
            key={`${move.word}-${move.row}-${move.column}-${move.direction}`}
            move={move}
            rank={index + 1}
            isSelected={index === selectedIndex}
            onSelect={() => onSelect(index)}
          />
        ))}
      </div>

      {selected && (
        <div className="border-t border-line p-3">
          <button
            type="button"
            onClick={() => onPlay(selected)}
            className="w-full cursor-pointer rounded-sm bg-ink py-2.5 text-sm font-medium tracking-wide text-surface transition-opacity hover:opacity-85"
          >
            Play {selected.word.toUpperCase()} · {selected.points} pts
          </button>
        </div>
      )}
    </section>
  );
}
