import { useState } from 'react';
import { Board } from '@/components/Board/Board';
import { Footer } from '@/components/Layout/Footer';
import { Header } from '@/components/Layout/Header';
import { Rack } from '@/components/Rack/Rack';
import { ResultsPanel } from '@/components/Results/ResultsPanel';
import { useBoard } from '@/hooks/useBoard';
import { useRack } from '@/hooks/useRack';
import { useSolver } from '@/hooks/useSolver';
import { cn } from '@/lib/utils';
import type { Move, PlacedTile } from '@/types/move';
import { RACK_SIZE } from '@/types/rack';

const NO_MOVES: readonly Move[] = [];
const NO_PREVIEW: readonly PlacedTile[] = [];

const SECTION = 'w-full max-w-xl';
const LABEL = 'font-mono text-[10px] font-medium uppercase tracking-[0.14em] text-ink-muted';
const RESET = 'cursor-pointer font-mono text-[10px] tracking-[0.06em] text-ink-muted underline decoration-dotted underline-offset-2 hover:text-ink';
const HINT = 'mt-3 text-center text-[11px] text-ink-muted';

function Home() {
  const board = useBoard();
  const rack = useRack();
  const solver = useSolver();
  const [selectedIndex, setSelectedIndex] = useState<number | null>(null);

  /** Results describe one board and rack, so any edit to either has to retire them. */
  const invalidate = () => {
    solver.reset();
    setSelectedIndex(null);
  };

  /** Wraps a mutation so the stale results go with it. Cursor moves are not mutations. */
  function edit<Args extends unknown[]>(mutate: (...args: Args) => void) {
    return (...args: Args) => {
      mutate(...args);
      invalidate();
    };
  }

  const playMove = (move: Move) => {
    board.applyPlacements(move.placements);
    rack.consume(move.placements);
    invalidate();
  };

  const moves = solver.state.status === 'solved' ? solver.state.moves : NO_MOVES;
  const preview =
    selectedIndex === null ? NO_PREVIEW : (moves[selectedIndex]?.placements ?? NO_PREVIEW);
  const isSolving = solver.state.status === 'loading';

  return (
    <div className="flex min-h-screen flex-col">
      <Header />

      <main className="flex flex-1 flex-col items-center gap-10 px-4 pb-16">
        <section className={SECTION}>
          <div className="mb-3 flex items-baseline justify-between">
            <h2 className={LABEL}>
              Rack · {rack.letters.length}/{RACK_SIZE}
            </h2>
            {!rack.isEmpty && (
              <button type="button" onClick={edit(rack.clear)} className={RESET}>
                clear
              </button>
            )}
          </div>

          <Rack
            letters={rack.letters}
            onAdd={edit(rack.add)}
            onRemoveAt={edit(rack.removeAt)}
            onRemoveLast={edit(rack.removeLast)}
          />

          <p className={HINT}>
            Type letters · <kbd className="font-mono text-ink-soft">?</kbd> for a blank · click a
            tile to remove it
          </p>
        </section>

        <section className={SECTION}>
          <div className="mb-3 flex items-baseline justify-between">
            <h2 className={LABEL}>Board</h2>
            {!board.isEmpty && (
              <button type="button" onClick={edit(board.clear)} className={RESET}>
                clear
              </button>
            )}
          </div>

          <Board
            letters={board.letters}
            cursor={board.cursor}
            preview={preview}
            onFocusCell={board.focusCell}
            onNudgeCursor={board.nudgeCursor}
            onType={edit(board.type)}
            onDeleteBack={edit(board.deleteBack)}
            onClearCell={edit(board.clearCell)}
          />

          <p className={HINT}>
            Click a square and type · click again to turn · backspace or right-click to erase
          </p>
        </section>

        <section className={SECTION}>
          <button
            type="button"
            onClick={() => solver.solve(rack.letters, board.letters)}
            disabled={rack.isEmpty || isSolving}
            className={cn(
              'w-full rounded-sm py-3 text-sm font-medium tracking-wide transition-opacity',
              rack.isEmpty || isSolving
                ? 'cursor-not-allowed bg-highlight text-ink-faint'
                : 'cursor-pointer bg-ink text-surface hover:opacity-85',
            )}
          >
            {isSolving ? 'Solving…' : 'Find Moves'}
          </button>

          {isSolving && (
            <p className={HINT}>
              Checking the whole dictionary against every square — this takes a while.
            </p>
          )}

          {solver.state.status === 'error' && (
            <p role="alert" className="mt-3 text-center text-[13px] text-danger">
              {solver.state.message}
            </p>
          )}

          {solver.state.status === 'idle' && board.isEmpty && (
            <p className={HINT}>
              The first move has to cover the centre square.
            </p>
          )}
        </section>

        {solver.state.status === 'solved' && (
          <ResultsPanel
            moves={solver.state.moves}
            selectedIndex={selectedIndex}
            onSelect={setSelectedIndex}
            onPlay={playMove}
          />
        )}
      </main>

      <Footer />
    </div>
  );
}

export default Home;
