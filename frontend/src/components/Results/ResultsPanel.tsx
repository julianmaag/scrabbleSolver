import { useMemo, useState } from 'react';
import { LengthFilter } from '@/components/Results/LengthFilter';
import { WordRow } from '@/components/Results/WordRow';
import { Tile } from '@/components/Tile/Tile';
import type { ScoredWord } from '@/types/solution';

type ResultsPanelProps = {
  words: ScoredWord[];
};

/** Owns only its own view state (the active length filter); the word list comes from the parent. */
export function ResultsPanel({ words }: ResultsPanelProps) {
  const [lengthFilter, setLengthFilter] = useState<number | null>(null);

  const availableLengths = useMemo(
    () => [...new Set(words.map((word) => word.name.length))].sort((a, b) => a - b),
    [words],
  );

  const visibleWords = useMemo(
    () => (lengthFilter === null ? words : words.filter((word) => word.name.length === lengthFilter)),
    [words, lengthFilter],
  );

  const bestPlay = visibleWords[0];

  return (
    <section className="w-full max-w-xl overflow-hidden rounded-lg border border-tile-edge/20 bg-white/55 shadow-panel">
      <header className="flex flex-wrap items-center justify-between gap-2 bg-linear-135 from-felt to-felt-dark px-4 py-3">
        <h2 className="font-display text-sm font-semibold text-gold">
          {words.length} word{words.length === 1 ? '' : 's'} found
        </h2>
        <LengthFilter
          lengths={availableLengths}
          selected={lengthFilter}
          onSelect={setLengthFilter}
        />
      </header>

      {bestPlay && (
        <div className="flex items-center gap-2 border-b border-tile-shadow/12 bg-gold/12 px-4 py-2.5">
          <span className="text-xs text-ink-soft">Best play:</span>
          <div className="flex gap-1">
            {[...bestPlay.name].map((char, index) => (
              <Tile key={index} letter={char} size="sm" />
            ))}
          </div>
          <span className="font-display text-sm font-semibold text-felt">
            {bestPlay.points} pts
          </span>
        </div>
      )}

      <div className="max-h-115 overflow-y-auto">
        {visibleWords.length === 0 ? (
          <p className="px-6 py-12 text-center font-display text-[15px] text-ink-muted">
            No words found for this filter.
          </p>
        ) : (
          visibleWords.map((word, index) => (
            <WordRow key={word.name} word={word} rank={index + 1} />
          ))
        )}
      </div>
    </section>
  );
}
