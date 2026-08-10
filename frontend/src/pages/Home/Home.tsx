import { Footer } from '@/components/Layout/Footer';
import { Header } from '@/components/Layout/Header';
import { Rack } from '@/components/Rack/Rack';
import { ResultsPanel } from '@/components/Results/ResultsPanel';
import { useRack } from '@/hooks/useRack';
import { useSolver } from '@/hooks/useSolver';
import { cn } from '@/lib/utils';
import { RACK_SIZE } from '@/types/rack';

function Home() {
    const rack = useRack();
    const solver = useSolver();

    // Editing the rack invalidates results that are already on screen.
    const addLetter = (char: string) => {
        rack.add(char);
        solver.reset();
    };
    const removeLetterAt = (index: number) => {
        rack.removeAt(index);
        solver.reset();
    };
    const removeLastLetter = () => {
        rack.removeLast();
        solver.reset();
    };
    const clearRack = () => {
        rack.clear();
        solver.reset();
    };

    const isSolving = solver.state.status === 'loading';

    return (
        <div className="flex min-h-screen flex-col bg-linear-170 from-board to-board-deep">
            <Header />

            <main className="flex flex-1 flex-col items-center px-4 pb-16">
                <section className="mb-6 w-full max-w-xl rounded-lg border border-tile-edge/25 bg-white/45 p-6 shadow-panel backdrop-blur-sm">
                    <div className="mb-4 flex items-center justify-between">
                        <h2 className="font-mono text-[11px] font-semibold uppercase tracking-[0.12em] text-ink-muted">
                            Your rack — {rack.letters.length}/{RACK_SIZE} tiles
                        </h2>
                        {!rack.isEmpty && (
                            <button
                                type="button"
                                onClick={clearRack}
                                className="cursor-pointer font-mono text-[11px] tracking-[0.08em] text-ink-muted underline decoration-dotted"
                            >
                                clear
                            </button>
                        )}
                    </div>

                    <Rack
                        letters={rack.letters}
                        onAdd={addLetter}
                        onRemoveAt={removeLetterAt}
                        onRemoveLast={removeLastLetter}
                    />

                    <p className="my-5 text-center text-xs text-ink-muted/80">
                        Type letters to add tiles · click a tile to remove it
                    </p>

                    <button
                        type="button"
                        onClick={() => solver.solve(rack.letters)}
                        disabled={rack.isEmpty || isSolving}
                        className={cn(
                            'w-full rounded py-3 font-display text-base font-semibold tracking-wider transition-all',
                            rack.isEmpty
                                ? 'cursor-not-allowed bg-felt/20 text-felt/40'
                                : 'cursor-pointer bg-linear-135 from-felt to-felt-dark text-gold shadow-lg shadow-felt-dark/30',
                        )}
                    >
                        {isSolving ? 'Solving…' : 'Find Words'}
                    </button>

                    {solver.state.status === 'error' && (
                        <p role="alert" className="mt-4 text-center text-sm text-letter-rare">
                            {solver.state.message}
                        </p>
                    )}
                </section>

                {solver.state.status === 'solved' && <ResultsPanel words={solver.state.words} />}

                {solver.state.status === 'idle' && rack.isEmpty && (
                    <p className="mt-4 max-w-xs text-center font-display text-[15px] italic leading-relaxed text-ink-muted">
                        Enter up to {RACK_SIZE} letters from your rack — the solver ranks every
                        valid Scrabble word by point value.
                    </p>
                )}
            </main>

            <Footer />
        </div>
    );
}

export default Home