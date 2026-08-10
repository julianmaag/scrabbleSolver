import { Tile } from '@/components/Tile/Tile';
import { cn } from '@/lib/utils';
import type { ScoredWord } from '@/types/solution';

const TOP_RANKS = 3;

type WordRowProps = {
  word: ScoredWord;
  rank: number;
};

/** One suggestion in the results list; the first few are highlighted as best plays. */
export function WordRow({ word, rank }: WordRowProps) {
  const isTop = rank <= TOP_RANKS;

  return (
    <div
      className={cn(
        'flex items-center gap-3 border-b border-tile-shadow/12 px-4 py-3',
        isTop && 'bg-gold/8',
      )}
    >
      <span
        className={cn(
          'w-6 shrink-0 text-right font-mono text-[11px] font-semibold',
          isTop ? 'text-ink-muted' : 'text-ink-faint',
        )}
      >
        {rank}
      </span>

      <div className="flex flex-1 gap-1">
        {[...word.name].map((char, index) => (
          <Tile key={index} letter={char} size="sm" />
        ))}
      </div>

      <span
        className={cn(
          'hidden flex-1 font-display text-[15px] lowercase text-ink sm:block',
          isTop ? 'font-semibold' : 'font-normal',
        )}
      >
        {word.name}
      </span>

      <div
        className={cn(
          'flex min-w-9 shrink-0 items-center justify-center gap-0.5 rounded px-2 py-1 font-mono text-[13px] font-semibold',
          isTop ? 'bg-felt text-gold' : 'bg-felt/8 text-felt',
        )}
      >
        {word.points}
        <span className="ml-px text-[9px] opacity-70">pts</span>
      </div>
    </div>
  );
}
