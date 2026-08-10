import type { ReactNode } from 'react';
import { cn } from '@/lib/utils';

type LengthFilterProps = {
  lengths: number[];
  selected: number | null;
  onSelect: (length: number | null) => void;
};

function FilterButton({
  active,
  onClick,
  children,
}: {
  active: boolean;
  onClick: () => void;
  children: ReactNode;
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      aria-pressed={active}
      className={cn(
        'cursor-pointer rounded border border-gold/30 px-2.5 py-1 font-mono text-xs font-semibold transition-colors',
        active ? 'bg-gold text-felt-dark' : 'bg-gold/15 text-gold hover:bg-gold/25',
      )}
    >
      {children}
    </button>
  );
}

/** Narrows the result list to words of a single length. */
export function LengthFilter({ lengths, selected, onSelect }: LengthFilterProps) {
  return (
    <div className="flex flex-wrap gap-1.5">
      <FilterButton active={selected === null} onClick={() => onSelect(null)}>
        all
      </FilterButton>
      {lengths.map((length) => (
        <FilterButton key={length} active={selected === length} onClick={() => onSelect(length)}>
          {length}L
        </FilterButton>
      ))}
    </div>
  );
}
